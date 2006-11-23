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
//package org.sakaiproject.event.impl;
//
//import java.util.Observable;
//import java.util.Observer;
//
//import junit.extensions.TestSetup;
//import junit.framework.Assert;
//import junit.framework.Test;
//import junit.framework.TestSuite;
//
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.sakaiproject.event.api.Event;
//import org.sakaiproject.event.api.EventTrackingService;
//import org.sakaiproject.test.SakaiTestBase;
//
///**
// * @author dlhaines
// *
// */
//public class ClusterEventTrackingTest extends SakaiTestBase implements Observer {
//	private static final Log log = LogFactory.getLog(ClusterEventTrackingTest.class);
//
//	// Our service
//	private EventTrackingService eventService;
//
//	// This test case acts as an observer on the event service in order to test whether
//	// the service's listeners are called when an event is posted.
//	private boolean observerCalled;
//	public void update(Observable o, Object arg) {
//		 observerCalled = true;
//	}
//
//	public static Test suite() {
//		TestSetup setup = new TestSetup(new TestSuite(ClusterEventTrackingTest.class)) {
//			protected void setUp() throws Exception {
//				log.debug("starting setup");
//				oneTimeSetup();
//				log.debug("finished setup");
//			}
//			protected void tearDown() throws Exception {
//				oneTimeTearDown();
//			}
//		};
//		return setup;
//	}
//	
//	public void setUp() {
//		eventService = (EventTrackingService)getService(EventTrackingService.class.getName());
//		eventService.addObserver(this);
//		observerCalled = false;
//	}
//
//	public void tearDown() {
//		eventService.deleteObserver(this);
//	}
//
//	public void testPostAndListen() throws Exception {
//		Event event = eventService.newEvent("test_event", "test_resource", true);
//		eventService.post(event);
//		Assert.assertTrue(observerCalled);
//	}
//}
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

import java.util.Observable;
import java.util.Observer;

import junit.extensions.TestSetup;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.event.api.Event;
import org.sakaiproject.event.api.EventTrackingService;
import org.sakaiproject.test.SakaiTestBase;

public class ClusterEventTrackingTest extends SakaiTestBase implements Observer {
	private static final Log log = LogFactory.getLog(ClusterEventTrackingTest.class);

	// Our service
	private EventTrackingService eventService;

	// This test case acts as an observer on the event service in order to test whether
	// the service's listeners are called when an event is posted.
	private boolean observerCalled;
	public void update(Observable o, Object arg) {
		 observerCalled = true;
	}

	public static Test suite() {
		TestSetup setup = new TestSetup(new TestSuite(ClusterEventTrackingTest.class)) {
			protected void setUp() throws Exception {
				log.debug("starting setup");
				oneTimeSetup();
				log.debug("finished setup");
			}
			protected void tearDown() throws Exception {
				oneTimeTearDown();
			}
		};
		return setup;
	}
	
	public void setUp() {
		eventService = (EventTrackingService)getService(EventTrackingService.class.getName());
		eventService.addObserver(this);
		observerCalled = false;
	}

	public void tearDown() {
		eventService.deleteObserver(this);
	}

	public void testPostAndListen() throws Exception {
		Event event = eventService.newEvent("test_event", "test_resource", true);
		eventService.post(event);
		Assert.assertTrue(observerCalled);
	}
}
