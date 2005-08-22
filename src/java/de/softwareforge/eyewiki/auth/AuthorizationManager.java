package de.softwareforge.eyewiki.auth;

/*
 * ========================================================================
 *
 * eyeWiki - a WikiWiki clone written in Java
 *
 * ========================================================================
 *
 * Copyright (C) 2005 Henning Schmiedehausen <henning@software-forge.com>
 *
 * based on
 *
 * JSPWiki - a JSP-based WikiWiki clone.
 * Copyright (C) 2002-2005 Janne Jalkanen (Janne.Jalkanen@iki.fi)
 *
 * ========================================================================
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * ========================================================================
 */

import java.security.Principal;
import java.security.acl.NotOwnerException;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;

import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.WikiException;
import de.softwareforge.eyewiki.WikiPage;
import de.softwareforge.eyewiki.WikiProperties;
import de.softwareforge.eyewiki.acl.AccessControlList;
import de.softwareforge.eyewiki.acl.AclEntryImpl;
import de.softwareforge.eyewiki.acl.AclImpl;
import de.softwareforge.eyewiki.attachment.Attachment;
import de.softwareforge.eyewiki.auth.permissions.DeletePermission;
import de.softwareforge.eyewiki.auth.permissions.EditPermission;
import de.softwareforge.eyewiki.auth.permissions.ViewPermission;
import de.softwareforge.eyewiki.auth.permissions.WikiPermission;
import de.softwareforge.eyewiki.exception.InternalWikiException;

/**
 * Manages all access control and authorization.
 *
 * @see UserManager
 */
