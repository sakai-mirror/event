/**********************************************************************************
 * $URL$
 * $Id$
 **********************************************************************************
 *
 * Copyright (c) 2003, 2004 The Regents of the University of Michigan, Trustees of Indiana University,
 *                  Board of Trustees of the Leland Stanford, Jr., University, and The MIT Corporation
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License");
 * By obtaining, using and/or copying this Original Work, you agree that you have read,
 * understand, and will comply with the terms and conditions of the Educational Community License.
 * You may obtain a copy of the License at:
 * 
 *      http://cvs.sakaiproject.org/licenses/license_1_0.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 **********************************************************************************/

// package
package org.sakaiproject.component.adaptor;

// imports
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.kernel.id.IdManager;
import org.sakaiproject.api.kernel.session.Session;
import org.sakaiproject.api.kernel.session.SessionBindingEvent;
import org.sakaiproject.api.kernel.session.SessionBindingListener;
import org.sakaiproject.api.kernel.session.SessionManager;
import org.sakaiproject.api.kernel.session.ToolSession;
import org.sakaiproject.api.kernel.thread_local.ThreadLocalManager;
import org.sakaiproject.service.framework.config.ServerConfigurationService;
import org.sakaiproject.service.framework.session.SessionState;
import org.sakaiproject.service.framework.session.SessionStateBindingListener;
import org.sakaiproject.service.framework.session.UsageSession;
import org.sakaiproject.service.framework.session.UsageSessionService;
import org.sakaiproject.service.framework.sql.SqlReader;
import org.sakaiproject.service.framework.sql.SqlService;
import org.sakaiproject.service.legacy.time.Time;
import org.sakaiproject.service.legacy.time.TimeService;
import org.sakaiproject.util.LoginUtil;

