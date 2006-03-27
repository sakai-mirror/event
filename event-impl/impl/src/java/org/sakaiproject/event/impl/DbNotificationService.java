/**********************************************************************************
* $URL$
* $Id$
***********************************************************************************
*
* Copyright (c) 2003, 2004, 2005 The Regents of the University of Michigan, Trustees of Indiana University,
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
package org.sakaiproject.component.legacy.notification;

// import
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.sakaiproject.event.api.Notification;
import org.sakaiproject.event.api.NotificationEdit;

/**
* <p>DbNotificationService is ... %%%.</p>
* 
* @author University of Michigan, Sakai Software Development Team
* @version $Revision$
*/
public class DbNotificationService
	extends BaseNotificationService
{
	/** Table name for users. */
	protected String m_tableName = "SAKAI_NOTIFICATION";

	/** If true, we do our locks in the remote database, otherwise we do them here. */
	protected boolean m_locksInDb = true;

	/*******************************************************************************
	* Constructors, Dependencies and their setter methods
	*******************************************************************************/

	/** Dependency: SqlService */
	protected SqlService m_sqlService = null;

	/**
	 * Dependency: SqlService.
	 * @param service The SqlService.
	 */
	public void setSqlService(SqlService service)
	{
		m_sqlService = service;
	}

	/**
	 * Configuration: set the table name
	 * @param path The table name.
	 */
	public void setTableName(String name)
	{
		m_tableName = name;
	}

	/**
	 * Configuration: set the locks-in-db
	 * @param value The locks-in-db value.
	 */
	public void setLocksInDb(String value)
	{
		m_locksInDb = new Boolean(value).booleanValue();
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

	/*******************************************************************************
	* Init and Destroy
	*******************************************************************************/

	/**
	 * Final initialization, once all dependencies are set.
	 */
	public void init()
	{
		try
		{
			// if we are auto-creating our schema, check and create
			if (m_autoDdl)
			{
				m_sqlService.ddl(this.getClass().getClassLoader(), "sakai_notification");
			}

			super.init();

			m_logger.info(this +".init(): table: " + m_tableName + " locks-in-db: " + m_locksInDb);
		}
		catch (Throwable t)
		{
			m_logger.warn(this +".init(): ", t);
		}
	}

	/*******************************************************************************
	* BaseNotificationService extensions
	*******************************************************************************/

	/**
	* Construct a Storage object.
	* @return The new storage object.
	*/
	protected Storage newStorage()
	{
		return new DbStorage(this);

	}	// newStorage

	/*******************************************************************************
	* Storage implementation
	*******************************************************************************/

	protected class DbStorage
		extends BaseDbSingleStorage
		implements Storage
	{
		/**
		* Construct.
		* @param user The StorageUser class to call back for creation of Resource and Edit objects.
		*/
		public DbStorage(StorageUser user)
		{
			super(m_tableName, "NOTIFICATION_ID", null, m_locksInDb, "notification", user, m_sqlService);

		}	// DbStorage

		public boolean check(String id) { return super.checkResource(id); }

		public Notification get(String id) { return (Notification) super.getResource(id); }

		public List getAll() { return super.getAllResources(); }

		/**
		* Get a Set of all the notifications that are interested in this Event function.
		* Note: instead of this looking, we could have an additional "key" in storage of the event... -ggolen
		* @param function The Event function
		* @return The Set (Notification) of all the notifications that are interested in this Event function.
		*/
		public List getAll(String function)
		{
			List rv = new Vector();
			if (function == null) return rv;

			List all = super.getAllResources();
			for (Iterator it = all.iterator(); it.hasNext(); )
			{
				Notification notification = (Notification) it.next();
				if (notification.containsFunction(function))
				{
					rv.add(notification);
				}
			}
			
			return rv;

		}	// getAll

		public NotificationEdit put(String id) { return (NotificationEdit) super.putResource(id, null); }

		public NotificationEdit edit(String id) { return (NotificationEdit) super.editResource(id); }

		public void commit(NotificationEdit edit) { super.commitResource(edit); }

		public void cancel(NotificationEdit edit) { super.cancelResource(edit); }

		public void remove(NotificationEdit edit) { super.removeResource(edit); }

	}	// DbStorage

}	// DbNotificationService



