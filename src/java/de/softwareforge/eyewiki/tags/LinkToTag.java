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
import de.softwareforge.eyewiki.WikiPage;
import de.softwareforge.eyewiki.attachment.Attachment;

/**
 * Writes a link to a Wiki page.  Body of the link becomes the actual text. The link is written regardless to whether the page
 * exists or not.
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
 * format - either "anchor" or "url" to output either an &lt;A&gt;... or just the HREF part of one.
 * </li>
 * <li>
 * template - Which template should we link to.
 * </li>
 * </ul>
 *
 *
 * @author Janne Jalkanen
 *
 * @since 2.0
 */
public class LinkToTag
        extends WikiLinkTag
{
    /** DOCUMENT ME! */
    private String m_version = null;

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getVersion()
    {
        return m_version;
    }

    /**
     * DOCUMENT ME!
     *
     * @param arg DOCUMENT ME!
     */
    public void setVersion(String arg)
    {
        m_version = arg;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    public int doWikiStartTag()
            throws IOException
    {
        String pageName = m_pageName;
        boolean isattachment = false;

        if (m_pageName == null)
        {
            WikiPage p = m_wikiContext.getPage();

            if (p != null)
            {
                pageName = p.getName();

                isattachment = (p instanceof Attachment);
            }
            else
            {
                return SKIP_BODY;
            }
        }

        JspWriter out = pageContext.getOut();
        String url;

        if (isattachment)
        {
            url = m_wikiContext.getURL(WikiContext.ATTACH, pageName, (getVersion() != null) ? ("version=" + getVersion()) : null);
        }
        else
        {
            StringBuffer params = new StringBuffer();

            if (getVersion() != null)
            {
                params.append("version=" + getVersion());
            }

            if (getTemplate() != null)
            {
                params.append(((params.length() > 0) ? "&amp;" : "") + "skin=" + getTemplate());
            }

            url = m_wikiContext.getURL(WikiContext.VIEW, pageName, params.toString());
        }

        switch (m_format)
        {
        case ANCHOR:

            StringBuffer sb = new StringBuffer("<a href=\"")
                    .append(url)
                    .append("\">");

            out.print(sb.toString());

            break;

        case URL:
            out.print(url);

            break;

        default:
            break;
        }

        return EVAL_BODY_INCLUDE;
    }
}
