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
<%@ page isErrorPage="true" %>
<%@ page import="org.apache.log4j.*" %>
<%@ page import="de.softwareforge.eyewiki.*" %>
<%@ page import="de.softwareforge.eyewiki.util.*" %>
<%@ page import="de.softwareforge.eyewiki.tags.WikiTagBase" %>
<%@ taglib uri="/WEB-INF/tld/eyewiki.tld" prefix="wiki" %>
<%!
    public void jspInit()
    {
        wiki = WikiEngine.getInstance( getServletConfig() );
    }
    Logger log = Logger.getLogger("eyeWiki");
    WikiEngine wiki;
%>
<%
    WikiContext wikiContext = wiki.createContext( request,
                                                  WikiContext.ERROR );
    String pagereq = wikiContext.getPage().getName();

    NDC.push( wiki.getApplicationName() + ":" + pagereq );

    pageContext.setAttribute( WikiTagBase.ATTR_CONTEXT,
                              wikiContext,
                              PageContext.REQUEST_SCOPE );

    response.setContentType("text/html; charset="+wiki.getContentEncoding() );

    String msg = "An unknown error was caught by Error.jsp";

    Throwable realcause = null;

    if( exception != null )
    {
        msg = exception.getMessage();
        if( msg == null || msg.length() == 0 )
        {
            msg = "An unknown exception "+exception.getClass().getName()+" was caught by Error.jsp.";
        }

        //
        //  This allows us to get the actual cause of the exception.
        //  Note the cast; at least Tomcat has two classes called "JspException"
        //  imported in JSP pages.
        //


        if( exception instanceof javax.servlet.jsp.JspException )
        {
            if (log.isDebugEnabled()) {
                log.debug("IS JSPEXCEPTION");
                realcause = ((javax.servlet.jsp.JspException)exception).getRootCause();
                log.debug("REALCAUSE="+realcause);
            }
        }

        if( realcause == null ) realcause = exception;
    }
    else
    {
        realcause = new Exception("Unknown general exception");
    }

    if (log.isDebugEnabled()) {
        log.debug("Error.jsp exception is: ",exception);
    }


    pageContext.setAttribute( "message", msg, PageContext.REQUEST_SCOPE );
%>

   <h3 class="errorpage">eyeWiki has detected an error</h3>

   <dl>
      <dt class="errorpage">Error Message</dt>
      <dd class="errorpage"><%=pageContext.getAttribute("message",PageContext.REQUEST_SCOPE)%></dd>
      <dt class="errorpage">Exception</dt>
      <dd class="errorpage"><%=realcause.getClass().getName()%></dd>
      <dt class="errorpage">Place where detected</dt>
      <dd class="errorpage"><%=FileUtil.getThrowingMethod(realcause)%></dd>
   </dl>

   <br clear="all" />
<%
    NDC.pop();
    NDC.remove();
%>

