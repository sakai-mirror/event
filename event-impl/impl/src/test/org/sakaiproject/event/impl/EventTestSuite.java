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

import java.sql.Types;
import java.util.Date;
import java.util.Map;

import javax.sql.DataSource;

import org.sakaiproject.event.api.ClusterEventSql;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

import junit.framework.Assert;

public class EventTestSuite extends AbstractDependencyInjectionSpringContextTests {

	@Override
	protected String[] getConfigLocations() {
		return new String[] {"spring-test.xml"};
	}

	private DataSource[] dataSources = new DataSource[2];
	private ClusterEventSql[] clusterEventSqls = new ClusterEventSql[2];

	public void onSetUp() {
		dataSources[0] = (DataSource)applicationContext.getBean("hsqlDataSource");
		dataSources[1] = (DataSource)applicationContext.getBean("mySqlDataSource");

		clusterEventSqls[0] = (ClusterEventSql)applicationContext.getBean("eventSqlHsql");
		clusterEventSqls[1] = (ClusterEventSql)applicationContext.getBean("eventSqlMySql");
	}
	
	public void testInsertAndSelectEvents() throws Exception {
		for(int i=0; i<dataSources.length; i++) {
			DataSource dataSource = dataSources[i];
			ClusterEventSql clusterEventSql = clusterEventSqls[i];
			
			// Ensure we've got our data source
			Assert.assertNotNull(dataSource);
			
			// Ensure we've got our sql generator
			Assert.assertNotNull(clusterEventSql);
			
			// Ensure that we can borrow connections from the DS
			try {
				dataSource.getConnection();
			} catch (Exception e) {
				fail("Hsql DB Server is not providing connections: " + e);
			}
			
			// Build a JDBC Template
			JdbcTemplate jdbc = new JdbcTemplate(dataSource);

			// Perform an insert
			String insertSql = clusterEventSql.returnInsertSakaiEvent();
			Object[] oa = new Object[] { new Date(), "test_event", "test_ref", null, null };
			int types[] = new int[] {Types.DATE, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR};
			jdbc.update(insertSql, oa, types);
			
			// Find the last event
			Long lastEventId = jdbc.queryForLong(clusterEventSql.returnSelectMaxEventId());
			
			// If this value wasn't found, JdbcTemplate will return zero.  Ensure we didn't get a zero result.
			Assert.assertFalse(new Long(0).equals(lastEventId));
			
			// Ensure that selecting the last event returns the right event object
			String selectSql = clusterEventSql.returnSelectEvent();
			
			Map resultMap = jdbc.queryForMap(selectSql, new Object[] {lastEventId});

			// FIXME: We can't do this query without a valid session ID.  There is a hidden
			// cross-service dependency here.

			//	Assert.assertEquals("test_event", resultMap.get("EVENT"));
		}
	}
}
