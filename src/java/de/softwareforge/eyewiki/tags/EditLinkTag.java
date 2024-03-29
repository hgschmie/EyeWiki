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
 * Writes an edit link.  Body of the link becomes the link text.
 *
 * <P>
 * <B>Attributes</B>
 * </p>
 *
 * <UL>
 * <li>
 * page - Page name to refer to.  Default is the current page.
 * </li>
 * <li>
 * format - Format, either "anchor" or "url".
 * </li>
 * <li>
 * version - Version number of the page to refer to.  Possible values are "this", meaning the version of the current page; or a
 * version number.  Default is always to point at the latest version of the page.
 * </li>
 * </ul>
 *
 *
 * @author Janne Jalkanen
 *
 * @since 2.0
 */
public class EditLinkTag
        extends WikiLinkTag
{
    /** DOCUMENT ME! */
    private String m_version = null;

    /**
     * DOCUMENT ME!
     *
     * @param vers DOCUMENT ME!
     */
    public void setVersion(String vers)
    {
        m_version = vers;
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
        WikiPage page = null;
        String versionString = "";
        String pageName = null;

        //
        //  Determine the page and the link.
        //
        if (m_pageName == null)
        {
            page = m_wikiContext.getPage();

            if (page == null)
            {
                // You can't call this on the page itself anyways.
                return SKIP_BODY;
            }
            else
            {
                pageName = page.getName();
            }
        }
        else
        {
            pageName = m_pageName;
        }

        //
        //  Determine the latest version, if the version attribute is "this".
        //
        if (m_version != null)
        {
            if ("this".equalsIgnoreCase(m_version))
            {
                if (page == null)
                {
                    // No page, so go fetch according to page name.
                    page = engine.getPage(m_pageName);
                }

                if (page != null)
                {
                    versionString = "version=" + page.getVersion();
                }
            }
            else
            {
                versionString = "version=" + m_version;
            }
        }

        //
        //  Finally, print out the correct link, according to what
        //  user commanded.
        //
        JspWriter out = pageContext.getOut();

        switch (m_format)
        {
        case ANCHOR:

            StringBuffer sb =
                new StringBuffer("<a href=\"")
                    .append(m_wikiContext.getURL(WikiContext.EDIT, pageName, versionString)).append("\">");
            out.print(sb.toString());

            break;

        case URL:
            out.print(m_wikiContext.getURL(WikiContext.EDIT, pageName, versionString));

            break;

        default:
            break;
        }

        return EVAL_BODY_INCLUDE;
    }
}
