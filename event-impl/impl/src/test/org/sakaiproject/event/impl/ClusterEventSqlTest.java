/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005, 2006 The Sakai Foundation.
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

import junit.framework.TestCase;

public class ClusterEventSqlTest extends TestCase {

	/*
	 * Testing logic that selects proper SQL based on the SQL dialect.
	 */
	
	/* test select event requests */
	public void testReturnSelectEventOracle() {
		assertEquals(ClusterEventSql.returnSelectEvent("oracle"),ClusterEventSql.returnSelectEventOracle());
	}
	
	public void testReturnSelectEventMysql() {
		assertEquals(ClusterEventSql.returnSelectEvent("mysql"),ClusterEventSql.returnSelectEventGeneric());
	}
	
	public void testReturnSelectEventHsql() {
		assertEquals(ClusterEventSql.returnSelectEvent("hsql"),ClusterEventSql.returnSelectEventGeneric());
	}
	
	/* test insert events requests */
	
	public void testReturnInsertSakaiEventHsql() {
		assertEquals(ClusterEventSql.returnInsertSakaiEvent("hsql"),ClusterEventSql.returnInsertSakaiEventGeneric());
	}
	
	public void testReturnInsertSakaiEventOracle() {
		assertEquals(ClusterEventSql.returnInsertSakaiEvent("oracle"),ClusterEventSql.returnInsertSakaiEventOracle());
	}
	
	public void testReturnInsertSakaiEventMysql() {
		assertEquals(ClusterEventSql.returnInsertSakaiEvent("mysql"),ClusterEventSql.returnInsertSakaiEventMysql());
	}

}
