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
import org.sakaiproject.event.api.ClusterEventSql;

public class ClusterEventSqlTextTest extends TestCase {

	/*
	 * Testing that different implementations give back appropriate
	 * sql.
	 */

	
	/* test select event requests */
	public void testReturnSelectEventOracle() {
		ClusterEventSql co = new ClusterEventSqlOracle();
		assertTrue("use Oracle hint",co.returnSelectEvent().contains(" FIRST_ROWS "));
	}
	
	/* test select event requests */
	public void testReturnInsertEventOracle() {
		ClusterEventSql co = new ClusterEventSqlOracle();
		assertTrue("Use oracle sequence",co.returnInsertSakaiEvent().contains(" SAKAI_EVENT_SEQ.NEXTVAL, "));
	}
	
	/* test select event requests */
	public void testReturnInsertEventHsql() {
		ClusterEventSql co = new ClusterEventSqlHsql();
		assertTrue("Use hsql sequence",co.returnInsertSakaiEvent().contains(" SAKAI_EVENT_SEQ,"));
	}
	
	/* test select event requests */
	public void testReturnInsertEventMySql() {
		ClusterEventSql co = new ClusterEventSqlMySql();
		assertTrue("Use MySql sequence",!co.returnInsertSakaiEvent().contains("EVENT_ID"));
	}
	

}
