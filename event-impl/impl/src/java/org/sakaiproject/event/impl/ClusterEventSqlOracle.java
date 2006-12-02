/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005, 2006 The Sakai Foundation.
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

public class ClusterEventSqlOracle extends ClusterEventSqlGeneric {

	public String returnSelectEvent() {
		String statement;
		// this now has Oracle specific hint to improve performance with large
		// tables -ggolden
		statement = "select /*+ FIRST_ROWS */ EVENT_ID,EVENT_DATE,EVENT,REF,SAKAI_EVENT.SESSION_ID,EVENT_CODE,SESSION_SERVER"
				+ " from SAKAI_EVENT,SAKAI_SESSION"
				+ " where (SAKAI_EVENT.SESSION_ID = SAKAI_SESSION.SESSION_ID(+)) and (EVENT_ID > ?)";
		return statement;
	}

	public String returnInsertSakaiEvent() {
		String statement;
		statement = "insert into SAKAI_EVENT"
				+ " (EVENT_ID,EVENT_DATE,EVENT,REF,SESSION_ID,EVENT_CODE)"
				+ " values ("
				// form the id based on the sequence
				+ " SAKAI_EVENT_SEQ.NEXTVAL,"
				// date
				+ " ?,"
				// event
				+ " ?,"
				// reference
				+ " ?,"
				// session id
				+ " ?,"
				// code
				+ " ?"

				+ " )";
		return statement;
	}
}