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

import javax.servlet.jsp.JspWriter;

import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.WikiPage;

/**
 * Writes a link to the Wiki PageInfo.  Body of the link becomes the actual text.
 *
 * <P>
 * <B>Attributes</B>
 * </p>
 *
 * <UL>
 * <li>
 * page - Page name to refer to.  Default is the current page.
 * </li>
 * </ul>
 *
 *
 * @author Janne Jalkanen
 *
 * @since 2.0
 */

// FIXME: Refactor together with LinkToTag and EditLinkTag.
public class PageInfoLinkTag
        extends WikiLinkTag
{
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
        String pageName = m_pageName;

        if (m_pageName == null)
        {
            WikiPage p = m_wikiContext.getPage();

            if (p != null)
            {
                pageName = p.getName();
            }
            else
            {
                return SKIP_BODY;
            }
        }

        if (engine.pageExists(pageName))
        {
            JspWriter out = pageContext.getOut();

            String url = m_wikiContext.getURL(WikiContext.INFO, pageName);

            switch (m_format)
            {
            case ANCHOR:
                out.print("<a href=\"" + url + "\">");

                break;

            case URL:
                out.print(url);

                break;

            default:
                break;
            }

            return EVAL_BODY_INCLUDE;
        }

        return SKIP_BODY;
    }
}
