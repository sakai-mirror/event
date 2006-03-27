/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005, 2006 The Sakai Foundation.
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

import java.util.List;

/**
 * <p>
 * Notification the interface for classes that act to notify, used with the GenericNotificationService.
 * </p>
 */
public interface Notification
{
	// TODO: extends entity? -ggolden
	/**
	 * Do the notification.
	 * 
	 * @param event
	 *        The event that matched criteria to cause the notification.
	 */
	void notify(Event event);

	/**
	 * Get the Event function. Only Events with this function code will trigger the notification.
	 * 
	 * @return The Event function to watch for.
	 */
	String getFunction();

	/**
	 * Get all the Event functions for this notification.
	 * 
	 * @return a List (String) of Event functions to watch for.
	 */
	List getFunctions();

	/**
	 * Check if the notification watches for events with this function code.
	 * 
	 * @param event
	 *        The Event function to test.
	 * @return true if this notification watches for evens with this function code, false if not.
	 */
	boolean containsFunction(String function);

	/**
	 * Get the resource reference filter. Only Events with references matching this will trigger the notification.
	 * 
	 * @return The resource reference filter.
	 */
	String getResourceFilter();

	/**
	 * Get the action helper that handles the notify() action.
	 * 
	 * @return The action helper that handles the notify() action.
	 */
	NotificationAction getAction();
}