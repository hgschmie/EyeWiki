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

import de.softwareforge.eyewiki.WikiConstants;
import de.softwareforge.eyewiki.WikiEngine;

/**
 * Writes an image link to the RSS file.
 *
 * @author Janne Jalkanen
 *
 * @since 2.0
 */
public class RSSImageLinkTag
        extends WikiTagBase
{
    /** DOCUMENT ME! */
    protected String m_title;

    /**
     * DOCUMENT ME!
     *
     * @param title DOCUMENT ME!
     */
    public void setTitle(String title)
    {
        m_title = title;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getTitle()
    {
        return m_title;
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

        String rssURL = engine.getGlobalRSSURL();

        if (rssURL != null)
        {
            StringBuffer sb =
                new StringBuffer("<div class=\"").append(WikiConstants.CSS_RSS).append("\"><a class=\"")
                                                 .append(WikiConstants.CSS_RSS).append("\" href=\"").append(rssURL)
                                                 .append("\"><img class=\"").append(WikiConstants.CSS_RSS).append("\" src=\"")
                                                 .append(engine.getBaseURL()).append("images/xml.png\" alt=\"[RSS]\" title=\"")
                                                 .append(getTitle()).append("\"/></a></div>");

            JspWriter out = pageContext.getOut();
            out.print(sb.toString());
        }

        return SKIP_BODY;
    }
}
