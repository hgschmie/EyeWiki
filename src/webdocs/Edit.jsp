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
<%@ page import="de.softwareforge.eyewiki.filters.*" %>
<%@ page import="java.util.*" %>
<%@ page import="de.softwareforge.eyewiki.tags.WikiTagBase" %>
<%@ page import="de.softwareforge.eyewiki.auth.AuthorizationManager" %>
<%@ page import="de.softwareforge.eyewiki.auth.UserProfile" %>
<%@ page import="de.softwareforge.eyewiki.auth.permissions.WikiPermission" %>
<%@ page import="de.softwareforge.eyewiki.auth.permissions.EditPermission" %>
<%@ page import="de.softwareforge.eyewiki.auth.permissions.CreatePermission" %>
<%@ page import="de.softwareforge.eyewiki.htmltowiki.HtmlStringToWikiTranslator" %>
<%@ page errorPage="/Error.jsp" %>
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
    String action  = request.getParameter("action");
    String ok      = request.getParameter("ok");
    String preview = request.getParameter("preview");
    String cancel  = request.getParameter("cancel");
    String append  = request.getParameter("append");
    String edit    = request.getParameter("edit");
    String author  = wiki.safeGetParameter( request, "author" );
    String text    = wiki.safeGetParameter( request, "text" );

    //
    //  Create context and continue
    //
    WikiContext wikiContext = wiki.createContext( request,
                                                  WikiContext.EDIT );
    String pagereq = wikiContext.getPage().getName();

    NDC.push( wiki.getApplicationName()+":"+pagereq );

    //
    //  WYSIWYG editor sends us its greetings
    //
    String htmlText = wiki.safeGetParameter( request, "htmlPageText" );
    if( htmlText != null && cancel == null )
    {
        text = new HtmlStringToWikiTranslator().translate(htmlText,wikiContext);
    }

    WikiPage wikipage = wikiContext.getPage();
    WikiPermission requiredPermission = null;
    WikiPage latestversion = wiki.getPage( pagereq );

    if( latestversion == null )
    {
        latestversion = wikiContext.getPage();
    }
    if( wiki.pageExists( wikipage ) )
    {
        requiredPermission = new EditPermission();
    }
    else
    {
        requiredPermission = new CreatePermission();
    }

    AuthorizationManager mgr = wiki.getAuthorizationManager();
    UserProfile currentUser  = wikiContext.getCurrentUser();

    if( !mgr.checkPermission(  wikiContext.getPage(),
                               currentUser,
                               requiredPermission ) )
    {
        if (log.isInfoEnabled()) {
            log.info("User "+currentUser.getName()+" has no access - redirecting to login page.");
        }
        String msg = "You do not seem to have the permissions for this operation. Would you like to login as another user?";
        wikiContext.setVariable( "msg", msg );
        String pageurl = wiki.encodeName( pagereq );
        response.sendRedirect( wiki.getBaseURL()+"Login.jsp?page="+pageurl );
        return;
    }

    pageContext.setAttribute( WikiTagBase.ATTR_CONTEXT,
                              wikiContext,
                              PageContext.REQUEST_SCOPE );

    //
    //  Set the response type before we branch.
    //

    response.setContentType("text/html; charset="+wiki.getContentEncoding() );
    response.setHeader( "Cache-control", "max-age=0" );
    response.setDateHeader( "Expires", new Date().getTime() );
    response.setDateHeader( "Last-Modified", new Date().getTime() );

    if (log.isDebugEnabled()) {
        //log.debug("Request character encoding="+request.getCharacterEncoding());
        //log.debug("Request content type+"+request.getContentType());
        log.debug("preview="+preview+", ok="+ok);
    }

    if( ok != null )
    {
        if (log.isInfoEnabled()) {
            log.info("Saving page "+pagereq+". User="+request.getRemoteUser()+", host="+request.getRemoteAddr() );
        }

        //  FIXME: I am not entirely sure if the JSP page is the
        //  best place to check for concurrent changes.  It certainly
        //  is the best place to show errors, though.

        long pagedate   = Long.parseLong(request.getParameter("edittime"));

        Date change = latestversion.getLastModified();

        if( change != null && change.getTime() != pagedate )
        {
            //
            // Someone changed the page while we were editing it!
            //

            log.info("Page changed, warning user.");

            pageContext.forward( "PageModified.jsp" );
            return;
        }

        //
        //  We expire ALL locks at this moment, simply because someone has
        //  already broken it.
        //
        PageLock lock = wiki.getPageManager().getCurrentLock( wikipage );
        wiki.getPageManager().unlockPage( lock );
        session.removeAttribute( "lock-"+pagereq );

        //
        //  Set author information
        //

        wikiContext.getPage().setAuthor( currentUser.getName() );

        //
        //  Figure out the actual page text
        //

        if( text == null )
        {
            throw new ServletException( "No parameter text set!" );
        }

        //
        //  If this is an append, then we just append it to the page.
        //  If it is a full edit, then we will replace the previous contents.
        //

        try
        {
            if( append != null )
            {
                StringBuffer pageText = new StringBuffer(wiki.getText( pagereq ));

                pageText.append( text );

                wiki.saveText( wikiContext, pageText.toString() );

            }
            else
            {
                wiki.saveText( wikiContext, text );
            }
        }
        catch( RedirectException ex )
        {
            session.setAttribute("msg", ex.getMessage());
            response.sendRedirect( ex.getRedirect() );
            return;
        }

        response.sendRedirect(wikiContext.getViewURL(pagereq));
        return;
    }
    else if( preview != null )
    {
        if (log.isDebugEnabled()) {
            log.debug("Previewing "+pagereq);
        }
        pageContext.forward( "Preview.jsp" );
    }
    else if( cancel != null )
    {
        if (log.isDebugEnabled()) {
            log.debug("Cancelled editing "+pagereq);
        }
        PageLock lock = (PageLock) session.getAttribute( "lock-"+pagereq );

        if( lock != null )
        {
            wiki.getPageManager().unlockPage( lock );
            session.removeAttribute( "lock-"+pagereq );
        }
        response.sendRedirect( wikiContext.getViewURL(pagereq) );
        return;
    }

    if (log.isInfoEnabled()) {
        log.info("Editing page "+pagereq+". User="+request.getRemoteUser()+", host="+request.getRemoteAddr() );
    }

    //
    //  Determine and store the date the latest version was changed.  Since
    //  the newest version is the one that is changed, we need to track
    //  that instead of the edited version.
    //
    long lastchange = 0;

    Date d = latestversion.getLastModified();
    if( d != null ) lastchange = d.getTime();

    pageContext.setAttribute( "lastchange",
                              Long.toString( lastchange ),
                              PageContext.REQUEST_SCOPE );

    //
    //  Attempt to lock the page.
    //
    PageLock lock = wiki.getPageManager().lockPage( wikipage,
                                                    currentUser.getName() );

    if( lock != null )
    {
        session.setAttribute( "lock-"+pagereq, lock );
    }

    String contentPage = wiki.getTemplateManager().findJSP( pageContext,
                                                            wikiContext.getTemplate(),
                                                            "EditTemplate.jsp" );
%>

<wiki:Include page="<%=contentPage%>" />

<%
    NDC.pop();
    NDC.remove();
%>
