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

import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;

import com.ecyrd.jspwiki.NoRequiredPropertyException;
import com.ecyrd.jspwiki.WikiContext;
import com.ecyrd.jspwiki.WikiEngine;
import com.ecyrd.jspwiki.WikiPage;
import com.ecyrd.jspwiki.WikiProperties;
import com.ecyrd.jspwiki.attachment.Attachment;


/**
 * Generates an RSS feed from the recent changes.
 *
 * <P>
 * We use the 1.0 spec, including the wiki-specific extensions.  Wiki extensions have been defined
 * in <A HREF="http://usemod.com/cgi-bin/mb.pl?ModWiki">UseMod:ModWiki</A>.
 * </p>
 *
 * @author Janne Jalkanen
 *
 * @since 1.7.5.
 */

// FIXME: Merge with rss.jsp
// FIXME: Limit diff and page content size.
public class RSSGenerator
        implements WikiProperties
{
    /** DOCUMENT ME! */
    private WikiEngine m_engine;

    /** DOCUMENT ME! */
    private String m_channelDescription = "";

    /** DOCUMENT ME! */
    private String m_channelLanguage = "en-us";

    /**
     * Initialize the RSS generator.
     *
     * @param engine DOCUMENT ME!
     * @param conf DOCUMENT ME!
     *
     * @throws NoRequiredPropertyException DOCUMENT ME!
     */
    public RSSGenerator(WikiEngine engine, Configuration conf)
            throws NoRequiredPropertyException
    {
        m_engine = engine;

        // FIXME: This assumes a bit too much.
        if (StringUtils.isEmpty(engine.getBaseURL()))
        {
            throw new NoRequiredPropertyException(
                "RSS requires jspwiki.baseURL to be set!", PROP_BASEURL);
        }

        m_channelDescription =
            conf.getString(PROP_RSS_CHANNEL_DESCRIPTION, PROP_RSS_CHANNEL_DESCRIPTION_DEFAULT);

        m_channelLanguage =
            conf.getString(PROP_RSS_CHANNEL_LANGUAGE, PROP_RSS_CHANNEL_LANGUAGE_DEFAULT);
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
        s = StringUtils.replace(s, "&", "&amp;");
        s = StringUtils.replace(s, "<", "&lt;");
        s = StringUtils.replace(s, "]]>", "]]&gt;");

        return s.trim();
    }

    private String getAuthor(WikiPage page)
    {
        String author = page.getAuthor();

        if (author == null)
        {
            author = "An unknown author";
        }

        return author;
    }

    private String getAttachmentDescription(Attachment att)
    {
        String author = getAuthor(att);
        StringBuffer sb = new StringBuffer();

        if (att.getVersion() != 1)
        {
            sb.append(
                author + " uploaded a new version of this attachment on " + att.getLastModified());
        }
        else
        {
            sb.append(author + " created this attachment on " + att.getLastModified());
        }

        sb.append("<br /><hr /><br />");
        sb.append(
            "Parent page: <a href=\""
            + m_engine.getURL(WikiContext.VIEW, att.getParentName(), null, true) + "\">"
            + att.getParentName() + "</a><br />");
        sb.append(
            "Info page: <a href=\"" + m_engine.getURL(WikiContext.INFO, att.getName(), null, true)
            + "\">" + att.getName() + "</a>");

        return sb.toString();
    }

    private String getPageDescription(WikiPage page)
    {
        StringBuffer buf = new StringBuffer();
        String author = getAuthor(page);

        if (page.getVersion() > 1)
        {
            String diff =
                m_engine.getDiff(page.getName(), page.getVersion() - 1, page.getVersion(), false);

            buf.append(
                author + " changed this page on " + page.getLastModified() + ":<br /><hr /><br />");
            buf.append(diff);
        }
        else
        {
            buf.append(
                author + " created this page on " + page.getLastModified() + ":<br /><hr /><br />");
            buf.append(m_engine.getHTML(page.getName()));
        }

        return buf.toString();
    }

    private String getEntryDescription(WikiPage page)
    {
        String res;

        if (page instanceof Attachment)
        {
            res = getAttachmentDescription((Attachment) page);
        }
        else
        {
            res = getPageDescription(page);
        }

        return res;
    }

    private String getEntryTitle(WikiPage page)
    {
        return page.getName();
    }

    /**
     * Generates the RSS resource.  You probably want to output this result into a file or
     * something, or serve as output from a servlet.
     *
     * @return DOCUMENT ME!
     */
    public String generate()
    {
        StringBuffer result = new StringBuffer();
        SimpleDateFormat iso8601fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        //
        //  Preamble
        //
        result.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");

        result.append(
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
            + "   xmlns=\"http://purl.org/rss/1.0/\"\n"
            + "   xmlns:dc=\"http://purl.org/dc/elements/1.1/\"\n"
            + "   xmlns:wiki=\"http://purl.org/rss/1.0/modules/wiki/\">\n");

        //
        //  Channel.
        //
        result.append(" <channel rdf:about=\"" + m_engine.getBaseURL() + "\">\n");

        result.append("  <title>").append(m_engine.getApplicationName()).append("</title>\n");

        // FIXME: This might fail in case the base url is not defined.
        result.append("  <link>").append(m_engine.getBaseURL()).append("</link>\n");

        result.append("  <description>");
        result.append(format(m_channelDescription));
        result.append("</description>\n");

        // According to feedvalidator.org, this element is not defined for RSS 1.0!
        result.append("  <language>");
        result.append(m_channelLanguage);
        result.append("</language>\n");

        //
        //  Now, list items.
        //
        Collection changed = m_engine.getRecentChanges();

        //  We need two lists, which is why we gotta make a separate list if
        //  we want to do just a single pass.
        StringBuffer itemBuffer = new StringBuffer();

        result.append("  <items>\n   <rdf:Seq>\n");

        int items = 0;

        for (Iterator i = changed.iterator(); i.hasNext() && (items < 15); items++)
        {
            WikiPage page = (WikiPage) i.next();

            String encodedName = m_engine.encodeName(page.getName());

            String url;

            if (page instanceof Attachment)
            {
                url = m_engine.getURL(WikiContext.ATTACH, page.getName(), null, true);
            }
            else
            {
                url = m_engine.getURL(WikiContext.VIEW, page.getName(), null, true);
            }

            result.append("    <rdf:li rdf:resource=\"" + url + "\" />\n");

            itemBuffer.append(" <item rdf:about=\"" + url + "\">\n");

            itemBuffer.append("  <title>");
            itemBuffer.append(getEntryTitle(page));
            itemBuffer.append("</title>\n");

            itemBuffer.append("  <link>");
            itemBuffer.append(url);
            itemBuffer.append("</link>\n");

            itemBuffer.append("  <description>");

            itemBuffer.append(format(getEntryDescription(page)));

            itemBuffer.append("</description>\n");

            if (page.getVersion() != -1)
            {
                itemBuffer.append("  <wiki:version>" + page.getVersion() + "</wiki:version>\n");
            }

            if (page.getVersion() > 1)
            {
                itemBuffer.append(
                    "  <wiki:diff>"
                    + m_engine.getURL(WikiContext.DIFF, page.getName(), "r1=-1", true)
                    + "</wiki:diff>\n");
            }

            //
            //  Modification date.
            //
            itemBuffer.append("  <dc:date>");

            Calendar cal = Calendar.getInstance();
            cal.setTime(page.getLastModified());
            cal.add(
                Calendar.MILLISECOND,
                -(cal.get(Calendar.ZONE_OFFSET)
                + (cal.getTimeZone().inDaylightTime(page.getLastModified())
                ? cal.get(Calendar.DST_OFFSET)
                : 0)));
            itemBuffer.append(iso8601fmt.format(cal.getTime()));
            itemBuffer.append("</dc:date>\n");

            //
            //  Author.
            //
            String author = getAuthor(page);
            itemBuffer.append("  <dc:contributor>\n");
            itemBuffer.append("   <rdf:Description");

            if (m_engine.pageExists(author))
            {
                itemBuffer.append(
                    " link=\"" + m_engine.getURL(WikiContext.VIEW, author, null, true) + "\"");
            }

            itemBuffer.append(">\n");
            itemBuffer.append("    <rdf:value>" + author + "</rdf:value>\n");
            itemBuffer.append("   </rdf:Description>\n");
            itemBuffer.append("  </dc:contributor>\n");

            //  PageHistory
            itemBuffer.append("  <wiki:history>");
            itemBuffer.append(m_engine.getURL(WikiContext.INFO, page.getName(), null, true));
            itemBuffer.append("</wiki:history>\n");

            //  Close up.
            itemBuffer.append(" </item>\n");
        }

        result.append("   </rdf:Seq>\n  </items>\n");
        result.append(" </channel>\n");

        result.append(itemBuffer.toString());

        //
        //  In the end, add a search box for JSPWiki
        //
        String searchURL = m_engine.getBaseURL() + "Search.jsp";

        result.append(" <textinput rdf:about=\"" + searchURL + "\">\n");

        result.append("  <title>Search</title>\n");
        result.append("  <description>Search this Wiki</description>\n");
        result.append("  <name>query</name>\n");
        result.append("  <link>" + searchURL + "</link>\n");

        result.append(" </textinput>\n");

        //
        //  Be a fine boy and close things.
        //
        result.append("</rdf:RDF>");

        return result.toString();
    }

    /**
     * Generates an RSS/RDF 1.0 feed.  Each item should be an instance of the RSSItem class.
     *
     * @param wikiContext DOCUMENT ME!
     * @param items DOCUMENT ME!
     * @param limit DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */

    // FIXME: Does not work.
    public String generateRSS(WikiContext wikiContext, List items, int limit)
    {
        StringBuffer result = new StringBuffer();
        SimpleDateFormat iso8601fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        //
        //  Preamble
        //
        result.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");

        result.append(
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
            + "   xmlns=\"http://purl.org/rss/1.0/\"\n"
            + "   xmlns:dc=\"http://purl.org/dc/elements/1.1/\"\n"
            + "   xmlns:wiki=\"http://purl.org/rss/1.0/modules/wiki/\">\n");

        //
        //  Channel.
        //
        result.append(" <channel rdf:about=\"" + m_engine.getBaseURL() + "\">\n");

        result.append("  <title>").append(m_engine.getApplicationName()).append("</title>\n");

        // FIXME: This might fail in case the base url is not defined.
        result.append("  <link>").append(m_engine.getBaseURL()).append("</link>\n");

        result.append("  <description>");
        result.append(format(m_channelDescription));
        result.append("</description>\n");

        // According to feedvalidator.org, this element is not defined for RSS 1.0!
        result.append("  <language>");
        result.append(m_channelLanguage);
        result.append("</language>\n");

        //  We need two lists, which is why we gotta make a separate list if
        //  we want to do just a single pass.
        StringBuffer itemBuffer = new StringBuffer();

        result.append("  <items>\n   <rdf:Seq>\n");

        /*
        int numItems = 0;
        for( Iterator i = items.iterator(); i.hasNext() && numItems < limit; numItems++ )
        {
            RSSItem item = (RSSItem) i.next();

            result.append("    <rdf:li rdf:resource=\""+item.getURL(wikiContext)+"\" />\n");

            itemBuffer.append(" <item rdf:about=\""+item.getURL(wikiContext)+"\">\n");

            itemBuffer.append("  <title>");
            itemBuffer.append( item.getTitle() );
            itemBuffer.append("</title>\n");

            itemBuffer.append("  <link>");
            itemBuffer.append( item.getURL(wikiContext) );
            itemBuffer.append("</link>\n");

            itemBuffer.append("  <description>");

            itemBuffer.append( format( item.getDescription() ) );

            itemBuffer.append("</description>\n");

            if( page.getVersion() != -1 )
            {
                itemBuffer.append("  <wiki:version>"+page.getVersion()+"</wiki:version>\n");
            }

            if( page.getVersion() > 1 )
            {
                itemBuffer.append("  <wiki:diff>"+
                              m_engine.getBaseURL()+"Diff.jsp?page="+
                              encodedName+
                              "&amp;r1=-1"+
                              "</wiki:diff>\n");
            }

            //
            //  Modification date.
            //
            itemBuffer.append("  <dc:date>");
            Calendar cal = Calendar.getInstance();
            cal.setTime( page.getLastModified() );
            cal.add( Calendar.MILLISECOND,
                     - (cal.get( Calendar.ZONE_OFFSET ) +
                        (cal.getTimeZone().inDaylightTime( page.getLastModified() ) ? cal.get( Calendar.DST_OFFSET ) : 0 )) );
            itemBuffer.append( iso8601fmt.format( cal.getTime() ) );
            itemBuffer.append("</dc:date>\n");

            //
            //  Author.
            //
            String author = getAuthor(page);
            itemBuffer.append("  <dc:contributor>\n");
            itemBuffer.append("   <rdf:Description");
            if( m_engine.pageExists(author) )
            {
                itemBuffer.append(" link=\""+m_engine.getViewURL(author)+"\"");
            }
            itemBuffer.append(">\n");
            itemBuffer.append("    <rdf:value>"+author+"</rdf:value>\n");
            itemBuffer.append("   </rdf:Description>\n");
            itemBuffer.append("  </dc:contributor>\n");


            //  PageHistory

            itemBuffer.append("  <wiki:history>");
            itemBuffer.append( m_engine.getBaseURL()+"PageInfo.jsp?page="+
                           encodedName );
            itemBuffer.append("</wiki:history>\n");

            //  Close up.
            itemBuffer.append(" </item>\n");
        }
        */
        result.append("   </rdf:Seq>\n  </items>\n");
        result.append(" </channel>\n");

        result.append(itemBuffer.toString());

        //
        //  In the end, add a search box for JSPWiki
        //
        String searchURL = m_engine.getBaseURL() + "Search.jsp";

        result.append(" <textinput rdf:about=\"" + searchURL + "\">\n");

        result.append("  <title>Search</title>\n");
        result.append("  <description>Search this Wiki</description>\n");
        result.append("  <name>query</name>\n");
        result.append("  <link>" + searchURL + "</link>\n");

        result.append(" </textinput>\n");

        //
        //  Be a fine boy and close things.
        //
        result.append("</rdf:RDF>");

        return result.toString();
    }
}
