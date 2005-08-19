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
<%@ taglib uri="/WEB-INF/tld/eyewiki.tld" prefix="wiki" %>

<%@ page import="org.apache.log4j.*" %>
<%@ page import="de.softwareforge.eyewiki.*" %>
<%@ page import="de.softwareforge.eyewiki.tags.WikiTagBase" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>

<%@ page errorPage="/Error.jsp" %>

<%!
    public void jspInit()
    {
        wiki = WikiEngine.getInstance( getServletConfig() );
    }

    Logger log = Logger.getLogger("eyeWiki");
    WikiEngine wiki;

%>


<%
    WikiContext wikiContext = wiki.createContext( request, WikiContext.CONFLICT );
    String pagereq = wikiContext.getPage().getName();

    NDC.push( wiki.getApplicationName()+":"+pagereq );

    String usertext = wiki.safeGetParameter( request, "text" );

    pageContext.setAttribute( WikiTagBase.ATTR_CONTEXT,
                              wikiContext,
                              PageContext.REQUEST_SCOPE );

    response.setContentType("text/html; charset="+wiki.getContentEncoding() );

    usertext = StringUtils.replace( usertext, "<", "&lt;" );
    usertext = StringUtils.replace( usertext, ">", "&gt;" );
    usertext = StringUtils.replace( usertext, "\n", "<BR />" );

    pageContext.setAttribute( "usertext",
                              usertext,
                              PageContext.REQUEST_SCOPE );

    String conflicttext = wiki.getText(pagereq);

    conflicttext = StringUtils.replace( conflicttext, "<", "&lt;" );
    conflicttext = StringUtils.replace( conflicttext, ">", "&gt;" );
    conflicttext = StringUtils.replace( conflicttext, "\n", "<BR />" );

    pageContext.setAttribute( "conflicttext",
                              conflicttext,
                              PageContext.REQUEST_SCOPE );

    if (log.isInfoEnabled()) {
        log.info("Page concurrently modified "+pagereq);
    }

    String contentPage = wiki.getTemplateManager().findJSP( pageContext,
                                                            wikiContext.getTemplate(),
                                                            "ViewTemplate.jsp" );
%>

<wiki:Include page="<%=contentPage%>" />

<%
    NDC.pop();
    NDC.remove();
%>
