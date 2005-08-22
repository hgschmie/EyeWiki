package de.softwareforge.eyewiki.rss;

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

import de.softwareforge.eyewiki.WikiPage;

/**
 * DOCUMENT ME!
 *
 * @author jalkanen
 *
 * @since
 */
public class Entry
{
    /** DOCUMENT ME! */
    private String m_content;

    /** DOCUMENT ME! */
    private String m_URL;

    /** DOCUMENT ME! */
    private String m_title;

    /** DOCUMENT ME! */
    private WikiPage m_page;

    /** DOCUMENT ME! */
    private String m_author;

    /**
     * DOCUMENT ME!
     *
     * @param author DOCUMENT ME!
     */
    public void setAuthor(String author)
    {
        m_author = author;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getAuthor()
    {
        return m_author;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public WikiPage getPage()
    {
        return m_page;
    }

    /**
     * DOCUMENT ME!
     *
     * @param p DOCUMENT ME!
     */
    public void setPage(WikiPage p)
    {
        m_page = p;
    }

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
     * @param url DOCUMENT ME!
     */
    public void setURL(String url)
    {
        m_URL = url;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getURL()
    {
        return m_URL;
    }

    /**
     * DOCUMENT ME!
     *
     * @param content DOCUMENT ME!
     */
    public void setContent(String content)
    {
        m_content = content;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getContent()
    {
        return m_content;
    }
}
