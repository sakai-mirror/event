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

public abstract class SqlApiTest extends DatabaseTestCase {
	
	public static final String TABLE_NAME = "MANUFACTURER";

	protected FlatXmlDataSet loadedDataSet = null;
	protected String inputDataSet = null;
	protected String ddlFile = null;
	protected String driverClassName = null;
	protected String dbName = null;
	protected String user = null;
	protected String pw = null;
	
	
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(new FileInputStream(inputDataSet));
	}
	
	protected IDatabaseConnection getConnection () throws Exception {
		Class driverClass = Class.forName(driverClassName);
		Connection jdbcConnection = DriverManager.getConnection(
				dbName,user, pw);

//		DatabaseConnection dc = new DatabaseConnection(jdbcConnection);
//		
//		return dc;
		return new DatabaseConnection(jdbcConnection);
	}
	
	
	protected DatabaseOperation getSetUpOperation() 
	throws Exception {
	//	return DatabaseOperation.REFRESH;
		return DatabaseOperation.CLEAN_INSERT;
	}
	
	protected DatabaseOperation getTearDownOperation() 
	throws Exception {
		return DatabaseOperation.NONE;
	}
	
	// These are based on code from HypersonicEnvironment in the dbunit code base.
	// http://dbunit.sourceforge.net/xref-test/org/dbunit/HypersonicEnvironment.html
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
	
	public static void executeSql( Connection connection, String sql ) throws SQLException {
		Statement statement = connection.createStatement();
		try
		{
			System.out.println("execute sql: ["+sql+"]");
			statement.execute(sql);
			System.out.println("execute sql: done 2");
		}
		catch (java.sql.SQLException e) {
			// ignore sql exception, assuming it is creating a table that already exists.
			System.out.println("SQLException: "+e);
		}
		finally
		{
			statement.close();
		}
	}
	
}
