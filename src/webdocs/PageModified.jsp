<%@ page import="org.apache.log4j.*" %>
<%@ page import="com.ecyrd.jspwiki.*" %>
<%@ page import="java.util.Calendar,java.util.Date" %>
<%@ page import="com.ecyrd.jspwiki.tags.WikiTagBase" %>
<%@ page import org.apache.commons.lang.StringUtils %>
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

    log.info("Page concurrently modified "+pagereq);

    String contentPage = wiki.getTemplateManager().findJSP( pageContext,
                                                            wikiContext.getTemplate(),
                                                            "ViewTemplate.jsp" );
%>

<wiki:Include page="<%=contentPage%>" />

<%
    NDC.pop();
    NDC.remove();
%>
