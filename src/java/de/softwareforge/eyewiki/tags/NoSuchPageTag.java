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
import de.softwareforge.eyewiki.providers.ProviderException;

/**
 * Includes the body in case there is no such page available.
 *
 * @author Janne Jalkanen
 *
 * @since 2.0
 */
public class NoSuchPageTag
        extends WikiTagBase
{
    /** DOCUMENT ME! */
    private String m_pageName;

    /**
     * DOCUMENT ME!
     *
     * @param name DOCUMENT ME!
     */
    public void setPage(String name)
    {
        m_pageName = name;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getPage()
    {
        return m_pageName;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     * @throws ProviderException DOCUMENT ME!
     */
    public int doWikiStartTag()
            throws IOException, ProviderException
    {
        WikiEngine engine = m_wikiContext.getEngine();
        WikiPage page;

        if (m_pageName == null)
        {
            page = m_wikiContext.getPage();
        }
        else
        {
            page = engine.getPage(m_pageName);
        }

        if ((page != null) && engine.pageExists(page.getName(), page.getVersion()))
        {
            return SKIP_BODY;
        }

        return EVAL_BODY_INCLUDE;
    }
}
