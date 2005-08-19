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

<%@ page import="org.apache.log4j.*" %>
<%@ page import="java.util.*"%>
<%@ page import="de.softwareforge.eyewiki.*" %>
<%@ page import="org.apache.commons.configuration.*" %>
<%@ page import="java.text.*" %>
<%@ page import="de.softwareforge.eyewiki.util.*" %>
<%@ page import="de.softwareforge.eyewiki.plugin.PluginManager" %>
<%@ page import="de.softwareforge.eyewiki.plugin.WeblogPlugin" %>
<%@ taglib uri="/WEB-INF/tld/oscache.tld" prefix="oscache" %>

<%!
    public void jspInit()
    {
        wiki = WikiEngine.getInstance( getServletConfig() );
    }

    WikiEngine wiki;
%>

<%
    String mode = request.getParameter("mode");

    if( mode == null || !(mode.equals("blog") || mode.equals("wiki")) ) mode = "blog";

    WikiContext wikiContext = wiki.createContext( request, "rss" );
    WikiPage    wikipage    = wikiContext.getPage();

    if( wiki.getBaseURL().length() == 0 )
    {
        response.sendError( 500, "The eyewiki.baseURL property has not been defined for this wiki - cannot generate RSS" );
        return;
    }

    NDC.push( wiki.getApplicationName()+":"+wikipage.getName() );

    //
    //  Force the TranslatorReader to output absolute URLs
    //  regardless of the current settings.
    //
    wikiContext.setVariable( WikiProperties.PROP_REFSTYLE, WikiProperties.PROP_REFSTYLE_DEFAULT );

    response.setContentType("application/rss+xml; charset=UTF-8");

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
        if( mode.equals("blog") )
        {
            WeblogPlugin plug = (WeblogPlugin) pluginManager.findPlugin("WeblogPlugin");

            changed = plug.findBlogEntries(wikipage.getName(),
                    new Date(0L),
                    new Date());
        }
        else
        {
            changed = wiki.getVersionHistory(wikipage.getName());
        }

        Collections.sort( changed, new PageTimeComparator() );

        for( Iterator i = changed.iterator(); i.hasNext(); )
        {
            WikiPage p = (WikiPage) i.next();

            if( !HttpUtil.checkFor304( request, p ) ) hasChanged = true;
            if( p.getLastModified().after( latest ) ) latest = p.getLastModified();
        }
    }

    if( !hasChanged && changed.size() > 0 )
    {
        response.sendError( HttpServletResponse.SC_NOT_MODIFIED );
        return;
    }

    response.addDateHeader("Last-Modified",latest.getTime());
%>
<%-- <oscache:cache time="300"> --%>
<%
        if( mode.equals("blog") )
        {
            out.println(wiki.getRSSGenerator().generateBlogRSS( wikiContext, changed ));
        }
        else
        {
            out.println(wiki.getRSSGenerator().generatePageRSS( wikiContext, changed ));
        }
%>
<%-- </oscache:cache> --%>
<%
    NDC.pop();
    NDC.remove();
%>
