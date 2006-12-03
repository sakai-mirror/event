package org.sakaiproject.event.impl;

public class SqlApiTestHsql extends SqlApiTest {

		public SqlApiTestHsql(String name) {
			pw = "";
			user = "sa";
			dbName = "jdbc:hsqldb:.";
			driverClassName = "org.hsqldb.jdbcDriver";
		}
}
