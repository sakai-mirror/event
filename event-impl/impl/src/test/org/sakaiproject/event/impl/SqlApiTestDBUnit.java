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
import java.io.InputStream;
import java.sql.SQLException;

/*******************************************************************************
 * 
 * @author dlhaines
 * 
 * Class will make some simple tests using hsqldb and the tests supplied with
 * dbUnit to make sure that this dbUnit code is working. If it runs successfully
 * then dbUnit is running and, if so configured, will read in a DDL file on
 * startup.
 * 
 * TTD: 
 * - Read fixed values from properties file.
 * - make the ddl file run only with the first test.
 * 
 * Much of this is based on examples from
 * http://www.realsolve.co.uk/site/tech/dbunit-quickstart.php
 ******************************************************************************/

public class SqlApiTestDBUnit extends AbstractSqlApiTest {

	// Name of test table.
	public static final String TABLE_NAME = "MANUFACTURER";

	/*
	 * Setup hsql values for the database.
	 */
	public SqlApiTestDBUnit(String name) throws SQLException, Exception {

		super(name);

		inputDataSet = "dbunitTestDATA.xml";
		ddlFile = "dbunitTestDDL.sql";
		pw = "";
		user = "sa";

		// Use a transient in-memory db by default but, as the commented code shows,
		// you can specify an on-disk persistant db.  That is most useful for debugging.
		
		dbName = "jdbc:hsqldb:.";
		// dbName = "jdbc:hsqldb:/Users/dlhaines/dev/sakai/refactor-sql/trunk/event/event-impl/impl/src/test/dbtestdb";

		String dbProperties = "";
		// If you need to add values to the db string you can use this.  It is most helpful for debugging.
		//dbProperties = ";hsqldb.applog=5;server.silent=false;hsqldb.trace=true";
		dbName += dbProperties;
		driverClassName = "org.hsqldb.jdbcDriver";

		// If there is a ddlFile then run it.
		if (ddlFile != null && ddlFile.length() > 0) {
			InputStream in = this.getClass().getClassLoader().getResourceAsStream(ddlFile);
			executeDdlFile(in, getConnection().getConnection());
			// Figure out how to avoid running the ddlFile more than once.
		}
	}

	
		/************** Simple sanity tests **************/
	
	// Make sure there are two tests to see what happens when
	// setup between tests.
	
	// Make sure that can find a test that doesn't depend
	// on any files, so that can see if the junit approach
	// itself is having problems.

	public void testEmpty() {
		assertTrue("must pass", 1 == 1);
	}

	// Make sure that the default dataset loaded and has the
	// right number of rows, so it is likely that the right
	// data has loaded.
	
	public void testCheckDataLoaded() throws Exception {
		assertNotNull(loadedDataSet);
		int rowCount = loadedDataSet.getTable(TABLE_NAME).getRowCount();
		assertEquals(2, rowCount);
	}

}