/**
 * <p>
 * UsageSessionServiceAdaptor implements Sakai1's UsageSessionService for Sakai. The Session aspects are done as an adaptor to the SessionManager. UsageSession entities are handled as was in the ClusterUsageSessionService.
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public class UsageSessionServiceAdaptor implements UsageSessionService
{
	/** Our log (commons). */
	private static Log M_log = LogFactory.getLog(UsageSessionServiceAdaptor.class);

	/** Storage manager for this service. */
	protected Storage m_storage = null;

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Abstractions, etc.
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**
	 * Construct storage for this service.
	 */
	protected Storage newStorage()
	{
		return new ClusterStorage();
	}

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Dependencies and their setter methods
	 *********************************************************************************************************************************************************************************************************************************************************/

	/** Dependency: TimeService. */
	protected TimeService m_timeService = null;

	/**
	 * Dependency: TimeService.
	 * 
	 * @param service
	 *        The TimeService.
	 */
	public void setTimeService(TimeService service)
	{
		m_timeService = service;
	}

	/** Dependency: SqlService. */
	protected SqlService m_sqlService = null;

	/**
	 * Dependency: SqlService.
	 * 
	 * @param service
	 *        The SqlService.
	 */
	public void setSqlService(SqlService service)
	{
		m_sqlService = service;
	}

	/** Dependency: ServerConfigurationService. */
	protected ServerConfigurationService m_serverConfigurationService = null;

	/**
	 * Dependency: ServerConfigurationService.
	 * 
	 * @param service
	 *        The ServerConfigurationService.
	 */
	public void setServerConfigurationService(ServerConfigurationService service)
	{
		m_serverConfigurationService = service;
	}

	/** Dependency: the current manager. */
	protected ThreadLocalManager m_threadLocalManager = null;

	/**
	 * Dependency - set the current manager.
	 * 
	 * @param value
	 *        The current manager.
	 */
	public void setThreadLocalManager(ThreadLocalManager manager)
	{
		m_threadLocalManager = manager;
	}

	/** Dependency: the session manager. */
	protected SessionManager m_sessionManager = null;

	/**
	 * Dependency - set the session manager.
	 * 
	 * @param value
	 *        The session manager.
	 */
	public void setSessionManager(SessionManager manager)
	{
		m_sessionManager = manager;
	}

	/** Dependency: the id manager. */
	protected IdManager m_idManager = null;

	/**
	 * Dependency - set the id manager.
	 * 
	 * @param value
	 *        The id manager.
	 */
	public void setIdManager(IdManager manager)
	{
		m_idManager = manager;
	}

	/** Configuration: to run the ddl on init or not. */
	protected boolean m_autoDdl = false;

	/**
	 * Configuration: to run the ddl on init or not.
	 * 
	 * @param value
	 *        the auto ddl value.
	 */
	public void setAutoDdl(String value)
	{
		m_autoDdl = new Boolean(value).booleanValue();
	}

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Init and Destroy
	 *********************************************************************************************************************************************************************************************************************************************************/

	public UsageSessionServiceAdaptor()
	{
		m_storage = newStorage();
	}

	/**
	 * Final initialization, once all dependencies are set.
	 */
	public void init()
	{
		try
		{
			// open storage
			m_storage.open();

			M_log.info("init()");
		}
		catch (Throwable t)
		{
			M_log.warn("init(): ", t);
		}
	}

	/**
	 * Returns to uninitialized state.
	 */
	public void destroy()
	{
		m_storage.close();

		M_log.info("destroy()");
	}

	/**********************************************************************************************************************************************************************************************************************************************************
	 * UsageSessionService implementation
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**
	 * @inheritDoc
	 */
	public UsageSession startSession(String userId, String remoteAddress, String userAgent)
	{
		// do we have a current session?
		Session s = m_sessionManager.getCurrentSession();
		if (s != null)
		{
			UsageSession session = (UsageSession) s.getAttribute(USAGE_SESSION_KEY);
			if (session != null)
			{
				// If we have a session for this user, simply reuse
				if (userId != null && userId.equals(session.getUserId()))
				{
					return session;
				}
				
				// if it is for another user, we will create a new session, log a warning, and unbound/close the existing one
				s.setAttribute(USAGE_SESSION_KEY, null);
				M_log.warn("startSession: replacing existing UsageSession: " + session.getId() + " user: " + session.getUserId()
						+ " for new user: " + userId);
			}

			// create the usage session and bind it to the session
			session = new BaseUsageSession(m_idManager.createUuid(), m_serverConfigurationService.getServerIdInstance(), userId,
					remoteAddress, userAgent, null, null);

			// store
			if (m_storage.addSession(session))
			{
				// set as the current session
				s.setAttribute(USAGE_SESSION_KEY, session);

				return session;
			}
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public UsageSession getSession()
	{
		UsageSession rv = null;

		// do we have a current session?
		Session s = m_sessionManager.getCurrentSession();
		if (s != null)
		{
			// do we have a usage session in the session?
			rv = (BaseUsageSession) s.getAttribute(USAGE_SESSION_KEY);
		}

		else
		{
			M_log.warn("getSession: no current SessionManager session!");
		}

		return rv;
	}

	/**
	 * @inheritDoc
	 */
	public String getSessionUserId()
	{
		// just return the session manage's current user id
		String rv = null;

		// do we have a current session?
		Session s = m_sessionManager.getCurrentSession();
		if (s != null)
		{
			// use the authenticated user uuid in the session
			rv = s.getUserId();
		}

		// may be null, which indicates that there's no user id available
		return rv;
	}

	/**
	 * @inheritDoc
	 */
	public String getSessionId()
	{
		String rv = null;

        // See http://bugs.sakaiproject.org/jira/browse/SAK-1507
        // At server startup, when Spring is initializing components, there may not
        // be a session manager yet.  This adaptor may be called before all components
        // are initialized since there are hidden dependencies (through static covers)
        // of which Spring is not aware.  Therefore, check for and handle a null 
        // m_sessionManager.
        if (m_sessionManager == null) return null;
        
		// do we have a current session?
		Session s = m_sessionManager.getCurrentSession();
		if (s != null)
		{
			// do we have a usage session in the session?
			BaseUsageSession session = (BaseUsageSession) s.getAttribute(USAGE_SESSION_KEY);
			if (session != null)
			{
				rv = session.getId();
			}
		}

		// may be null, which indicates that there's no session
		return rv;
	}

	/**
	 * @inheritDoc
	 */
	public SessionState getSessionState(String key)
	{
		// map this to the sakai session's tool session concept, using key as the placement id
		Session s = m_sessionManager.getCurrentSession();
		if (s != null)
		{
			return new SessionStateWrapper(s.getToolSession(key));
		}

		M_log.warn("getSessionState(): no session:  key: " + key);
		return null;
	}

	/**
	 * @inheritDoc
	 */
	public UsageSession setSessionActive(boolean auto)
	{
		throw new UnsupportedOperationException();
		//		BaseUsageSession session = (BaseUsageSession) getSession();
		//		if (session == null) return null;
		//
		//		if (session.isClosed()) return session;
		//
		//		if (auto)
		//		{
		//			// do not mark the current session as having user activity
		//			// but close it if it's timed out from no user activity
		//			if (session.isInactive())
		//			{
		//				session.close();
		//			}
		//		}
		//		else
		//		{
		//			// mark the current session as having user activity
		//			session.setActivity();
		//		}
		//
		//		return session;
	}

	/**
	 * @inheritDoc
	 */
	public UsageSession getSession(String id)
	{
		UsageSession rv = m_storage.getSession(id);

		return rv;
	}

	/**
	 * @inheritDoc
	 */
	public List getSessions(List ids)
	{
		List rv = m_storage.getSessions(ids);

		return rv;
	}

	/**
	 * @inheritDoc
	 */
	public List getSessions(String criteria, Object[] values)
	{
		List rv = m_storage.getSessions(criteria, values);

		return rv;
	}

	/**
	 * @inheritDoc
	 */
	public int getSessionInactiveTimeout()
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * @inheritDoc
	 */
	public int getSessionLostTimeout()
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * @inheritDoc
	 */
	public List getOpenSessions()
	{
		return m_storage.getOpenSessions();
	}

	/**
	 * @inheritDoc
	 */
	public Map getOpenSessionsByServer()
	{
		List all = m_storage.getOpenSessions();

		Map byServer = new TreeMap();

		List current = null;
		String key = null;

		for (Iterator i = all.iterator(); i.hasNext();)
		{
			UsageSession s = (UsageSession) i.next();

			// to start, or when the server changes, create a new inner list and add to the map
			if ((key == null) || (!key.equals(s.getServer())))
			{
				key = s.getServer();
				current = new Vector();
				byServer.put(key, current);
			}

			current.add(s);
		}

		return byServer;
	}

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Storage
	 *********************************************************************************************************************************************************************************************************************************************************/

	protected interface Storage
	{
		/**
		 * Open.
		 */
		void open();

		/**
		 * Close.
		 */
		void close();

		/**
		 * Take this session into storage.
		 * 
		 * @param session
		 *        The usage session.
		 * @return true if added successfully, false if not.
		 */
		boolean addSession(UsageSession session);

		/**
		 * Access a session by id
		 * 
		 * @param id
		 *        The session id.
		 * @return The session object.
		 */
		UsageSession getSession(String id);

		/**
		 * Access a bunch of sessions by the List id session ids.
		 * 
		 * @param ids
		 *        The session id List.
		 * @return The List (UsageSession) of session objects for these ids.
		 */
		List getSessions(List ids);

		/**
		 * Access a List of usage sessions by *arbitrary criteria*.
		 * 
		 * @param criteria
		 *        A string with meaning known to the particular implementation of the API running.
		 * @param fields
		 *        Optional values to go with the criteria in an implementation specific way.
		 * @return The List (UsageSession) of UsageSession object for these ids.
		 */
		List getSessions(String criteria, Object[] values);

		/**
		 * This session is now closed.
		 * 
		 * @param session
		 *        The session which is closed.
		 */
		void closeSession(UsageSession session);

		/**
		 * Access a list of all open sessions.
		 * 
		 * @return a List (UsageSession) of all open sessions, ordered by server, then by start (asc)
		 */
		List getOpenSessions();
	}

	/**********************************************************************************************************************************************************************************************************************************************************
	 * UsageSession
	 *********************************************************************************************************************************************************************************************************************************************************/

	protected class BaseUsageSession implements UsageSession, SessionBindingListener
	{
		/** The user id for this session. */
		protected String m_user = null;

		/** The unique id for this session. */
		protected String m_id = null;

		/** The server which is hosting the session. */
		protected String m_server = null;

		/** The IP Address from which this session originated. */
		protected String m_ip = null;

		/** The User Agent string describing the browser used in this session. */
		protected String m_userAgent = null;

		/** The BrowserID string describing the browser used in this session. */
		protected String m_browserId = null;

		/** The time the session was started */
		protected Time m_start = null;

		/** The time the session was closed. */
		protected Time m_end = null;

		/**
		 * Construct fully.
		 * 
		 * @param id
		 *        The session id.
		 * @param server
		 *        The server id which is hosting the session.
		 * @param user
		 *        The user id for this session.
		 * @param address
		 *        The IP Address from which this session originated.
		 * @param agent
		 *        The User Agent string describing the browser used in this session.
		 */
		public BaseUsageSession(String id, String server, String user, String address, String agent, Time start, Time end)
		{
			m_id = id;
			m_server = server;
			m_user = user;
			m_ip = address;
			m_userAgent = agent;
			if (start != null)
			{
				m_start = start;
				m_end = end;
			}
			else
			{
				m_start = m_timeService.newTime();
				m_end = m_start;
			}
			setBrowserId(agent);
		}

		/**
		 * Set the browser id for this session, decoded from the user agent string.
		 * 
		 * @param agent
		 *        The user agent string.
		 */
		protected void setBrowserId(String agent)
		{
			if (agent == null)
			{
				m_browserId = UNKNOWN;
			}

			// test whether agent is UserAgent value for a known browser.
			// should we also check version number?
			else if (agent.indexOf("Netscape") >= 0 && agent.indexOf("Mac") >= 0)
			{
				m_browserId = MAC_NN;
			}
			else if (agent.indexOf("Netscape") >= 0 && agent.indexOf("Windows") >= 0)
			{
				m_browserId = WIN_NN;
			}
			else if (agent.indexOf("MSIE") >= 0 && agent.indexOf("Mac") >= 0)
			{
				m_browserId = MAC_IE;
			}
			else if (agent.indexOf("MSIE") >= 0 && agent.indexOf("Windows") >= 0)
			{
				m_browserId = WIN_IE;
			}
			else if (agent.indexOf("Camino") >= 0 && agent.indexOf("Macintosh") >= 0)
			{
				m_browserId = MAC_CM;
			}
			else if (agent.startsWith("Mozilla") && agent.indexOf("Windows") >= 0)
			{
				m_browserId = WIN_MZ;
			}
			else if (agent.startsWith("Mozilla") && agent.indexOf("Macintosh") >= 0)
			{
				m_browserId = MAC_MZ;
			}
			else
			{
				m_browserId = UNKNOWN;
			}
		}

		/**
		 * Close the session.
		 */
		protected void close()
		{
			if (!isClosed())
			{
				m_end = m_timeService.newTime();
				m_storage.closeSession(this);
			}
		}

		/**
		 * @inheritDoc
		 */
		public boolean isClosed()
		{
			return (!(m_end.equals(m_start)));
		}

		/**
		 * @inheritDoc
		 */
		public String getUserId()
		{
			return m_user;
		}

		/**
		 * @inheritDoc
		 */
		public String getId()
		{
			return m_id;
		}

		/**
		 * @inheritDoc
		 */
		public String getServer()
		{
			return m_server;
		}

		/**
		 * @inheritDoc
		 */
		public String getIpAddress()
		{
			return m_ip;
		}

		/**
		 * @inheritDoc
		 */
		public String getUserAgent()
		{
			return m_userAgent;
		}

		/**
		 * @inheritDoc
		 */
		public String getBrowserId()
		{
			return m_browserId;
		}

		/**
		 * @inheritDoc
		 */
		public Time getStart()
		{
			return m_timeService.newTime(m_start.getTime());
		}

		/**
		 * @inheritDoc
		 */
		public Time getEnd()
		{
			return m_timeService.newTime(m_end.getTime());
		}

		/**
		 * There's new user activity now.
		 */
		protected void setActivity()
		{
			throw new UnsupportedOperationException();
		}

		/**
		 * Has this session gone inactive?
		 * 
		 * @return True if the session has seen no activity in the last timeout period, false if it's still active.
		 */
		protected boolean isInactive()
		{
			throw new UnsupportedOperationException();
		}

		/**
		 * @inheritDoc
		 */
		public void valueBound(SessionBindingEvent sbe)
		{
		}

		/**
		 * @inheritDoc
		 */
		public void valueUnbound(SessionBindingEvent sbe)
		{
			// if we didn't close this already, close
			if (!isClosed())
			{
				// close the session
				close();

				// generate the logout event
				LoginUtil.logoutEvent(this);
			}
		}

		/**
		 * @inheritDoc
		 */
		public int compareTo(Object obj)
		{
			if (!(obj instanceof UsageSession)) throw new ClassCastException();

			// if the object are the same, say so
			if (obj == this) return 0;

			// start the compare by comparing their users
			int compare = getUserId().compareTo(((UsageSession) obj).getUserId());

			// if these are the same
			if (compare == 0)
			{
				// sort based on (unique) id
				compare = getId().compareTo(((UsageSession) obj).getId());
			}

			return compare;
		}

		/**
		 * @inheritDoc
		 */
		public String toString()
		{
			return "[" + ((m_id == null) ? "" : m_id) + " | " + ((m_server == null) ? "" : m_server) + " | "
					+ ((m_user == null) ? "" : m_user) + " | " + ((m_ip == null) ? "" : m_ip) + " | "
					+ ((m_userAgent == null) ? "" : m_userAgent) + " | " + m_start.toStringGmtFull() + " ]";
		}
	}

	/**********************************************************************************************************************************************************************************************************************************************************
	 * SessionState
	 *********************************************************************************************************************************************************************************************************************************************************/

	public class SessionStateWrapper implements SessionState
	{
		/** The ToolSession object wrapped. */
		protected ToolSession m_session = null;

		public SessionStateWrapper(ToolSession session)
		{
			m_session = session;
		}

		/**
		 * @inheritDoc
		 */
		public Object getAttribute(String name)
		{
			return m_session.getAttribute(name);
		}

		/**
		 * @inheritDoc
		 */
		public Object setAttribute(String name, Object value)
		{
			Object old = m_session.getAttribute(name);
			unBindAttributeValue(name, old);

			m_session.setAttribute(name, value);
			bindAttributeValue(name, value);

			return old;
		}

		/**
		 * @inheritDoc
		 */
		public Object removeAttribute(String name)
		{
			Object old = m_session.getAttribute(name);
			unBindAttributeValue(name, old);

			m_session.removeAttribute(name);

			return old;
		}

		/**
		 * @inheritDoc
		 */
		public void clear()
		{
			// unbind
			for (Enumeration e = m_session.getAttributeNames(); e.hasMoreElements();)
			{
				String name = (String) e.nextElement();
				Object value = m_session.getAttribute(name);
				unBindAttributeValue(name, value);
			}

			m_session.clearAttributes();
		}

		/**
		 * @inheritDoc
		 */
		public List getAttributeNames()
		{
			List rv = new Vector();
			for (Enumeration e = m_session.getAttributeNames(); e.hasMoreElements();)
			{
				String name = (String) e.nextElement();
				rv.add(name);
			}

			return rv;
		}

		/**
		 * @inheritDoc
		 */
		public int size()
		{
			throw new UnsupportedOperationException();
		}

		/**
		 * @inheritDoc
		 */
		public boolean isEmpty()
		{
			throw new UnsupportedOperationException();
		}

		/**
		 * @inheritDoc
		 */
		public boolean containsKey(Object key)
		{
			throw new UnsupportedOperationException();
		}

		/**
		 * @inheritDoc
		 */
		public boolean containsValue(Object value)
		{
			throw new UnsupportedOperationException();
		}

		/**
		 * @inheritDoc
		 */
		public Object get(Object key)
		{
			throw new UnsupportedOperationException();
		}

		/**
		 * @inheritDoc
		 */
		public Object put(Object key, Object value)
		{
			throw new UnsupportedOperationException();
		}

		/**
		 * @inheritDoc
		 */
		public Object remove(Object key)
		{
			throw new UnsupportedOperationException();
		}

		/**
		 * @inheritDoc
		 */
		public void putAll(Map t)
		{
			throw new UnsupportedOperationException();
		}

		/**
		 * @inheritDoc
		 */
		public Set keySet()
		{
			throw new UnsupportedOperationException();
		}

		/**
		 * @inheritDoc
		 */
		public Collection values()
		{
			throw new UnsupportedOperationException();
		}

		/**
		 * @inheritDoc
		 */
		public Set entrySet()
		{
			throw new UnsupportedOperationException();
		}

		/**
		 * If the object is a SessionStateBindingListener, unbind it
		 * 
		 * @param attributeName
		 *        The attribute name.
		 * @param attribute
		 *        The attribute object
		 */
		protected void unBindAttributeValue(String attributeName, Object attribute)
		{
			// if this object wants session binding notification
			if ((attribute != null) && (attribute instanceof SessionStateBindingListener))
			{
				try
				{
					((SessionStateBindingListener) attribute).valueUnbound(null, attributeName);
				}
				catch (Throwable e)
				{
					M_log.warn("unBindAttributeValue: unbinding exception: ", e);
				}
			}
		}

		/**
		 * If the object is a SessionStateBindingListener, bind it
		 * 
		 * @param attributeName
		 *        The attribute name.
		 * @param attribute
		 *        The attribute object
		 */
		protected void bindAttributeValue(String attributeName, Object attribute)
		{
			// if this object wants session binding notification
			if ((attribute != null) && (attribute instanceof SessionStateBindingListener))
			{
				try
				{
					((SessionStateBindingListener) attribute).valueBound(null, attributeName);
				}
				catch (Throwable e)
				{
					M_log.warn("bindAttributeValue: unbinding exception: ", e);
				}
			}
		}
	}

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Storage component
	 *********************************************************************************************************************************************************************************************************************************************************/

	protected class ClusterStorage implements Storage
	{
		/**
		 * Open and be ready to read / write.
		 */
		public void open()
		{
			// if we are auto-creating our schema, check and create
			if (m_autoDdl)
			{
				m_sqlService.ddl(this.getClass().getClassLoader(), "sakai_session");
			}
		}

		/**
		 * Close.
		 */
		public void close()
		{
		}

		/**
		 * Take this session into storage.
		 * 
		 * @param session
		 *        The usage session.
		 * @return true if added successfully, false if not.
		 */
		public boolean addSession(UsageSession session)
		{
			// and store it in the db
			String statement = "insert into SAKAI_SESSION (SESSION_ID,SESSION_SERVER,SESSION_USER,SESSION_IP,SESSION_USER_AGENT,SESSION_START,SESSION_END) values (?, ?, ?, ?, ?, ?, ?)";

			// collect the fields
			Object fields[] = new Object[7];
			fields[0] = session.getId();
			fields[1] = session.getServer();
			fields[2] = session.getUserId();
			fields[3] = session.getIpAddress();
			fields[4] = session.getUserAgent();
			fields[5] = session.getStart();
			fields[6] = session.getEnd();

			// process the insert
			boolean ok = m_sqlService.dbWrite(statement, fields);
			if (!ok)
			{
				M_log.warn(".addSession(): dbWrite failed");
				return false;
			}
			
			return true;

		} // addSession

		/**
		 * Access a session by id
		 * 
		 * @param id
		 *        The session id.
		 * @return The session object.
		 */
		public UsageSession getSession(String id)
		{
			UsageSession rv = null;

			// check the db
			String statement = "select SESSION_ID,SESSION_SERVER,SESSION_USER,SESSION_IP,SESSION_USER_AGENT,SESSION_START,SESSION_END from SAKAI_SESSION where SESSION_ID = ?";

			// send in the last seq number parameter
			Object[] fields = new Object[1];
			fields[0] = id;

			List sessions = m_sqlService.dbRead(statement, fields, new SqlReader()
			{
				public Object readSqlResultRecord(ResultSet result)
				{
					try
					{
						// read the UsageSession
						String id = result.getString(1);
						String server = result.getString(2);
						String userId = result.getString(3);
						String ip = result.getString(4);
						String agent = result.getString(5);
						Time start = m_timeService.newTime(result.getTimestamp(6, m_sqlService.getCal()).getTime());
						Time end = m_timeService.newTime(result.getTimestamp(7, m_sqlService.getCal()).getTime());

						UsageSession session = new BaseUsageSession(id, server, userId, ip, agent, start, end);
						return session;
					}
					catch (SQLException ignore)
					{
						return null;
					}
				}
			});

			if (!sessions.isEmpty()) rv = (UsageSession) sessions.get(0);

			return rv;

		} // getSession

		/**
		 * @inheritDoc
		 */
		public List getSessions(List ids)
		{
			// TODO: do this in a single SQL call! -ggolden
			List rv = new Vector();
			for (Iterator i = ids.iterator(); i.hasNext();)
			{
				String id = (String) i.next();
				UsageSession s = getSession(id);
				if (s != null)
				{
					rv.add(s);
				}
			}

			return rv;
		}

		/**
		 * Access a List of usage sessions by *arbitrary criteria*.
		 * 
		 * @param criteria
		 *        A string with meaning known to the particular implementation of the API running.
		 * @param fields
		 *        Optional values to go with the criteria in an implementation specific way.
		 * @return The List (UsageSession) of UsageSession object for these ids.
		 */
		public List getSessions(String criteria, Object[] values)
		{
			UsageSession rv = null;

			// use criteria as the where clause
			String statement = "select SESSION_ID,SESSION_SERVER,SESSION_USER,SESSION_IP,SESSION_USER_AGENT,SESSION_START,SESSION_END"
					+ " from SAKAI_SESSION where SESSION_ID IN ( " + criteria + " )";

			List sessions = m_sqlService.dbRead(statement, values, new SqlReader()
			{
				public Object readSqlResultRecord(ResultSet result)
				{
					try
					{
						// read the UsageSession
						String id = result.getString(1);
						String server = result.getString(2);
						String userId = result.getString(3);
						String ip = result.getString(4);
						String agent = result.getString(5);
						Time start = m_timeService.newTime(result.getTimestamp(6, m_sqlService.getCal()).getTime());
						Time end = m_timeService.newTime(result.getTimestamp(7, m_sqlService.getCal()).getTime());

						UsageSession session = new BaseUsageSession(id, server, userId, ip, agent, start, end);
						return session;
					}
					catch (SQLException ignore)
					{
						return null;
					}
				}
			});

			return sessions;
		}

		/**
		 * This session is now closed.
		 * 
		 * @param session
		 *        The session which is closed.
		 */
		public void closeSession(UsageSession session)
		{
			// close the session on the db
			String statement = "update SAKAI_SESSION set SESSION_END = ? where SESSION_ID = ?";

			// collect the fields
			Object fields[] = new Object[2];
			fields[0] = session.getEnd();
			fields[1] = session.getId();

			// process the statement
			boolean ok = m_sqlService.dbWrite(statement, fields);
			if (!ok)
			{
				M_log.warn(".closeSession(): dbWrite failed");
			}

		} // closeSession

		/**
		 * Access a list of all open sessions.
		 * 
		 * @return a List (UsageSession) of all open sessions, ordered by server, then by start (asc)
		 */
		public List getOpenSessions()
		{
			UsageSession rv = null;

			// check the db
			String statement = "select SESSION_ID,SESSION_SERVER,SESSION_USER,SESSION_IP,SESSION_USER_AGENT,SESSION_START,SESSION_END"
					+ " from SAKAI_SESSION where SESSION_START = SESSION_END ORDER BY SESSION_SERVER ASC, SESSION_START ASC";

			List sessions = m_sqlService.dbRead(statement, null, new SqlReader()
			{
				public Object readSqlResultRecord(ResultSet result)
				{
					try
					{
						// read the UsageSession
						String id = result.getString(1);
						String server = result.getString(2);
						String userId = result.getString(3);
						String ip = result.getString(4);
						String agent = result.getString(5);
						Time start = m_timeService.newTime(result.getTimestamp(6, m_sqlService.getCal()).getTime());
						Time end = m_timeService.newTime(result.getTimestamp(7, m_sqlService.getCal()).getTime());

						UsageSession session = new BaseUsageSession(id, server, userId, ip, agent, start, end);
						return session;
					}
					catch (SQLException ignore)
					{
						return null;
					}
				}
			});

			return sessions;
		}
	}
}



