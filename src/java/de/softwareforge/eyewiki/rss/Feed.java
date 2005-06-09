package de.softwareforge.eyewiki.rss;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import de.softwareforge.eyewiki.WikiContext;

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
