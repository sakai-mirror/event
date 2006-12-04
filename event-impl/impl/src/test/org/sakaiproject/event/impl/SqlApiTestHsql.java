package org.sakaiproject.event.impl;

import java.io.File;
import java.io.FileInputStream;
import java.sql.SQLException;



public class SqlApiTestHsql extends SqlApiTest {
	
//	protected String sakai_event_schema = new FileInputStream(sakai_event_schema_file);
//	protected String ddlFile = "/Users/dlhaines/dev/sakai/refactor-sql/trunk/event/event-impl/impl/src/sql/hsqldb/sakai_event.sql";
//	protected String inputDataSet = "/Users/dlhaines/dev/sakai/refactor-sql/trunk/event/event-impl/impl/src/test/dataset.xml";
//	ddlFile = "/Users/dlhaines/dev/sakai/refactor-sql/trunk/event/event-impl/impl/src/sql/hsqldb/sakai_event.sql";
	
	public SqlApiTestHsql(String name) throws SQLException, Exception {
		inputDataSet = "/Users/dlhaines/dev/sakai/refactor-sql/trunk/event/event-impl/impl/src/test/dataset.xml";
		ddlFile = "/Users/dlhaines/dev/sakai/refactor-sql/trunk/event/event-impl/impl/src/sql/hsqldb/sakai_event.sql";

		pw = "";
		user = "sa";
		dbName = "jdbc:hsqldb:.";
		driverClassName = "org.hsqldb.jdbcDriver";
		
		
		if (ddlFile != null && ddlFile.length() > 0) {
			executeDdlFile(new File(ddlFile), getConnection().getConnection());
		}
	}
	
	public void testCheckDataLoaded() throws Exception {
		assertNotNull(loadedDataSet);
		int rowCount = loadedDataSet.getTable(TABLE_NAME).getRowCount();
		assertEquals(2,rowCount);
	}
	
	
}
