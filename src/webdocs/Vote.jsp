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
<%@ page import="org.apache.log4j.*" %>
<%@ page import="de.softwareforge.eyewiki.*" %>
<%@ page import="de.softwareforge.eyewiki.tags.WikiTagBase" %>
<%@ page import="de.softwareforge.eyewiki.plugin.VotePlugin" %>
<%@ page import="de.softwareforge.eyewiki.plugin.PluginManager" %>
<%@ page import="org.apache.commons.lang.BooleanUtils" %>

<%@ page errorPage="/Error.jsp" %>

<%@ taglib uri="/WEB-INF/tld/eyewiki.tld" prefix="wiki" %>

<%!
    public void jspInit()
    {
        wiki = WikiEngine.getInstance( getServletConfig() );
    }

    Logger log = Logger.getLogger("eyeWiki");
    WikiEngine wiki;
%><%

    WikiContext wikiContext = wiki.createContext( request, WikiContext.VIEW );
    String pagereq = wikiContext.getPage().getName();
    String vote    = request.getParameter("vote");

    NDC.push( wiki.getApplicationName()+":"+pagereq );

    if (log.isInfoEnabled()) {
        log.info("Vote '"+pagereq+"' from "+request.getRemoteAddr()+" by "+request.getRemoteUser() );
    }


    pageContext.setAttribute( WikiTagBase.ATTR_CONTEXT,
                              wikiContext,
                              PageContext.REQUEST_SCOPE );

    PluginManager pluginManager = wiki.getPluginManager();

    if (pluginManager != null)
    {
        VotePlugin plugin = (VotePlugin) pluginManager.findPlugin("VotePlugin");

        plugin.vote( wikiContext, BooleanUtils.toBoolean(vote) ? 1 : -1 );
    }

    NDC.pop();
    NDC.remove();

    response.sendRedirect( wiki.getBaseURL()+"Wiki.jsp?page=VoteOk" );
%>

