/*
    JSPWiki - a JSP-based WikiWiki clone.

    Copyright (C) 2001-2002 Janne Jalkanen (Janne.Jalkanen@iki.fi)

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
package com.ecyrd.jspwiki.tags;

import java.io.IOException;


/**
 * Root class for different internal wiki links.  Cannot be used directly, but provides basic stuff
 * for other classes.
 *
 * <P>
 * Extend from this class if you need the following attributes.
 * </p>
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
 * format - Either "url" or "anchor".  If "url", will provide just the URL for the link.  If
 * "anchor", will output proper HTML (&lt;a&gt; href="...).
 * </li>
 * </ul>
 *
 *
 * @author Janne Jalkanen
 *
 * @since 2.0
 */
public abstract class WikiLinkTag
        extends WikiTagBase
{
    /** DOCUMENT ME! */
    public static final int ANCHOR = 0;

    /** DOCUMENT ME! */
    public static final int URL = 1;

    /** DOCUMENT ME! */
    protected String m_pageName;

    /** DOCUMENT ME! */
    protected int m_format = ANCHOR;

    /** DOCUMENT ME! */
    protected String m_template;

    /**
     * DOCUMENT ME!
     *
     * @param page DOCUMENT ME!
     */
    public void setPage(String page)
    {
        m_pageName = page;
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
     */
    public String getTemplate()
    {
        return m_template;
    }

    /**
     * DOCUMENT ME!
     *
     * @param arg DOCUMENT ME!
     */
    public void setTemplate(String arg)
    {
        m_template = arg;
    }

    /**
     * DOCUMENT ME!
     *
     * @param mode DOCUMENT ME!
     */
    public void setFormat(String mode)
    {
        if ("url".equalsIgnoreCase(mode))
        {
            m_format = URL;
        }
        else
        {
            m_format = ANCHOR;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int doEndTag()
    {
        try
        {
            if (m_format == ANCHOR)
            {
                pageContext.getOut().print("</a>");
            }
        }
        catch (IOException e)
        {
            // FIXME: Should do something?
        }

        return EVAL_PAGE;
    }
}
