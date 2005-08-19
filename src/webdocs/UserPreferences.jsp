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
<%@ page import="de.softwareforge.eyewiki.auth.UserManager" %>
<%@ page errorPage="/Error.jsp" %>
<%@ taglib uri="/WEB-INF/tld/eyewiki.tld" prefix="wiki" %>

<%!
    public void jspInit()
    {
        wiki = WikiEngine.getInstance( getServletConfig() );
    }
    WikiEngine wiki;
%>

<%
    WikiContext wikiContext = wiki.createContext( request, WikiContext.PREFS );
    String pagereq = wikiContext.getPage().getName();
    UserManager mgr = wiki.getUserManager();

    NDC.push( wiki.getApplicationName()+":"+pagereq );

    pageContext.setAttribute( WikiTagBase.ATTR_CONTEXT,
                              wikiContext,
                              PageContext.REQUEST_SCOPE );

    String ok = request.getParameter("ok");
    String clear = request.getParameter("clear");

    if( ok != null || "save".equals(request.getParameter("action")) )
    {
        mgr.logout( session );
        String name = wiki.safeGetParameter( request, "username" );

        if( name != null && name.length() > 0 )
        {
            mgr.setUserCookie( response, name );
        }

        response.sendRedirect( wiki.getBaseURL()+"UserPreferences.jsp" );
    }
    else if( clear != null )
    {
        mgr.logout( session );
        Cookie prefs = new Cookie( WikiEngine.PREFS_COOKIE_NAME, "" );
        prefs.setMaxAge( 0 );
        response.addCookie( prefs );

        response.sendRedirect( wiki.getBaseURL()+"UserPreferences.jsp" );
    }
    else
    {
        response.setContentType("text/html; charset="+wiki.getContentEncoding() );
        String contentPage = wiki.getTemplateManager().findJSP( pageContext,
                                                                wikiContext.getTemplate(),
                                                                "ViewTemplate.jsp" );
%>

        <wiki:Include page="<%=contentPage%>" />

<%
    } // Else
    NDC.pop();
    NDC.remove();
%>

