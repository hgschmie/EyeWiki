<%@ page import="org.apache.log4j.*" %>
<%@ page import="de.softwareforge.eyewiki.*" %>
<%@ page import="de.softwareforge.eyewiki.tags.WikiTagBase" %>
<%@ page import="de.softwareforge.eyewiki.auth.*" %>
<%@ page errorPage="/Error.jsp" %>
<%@ taglib uri="/WEB-INF/tld/eyewiki.tld" prefix="wiki" %>

<!--
    This is a sample login page, in case you prefer a clear
    front page instead of the default sign-in type login box
    at the side of the normal entry page. Set this page in
    the welcome-file-list tag in web.xml to default here 
    when entering the site.
-->


<%! 
    public void jspInit()
    {
        wiki = WikiEngine.getInstance( getServletConfig() );
    }
    WikiEngine wiki;
%>

<%
    WikiContext wikiContext = wiki.createContext( request, WikiContext.LOGIN );
    String pagereq = wikiContext.getPage().getName();

    NDC.push( wiki.getApplicationName() + ":Login.jsp"  );

    pageContext.setAttribute( WikiTagBase.ATTR_CONTEXT,
                              wikiContext,
                              PageContext.REQUEST_SCOPE );

    String action = request.getParameter("action");
    String uid    = wiki.safeGetParameter( request,"uid" );
    String passwd = wiki.safeGetParameter( request,"passwd" );

    UserManager mgr = wiki.getUserManager();

    session.setAttribute("msg","");

    if( "login".equals(action) )
    {
        mgr.setUserCookie( response, uid );

        try
        {
            if( mgr.login( uid, passwd, session ) )
            {
                response.sendRedirect( wikiContext.getViewURL(pagereq) );
                return;
            }
            else
            {
                if( passwd.length() > 0 && passwd.toUpperCase().equals(passwd) )
                {
                    session.setAttribute("msg", "Invalid login (please check your Caps Lock key)");
                }
                else
                {
                    session.setAttribute("msg", "Not a valid login.");
                }
            }
        }
        catch( PasswordExpiredException e )
        {
            session.setAttribute("msg", "Your password has expired!  Please enter a new one!");
            response.sendRedirect( wikiContext.getViewURL("UserPreferences") );
            return;
        }
        catch( WikiSecurityException e )
        {
            session.setAttribute("msg", e.getMessage());            
        }
    }
    else if( "logout".equals(action) )
    {
        mgr.logout( session );
        response.sendRedirect( wikiContext.getViewURL(pagereq) );
        return;
    }

    response.setContentType("text/html; charset="+wiki.getContentEncoding() );
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
        "http://www.w3.org/TR/html4/loose.dtd">

<html>

<head>
  <title><wiki:Variable var="applicationname"/> login</title>
  <wiki:Include page="commonheader.jsp"/>
  <meta name="robots" content="noindex,nofollow">
</head>

<body class="login">
  <form action="<wiki:Variable var="baseURL"/>Login.jsp" accept-charset="<wiki:ContentEncoding />" method="post" />
  <input type="hidden" name="page" value="<wiki:Variable var="pagename" />" />
  <table class="login"/>
    <tr>
      <td colspan="2">
        <h3 class="login">Welcome to <wiki:Variable var="applicationname"/></h3>
      </td>
    </tr>
    <tr>
      <td colspan="2" class="error"><wiki:Variable var="msg" /></td>
    </tr>
    <tr>
      <td class="loginmsg">Login:</td>
      <td class="login"><input type="text" size="20" name="uid" value="<wiki:Variable var="uid" default="" />" /></td>
    </tr>
    <tr>
      <td class="loginmsg">Password:</td>
      <td class="login"><input type="password" size="20" name="passwd" /></td>
    </tr>
    <tr>
      <td colspan="2" class="login">
        <input type="submit" name="action" value="login" />
      </td>
    </tr>
  </table>
</body>

</html>
<%
    NDC.pop();
    NDC.remove();
%>
