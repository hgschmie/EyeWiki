/*
   JSPWiki - a JSP-based WikiWiki clone.

   Copyright (C) 2001-2003 Janne Jalkanen (Janne.Jalkanen@iki.fi)

   This program is free software; you can redistribute it and/or modify
   it under the terms of the GNU Lesser General Public License as published by
   the Free Software Foundation; either version 2.1 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General Public License
   along with this program; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
package com.ecyrd.jspwiki.auth;

import java.security.Principal;
import java.security.acl.NotOwnerException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;

import com.ecyrd.jspwiki.WikiEngine;
import com.ecyrd.jspwiki.WikiException;
import com.ecyrd.jspwiki.WikiPage;
import com.ecyrd.jspwiki.WikiProperties;
import com.ecyrd.jspwiki.acl.AccessControlList;
import com.ecyrd.jspwiki.acl.AclEntryImpl;
import com.ecyrd.jspwiki.acl.AclImpl;
import com.ecyrd.jspwiki.attachment.Attachment;
import com.ecyrd.jspwiki.auth.permissions.DeletePermission;
import com.ecyrd.jspwiki.auth.permissions.EditPermission;
import com.ecyrd.jspwiki.auth.permissions.ViewPermission;
import com.ecyrd.jspwiki.auth.permissions.WikiPermission;
import com.ecyrd.jspwiki.exception.InternalWikiException;
import com.ecyrd.jspwiki.exception.NoRequiredPropertyException;
import com.ecyrd.jspwiki.util.ClassUtil;


/**
 * Manages all access control and authorization.
 *
 * @see UserManager
 */
public class AuthorizationManager
        implements WikiProperties
{
    /** DOCUMENT ME! */
    static Logger log = Logger.getLogger(AuthorizationManager.class);

    /** DOCUMENT ME! */
    private WikiAuthorizer m_authorizer;

    /** DOCUMENT ME! */
    private AccessControlList m_defaultPermissions;

    /** DOCUMENT ME! */
    private boolean m_strictLogins = false;

    /** If true, allows the old auth system to be used. */
    private boolean m_useOldAuth = false;

    /** DOCUMENT ME! */
    private WikiEngine m_engine;

    /**
     * Creates a new AuthorizationManager, owned by engine and initialized according to the
     * settings in properties. Expects to find property 'jspwiki.authorizer' with a valid
     * WikiAuthorizer implementation name to take care of authorization.
     *
     * @param engine DOCUMENT ME!
     * @param conf DOCUMENT ME!
     *
     * @throws WikiException DOCUMENT ME!
     * @throws InternalWikiException DOCUMENT ME!
     */
    public AuthorizationManager(WikiEngine engine, Configuration conf)
            throws WikiException
    {
        m_engine = engine;

        m_useOldAuth = conf.getBoolean(PROP_AUTH_USEOLDAUTH, PROP_AUTH_USEOLDAUTH_DEFAULT);

        m_strictLogins = conf.getBoolean(PROP_AUTH_STRICTLOGINS, PROP_AUTH_STRICTLOGINS_DEFAULT);

        if (!m_useOldAuth)
        {
            return;
        }

        m_authorizer = getAuthorizerImplementation(conf);
        m_authorizer.initialize(engine, conf);

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
        ae.addPermission(new EditPermission());

        AclEntryImpl aeneg = new AclEntryImpl();
        aeneg.setPrincipal(allGroup);
        aeneg.setNegativePermissions();
        aeneg.addPermission(new DeletePermission());

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
     * Returns true, if strict logins are required.  Strict logins mean that all pages are
     * accessible only to users who have logged in.
     *
     * @return DOCUMENT ME!
     */
    public boolean strictLogins()
    {
        return m_strictLogins;
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
     * Attempts to locate and initialize a WikiAuthorizer to use with this manager. Throws a
     * WikiException if no entry is found, or if one fails to initialize.
     *
     * @param conf jspwiki.properties, containing a 'jpswiki.authorizer' class name
     *
     * @return a WikiAuthorizer used to get page authorization information
     *
     * @throws WikiException
     * @throws NoRequiredPropertyException DOCUMENT ME!
     */
    private WikiAuthorizer getAuthorizerImplementation(Configuration conf)
            throws WikiException
    {
        WikiAuthorizer impl = null;

        String authClassName = conf.getString(PROP_CLASS_AUTHORIZER, PROP_CLASS_AUTHORIZER_DEFAULT);

        if (authClassName != null)
        {
            try
            {
                // TODO: this should probably look in package ...modules
                Class authClass =
                    ClassUtil.findClass(DEFAULT_AUTH_MODULES_CLASS_PREFIX, authClassName);
                impl = (WikiAuthorizer) authClass.newInstance();

                return (impl);
            }
            catch (ClassNotFoundException e)
            {
                log.fatal("WikiAuthorizer " + authClassName + " cannot be found", e);
                throw new WikiException("WikiAuthorizer " + authClassName + " cannot be found");
            }
            catch (InstantiationException e)
            {
                log.fatal("Authorizer " + authClassName + " cannot be created", e);
                throw new WikiException("Authorizer " + authClassName + " cannot be created");
            }
            catch (IllegalAccessException e)
            {
                log.fatal("You are not allowed to access this authorizer class", e);
                throw new WikiException("You are not allowed to access this authorizer class");
            }
        }
        else
        {
            throw new NoRequiredPropertyException(
                "Unable to find a " + PROP_CLASS_AUTHORIZER + " entry in the properties.",
                PROP_CLASS_AUTHORIZER);
        }
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
     * Returns true or false, depending on whether this action is allowed.  This method returns
     * true for 2.2.
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

        AccessControlList acl = getAcl(page);

        //
        //  Does the page in question have an access control list?
        //
        if (acl != null)
        {
            if (log.isDebugEnabled())
            {
                log.debug("ACL for this page is: " + acl);
                log.debug("Checking for wup: " + wup);
                log.debug("Permission: " + permission);
            }

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
        }

        //
        //  If there was no result, then query from the default
        //  permission set of the authorizer.
        //
        if (res == AccessControlList.NONE)
        {
            if (log.isDebugEnabled())
            {
                log.debug(
                    "Page defines no permissions for " + wup.getName() + ", checking defaults.");
            }

            acl = m_authorizer.getDefaultPermissions();

            if (acl != null)
            {
                res = acl.findPermission(wup, permission);
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
}
