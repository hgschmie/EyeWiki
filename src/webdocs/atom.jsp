<%--
 ========================================================================

 eyeWiki - a WikiWiki clone written in Java

 ========================================================================

 Copyright (C) 2005 Henning Schmiedehausen <henning@software-forge.com>

 based on

 JSPWiki - a JSP-based WikiWiki clone.
 Copyright (C) 2002-2005 Janne Jalkanen (Janne.Jalkanen@iki.fi)

 ========================================================================

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

 ========================================================================
--%>
<?xml version="1.0" encoding="UTF-8"?>
<?xml-stylesheet href="<%=wiki.getBaseURL()%>atom.css" type="text/css"?>

<%@ page import="org.apache.log4j.*" %>
<%@ page import="java.util.*"%>
<%@ page import="de.softwareforge.eyewiki.*" %>
<%@ page import="de.softwareforge.eyewiki.plugin.PluginManager" %>
<%@ page import="de.softwareforge.eyewiki.plugin.WeblogPlugin" %>
<%@ page import="org.apache.commons.configuration.*" %>
<%@ page import="java.text.*" %>
<%@ page import="de.softwareforge.eyewiki.rss.*" %>
<%@ page import="de.softwareforge.eyewiki.util.*" %>
<%!
    public void jspInit()
    {
        wiki = WikiEngine.getInstance( getServletConfig() );
    }

    WikiEngine wiki;

    private String getFormattedDate( Date d )
    {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat iso8601fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        cal.setTime( d );
        cal.add( Calendar.MILLISECOND,
                 - (cal.get( Calendar.ZONE_OFFSET ) +
                    (cal.getTimeZone().inDaylightTime( d ) ? cal.get( Calendar.DST_OFFSET ) : 0 )) );
        return iso8601fmt.format( cal.getTime() );
    }
%>

<%
    WikiContext wikiContext = wiki.createContext( request, "rss" );
    WikiPage    wikipage    = wikiContext.getPage();

    if( wiki.getBaseURL().length() == 0 )
    {
        response.sendError( 500, "The eyewiki.baseURL property has not been defined for this wiki - cannot generate Atom feed" );
        return;
    }

    NDC.push( wiki.getApplicationName()+":"+wikipage.getName() );

    //
    //  Force the TranslatorReader to output absolute URLs
    //  regardless of the current settings.
    //
    wikiContext.setVariable( WikiProperties.PROP_REFSTYLE, WikiProperties.PROP_REFSTYLE_DEFAULT );

    response.setContentType("text/xml; charset=UTF-8" );

    StringBuffer result = new StringBuffer();
    SimpleDateFormat iso8601fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    String channelDescription = null;
    String channelLanguage    = null;
    Configuration conf = wiki.getWikiConfiguration();

    try
    {
        channelDescription = conf.getString(WikiProperties.PROP_RSS_CHANNEL_DESCRIPTION );
        channelLanguage    = conf.getString(WikiProperties.PROP_RSS_CHANNEL_LANGUAGE );
    }
    catch( NoSuchElementException e)
    {
        throw new JspException("Did not find a required property!");
    }

    //
    //  Check if nothing has changed, so we can just return a 304
    //
    boolean hasChanged = false;
	Date latest = new Date(0);
	List changed = null;


    //
    //  Now, list items.
    //
    PluginManager pluginManager = wiki.getPluginManager();

    if (pluginManager != null)
    {
        WeblogPlugin plug = (WeblogPlugin) pluginManager.findPlugin("WeblogPlugin");

        changed = plug.findBlogEntries(wikipage.getName(),
                new Date(0L),
                new Date());

        Collections.sort( changed, new PageTimeComparator() );

        for( Iterator i = changed.iterator(); i.hasNext(); )
        {
            WikiPage p = (WikiPage) i.next();

            if( !HttpUtil.checkFor304( request, p ) ) hasChanged = true;
            if( p.getLastModified().after( latest ) ) latest = p.getLastModified();
        }
    }

    if( !hasChanged )
    {
        response.sendError( HttpServletResponse.SC_NOT_MODIFIED );
        return;
    }

    response.addDateHeader("Last-Modified",latest.getTime());

%>