public class AuthorizationManager
        implements WikiProperties
{
    /** DOCUMENT ME! */
    private static final Logger log = Logger.getLogger(AuthorizationManager.class);

    /** DOCUMENT ME! */
    private final WikiAuthorizer m_authorizer;

    /** DOCUMENT ME! */
    private AccessControlList m_defaultPermissions;

    /** DOCUMENT ME! */
    private boolean m_strictLogins = false;

    /** If true, allows the old auth system to be used. */
    private boolean m_useOldAuth = false;

    /** DOCUMENT ME! */
    private final WikiEngine m_engine;

    /**
     * Creates a new AuthorizationManager, owned by engine and initialized according to the settings in properties. Expects to find
     * property 'eyewiki.authorizer' with a valid WikiAuthorizer implementation name to take care of authorization.
     *
     * @param engine DOCUMENT ME!
     * @param conf DOCUMENT ME!
     * @param authorizer DOCUMENT ME!
     *
     * @throws WikiException DOCUMENT ME!
     * @throws InternalWikiException DOCUMENT ME!
     */
    public AuthorizationManager(final WikiEngine engine, final Configuration conf, final WikiAuthorizer authorizer)
            throws WikiException
    {
        m_engine = engine;
        m_authorizer = authorizer;

        m_useOldAuth = conf.getBoolean(PROP_AUTH_USEOLDAUTH, PROP_AUTH_USEOLDAUTH_DEFAULT);
        m_strictLogins = conf.getBoolean(PROP_AUTH_STRICTLOGINS, PROP_AUTH_STRICTLOGINS_DEFAULT);

        if (!m_useOldAuth)
        {
            return;
        }

        AclEntryImpl ae = new AclEntryImpl();

        //
        //  Default set of permissions for everyone:
        //  ALLOW: View, Edit
        //  DENY:  Delete
        //
        WikiGroup allGroup = new AllGroup();
        allGroup.setName("All");
        ae.setPrincipal(allGroup);
        ae.addPermission(new ViewPermission());

        AclEntryImpl aeneg = new AclEntryImpl();
        aeneg.setPrincipal(allGroup);
        aeneg.setNegativePermissions();
        aeneg.addPermission(new DeletePermission());
        aeneg.addPermission(new EditPermission());

        try
        {
            m_defaultPermissions = new AclImpl();
            m_defaultPermissions.addEntry(null, ae);
            m_defaultPermissions.addEntry(null, aeneg);
        }
        catch (NotOwnerException e)
        {
            throw new InternalWikiException("Nobody told me that owners were in use");
        }
    }

    /**
     * Attempts to find the ACL of a page. If the page has a parent page, then that is tried also.
     *
     * @param page DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private AccessControlList getAcl(WikiPage page)
    {
        //
        //  Does the page already have cached ACLs?
        //
        AccessControlList acl = page.getAcl();

        if (acl == null)
        {
            //
            //  Nope, check if we can get them from the authorizer
            //
            acl = m_authorizer.getPermissions(page);

            //
            //  If still no go, try the parent.
            //
            if ((acl == null) && page instanceof Attachment)
            {
                WikiPage parent = m_engine.getPage(((Attachment) page).getParentName());

                acl = getAcl(parent);
            }
        }

        return acl;
    }

    /**
     * Returns true or false, depending on whether this action is allowed for this WikiPage.
     *
     * @param page DOCUMENT ME!
     * @param wup DOCUMENT ME!
     * @param permission Any of the available permissions "view", "edit, "comment", etc.
     *
     * @return DOCUMENT ME!
     */
    public boolean checkPermission(WikiPage page, UserProfile wup, String permission)
    {
        return checkPermission(page, wup, WikiPermission.newInstance(permission));
    }

    /**
     * Returns true or false, depending on whether this action is allowed.  This method returns true for 2.2.
     *
     * @param page DOCUMENT ME!
     * @param wup DOCUMENT ME!
     * @param permission DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws InternalWikiException DOCUMENT ME!
     */
    public boolean checkPermission(WikiPage page, UserProfile wup, WikiPermission permission)
    {
        int res = AccessControlList.NONE;
        UserManager userManager = m_engine.getUserManager();

        //
        //  A slight sanity check.
        //
        if (wup == null)
        {
            return false;
        }

        //
        //  If auth is turned off, return immediately for speed
        //
        if (!m_useOldAuth)
        {
            return true;
        }

        //
        //  Yup, superusers can do anything.
        //
        if (wup.isAuthenticated() && userManager.isAdministrator(wup))
        {
            return true;
        }

        if (log.isDebugEnabled())
        {
            log.debug("Checking for wup: " + wup);
            log.debug("Permission: " + permission);
        }

        AccessControlList acl = getAcl(page);

        //
        //  Does the page in question have an access control list?
        //
        if (acl != null)
        {
            if (log.isDebugEnabled())
            {
                log.debug("ACL for this page is: " + acl);
            }

            res = checkAuthentication(wup, acl, permission);
        }

        //
        //  If there was no result, then query from the default
        //  permission set of the authorizer.
        //
        if (res == AccessControlList.NONE)
        {
            acl = m_authorizer.getDefaultPermissions();

            if (acl != null)
            {
                if (log.isDebugEnabled())
                {
                    log.debug("Page defines no permissions for " + wup.getName() + ", checking defaults.");
                    log.debug("Default ACL is: " + acl);
                }

                res = checkAuthentication(wup, acl, permission);
            }
        }

        //
        //  If there still is nothing, then query from the Wiki default
        //  set of permissions.
        //
        if (res == AccessControlList.NONE)
        {
            if (log.isDebugEnabled())
            {
                log.debug("No defaults exist, falling back to hardcoded permissions.");
            }

            // Hard coded default: Only View is allowed
            res = m_defaultPermissions.findPermission(wup, permission);
        }

        if (log.isDebugEnabled())
        {
            log.debug("Permission " + permission + " for user " + wup + " is " + res);
        }

        if (res == AccessControlList.NONE)
        {
            throw new InternalWikiException("No default policy has been defined!");
        }

        return res == AccessControlList.ALLOW;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isOldAuth()
    {
        return m_useOldAuth;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isStrictLogins()
    {
        return m_strictLogins;
    }

    private int checkAuthentication(final UserProfile wup, final AccessControlList acl, final WikiPermission permission)
    {
        int res = AccessControlList.NONE;

        if (wup.isAuthenticated())
        {
            res = acl.findPermission(wup, permission);
        }

        //
        //  If there as no entry for the user, then try all of his groups
        //
        if (res == AccessControlList.NONE)
        {
            log.debug("Checking groups...");

            try
            {
                UserManager userManager = m_engine.getUserManager();
                List list = userManager.getGroupsForPrincipal(wup);

                for (Iterator i = list.iterator(); i.hasNext();)
                {
                    res = acl.findPermission((Principal) i.next(), permission);

                    if (res != AccessControlList.NONE)
                    {
                        break;
                    }
                }
            }
            catch (NoSuchPrincipalException e)
            {
                log.warn("Internal trouble: No principal defined for requested user.", e);
            }
        }

        return res;
    }
}
