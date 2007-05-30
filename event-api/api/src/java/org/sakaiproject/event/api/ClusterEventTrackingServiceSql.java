/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/rsmart/dbrefactor/chat/chat-api/api/src/java/org/sakaiproject/chat/api/ChatServiceSql.java $
 * $Id: ChatServiceSql.java 3560 2007-02-19 22:08:01Z jbush@rsmart.com $
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005, 2006 The Sakai Foundation.
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
package org.sakaiproject.event.api;




/**
 * database methods.
 */
public interface ClusterEventTrackingServiceSql {

   /**
    * returns the sql statement which inserts an event into the sakai_event table.
    */
   public String getInsertEventSql();

   /**
    * returns the sql statement which retrieves an event from the sakai_event and sakai_session tables.
    */
   public String getEventSql();

   /**
    * returns the sql statement which retrieves the largest event id from the sakai_event table.
    */
   public String getMaxEventIdSql();
}
