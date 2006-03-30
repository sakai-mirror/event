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

package org.sakaiproject.util;

import java.util.List;
import java.util.Vector;

import org.sakaiproject.alias.api.Alias;
import org.sakaiproject.alias.cover.AliasService;
import org.sakaiproject.authz.cover.SecurityService;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.cover.EntityManager;
import org.sakaiproject.event.api.Event;
import org.sakaiproject.event.api.NotificationAction;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.cover.SiteService;

/**
 * <p>
 * SiteEmailNotification is an EmailNotification that selects the site's participants (based on site access) as the recipients of the notification.
 * </p>
 * <p>
 * getRecipients() is satisified here, but you can refine it by implementing getResourceAbility()
 * </p>
 * Although these are not abstract, the following still need be specified to extend the class:
 * <ul>
 * <li>getMessage()</li>
 * <li>getHeaders()</li>
 * <li>getTag()</li>
 * <li>isBodyHTML()</li>
 * <li>headerToRecipient</li>
 * </ul>
 * </p>
 * <p>
 * getClone() should also be extended to clone the proper type of object.
 * </p>
 * 
 * @author Sakai Software Development Team
 */
public class SiteEmailNotification extends EmailNotification
{
	/**
	 * Construct.
	 */
	public SiteEmailNotification()
	{
	}

	/**
	 * Construct.
	 * 
	 * @param siteId
	 *        The id of the site whose users will get a mailing.
	 */
	public SiteEmailNotification(String siteId)
	{
		super(siteId);
	}

	/**
	 * @inheritDoc
	 */
	public NotificationAction getClone()
	{
		SiteEmailNotification clone = new SiteEmailNotification();
		clone.set(this);

		return clone;
	}

	/**
	 * @inheritDoc
	 */
	protected List getRecipients(Event event)
	{
		// get the resource reference
		Reference ref = EntityManager.newReference(event.getResource());

		// use either the configured site, or if not configured, the site (context) of the resource
		String siteId = (getSite() != null) ? getSite() : ref.getContext();

		// if the site is published, use the list of users who can SITE_VISIT the site,
		// else use the list of users who can SITE_VISIT_UNP the site.
		try
		{
			Site site = SiteService.getSite(siteId);
			String ability = SiteService.SITE_VISIT;
			if (!site.isPublished())
			{
				ability = SiteService.SITE_VISIT_UNPUBLISHED;
			}

			// get the list of users
			List users = SecurityService.unlockUsers(ability, SiteService.siteReference(siteId));

			// get the list of users who have the appropriate access to the resource
			if (getResourceAbility() != null)
			{
				List users2 = SecurityService.unlockUsers(getResourceAbility(), ref.getReference());

				// find intersection of users and user2
				users.retainAll(users2);
			}

			return users;
		}
		catch (Exception any)
		{
			return new Vector();
		}
	}

	/**
	 * Format a To: header value for the site.
	 * 
	 * @param event
	 *        The event that matched criteria to cause the notification.
	 * @return the to: header for the email.
	 */
	protected String getSiteTo(Event event)
	{
		// get the resource reference
		Reference ref = EntityManager.newReference(event.getResource());

		// use either the configured site, or if not configured, the site (context) of the resource
		String siteId = (getSite() != null) ? getSite() : ref.getContext();

		// make the to: field look like the email address of the site

		// select the site's main mail channel alias, or the site's alias, or the site id
		String siteMailId = siteId;

		// first check aliases for the site's main email channel
		// TODO: stolen from MailArchiveService to avoid dependency ( MailArchiveService.channelReference(siteId, SiteService.MAIN_CONTAINER) ) -ggolden
		String channelRef = "/channel/" + siteId + "/" + "main";
		List aliases = AliasService.getAliases(channelRef, 1, 1);
		if (aliases.isEmpty())
		{
			// next try aliases for the site
			String siteRef = SiteService.siteReference(siteId);
			aliases = AliasService.getAliases(siteRef, 1, 1);
		}

		// if there was an alias
		if (!aliases.isEmpty())
		{
			siteMailId = ((Alias) aliases.get(0)).getId();
		}

		return siteMailId + " <" + siteMailId + "@" + ServerConfigurationService.getServerName() + ">";
	}

	/**
	 * Get the additional security function string needed for the resource that is the target of the notification <br />
	 * users who get notified need to have this ability with this resource, too.
	 * 
	 * @return The additional ability string needed for a user to receive notification.
	 */
	protected String getResourceAbility()
	{
		return null;
	}
}
