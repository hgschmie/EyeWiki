package de.softwareforge.eyewiki.auth.modules;

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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import org.picocontainer.Startable;


import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.WikiPage;
import de.softwareforge.eyewiki.auth.NoSuchPrincipalException;
import de.softwareforge.eyewiki.auth.UndefinedPrincipal;
import de.softwareforge.eyewiki.auth.UserDatabase;
import de.softwareforge.eyewiki.auth.UserProfile;
import de.softwareforge.eyewiki.auth.WikiGroup;
import de.softwareforge.eyewiki.auth.WikiPrincipal;
import de.softwareforge.eyewiki.filters.BasicPageFilter;
import de.softwareforge.eyewiki.filters.FilterManager;
import de.softwareforge.eyewiki.filters.PageFilter;
import de.softwareforge.eyewiki.providers.ProviderException;


/**
 * This default UserDatabase implementation provides user profiles and groups to eyeWiki.
 *
 * <p>
 * UserProfiles are simply created upon request, and cached locally. More intricate providers might
 * look up profiles in a remote DB, provide an unauthenticatable object for unknown users, etc.
 * </p>
 *
 * <p>
 * The authentication of a user is done elsewhere (see WikiAuthenticator); newly created profiles
 * should have login status UserProfile.NONE.
 * </p>
 *
 * <p>
 * Groups are  based on WikiPages. The name of the page determines the group name (as a convention,
 * we suggest the name of the page ends in Group, e.g. EditorGroup). By setting attribute
 * 'members' on the page, the named members are added to the group:
 * <pre>
 * [{SET members fee fie foe foo}]
 * </pre>
 * </p>
 *
 * <p>
 * The list of members can be separated by commas or spaces.
 * </p>
 *
 * <p>
 * TODO: are 'named members' supposed to be usernames, or are group names allowed? (Suggestion:
 * both)
 * </p>
 */
