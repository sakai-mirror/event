package org.sakaiproject.event.impl;

import java.io.File;
import java.io.FileInputStream;
import java.sql.SQLException;



public class SqlApiTestDbunit extends SqlApiTest {
	
//	protected String sakai_event_schema = new FileInputStream(sakai_event_schema_file);
//	protected String ddlFile = "/Users/dlhaines/dev/sakai/refactor-sql/trunk/event/event-impl/impl/src/test/dbunitTest.sql";

	public SqlApiTestDbunit(String name) throws SQLException, Exception {
		
		// an exeriment to see if we can get more tracing to see what the heck is
		// giving a null AssertionFailedError with no stack trace.
		inputDataSet = "/Users/dlhaines/dev/sakai/refactor-sql/trunk/event/event-impl/impl/src/test/dataset.xml";
		ddlFile = "/Users/dlhaines/dev/sakai/refactor-sql/trunk/event/event-impl/impl/src/test/dbunitTest.sql";
		pw = "";
		user = "sa";
	//	dbName = "jdbc:hsqldb:.";
		dbName = "jdbc:hsqldb:/Users/dlhaines/dev/sakai/refactor-sql/trunk/event/event-impl/impl/src/test/dbtestdb";
		String dbProperties = ";hsqldb.applog=5;server.silent=false";
		dbName += dbProperties;
		driverClassName = "org.hsqldb.jdbcDriver";
		
		
		if (ddlFile != null && ddlFile.length() > 0) {
			executeDdlFile(new File(ddlFile), getConnection().getConnection());
		}
	}
	
	public void testMustFail () {
		System.out.println("test must fail");
		fail("must fail");
	}
	
//	public void testCheckDataLoaded() throws Exception {
//		System.out.println("in CheckDataLoaded");
//		assertTrue("must fail",1==0);
//		assertNotNull("data set loaded",loadedDataSet);
//		int rowCount = loadedDataSet.getTable(TABLE_NAME).getRowCount();
//		assertEquals("row count",2,rowCount);
//		System.out.println("end checkdatatloaded");
//	}
	
//	public void testSomething() throws Exception {
//		System.out.println("in TestSomething");
//		assertEquals("must fail",2,3);
//		System.out.println("at end of TestSomething");
//	}
	
	
}
