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

import javax.servlet.http.HttpSession;

import de.softwareforge.eyewiki.PageLock;
import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.WikiPage;
import de.softwareforge.eyewiki.manager.PageManager;
import de.softwareforge.eyewiki.providers.ProviderException;

/**
 * DOCUMENT ME!
 *
 * @author Janne Jalkanen
 *
 * @since 2.0
 */
public class CheckLockTag
        extends WikiTagBase
{
    /** DOCUMENT ME! */
    public static final int LOCKED = 0;

    /** DOCUMENT ME! */
    public static final int NOTLOCKED = 1;

    /** DOCUMENT ME! */
    public static final int OWNED = 2;

    /** DOCUMENT ME! */
    private int m_mode;

    /**
     * DOCUMENT ME!
     *
     * @param arg DOCUMENT ME!
     */
    public void setMode(String arg)
    {
        if ("locked".equals(arg))
        {
            m_mode = LOCKED;
        }
        else if ("owned".equals(arg))
        {
            m_mode = OWNED;
        }
        else
        {
            m_mode = NOTLOCKED;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     * @throws ProviderException DOCUMENT ME!
     */
    public final int doWikiStartTag()
            throws IOException, ProviderException
    {
        WikiEngine engine = m_wikiContext.getEngine();
        WikiPage page = m_wikiContext.getPage();

        if ((page != null) && engine.pageExists(page))
        {
            PageManager mgr = engine.getPageManager();

            PageLock lock = mgr.getCurrentLock(page);

            HttpSession session = pageContext.getSession();

            PageLock userLock = (PageLock) session.getAttribute("lock-" + page.getName());

            if (((lock != null) && (m_mode == LOCKED) && (lock != userLock))
                            || ((lock != null) && (m_mode == OWNED) && (lock == userLock))
                            || ((lock == null) && (m_mode == NOTLOCKED)))
            {
                String id = getId();

                if ((id != null) && (lock != null))
                {
                    pageContext.setAttribute(id, lock);
                }

                return EVAL_BODY_INCLUDE;
            }
        }

        return SKIP_BODY;
    }
}
