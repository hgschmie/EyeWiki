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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import de.softwareforge.eyewiki.WikiContext;

/**
 * DOCUMENT ME!
 *
 * @author jalkanen
 *
 * @since
 */
public abstract class Feed
{
    /** DOCUMENT ME! */
    protected List m_entries = new ArrayList();

    /** DOCUMENT ME! */
    protected String m_feedURL;

    /** DOCUMENT ME! */
    protected String m_channelTitle;

    /** DOCUMENT ME! */
    protected String m_channelDescription;

    /** DOCUMENT ME! */
    protected String m_channelLanguage;

    /** DOCUMENT ME! */
    protected WikiContext m_wikiContext;

    /**
     * Creates a new Feed object.
     *
     * @param context DOCUMENT ME!
     */
    public Feed(WikiContext context)
    {
        m_wikiContext = context;
    }

    /**
     * DOCUMENT ME!
     *
     * @param e DOCUMENT ME!
     */
    public void addEntry(Entry e)
    {
        m_entries.add(e);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract String getString();

    /**
     * DOCUMENT ME!
     *
     * @return Returns the m_channelDescription.
     */
    public String getChannelDescription()
    {
        return m_channelDescription;
    }

    /**
     * DOCUMENT ME!
     *
     * @param description The m_channelDescription to set.
     */
    public void setChannelDescription(String description)
    {
        m_channelDescription = description;
    }

    /**
     * DOCUMENT ME!
     *
     * @return Returns the m_channelLanguage.
     */
    public String getChannelLanguage()
    {
        return m_channelLanguage;
    }

    /**
     * DOCUMENT ME!
     *
     * @param language The m_channelLanguage to set.
     */
    public void setChannelLanguage(String language)
    {
        m_channelLanguage = language;
    }

    /**
     * DOCUMENT ME!
     *
     * @return Returns the m_channelTitle.
     */
    public String getChannelTitle()
    {
        return m_channelTitle;
    }

    /**
     * DOCUMENT ME!
     *
     * @param title The m_channelTitle to set.
     */
    public void setChannelTitle(String title)
    {
        m_channelTitle = title;
    }

    /**
     * DOCUMENT ME!
     *
     * @return Returns the m_feedURL.
     */
    public String getFeedURL()
    {
        return m_feedURL;
    }

    /**
     * DOCUMENT ME!
     *
     * @param m_feedurl The m_feedURL to set.
     */
    public void setFeedURL(String m_feedurl)
    {
        m_feedURL = m_feedurl;
    }

    /**
     * Does the required formatting and entity replacement for XML.
     *
     * @param s DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static String format(String s)
    {
        if (s != null)
        {
            s = StringUtils.replace(s, "&", "&amp;");
            s = StringUtils.replace(s, "<", "&lt;");
            s = StringUtils.replace(s, ">", "&gt;");

            return s.trim();
        }

        return null;
    }
}
