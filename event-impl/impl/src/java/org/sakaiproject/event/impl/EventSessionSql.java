/**
 * 
 */
package org.sakaiproject.event.impl;

class EventSessionSql {

	// The following SessionSQL methods return SQL used by the
	// ClusterStorage class. They should be externalized to allow swapping
	// in
	// versions optimized for particular sql dialects.

	static String returnInsertSession() {
		String statement = "insert into SAKAI_SESSION (SESSION_ID,SESSION_SERVER,SESSION_USER,SESSION_IP,SESSION_USER_AGENT,SESSION_START,SESSION_END) values (?, ?, ?, ?, ?, ?, ?)";
		return statement;
	}

	static String returnSelectSession() {
		String statement = "select SESSION_ID,SESSION_SERVER,SESSION_USER,SESSION_IP,SESSION_USER_AGENT,SESSION_START,SESSION_END from SAKAI_SESSION where SESSION_ID = ?";
		return statement;
	}

	static String returnSomeSessions(String criteria) {
		String statement = "select SESSION_ID,SESSION_SERVER,SESSION_USER,SESSION_IP,SESSION_USER_AGENT,SESSION_START,SESSION_END"
				+ " from SAKAI_SESSION where SESSION_ID IN ( "
				+ criteria
				+ " )";
		return statement;
	}

	static String returnUpdateSessionEnd() {
		String statement = "update SAKAI_SESSION set SESSION_END = ? where SESSION_ID = ?";
		return statement;
	}

	static String returnSelectOpenSessions() {
		String statement = "select SESSION_ID,SESSION_SERVER,SESSION_USER,SESSION_IP,SESSION_USER_AGENT,SESSION_START,SESSION_END"
				+ " from SAKAI_SESSION where SESSION_START = SESSION_END ORDER BY SESSION_SERVER ASC, SESSION_START ASC";
		return statement;
	}
	
	static String returnGetSessionJoin(String joinTable, String joinAlias, String joinColumn, String joinCriteria, String alias) {
		String statement = "select " + alias + ".SESSION_ID," + alias + ".SESSION_SERVER," + alias
				+ ".SESSION_USER," + alias + ".SESSION_IP," + alias + ".SESSION_USER_AGENT," + alias + ".SESSION_START," + alias + ".SESSION_END"
				+ " from SAKAI_SESSION " + alias
				+ " inner join " + joinTable + " " + joinAlias
				+ " ON " + alias + ".SESSION_ID = " + joinAlias + "." + joinColumn
				+ " where " + joinCriteria;
		return statement;
	}

}