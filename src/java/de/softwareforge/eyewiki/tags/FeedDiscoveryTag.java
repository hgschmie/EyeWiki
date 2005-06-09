package de.softwareforge.eyewiki.tags;

import java.io.IOException;


import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.WikiPage;
import de.softwareforge.eyewiki.plugin.WeblogPlugin;
import de.softwareforge.eyewiki.util.BlogUtil;
import de.softwareforge.eyewiki.util.TextUtil;


/**
 *  Outputs links to all the site feeds and APIs this Wiki/blog supports.
 *
 *  @author Janne Jalkanen
 *  @since 2.2
 */
public class FeedDiscoveryTag
        extends WikiTagBase
{
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
        WikiPage page = m_wikiContext.getPage();

        String encodedName = engine.encodeName(page.getName());

        String rssURL = engine.getGlobalRSSURL();
        String atomPostURL = engine.getBaseURL()+"atom/"+encodedName;
        String rssFeedURL  = engine.getBaseURL()+"rss.jsp?page="+encodedName+"&amp;mode=wiki";

        if( rssURL != null )
        {
            String siteName = BlogUtil.getSiteName(m_wikiContext);
            siteName = TextUtil.replaceEntities( siteName );
            
            pageContext.getOut().print("<link rel=\"alternate\" type=\"application/rss+xml\" title=\"RSS feed for the entire site.\" href=\""+rssURL+"\" />\n");
            pageContext.getOut().print("<link rel=\"alternate\" type=\"application/rss+xml\" title=\"RSS feed for page "+siteName+".\" href=\""+rssFeedURL+"\" />\n");

            // TODO: Enable this
            /*
            pageContext.getOut().print("<link rel=\"service.post\" type=\"application/atom+xml\" title=\""+
                                       siteName+"\" href=\""+atomPostURL+"\" />\n");
            */
            // FIXME: This does not work always, as plugins are not initialized until the first fetch
            if( "true".equals(page.getAttribute(WeblogPlugin.ATTR_ISWEBLOG)) )
            {
                String blogFeedURL  = engine.getBaseURL()+"rss.jsp?page="+encodedName;
                String atomFeedURL = engine.getBaseURL()+"atom.jsp?page="+encodedName;
        
                pageContext.getOut().print("<link rel=\"alternate\" type=\"application/rss+xml\" title=\"RSS feed for weblog "+
                                           siteName+".\" href=\""+blogFeedURL+"\" />\n");

                pageContext.getOut().print("<link rel=\"service.feed\" type=\"application/atom+xml\" title=\""+
                                           siteName+"\" href=\""+atomFeedURL+"\" />\n");
            }
        }

        return SKIP_BODY;
    }
}
