package de.softwareforge.eyewiki.tags;


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
import java.io.IOException;

import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.WikiPage;
import de.softwareforge.eyewiki.auth.AuthorizationManager;
import de.softwareforge.eyewiki.auth.UserProfile;

/**
 * Tells if a page may be edited.  This tag takes care of all possibilities, user permissions, page version, etc.
 *
 * @author Janne Jalkanen
 *
 * @since 2.0
 */
public class PermissionTag
        extends WikiTagBase
{
    /** DOCUMENT ME! */
    private String m_permission;

    /**
     * DOCUMENT ME!
     *
     * @param permission DOCUMENT ME!
     */
    public void setPermission(String permission)
    {
        m_permission = permission;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    public final int doWikiStartTag()
            throws IOException
    {
        WikiEngine engine = m_wikiContext.getEngine();
        WikiPage page = m_wikiContext.getPage();
        AuthorizationManager mgr = engine.getAuthorizationManager();
        boolean gotPermission = false;
        UserProfile userprofile = m_wikiContext.getCurrentUser();

        if (page != null)
        {
            gotPermission = mgr.checkPermission(page, userprofile, m_permission);
        }

        return gotPermission ? EVAL_BODY_INCLUDE : SKIP_BODY;
    }
}
