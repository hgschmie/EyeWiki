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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.security.ProviderException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import org.picocontainer.Startable;

import com.ecyrd.jspwiki.PageTimeComparator;
import com.ecyrd.jspwiki.WikiContext;
import com.ecyrd.jspwiki.WikiEngine;
import com.ecyrd.jspwiki.WikiPage;
import com.ecyrd.jspwiki.WikiProperties;
import com.ecyrd.jspwiki.attachment.Attachment;
import com.ecyrd.jspwiki.exception.NoRequiredPropertyException;
import com.ecyrd.jspwiki.exception.NoSuchVariableException;
import com.ecyrd.jspwiki.manager.VariableManager;
import com.ecyrd.jspwiki.util.FileUtil;
import com.ecyrd.jspwiki.variable.AbstractSimpleVariable;
import com.ecyrd.jspwiki.variable.WikiVariable;


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
        implements WikiProperties, Startable
{
    /** DOCUMENT ME! */
    private static final Logger log = Logger.getLogger(RSSGenerator.class);

    /** DOCUMENT ME! */
    private WikiEngine m_engine;

    /** DOCUMENT ME! */
    private String m_channelDescription = "";

    /** DOCUMENT ME! */
    private String m_channelLanguage = "en-us";

    /** Stores the relative URL to the global RSS feed. */
    private String m_rssURL;

    /** The Generator Configuration */
    private final Configuration m_conf;

    private final VariableManager variableManager;

    private static final int MAX_CHARACTERS = Integer.MAX_VALUE;
    
    /**
     * Initialize the RSS generator.
     *
     * @param engine DOCUMENT ME!
     * @param conf DOCUMENT ME!
     *
     * @throws NoRequiredPropertyException DOCUMENT ME!
     */
    public RSSGenerator(final WikiEngine engine, final Configuration conf, final VariableManager variableManager)
            throws NoRequiredPropertyException
    {
        m_engine = engine;
        m_conf = conf;
        this.variableManager = variableManager;

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

    public synchronized void start()
    {
        variableManager.registerVariable("jspwiki.rss.generate", new RSSVariable());

        new RSSThread().start();
    }


    public synchronized void stop()
    {

    }

    public synchronized String getGlobalRSSURL()
    {
        return m_rssURL;
    }

    private synchronized void setGlobalRSSURL(final String rssURL)
    {
        this.m_rssURL = rssURL;
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
            // FIXME: Will fail when non-contiguous versions
            String diff =
                    m_engine.getDiff(page.getName(), page.getVersion() - 1, page.getVersion(), false);

            buf.append(author)
                    .append(" changed this page on ")
                    .append(page.getLastModified())
                    .append(":<br /><hr /><br />")
                    .append(diff);
        }
        else
        {
            buf.append(author)
                    .append(" created this page on ")
                    .append(page.getLastModified())
                    .append(":<br /><hr /><br />")
                    .append(m_engine.getHTML(page.getName()));
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
        WikiContext context = new WikiContext(m_engine, new WikiPage("__DUMMY"));
        context.setRequestContext(WikiContext.RSS);
        
        String result = generateWikiRSS(context);
        
        result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + result;
        
        return result;
    }
/*
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

        String baseURL = m_engine.getBaseURL();
        //
        //  Channel.
        //
        result.append(" <channel rdf:about=\"")
                .append(baseURL)
                .append("\">\n");

        result.append("  <title>").append(m_engine.getApplicationName()).append("</title>\n");

        // FIXME: This might fail in case the base url is not defined.
        result.append("  <link>")
                .append(baseURL).append("</link>\n")
                .append("  <description>")
                .append(format(m_channelDescription))
                .append("</description>\n");

        // According to feedvalidator.org, this element is not defined for RSS 1.0!
        result.append("  <language>")
                .append(m_channelLanguage)
                .append("</language>\n");

        //
        //  Now, list items.
        //
        Collection changed = m_engine.getRecentChanges();

        //  We need two lists, which is why we gotta make a separate list if
        //  we want to do just a single pass.
        StringBuffer itemBuffer = new StringBuffer();

        result.append("  <items>\n   <rdf:Seq>\n");

        int items = 0;

        for (Iterator i = changed.iterator(); i.hasNext() && items < 15; items++)
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

            result.append("    <rdf:li rdf:resource=\"")
                    .append(url)
                    .append("\" />\n");

            itemBuffer.append(" <item rdf:about=\"")
                    .append(url)
                    .append("\">\n")
                    .append("  <title>")
                    .append(getEntryTitle(page))
                    .append("</title>\n")
                    .append("  <link>")
                    .append(url)
                    .append("</link>\n")
                    .append("  <description>")
                    .append(format(getEntryDescription(page)))
                    .append("</description>\n");

            if (page.getVersion() != -1)
            {
                itemBuffer.append("  <wiki:version>")
                        .append(page.getVersion())
                        .append("</wiki:version>\n");
            }

            if (page.getVersion() > 1)
            {
                itemBuffer.append("  <wiki:diff>")
                        .append(m_engine.getURL(WikiContext.DIFF, page.getName(), "r1=-1", true))
                        .append("</wiki:diff>\n");
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
            itemBuffer.append("  <dc:contributor>\n")
                    .append("   <rdf:Description");

            if (m_engine.pageExists(author))
            {
                itemBuffer.append(
                        " link=\"")
                        .append(m_engine.getURL(WikiContext.VIEW, author, null, true))
                        .append("\"");
            }

            itemBuffer.append(">\n")
                    .append("    <rdf:value>")
                    .append(author)
                    .append("</rdf:value>\n")
                    .append("   </rdf:Description>\n")
                    .append("  </dc:contributor>\n");

            //  PageHistory
            itemBuffer.append("  <wiki:history>")
                    .append(m_engine.getURL(WikiContext.INFO, page.getName(), null, true))
                    .append("</wiki:history>\n");

            //  Close up.
            itemBuffer.append(" </item>\n");
        }

        result.append("   </rdf:Seq>\n  </items>\n")
                .append(" </channel>\n")
                .append(itemBuffer.toString());

        //
        //  In the end, add a search box for JSPWiki
        //
        String searchURL = baseURL + "Search.jsp";

        result.append(" <textinput rdf:about=\"")
                .append(searchURL)
                .append("\">\n")
                .append("  <title>Search</title>\n")
                .append("  <description>Search this Wiki</description>\n")
                .append("  <name>query</name>\n")
                .append("  <link>")
                .append(searchURL)
                .append("</link>\n")
                .append(" </textinput>\n");

        //
        //  Be a fine boy and close things.
        //
        result.append("</rdf:RDF>");

        return result.toString();
    }
*/

    /**
     *  Generates an RSS/RDF 1.0 feed for the entire wiki.  Each item should be an instance of the RSSItem class.
     */
    public String generateWikiRSS(WikiContext wikiContext)
    {
        RSS10Feed feed = new RSS10Feed(wikiContext);
        
        feed.setChannelTitle(m_engine.getApplicationName());
        feed.setFeedURL(m_engine.getBaseURL());
        feed.setChannelLanguage(m_channelLanguage);
        feed.setChannelDescription(m_channelDescription);

        Collection changed = m_engine.getRecentChanges();

        int items = 0;
        for (Iterator i = changed.iterator(); i.hasNext() && items < 15; items++)
        {
            WikiPage page = (WikiPage) i.next();
            
            Entry e = new Entry();
            
            e.setPage(page);

            String url;

            if (page instanceof Attachment)
            {
                url = m_engine.getURL(WikiContext.ATTACH, 
                                       page.getName(),
                                       null,
                                       true);
            }
            else
            {
                url = m_engine.getURL(WikiContext.VIEW, 
                                       page.getName(),
                                       null,
                                       true);
            }
            
            e.setURL(url);
            e.setTitle(getEntryTitle(page));
            e.setContent(getEntryDescription(page));
            e.setAuthor(getAuthor(page));
            
            feed.addEntry(e);
        }
        
        return feed.getString();
    }

    public String generatePageRSS(WikiContext wikiContext, List changed)
    {
        RSS10Feed feed = new RSS10Feed(wikiContext);
        
        feed.setChannelTitle(m_engine.getApplicationName());
        feed.setFeedURL(m_engine.getBaseURL());
        feed.setChannelLanguage(m_channelLanguage);
        feed.setChannelDescription(m_channelDescription);

        Collections.sort(changed, new PageTimeComparator());
                
        int items = 0;
        for (Iterator i = changed.iterator(); i.hasNext() && items < 15; items++)
        {
            WikiPage page = (WikiPage) i.next();
            
            Entry e = new Entry();
            
            e.setPage(page);

            String url;

            if (page instanceof Attachment)
            {
                url = m_engine.getURL(WikiContext.ATTACH, 
                                       page.getName(),
                                       "version=" + page.getVersion(),
                                       true);
            }
            else
            {
                url = m_engine.getURL(WikiContext.VIEW, 
                                       page.getName(),
                                       "version=" + page.getVersion(),
                                       true);
            }
            
            e.setURL(url);
            e.setTitle(getEntryTitle(page));
            e.setContent(getEntryDescription(page));
            e.setAuthor(getAuthor(page));
            
            feed.addEntry(e);
        }
        
        return feed.getString();
    }

    
    public String generateBlogRSS(WikiContext wikiContext, List changed)
        throws ProviderException
    {
        RSS10Feed feed = new RSS10Feed(wikiContext);
        
        log.debug("Generating RSS for blog, size=" + changed.size());
        
        feed.setChannelTitle(m_engine.getApplicationName() + ":" + wikiContext.getPage().getName());
        feed.setFeedURL(wikiContext.getViewURL(wikiContext.getPage().getName()));
        feed.setChannelLanguage(m_channelLanguage);
        
        String channelDescription;
        
        try
        {
            channelDescription = m_engine.getVariableManager().getValue(wikiContext, WikiProperties.PROP_RSS_CHANNEL_DESCRIPTION);
            feed.setChannelDescription(channelDescription);
        }
        catch(NoSuchVariableException e) {}

        Collections.sort(changed, new PageTimeComparator());

        int items = 0;
        for (Iterator i = changed.iterator(); i.hasNext() && items < 15; items++)
        {
            WikiPage page = (WikiPage) i.next();
            
            Entry e = new Entry();
            
            e.setPage(page);

            String url;

            if (page instanceof Attachment)
            {
                url = m_engine.getURL(WikiContext.ATTACH, 
                                       page.getName(),
                                       null,
                                       true);
            }
            else
            {
                url = m_engine.getURL(WikiContext.VIEW, 
                                       page.getName(),
                                       null,
                                       true);
            }
            
            e.setURL(url);
            
            //
            //  Title
            //
            
            String pageText = m_engine.getText(page.getName());
            String title = "";
            int firstLine = pageText.indexOf('\n');

            if (firstLine > 0)
            {
                title = pageText.substring(0, firstLine);
            }
            
            if (title.trim().length() == 0)
            {
                title = page.getName();
            }

            // Remove wiki formatting
            while (title.startsWith("!"))
            {
                title = title.substring(1);
            }
            
            e.setTitle(title);
            
            //
            //  Description
            //
            
            if (firstLine > 0)
            {
                int maxlen = pageText.length();
                if (maxlen > MAX_CHARACTERS)
                {
                    maxlen = MAX_CHARACTERS;
                }

                if (maxlen > 0)
                {
                    pageText = m_engine.textToHTML(wikiContext, 
                                                    pageText.substring(firstLine+1,
                                                                        maxlen).trim());
                    
                    if (maxlen == MAX_CHARACTERS)
                    {
                        pageText += "...";
                    }
                    
                    e.setContent(pageText);
                }
                else
                {
                    e.setContent(title);
                }
            }
            else
            {
                e.setContent(title);
            }

            e.setAuthor(getAuthor(page));
            
            feed.addEntry(e);
        }
        
        return feed.getString();
    }

    private class RSSVariable
            extends AbstractSimpleVariable
            implements WikiVariable
    {
        public String getValue(WikiContext context, String variableName)
        {
            // URL exists: We generate a RSS Stream. Else not.
            return String.valueOf(getGlobalRSSURL() != null);
        }
    }

    /**
     * Runs the RSS generation thread. FIXME: MUST be somewhere else, this is not a good place.
     */
    private class RSSThread
            extends Thread
    {
        /**
         * DOCUMENT ME!
         */
        public void run()
        {
            String rootPath = m_engine.getRootPath();

            if (rootPath == null)
            {
                log.error("Could not determine root path of the Wiki, cannot write RSS Feeds");
            }

            try
            {
                String fileName = m_conf.getString(PROP_RSS_FILE, PROP_RSS_FILE_DEFAULT);

                int rssInterval = m_conf.getInt(PROP_RSS_INTERVAL, PROP_RSS_INTERVAL_DEFAULT);

                if (log.isDebugEnabled())
                {
                    log.debug("RSS file will be at " + fileName);
                    log.debug("RSS refresh interval (seconds): " + rssInterval);
                }

                while (true)
                {
                    Writer out = null;
                    Reader in = null;

                    try
                    {
                        //
                        //  Generate RSS file, output it to
                        //  default "rss.rdf".
                        //
                        if (log.isDebugEnabled())
                        {
                            log.debug("Regenerating RSS feed to " + fileName);
                        }

                        String feed = generate();

                        File file = new File(rootPath, fileName);

                        in = new StringReader(feed);
                        out = new BufferedWriter(
                                new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));

                        FileUtil.copyContents(in, out);

                        setGlobalRSSURL(fileName);
                    }
                    catch (IOException e)
                    {
                        log.error("Cannot generate RSS feed to " + fileName, e);
                        setGlobalRSSURL(null);
                    }
                    finally
                    {
                        IOUtils.closeQuietly(in);
                        IOUtils.closeQuietly(out);
                    }

                    Thread.sleep(rssInterval * 1000L);
                } // while
            }
            catch (InterruptedException e)
            {
                log.error("RSS thread interrupted, no more RSS feeds", e);
            }

            //
            // Signal: no more RSS feeds.
            //
            setGlobalRSSURL(null);
        }
    }
}
