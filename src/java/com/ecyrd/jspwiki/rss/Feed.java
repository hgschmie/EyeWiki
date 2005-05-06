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
package com.ecyrd.jspwiki.rss;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ecyrd.jspwiki.WikiContext;

/**
 *  @author jalkanen
 *
 *  @since 
 */
public abstract class Feed
{
    protected List m_entries = new ArrayList();
    
    protected String m_feedURL;
    protected String m_channelTitle;
    protected String m_channelDescription;
    protected String m_channelLanguage;

    protected WikiContext m_wikiContext;
    
    public Feed(WikiContext context)
    {
        m_wikiContext = context;
    }
    
    public void addEntry(Entry e)
    {
        m_entries.add(e);
    }

    public abstract String getString();
    /**
     * @return Returns the m_channelDescription.
     */
    public String getChannelDescription()
    {
        return m_channelDescription;
    }
    /**
     * @param description The m_channelDescription to set.
     */
    public void setChannelDescription(String description)
    {
        m_channelDescription = description;
    }
    /**
     * @return Returns the m_channelLanguage.
     */
    public String getChannelLanguage()
    {
        return m_channelLanguage;
    }
    /**
     * @param language The m_channelLanguage to set.
     */
    public void setChannelLanguage(String language)
    {
        m_channelLanguage = language;
    }
    /**
     * @return Returns the m_channelTitle.
     */
    public String getChannelTitle()
    {
        return m_channelTitle;
    }
    /**
     * @param title The m_channelTitle to set.
     */
    public void setChannelTitle(String title)
    {
        m_channelTitle = title;
    }
 
    /**
     * @return Returns the m_feedURL.
     */
    public String getFeedURL()
    {
        return m_feedURL;
    }
    /**
     * @param m_feedurl The m_feedURL to set.
     */
    public void setFeedURL(String m_feedurl)
    {
        m_feedURL = m_feedurl;
    }
    
    /**
     *  Does the required formatting and entity replacement for XML.
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
