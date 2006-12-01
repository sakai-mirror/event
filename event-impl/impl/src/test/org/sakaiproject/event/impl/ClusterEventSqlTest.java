package org.sakaiproject.event.impl;


import junit.framework.TestCase;

public class ClusterEventSqlTest extends TestCase {

	ClusterEventSql es = null;
	
	protected void setUp() throws Exception 
	{
		super.setUp();
		es = (ClusterEventSql) new ClusterEventSql();
	}
	
	/*
	 * Test method for 'org.sakaiproject.event.impl.ClusterEventSql.returnSelectEvent(String)'
	 */
	public void testReturnSelectEventOracle() {
		assertEquals(es.returnSelectEvent("oracle"),es.returnSelectEventOracle());
	}
	
	public void testReturnSelectEventMysql() {
		assertEquals(es.returnSelectEvent("mysql"),es.returnSelectEventGeneric());
	}
	
	public void testReturnSelectEventHsql() {
		assertEquals(es.returnSelectEvent("hsql"),es.returnSelectEventGeneric());
	}
	
	public void testReturnInsertSakaiEventHsql() {
		assertEquals(es.returnInsertSakaiEvent("hsql"),es.returnInsertSakaiEventGeneric());
	}
	
	public void testReturnInsertSakaiEventOracle() {
		assertEquals(es.returnInsertSakaiEvent("oracle"),es.returnInsertSakaiEventOracle());
	}
	
	public void testReturnInsertSakaiEventMysql() {
		assertEquals(es.returnInsertSakaiEvent("mysql"),es.returnInsertSakaiEventMysql());
	}

	/*
	 * Test method for 'org.sakaiproject.event.impl.ClusterEventSql.returnSelectMaxEventId()'
	 */
	public void testReturnSelectMaxEventId() {
		// TODO Auto-generated method stub

	}

}
