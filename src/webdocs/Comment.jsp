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
<%@ page import="de.softwareforge.eyewiki.util.HttpUtil" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="de.softwareforge.eyewiki.tags.WikiTagBase" %>
<%@ page import="de.softwareforge.eyewiki.auth.AuthorizationManager" %>
<%@ page import="de.softwareforge.eyewiki.auth.UserProfile" %>
<%@ page import="de.softwareforge.eyewiki.auth.permissions.CommentPermission" %>
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
    String edit    = request.getParameter("edit");
    String author  = wiki.safeGetParameter( request, "author" );
    String link    = wiki.safeGetParameter( request, "link" );
    String remember = request.getParameter("remember");

    WikiContext wikiContext = wiki.createContext( request, 
                                                  WikiContext.COMMENT );

    String pagereq = wikiContext.getPage().getName();

    NDC.push( wiki.getApplicationName()+":"+pagereq );    

    WikiPage wikipage = wikiContext.getPage();
    WikiPage latestversion = wiki.getPage( pagereq );

    if( latestversion == null )
    {
        latestversion = wikiContext.getPage();
    }

    AuthorizationManager mgr = wiki.getAuthorizationManager();
    UserProfile currentUser  = wikiContext.getCurrentUser();

    if( !mgr.checkPermission( wikiContext.getPage(),
                              currentUser,
                              new CommentPermission() ) )
    {
        if (log.isInfoEnabled()) {
            log.info("User "+currentUser.getName()+" has no access - redirecting to login page.");
        }
        String pageurl = wiki.encodeName( pagereq );
        response.sendRedirect( wiki.getBaseURL()+"Login.jsp?page="+pageurl );
        return;
    }

    pageContext.setAttribute( WikiTagBase.ATTR_CONTEXT,
                              wikiContext,
                              PageContext.REQUEST_SCOPE );

    String storedlink = HttpUtil.retrieveCookieValue( request, "link" );
    if( storedlink == null ) storedlink = "";
    
    pageContext.setAttribute( "link", storedlink, PageContext.REQUEST_SCOPE );

    //
    //  Set the response type before we branch.
    //

    response.setContentType("text/html; charset="+wiki.getContentEncoding() );
    response.setHeader( "Cache-control", "max-age=0" );
    response.setDateHeader( "Expires", new Date().getTime() );
    response.setDateHeader( "Last-Modified", new Date().getTime() );

    if (log.isDebugEnabled()) {
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

        wikipage.setAuthor( currentUser.getName() );

        StringBuffer pageText = new StringBuffer(wiki.getPureText( wikipage ));

        if (log.isDebugEnabled()) {
            log.debug("Page initial contents are "+pageText.length()+" chars");
        }

        //
        //  Add a line on top only if we need to separate it from the content.
        //
        if( pageText.length() > 0 )
        {
            pageText.append( "\n\n----\n\n" );
        }        

        pageText.append( wiki.safeGetParameter( request, "text" ) );

        if (log.isDebugEnabled()) {
            log.debug("Author name ="+author);
        }

        if( author != null && author.length() > 0 )
        {
            String signature = author;
            
            if( link != null )
            {
                link = HttpUtil.guessValidURI( link );
                
                signature = "["+author+"|"+link+"]";
            }
            
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat fmt = new SimpleDateFormat("dd-MMM-yyyy");

            pageText.append("\n\n--"+signature+", "+fmt.format(cal.getTime()));
        }

        wiki.saveText( wikiContext, pageText.toString() );

        if( remember != null )
        {
            wiki.getUserManager().setUserCookie( response, author );            
            if( link != null )
            {
                Cookie linkcookie = new Cookie("link", link);
                linkcookie.setMaxAge(1001*24*60*60);
                response.addCookie( linkcookie );
            }
        }

        response.sendRedirect(wikiContext.getViewURL(pagereq));
        return;
    }
    else if( preview != null )
    {
        if (log.isDebugEnabled()) {
            log.debug("Previewing "+pagereq);
        }

        if( author == null ) author = "";
        pageContext.forward( "Preview.jsp?action=comment&author="+author );
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
        log.info("Commenting page "+pagereq+". User="+request.getRemoteUser()+", host="+request.getRemoteAddr() );
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

	//  This is a hack to get the preview to work.
	pageContext.setAttribute( "comment", Boolean.TRUE, PageContext.REQUEST_SCOPE );
	
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
