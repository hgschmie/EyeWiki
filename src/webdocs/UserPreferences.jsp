<%@ page import="org.apache.log4j.*" %>
<%@ page import="com.ecyrd.jspwiki.*" %>
<%@ page import="com.ecyrd.jspwiki.tags.WikiTagBase" %>
<%@ page import="com.ecyrd.jspwiki.auth.UserProfile" %>
<%@ page import="com.ecyrd.jspwiki.auth.UserManager" %>
<%@ page errorPage="/Error.jsp" %>
<%@ taglib uri="/WEB-INF/jspwiki.tld" prefix="wiki" %>

<%! 
    public void jspInit()
    {
        wiki = WikiEngine.getInstance( getServletConfig() );
    }
    Category log = Category.getInstance("JSPWiki"); 
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

