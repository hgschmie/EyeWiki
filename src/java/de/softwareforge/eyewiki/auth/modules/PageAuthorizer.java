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
import java.security.acl.AclEntry;
import java.security.acl.NotOwnerException;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;


import de.softwareforge.eyewiki.TranslatorReader;
import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.WikiPage;
import de.softwareforge.eyewiki.acl.AccessControlList;
import de.softwareforge.eyewiki.acl.AclEntryImpl;
import de.softwareforge.eyewiki.acl.AclImpl;
import de.softwareforge.eyewiki.auth.UserManager;
import de.softwareforge.eyewiki.auth.WikiAuthorizer;
import de.softwareforge.eyewiki.auth.WikiSecurityException;
import de.softwareforge.eyewiki.auth.permissions.WikiPermission;
import de.softwareforge.eyewiki.exception.InternalWikiException;


/**
 * This is a simple authorizer that just simply takes the permissions from a page.
 *
 * @author Janne Jalkanen
 */
public class PageAuthorizer
        implements WikiAuthorizer
{
    /** DOCUMENT ME! */
    private static final Logger log = Logger.getLogger(PageAuthorizer.class);

    // FIXME: Should be settable.

    /** DOCUMENT ME! */
    public static final String DEFAULT_PERMISSIONPAGE = "DefaultPermissions";

    /** DOCUMENT ME! */
    public static final String VAR_PERMISSIONS = "defaultpermissions";

    /** DOCUMENT ME! */
    private WikiEngine m_engine;

    /** DOCUMENT ME! */
    private AccessControlList m_defaultPermissions = null;

    /**
     * DOCUMENT ME!
     *
     * @param engine DOCUMENT ME!
     * @param conf DOCUMENT ME!
     */
    public PageAuthorizer(WikiEngine engine, Configuration conf)
    {
        m_engine = engine;
    }

    private void buildDefaultPermissions()
    {
        m_defaultPermissions = new AclImpl();

        WikiPage defpage = m_engine.getPage(DEFAULT_PERMISSIONPAGE);

        if (defpage == null)
        {
            return;
        }

        WikiContext context = new WikiContext(m_engine, defpage);
        context.setVariable(TranslatorReader.PROP_RUNPLUGINS, "false");
        m_engine.getHTML(context, defpage); // Do not remove; this method call updates the ACLs!

        String defperms = (String) defpage.getAttribute(VAR_PERMISSIONS);

        if (defperms != null)
        {
            StringTokenizer tokety = new StringTokenizer(defperms, ";");

            WikiPage p = new WikiPage("Dummy");

            while (tokety.hasMoreTokens())
            {
                String rule = tokety.nextToken();

                try
                {
                    AccessControlList acl = parseAcl(p, m_engine.getUserManager(), rule);
                    p.setAcl(acl);
                }
                catch (WikiSecurityException wse)
                {
                    log.error(
                        "Error on the default permissions page '" + DEFAULT_PERMISSIONPAGE + "':"
                        + wse.getMessage());

                    // FIXME: SHould do something else as well?  This msg only goes to the logs, and is thus not visible to users...
                }
            }

            m_defaultPermissions = p.getAcl();
        }
    }

    /**
     * A helper method for parsing textual AccessControlLists.  The line is in form "(ALLOW|DENY)
     * &lt;permission&gt; &lt;principal&gt;,&lt;principal&gt;,&lt;principal&gt;
     *
     * @param page The current wiki page.  If the page already has an ACL, it will be used as a
     *        basis for this ACL in order to avoid the creation of a new one.
     * @param mgr The UserManager, which is used to query things like the Principal.
     * @param ruleLine The rule line, as described above.
     *
     * @return A valid Access Control List.  May be empty.
     *
     * @throws WikiSecurityException if the ruleLine was faulty somehow.
     * @throws InternalWikiException DOCUMENT ME!
     *
     * @since 2.2
     */
    public static AccessControlList parseAcl(WikiPage page, UserManager mgr, String ruleLine)
            throws WikiSecurityException
    {
        AccessControlList acl = page.getAcl();

        if (acl == null)
        {
            acl = new AclImpl();
        }

        try
        {
            StringTokenizer fieldToks = new StringTokenizer(ruleLine);
            String policy = fieldToks.nextToken();
            String chain = fieldToks.nextToken();

            while (fieldToks.hasMoreTokens())
            {
                String roleOrPerm = fieldToks.nextToken(",").trim();
                boolean isNegative = true;

                Principal principal = mgr.getPrincipal(roleOrPerm);

                if (policy.equals("ALLOW"))
                {
                    isNegative = false;
                }

                AclEntry oldEntry = acl.getEntry(principal, isNegative);

                if (oldEntry != null)
                {
                    if (log.isDebugEnabled())
                    {
                        log.debug("Adding to old acl list: " + principal + ", " + chain);
                    }

                    oldEntry.addPermission(WikiPermission.newInstance(chain));
                }
                else
                {
                    if (log.isDebugEnabled())
                    {
                        log.debug("Adding new acl entry for " + chain);
                    }

                    AclEntry entry = new AclEntryImpl();

                    entry.setPrincipal(principal);

                    if (isNegative)
                    {
                        entry.setNegativePermissions();
                    }

                    entry.addPermission(WikiPermission.newInstance(chain));

                    acl.addEntry(principal, entry);
                }
            }

            page.setAcl(acl);

            log.debug(acl.toString());
        }
        catch (NoSuchElementException nsee)
        {
            log.warn("Invalid access rule: " + ruleLine + " - defaults will be used.");
            throw new WikiSecurityException("Invalid access rule: " + ruleLine);
        }
        catch (NotOwnerException noe)
        {
            throw new InternalWikiException(
                "Someone has implemented access control on access control lists without telling me.");
        }
        catch (IllegalArgumentException iae)
        {
            throw new WikiSecurityException("Invalid permission type: " + ruleLine);
        }

        return acl;
    }

    /**
     * DOCUMENT ME!
     *
     * @param page DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public AccessControlList getPermissions(WikiPage page)
    {
        AccessControlList acl = page.getAcl();

        //
        //  If the ACL has not yet been parsed, we'll do it here.
        //
        if (acl == null)
        {
            WikiContext context = new WikiContext(m_engine, page);
            context.setVariable(TranslatorReader.PROP_RUNPLUGINS, "false");
            m_engine.getHTML(context, page); // Do not remove; this method call updates the ACLs!
            acl = page.getAcl();
        }

        if (log.isDebugEnabled())
        {
            log.debug("page=" + page.getName() + "\n" + acl);
        }

        return acl;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public AccessControlList getDefaultPermissions()
    {
        if (m_defaultPermissions == null)
        {
            buildDefaultPermissions();
        }

        return m_defaultPermissions;
    }
}
