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

class ClusterEventSql {

	static String returnSelectEvent(String vendor) {
		String statement = null;
		if ("oracle".equals(vendor)) {
			statement = returnSelectEventOracle();
		} else
		// non-Oracle, without Oracle hint
		{
			statement = returnSelectEventGeneric();
		}
		return statement;
	}

	/**
	 * Form the proper event insert statement for the database technology.
	 * 
	 * @param vendor
	 *            TODO
	 * 
	 * @return The SQL insert statement for writing an event.
	 */

	static protected String returnInsertSakaiEvent(String vendor) {
		String statement;
		if ("oracle".equals(vendor)) {
			statement = returnInsertSakaiEventOracle();
		} else if ("mysql".equals(vendor)) {
			statement = returnInsertSakaiEventMysql();
		} else
		// if ("hsqldb".equals(sqlService().getVendor()))
		{
			statement = returnInsertSakaiEventGeneric();
		}

		return statement;
	}
	
	/* Methods above here determine which dialect specific method to call.
	 * The following methods return specific sql.
	 */ 

	static protected String returnSelectMaxEventId() {
		String statement = "select MAX(EVENT_ID) from SAKAI_EVENT";
		return statement;
	}

	static protected String returnInsertSakaiEventOracle() {
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

	static protected String returnInsertSakaiEventMysql() {
		String statement;
		// leave out the EVENT_ID as it will be automatically generated on the
		// server
		statement = "insert into SAKAI_EVENT"
				+ " (EVENT_DATE,EVENT,REF,SESSION_ID,EVENT_CODE)" + " values ("
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

	static protected String returnInsertSakaiEventGeneric() {
		String statement;
		statement = "insert into SAKAI_EVENT"
				+ " (EVENT_ID,EVENT_DATE,EVENT,REF,SESSION_ID,EVENT_CODE)"
				+ " values ("
				// form the id based on the sequence
				+ " NEXT VALUE FOR SAKAI_EVENT_SEQ,"
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

	static protected String returnSelectEventOracle() {
		String statement;
		// this now has Oracle specific hint to improve performance with large
		// tables -ggolden
		statement = "select /*+ FIRST_ROWS */ EVENT_ID,EVENT_DATE,EVENT,REF,SAKAI_EVENT.SESSION_ID,EVENT_CODE,SESSION_SERVER"
				+ " from SAKAI_EVENT,SAKAI_SESSION"
				+ " where (SAKAI_EVENT.SESSION_ID = SAKAI_SESSION.SESSION_ID(+)) and (EVENT_ID > ?)";
		return statement;
	}

	static protected String returnSelectEventGeneric() {
		String statement;
		statement = "select EVENT_ID,EVENT_DATE,EVENT,REF,SAKAI_EVENT.SESSION_ID,EVENT_CODE,SESSION_SERVER"
				+ " from SAKAI_EVENT,SAKAI_SESSION"
				+ " where (SAKAI_EVENT.SESSION_ID = SAKAI_SESSION.SESSION_ID) and (EVENT_ID > ?)";
		return statement;
	}
}