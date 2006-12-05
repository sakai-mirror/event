/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2006 The Sakai Foundation.
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

import java.io.File;
import java.io.FileInputStream;
import java.sql.SQLException;



public class SqlApiTestHsql extends AbstractSqlApiTest {
	
	public SqlApiTestHsql(String name) throws SQLException, Exception {
		
		super(name);
		
		// inputDataSet = "/Users/dlhaines/dev/sakai/refactor-sql/trunk/event/event-impl/impl/src/test/dataset.xml";
		ddlFile = "/Users/dlhaines/dev/sakai/refactor-sql/trunk/event/event-impl/impl/src/sql/hsqldb/sakai_event.sql";

		pw = "";
		user = "sa";
		dbName = "jdbc:hsqldb:.";
		driverClassName = "org.hsqldb.jdbcDriver";
		
		
		if (ddlFile != null && ddlFile.length() > 0) {
			executeDdlFile(new File(ddlFile), getConnection().getConnection());
			ddlFile = null;
		}
	}
	
	// need to generate test for event tables.
	
//	public void testCheckDataLoaded() throws Exception {
//		assertNotNull(loadedDataSet);
//		int rowCount = loadedDataSet.getTable(TABLE_NAME).getRowCount();
//		assertEquals(2,rowCount);
//	}
	
	
}
