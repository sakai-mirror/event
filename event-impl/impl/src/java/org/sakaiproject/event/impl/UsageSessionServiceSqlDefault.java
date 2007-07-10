/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/rsmart/dbrefactor/chat/chat-impl/impl/src/java/org/sakaiproject/chat/impl/UsageSessionServiceSqlDefault.java $
 * $Id: UsageSessionServiceSqlDefault.java 3560 2007-02-19 22:08:01Z jbush@rsmart.com $
 ***********************************************************************************
 *
 * Copyright (c) 2007 The Sakai Foundation.
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/
package org.sakaiproject.event.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * methods for accessing session usage data in a database.
 */
public class UsageSessionServiceSqlDefault implements UsageSessionServiceSql
{

   /**
	 * returns the sql statement which inserts a sakai session into the sakai_session table.
	 */
	public String getInsertSakaiSessionSql()
	{
		return "insert into SAKAI_SESSION (SESSION_ID,SESSION_SERVER,SESSION_USER,SESSION_IP,SESSION_USER_AGENT,SESSION_START,SESSION_END) values (?, ?, ?, ?, ?, ?, ?)";
	}

	/**
	 * returns the sql statement which retrieves a sakai session from the sakai_session table for a given session id.
	 */
	public String getSakaiSessionSql1()
	{
		return "select SESSION_ID,SESSION_SERVER,SESSION_USER,SESSION_IP,SESSION_USER_AGENT,SESSION_START,SESSION_END from SAKAI_SESSION where SESSION_ID = ?";
	}

	/**
	 * returns the sql statement which retrieves all the sakai sessions from the sakai_session table where the session start time equals the session
	 * end time.
	 */
	public String getSakaiSessionSql2()
	{
		return "select SESSION_ID,SESSION_SERVER,SESSION_USER,SESSION_IP,SESSION_USER_AGENT,SESSION_START,SESSION_END " + "from   SAKAI_SESSION "
				+ "where  SESSION_START = SESSION_END ORDER BY SESSION_SERVER ASC, SESSION_START ASC";
	}

	/**
	 * returns the sql statement which retrieves all the sakai sessions from the sakai_session table based on a join column and criteria.
	 */
   public String getSakaiSessionSql3(String alias, String joinAlias, String joinTable, String joinColumn, String joinCriteria)
   {
      return "select " + alias + ".SESSION_ID," + alias + ".SESSION_SERVER," + alias + ".SESSION_USER," + alias + ".SESSION_IP," + alias + ".SESSION_USER_AGENT," + alias + ".SESSION_START," + alias + ".SESSION_END " +
             "from   SAKAI_SESSION " + alias                                    + " " +
             "inner join " + joinTable + " " + joinAlias                        + " " +
             "ON "    + alias + ".SESSION_ID = " + joinAlias + "." + joinColumn + " " +
             "where " + joinCriteria;
   }

   /**
    * returns the sql statement which updates a sakai session in the sakai_session table for a given session id.
    */
	public String getUpdateSakaiSessionSql()
	{
		return "update SAKAI_SESSION set SESSION_END = ? where SESSION_ID = ?";
	}
}