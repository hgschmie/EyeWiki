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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;

import com.ecyrd.jspwiki.TranslatorReader;
import com.ecyrd.jspwiki.WikiConstants;
import com.ecyrd.jspwiki.WikiEngine;
import com.ecyrd.jspwiki.WikiException;
import com.ecyrd.jspwiki.WikiProperties;
import com.ecyrd.jspwiki.exception.NoRequiredPropertyException;
import com.ecyrd.jspwiki.util.HttpUtil;


/**
 * Manages user accounts, logins/logouts, passwords, etc.
 *
 * @author Janne Jalkanen
 * @author Erik Bunn
 */
public class UserManager
        implements WikiProperties
{
    /** DOCUMENT ME! */
    private static final Logger log = Logger.getLogger(UserManager.class);

    /** The name the UserProfile is stored in a Session by. */
    public static final String WIKIUSER = "currentUser";

    // FIXME: These should probably be localized.
    // FIXME: All is used as a catch-all.

    /** DOCUMENT ME! */
    public static final String GROUP_GUEST = "Guest";

    /** DOCUMENT ME! */
    public static final String GROUP_NAMEDGUEST = "NamedGuest";

    /** DOCUMENT ME! */
    public static final String GROUP_KNOWNPERSON = "KnownPerson";

    /** If true, logs the IP address of the editor */
    private boolean m_storeIPAddress = PROP_AUTH_STOREIPADDRESS_DEFAULT;

    /** DOCUMENT ME! */
    private HashMap m_groups = new HashMap();

    /** DOCUMENT ME! */
    private final WikiAuthenticator m_authenticator;

    /** DOCUMENT ME! */
    private final UserDatabase m_database;

    /** DOCUMENT ME! */
    private String m_administrator;

    /** DOCUMENT ME! */
    private boolean m_useOldAuth = false;

    /**
     * Creates an UserManager instance for the given WikiEngine and the specified set of
     * properties.  All initialization for the modules is done here.
     *
     * @param engine DOCUMENT ME!
     * @param conf DOCUMENT ME!
     *
     * @throws WikiException DOCUMENT ME!
     */
    public UserManager(final Configuration conf,
            final AuthorizationManager authorizationManager,
            final WikiAuthenticator authenticator,
            final UserDatabase userDatabase)
            throws WikiException
    {
        m_authenticator = authenticator;
        m_database = userDatabase;

        m_storeIPAddress =
            conf.getBoolean(PROP_AUTH_STOREIPADDRESS, PROP_AUTH_STOREIPADDRESS_DEFAULT);
        m_administrator = conf.getString(PROP_AUTH_ADMINISTRATOR, PROP_AUTH_ADMINISTRATOR_DEFAULT);
        m_useOldAuth = authorizationManager.isOldAuth();

        if (!m_useOldAuth)
        {
            return;
        }

        WikiGroup all = new AllGroup();
        all.setName("All");
        m_groups.put(GROUP_GUEST, new AllGroup());

        // m_groups.put( "All",             all );
        m_groups.put(GROUP_NAMEDGUEST, new NamedGroup());
        m_groups.put(GROUP_KNOWNPERSON, new KnownGroup());

        if (m_authenticator == null)
        {
            throw new NoRequiredPropertyException("Unable to find an Authenticator entry in the component configuration!", WikiConstants.AUTHENTICATOR);
        }

        if (m_database == null)
        {
            throw new NoRequiredPropertyException("Unable to find an Database entry in the component configuration!", WikiConstants.USER_DATABASE);
        }

        if (log.isInfoEnabled())
        {
            log.info("Initialized " + m_authenticator.getClass().getName() + " for authentication.");
            log.info("Initialized " + m_database.getClass().getName() + " as user database.");
        }
    }

    public synchronized void stop()
    {
        // GNDN
    }

    /**
     * Convenience shortcut to UserDatabase.getUserProfile().
     *
     * @param name DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public UserProfile getUserProfile(String name)
    {
        if (m_database == null)
        {
            // No user database, so return a dummy profile
            UserProfile wup = new UserProfile();
            wup.setName(name);
            wup.setLoginName(name);
            wup.setLoginStatus(UserProfile.COOKIE);

            return wup;
        }

        WikiPrincipal up = m_database.getPrincipal(name);

        if (!(up instanceof UserProfile))
        {
            if (log.isInfoEnabled())
            {
                log.info(name + " is not a user!");
            }

            up = null;
        }

        return ((UserProfile) up);
    }

    /**
     * Returns the UserDatabase employed by this UserManager.
     *
     * @return DOCUMENT ME!
     */
    public UserDatabase getUserDatabase()
    {
        return (m_database);
    }

    /**
     * Returns the WikiAuthenticator object employed by this UserManager.
     *
     * @return DOCUMENT ME!
     */
    public WikiAuthenticator getAuthenticator()
    {
        return (m_authenticator);
    }

    /**
     * Returns true, if the user or the group represents a super user, which should be allowed
     * access to everything.
     *
     * @param p Principal to check for administrator access.
     *
     * @return true, if the principal is an administrator.
     */
    public boolean isAdministrator(WikiPrincipal p)
    {
        //
        //  Direct name matches are returned always.
        //
        if (p.getName().equals(m_administrator))
        {
            return true;
        }

        //
        //  Try to get the super group and check if the user is a part
        //  of it.
        //
        WikiGroup superPrincipal = getWikiGroup(m_administrator);

        if (superPrincipal == null)
        {
            // log.warn("No supergroup '"+m_administrator+"' exists; you should create one.");
            return false;
        }

        return superPrincipal.isMember(p);
    }

    /**
     * Returns a WikiGroup instance for a given name.  WikiGroups are cached, so there is basically
     * a singleton across the Wiki for a group. The reason why this class caches them instead of
     * the WikiGroup class itself is that it is the business of the User Manager to handle such
     * issues.
     *
     * @param name Name of the group.  This is case-sensitive.
     *
     * @return A WikiGroup instance.
     */

    // FIXME: Someone should really check when groups cease to be used,
    //        and release groups that are not being used.
    // FIXME: Error handling is still deficient.
    public WikiGroup getWikiGroup(String name)
    {
        WikiGroup group;

        synchronized (m_groups)
        {
            group = (WikiGroup) m_groups.get(name);

            if (group == null)
            {
                WikiPrincipal p = m_database.getPrincipal(name);

                if (!(p instanceof WikiGroup))
                {
                    if (log.isInfoEnabled())
                    {
                        log.info(name + " is not a group!");
                    }
                }
                else
                {
                    group = (WikiGroup) p;
                }
            }
        }

        return group;
    }

    /**
     * Returns a list of all WikiGroups this Principal is a member of.
     *
     * @param user DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws NoSuchPrincipalException DOCUMENT ME!
     */

    // FIXME: This is not a very good solution; UserProfile
    //        should really cache the information.
    // FIXME: Should really query the page manager.
    public List getGroupsForPrincipal(Principal user)
            throws NoSuchPrincipalException
    {
        List list = null;

        //
        // Add the groups ONLY if the user has been authenticated.
        //
        // FIXME: This is probably the wrong place, since this prevents
        // us from querying stuff later on.
        if (user instanceof UserProfile && ((UserProfile) user).isAuthenticated())
        {
            if (m_database != null)
            {
                list = m_database.getGroupsForPrincipal(user);
            }
        }

        if (list == null)
        {
            list = new ArrayList();
        }

        //
        //  Add the default groups.
        //
        synchronized (m_groups)
        {
            for (Iterator i = m_groups.values().iterator(); i.hasNext();)
            {
                WikiGroup g = (WikiGroup) i.next();

                if (g.isMember(user))
                {
                    if (log.isDebugEnabled())
                    {
                        log.debug("User " + user.getName() + " is a member of " + g.getName());
                    }

                    list.add(g);
                }
            }
        }

        return list;
    }

    /**
     * Attempts to find a Principal from the list of known principals.
     *
     * @param name DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Principal getPrincipal(String name)
    {
        Principal p = getWikiGroup(name);

        if (p == null)
        {
            p = getUserProfile(name);

            if (p == null)
            {
                if (log.isDebugEnabled())
                {
                    log.debug("No such principal defined: " + name + ", using UndefinedPrincipal");
                }

                p = new UndefinedPrincipal(name);
            }
        }

        return p;
    }

    /**
     * Attempts to perform a login for the given username/password combination.  Also sets the
     * attribute UserManager.WIKIUSER in the current session, which can then be used to fetch the
     * current UserProfile.  Or you can be lazy and just call getUserProfile()...
     *
     * @param username The user name.  This is an user name, not a WikiName.  In most cases they
     *        are the same, but in some cases, they might not be.
     * @param password The password.
     * @param session DOCUMENT ME!
     *
     * @return true, if the username/password is valid.
     *
     * @throws WikiSecurityException if password has expired
     * @throws PasswordExpiredException DOCUMENT ME!
     */
    public boolean login(String username, String password, HttpSession session)
            throws WikiSecurityException
    {
        if (m_authenticator == null)
        {
            return false;
        }

        if (session == null)
        {
            log.error("No session provided, cannot log in.");

            return false;
        }

        UserProfile wup = getUserProfile(username);

        if (wup != null)
        {
            wup.setPassword(password);

            boolean isValid = false;
            boolean expired = false;

            try
            {
                isValid = m_authenticator.authenticate(wup);
            }
            catch (PasswordExpiredException e)
            {
                isValid = true;
                expired = true;
            }

            if (isValid)
            {
                wup.setLoginStatus(UserProfile.PASSWORD);
                session.setAttribute(WIKIUSER, wup);

                if (log.isInfoEnabled())
                {
                    log.info("Logged in user " + username);
                }

                if (expired)
                {
                    throw new PasswordExpiredException(""); //FIXME!
                }
            }
            else
            {
                if (log.isInfoEnabled())
                {
                    log.info(
                        "Username " + username + " attempted to log in with the wrong password.");
                }
            }

            return isValid;
        }

        return false;
    }

    /**
     * Logs a web user out, clearing the session.
     *
     * @param session The current HTTP session for this user.
     */
    public void logout(HttpSession session)
    {
        if (session != null)
        {
            UserProfile wup = (UserProfile) session.getAttribute(WIKIUSER);

            if (wup != null)
            {
                if (log.isInfoEnabled())
                {
                    log.info("logged out user " + wup.getName());
                }

                wup.setLoginStatus(UserProfile.NONE);
            }

            session.invalidate();
        }
    }

    /**
     * Gets a UserProfile, either from the request (presumably authenticated and with auth
     * information) or a new one (with default permissions).
     *
     * @param request The servlet request for this user.
     *
     * @return A valid UserProfile.  Can also return null in case it is not possible to get an
     *         UserProfile.
     *
     * @since 2.1.10.
     */
    public UserProfile getUserProfile(HttpServletRequest request)
    {
        // First, see if we already have a user profile.
        HttpSession session = request.getSession(true);
        UserProfile wup = (UserProfile) session.getAttribute(UserManager.WIKIUSER);

        if (wup != null)
        {
            return wup;
        }

        // Try to get a limited login. This will be inserted into the request.
        wup = limitedLogin(request);

        if (wup != null)
        {
            return wup;
        }

        log.error("Unable to get a default UserProfile!");

        return null;
    }

    /**
     * Performs a "limited" login: sniffs for a user name from a cookie or the client, and creates
     * a limited user profile based on it.
     *
     * @param request DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected UserProfile limitedLogin(HttpServletRequest request)
    {
        UserProfile wup = null;

        //
        //  First, checks whether container has done authentication for us.
        //
        String uid = request.getRemoteUser();

        if (uid != null)
        {
            wup = getUserProfile(uid);

            if (wup != null)
            {
                wup.setLoginStatus(UserProfile.CONTAINER);

                HttpSession session = request.getSession(true);
                session.setAttribute(WIKIUSER, wup);
            }
        }
        else
        {
            //
            //  See if a cookie exists, and create a default account.
            //
            uid = HttpUtil.retrieveCookieValue(request, WikiEngine.PREFS_COOKIE_NAME);

            if (uid != null)
            {
                if (log.isDebugEnabled())
                {
                    log.debug("Retrieved User from Cookie: " + uid);
                }

                try
                {
                    wup = UserProfile.parseStringRepresentation(uid);

                    if (wup != null)
                    {
                        wup.setLoginStatus(UserProfile.COOKIE);
                    }
                }
                catch (NoSuchElementException e)
                {
                    log.debug("No cookie found");
                }
            }
        }

        // If the UserDatabase declined to give us a UserPrincipal,
        // we manufacture one here explicitly.
        if (wup == null)
        {
            wup = new UserProfile();
            wup.setLoginName(GROUP_GUEST);
            wup.setLoginStatus(UserProfile.NONE);

            //
            //  No username either, so fall back to the IP address.
            //
            if (m_storeIPAddress)
            {
                wup.setName(request.getRemoteHost());
            }
            else
            {
                wup.setName(wup.getLoginName());
            }
        }

        //
        //  FIXME:
        //
        //  We cannot store the UserProfile into the session, because of the following:
        //  Assume that Edit.jsp is protected through container auth.
        //
        //  User without a cookie arrives through Wiki.jsp.  A
        //  UserProfile is created, which essentially contains his IP
        //  address.  If this is stored in the session, then, when the user
        //  tries to access the Edit.jsp page and container does auth, he will
        //  always be then known by his IP address, regardless of what the
        //  request.getRemoteUser() says.
        //  So, until this is solved, we create a new UserProfile on each
        //  access.  Ouch.
        // Limited login hasn't been authenticated. Just to emphasize the point:
        // wup.setPassword( null );
        // HttpSession session = request.getSession( true );
        // session.setAttribute( WIKIUSER, wup );
        return wup;
    }

    /**
     * Sets the username cookie.
     *
     * @param response DOCUMENT ME!
     * @param name DOCUMENT ME!
     *
     * @since 2.1.47.
     */
    public void setUserCookie(HttpServletResponse response, String name)
    {
        UserProfile profile = getUserProfile(TranslatorReader.cleanLink(name));

        // Set cookie only if we actually have a user database configured
        if (profile != null)
        {
            Cookie prefs =
                new Cookie(WikiEngine.PREFS_COOKIE_NAME, profile.getStringRepresentation());

            prefs.setMaxAge(1001 * 24 * 60 * 60); // 1001 days is default.

            response.addCookie(prefs);
        }
    }
}
