/**********************************************************************************
 * $URL$
 * $Id$
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

/**
 * database methods.
 */
public interface UsageSessionServiceSql
{
	/**
	 * returns the sql statement which inserts a sakai session into the sakai_session table.
	 */
	String getInsertSakaiSessionSql();

	/**
	 * returns the sql statement which retrieves a sakai session from the sakai_session table for a given session id.
	 */
	String getSakaiSessionSql1();

	/**
	 * returns the sql statement which retrieves all the sakai sessions from the sakai_session table where the session start time equals the session
	 * end time.
	 */
	String getSakaiSessionSql2();

	/**
	 * returns the sql statement which retrieves all the sakai sessions from the sakai_session table based on a join column and criteria.
	 */
	String getSakaiSessionSql3(String alias, String joinAlias, String joinTable, String joinColumn, String joinCriteria);

	/**
	 * returns the sql statement which updates a sakai session in the sakai_session table for a given session id.
	 */
	String getUpdateSakaiSessionSql();
}