<feed version="0.3" xmlns="http://purl.org/atom/ns#" xml:lang="<%=wiki.getContentEncoding()%>">
  <title mode="escaped" type="text/html"><%=wiki.getApplicationName()%></title>
  <%--<tagline>FIXME: We support no subtitles here</tagline> --%>

  <link rel="alternate" href="<%=wiki.getBaseURL()%>" title="<%=wiki.getApplicationName()%>" type="text/html"/>
<%
    Date    blogmodified = new Date();
    String  blogauthor   = "";

    if( changed.size() > 0 )
    {
        blogmodified = ((WikiPage)changed.get(0)).getLastModified();
        blogauthor   = ((WikiPage)changed.get(0)).getAuthor();
    }
%>
  <modified><%=iso8601fmt.format(blogmodified)%></modified>
  <author>
     <name><%=blogauthor%></name>
  </author>

  <info mode="xml" type="text/html">
      <div xmlns="http://www.w3.org/1999/xhtml">This is an Atom formatted XML site feed. It is intended to be viewed in a Newsreader or syndicated to another site.</div>
  </info>
<%
        int items = 0;
        for( Iterator i = changed.iterator(); i.hasNext() && items < 15; items++ )
        {
            WikiPage p = (WikiPage) i.next();

            String encodedName = wiki.encodeName(p.getName());

            String url = wikiContext.getViewURL(p.getName());

            out.println(" <entry>");

            //
            //  Title
            //
            out.println("  <title>");

            String pageText = wiki.getText(p.getName());
            String title = "";
            int firstLine = pageText.indexOf('\n');

            if( firstLine > 0 )
            {
                title = pageText.substring( 0, firstLine );
            }

            if( title.trim().length() == 0 ) title = p.getName();

            // Remove wiki formatting
            while( title.startsWith("!") ) title = title.substring(1);

            out.println( RSSGenerator.format(title) );
            out.println("</title>");

            //
            //  Link element
            //

            out.println("<link rel=\"alternate\" type=\"text/html\" href=\""+url+"\"/>");

            //
            //  Description
            //
            out.println("<content type=\"text/html\" mode=\"escaped\" xml:base=\""+wiki.getBaseURL()+"\">");
            out.print("<![CDATA[");

            if( firstLine > 0 )
            {
                int maxlen = pageText.length();
                // if( maxlen > 1000 ) maxlen = 1000; // Assume 112 bytes of growth.

                if( maxlen > 0 )
                {
                    pageText = wiki.textToHTML( wikiContext,
                                                pageText.substring( firstLine+1,
                                                                    maxlen ).trim() );
                    out.print( pageText );
                    // if( maxlen == 1000 ) out.print( "..." );
                }
                else
                {
                    out.print( RSSGenerator.format(title) );
                }
            }
            else
            {
                out.print( RSSGenerator.format(title) );
            }

            out.print("]]>");
            out.println("</content>");

            //
            //  Creation date.
            //
            out.print("<created>");
            WikiPage firstversion = wiki.getPage(p.getName(),1);

            out.print( getFormattedDate( firstversion.getLastModified() ) );
            out.print("</created>\n");


            //
            //  Issued date.  eyeWiki does not support drafts, so we essentially output
            //  the same date.
            //

            out.print("<issued>"+getFormattedDate(firstversion.getLastModified())+"</issued>\n");

            //
            //  Modification date.
            //
            out.print("<modified>");
            out.print( getFormattedDate(p.getLastModified()) );
            out.print("</modified>\n");

            //
            //  Author.
            //

            String author = p.getAuthor();
            if( author == null ) author = "unknown";

            out.println("  <author>");
            out.println("   <name>"+author+"</name>");
            /*
            //  This may be useful later on, once I figure out which <link>-tag to use.
            if( wiki.pageExists(author) )
            {
                out.println("<homepage>"+wikiContext.getViewURL(author)+"</homepage>");
            }
            */
            out.println("  </author>\n");

            //
            //  Unique id.  FIXME: is not really a GUID.
            //
            out.println("<id>"+url+"</id>");

            out.println(" </entry>\n");
        }
%>

</feed>

<%
    NDC.pop();
    NDC.remove();
%>
