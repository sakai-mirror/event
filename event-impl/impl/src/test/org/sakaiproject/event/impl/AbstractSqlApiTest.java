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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.dbunit.DatabaseTestCase;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;


/************************************************
 * base class for running dbUnit tests for Sakai 
 * SQL classes.
 * 
 * Extend this class for particular database implementation and for 
 * a particular dataset to test.
 * 
 * Might want to make a sub class that points to the testdata to use
 * and extend that for particular implementations (so can use same
 * testdata for different implementations.)
 * 
 * TTD:
 * - Find out what to do about SQL execeptions.  Currently they
 * are ignored since the ddl is run again for each test.  How can 
 * that be avoided?
 ************************************************/

public abstract class AbstractSqlApiTest extends DatabaseTestCase {
	
	/****************
	 * These values are filled in by instances of this class that deal with 
	 * particular database implementations and particular test data.
	 */

	// The dataset loaded for this test.
	protected FlatXmlDataSet loadedDataSet = null;
	
	// The file path for the input data set.
	protected String inputDataSet = null;
	
	// A file with initial DDL for this test.  This should run
	// onlyruns only when the test suite is first started,
	// but it runs with every test right now.
	protected String ddlFile = null;
	
	// Name of the DB driver to use.
	protected String driverClassName = null;
	
	// Name of the db as given to the database implementation
	protected String dbName = null;
	
	// The user to use.
	protected String user = null;
	
	// The password to use.
	protected String pw = null;
	
	public AbstractSqlApiTest(String name) {
		super(name);
	}
	
	// Supply a default dataset to use.
	protected IDataSet getDataSet() throws Exception {
		FileInputStream fis = new FileInputStream(inputDataSet);
		FlatXmlDataSet fxds = new FlatXmlDataSet(fis);
		loadedDataSet = fxds;
		return loadedDataSet;
	}
	
	// Supply a method to make the database connection.
	protected IDatabaseConnection getConnection () throws Exception {
		Class driverClass = Class.forName(driverClassName);
		Connection jdbcConnection = DriverManager.getConnection(
				dbName,user, pw);
		
		return new DatabaseConnection(jdbcConnection);
	}
	
	// dbUnit will perform this before each test.
	// Some other values are: 
	//   - DatabaseOperation.REFRESH; - restore the data
	//      from the input file into the db.  Any additional data
	//      in the db is not changed.
	//   - DatabaseOperation.NONE - to do nothing.
	protected DatabaseOperation getSetUpOperation() 
	throws Exception {
		return DatabaseOperation.CLEAN_INSERT;
	}
	
	// dbUnit will perform this after each test.
	// By convention each test will set up data as required and 
	// this tear down will do nothing.
	protected DatabaseOperation getTearDownOperation() 
	throws Exception {
		return DatabaseOperation.NONE;
	}
	
	// These methods are based on code from HypersonicEnvironment in the dbunit code base.
	// http://dbunit.sourceforge.net/xref-test/org/dbunit/HypersonicEnvironment.html
	
	// dbUnit doesn't execute ddl by default.  Suppy a routine to do that.
	public static void executeDdlFile(File ddlFile, Connection connection) throws Exception
	{
		
		BufferedReader sqlReader = new BufferedReader(new FileReader(ddlFile));
		StringBuffer sqlBuffer = new StringBuffer();
		while (sqlReader.ready())
		{
			String line = sqlReader.readLine();
			if (!line.startsWith("-"))
			{
				sqlBuffer.append(line);
			}
		}
		
		String sql = sqlBuffer.toString();
		executeSql( connection, sql );
	}
	
	// Helper method for executing the ddl file.
	public static void executeSql( Connection connection, String sql ) throws SQLException {
	//	System.out.println("in eS");
		Statement statement = connection.createStatement();
		try
		{
	//		System.out.println("execute sql: ["+sql+"]");
			statement.execute(sql);
	//		System.out.println("execute sql: done 2");
		}
		catch (java.sql.SQLException e) {
			// ignore sql exception, assuming it is creating a table that already exists.
	//		System.out.println("SQLException: "+e);
		}
		finally
		{
	//		System.out.println("begin eS finally");
			statement.close();
	//		System.out.println("end eS finally");
		}
	}
	
}