public class WikiDatabase
        implements UserDatabase, Startable
{
    /** DOCUMENT ME! */
    private static final Logger log = Logger.getLogger(WikiDatabase.class);

    /**
     * The attribute to set on a page - [{SET members ...}] - to define members of the group named
     * by that page.
     */
    public static final String ATTR_MEMBERLIST = "members";

    /** DOCUMENT ME! */
    private WikiEngine m_engine;

    /** DOCUMENT ME! */
    private HashMap m_groupPrincipals = new HashMap();

    /** DOCUMENT ME! */
    private HashMap m_userPrincipals = new HashMap();

    /**
     * DOCUMENT ME!
     *
     * @param engine DOCUMENT ME!
     * @param conf DOCUMENT ME!
     */
    public WikiDatabase(WikiEngine engine, FilterManager filterManager)
    {
        m_engine = engine;
        filterManager.addPageFilter(new SaveFilter());
    }

    public synchronized void start()
    {
        initUserDatabase();
    }

    public synchronized void stop()
    {
        // GNDN
    }

    // This class must contain a large cache for user databases.
    // FIXME: Needs to cache this somehow; this is far too slow!
    public List getGroupsForPrincipal(Principal p)
            throws NoSuchPrincipalException
    {
        List memberList = new ArrayList();

        if (log.isDebugEnabled())
        {
            log.debug("Finding groups for " + p.getName());
        }

        for (Iterator i = m_groupPrincipals.values().iterator(); i.hasNext();)
        {
            Object o = i.next();

            if (o instanceof WikiGroup)
            {
                if (log.isDebugEnabled())
                {
                    log.debug("  Checking group: " + o);
                }

                if (((WikiGroup) o).isMember(p))
                {
                    if (log.isDebugEnabled())
                    {
                        log.debug("     Is member");
                    }

                    memberList.add(o);
                }
            }
            else
            {
                if (log.isDebugEnabled())
                {
                    log.debug("  Found strange object: " + o.getClass());
                }
            }
        }

        return memberList;
    }

    /**
     * List contains a bunch of Strings to denote members of this group.
     *
     * @param groupName DOCUMENT ME!
     * @param memberList DOCUMENT ME!
     */
    protected void updateGroup(String groupName, List memberList)
    {
        WikiGroup group = (WikiGroup) m_groupPrincipals.get(groupName);

        if ((group == null) && (memberList == null))
        {
            return;
        }

        if ((group == null) && (memberList != null))
        {
            if (log.isDebugEnabled())
            {
                log.debug("Adding new group: " + groupName);
            }

            group = new WikiGroup();
            group.setName(groupName);
        }

        if ((group != null) && (memberList == null))
        {
            if (log.isDebugEnabled())
            {
                log.debug("Detected removed group: " + groupName);
            }

            m_groupPrincipals.remove(groupName);

            return;
        }

        for (Iterator j = memberList.iterator(); j.hasNext();)
        {
            Principal udp = new UndefinedPrincipal((String) j.next());

            group.addMember(udp);

            if (log.isDebugEnabled())
            {
                log.debug("** Added member: " + udp.getName());
            }
        }

        m_groupPrincipals.put(groupName, group);
    }

    /**
     * DOCUMENT ME!
     */
    protected void initUserDatabase()
    {
        log.info("Initializing user database group information from wiki pages...");

        try
        {
            Collection allPages = m_engine.getPageManager().getAllPages();

            m_groupPrincipals.clear();

            for (Iterator i = allPages.iterator(); i.hasNext();)
            {
                WikiPage p = (WikiPage) i.next();

                // lazy loading of pages with PageAuthorizer not possible,
                // because the authentication information must be
                // present on wiki initialization
                List memberList = parseMemberList((String) p.getAttribute(ATTR_MEMBERLIST));

                if (memberList != null)
                {
                    updateGroup(p.getName(), memberList);
                }
            }
        }
        catch (ProviderException e)
        {
            log.fatal("Cannot start database", e);
        }
    }

    /**
     * Stores a UserProfile with expiry information.
     *
     * @param name DOCUMENT ME!
     * @param p DOCUMENT ME!
     */
    private void storeUserProfile(String name, UserProfile p)
    {
        m_userPrincipals.put(name, new TimeStampWrapper(p, 24 * 3600 * 1000));
    }

    /**
     * Returns a stored UserProfile, taking expiry into account.
     *
     * @param name DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private UserProfile getUserProfile(String name)
    {
        TimeStampWrapper w = (TimeStampWrapper) m_userPrincipals.get(name);

        if ((w != null) && (w.expires() < System.currentTimeMillis()))
        {
            w = null;
            m_userPrincipals.remove(name);
        }

        if (w != null)
        {
            return ((UserProfile) w.getContent());
        }

        return (null);
    }

    /**
     * Returns a principal; UserPrincipal storage is scanned first, then WikiGroup storage. If
     * neither contains the requested principal, a new (empty) UserPrincipal is returned.
     *
     * @param name DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public WikiPrincipal getPrincipal(String name)
    {
        // FIX: requests for non-existent users can now override groups.
        WikiPrincipal rval = (WikiPrincipal) getUserProfile(name);

        if (rval == null)
        {
            rval = (WikiPrincipal) m_groupPrincipals.get(name);
        }

        if (rval == null)
        {
            rval = new UserProfile();
            rval.setName(name);

            // Store, to reduce creation overhead. Expire in one day.
            storeUserProfile(name, (UserProfile) rval);
        }

        return (rval);
    }

    /**
     * Parses through the member list of a page.
     *
     * @param memberLine DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private List parseMemberList(String memberLine)
    {
        if (memberLine == null)
        {
            return null;
        }

        if (log.isDebugEnabled())
        {
            log.debug("Parsing member list: " + memberLine);
        }

        StringTokenizer tok = new StringTokenizer(memberLine, ", ");

        ArrayList members = new ArrayList();

        while (tok.hasMoreTokens())
        {
            String uid = tok.nextToken();

            if (log.isDebugEnabled())
            {
                log.debug("  Adding member: " + uid);
            }

            members.add(uid);
        }

        return members;
    }

    /**
     * This special filter class is used to refresh the database after a page has been changed.
     */

    // FIXME: eyeWiki should really take care of itself that any metadata
    //        relevant to a page is refreshed.
    private class SaveFilter
            extends BasicPageFilter
            implements PageFilter
    {
        private SaveFilter()
        {
            super(null);
        }
        
        public int getPriority()
        {
            return PageFilter.MAX_PRIORITY;
        }
        
        public boolean isVisible()
        {
            return false;
        }
        
        /**
         * DOCUMENT ME!
         *
         * @param context DOCUMENT ME!
         * @param content DOCUMENT ME!
         */
        public void postSave(WikiContext context, String content)
        {
            WikiPage p = context.getPage();

            if (log.isDebugEnabled())
            {
                log.debug(
                    "Skimming through page " + p.getName() + " to see if there are new users...");
            }

            m_engine.textToHTML(context, content);

            String members = (String) p.getAttribute(ATTR_MEMBERLIST);

            updateGroup(p.getName(), parseMemberList(members));
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Revision$
     */
    public static class TimeStampWrapper
    {
        /** DOCUMENT ME! */
        private Object contained = null;

        /** DOCUMENT ME! */
        private long expirationTime = -1;

        /**
         * Creates a new TimeStampWrapper object.
         *
         * @param item DOCUMENT ME!
         * @param expiresIn DOCUMENT ME!
         */
        public TimeStampWrapper(Object item, long expiresIn)
        {
            contained = item;
            expirationTime = System.currentTimeMillis() + expiresIn;
        }

        /**
         * DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        public Object getContent()
        {
            return (contained);
        }

        /**
         * DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        public long expires()
        {
            return (expirationTime);
        }
    }
}
