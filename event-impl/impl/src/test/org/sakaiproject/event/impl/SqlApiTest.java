package org.sakaiproject.event.impl;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;

import org.dbunit.DatabaseTestCase;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

public abstract class SqlApiTest extends DatabaseTestCase {
	
	public static final String TABLE_NAME = "MANUFACTURER";
	
	protected String inputDataSet = "./dataset.xml";
	
	private FlatXmlDataSet loadedDataSet = null;
	
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
		return new DatabaseConnection(jdbcConnection);
	}
	
	public void testCheckDataLoaded() throws Exception {
		assertNotNull(loadedDataSet);
		int rowCount = loadedDataSet.getTable(TABLE_NAME).getRowCount();
		assertEquals(2,rowCount);
	}
}
