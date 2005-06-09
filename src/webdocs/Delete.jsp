<%@ page import="org.apache.log4j.*" %>
<%@ page import="de.softwareforge.eyewiki.*" %>
<%@ page import="java.util.*" %>
<%@ page import="de.softwareforge.eyewiki.tags.WikiTagBase" %>
<%@ page import="de.softwareforge.eyewiki.auth.AuthorizationManager" %>
<%@ page import="de.softwareforge.eyewiki.auth.UserProfile" %>
<%@ page import="de.softwareforge.eyewiki.auth.permissions.*" %>
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
    WikiContext wikiContext = wiki.createContext( request, 
                                                  WikiContext.DELETE );
    String pagereq = wikiContext.getPage().getName();

    NDC.push( wiki.getApplicationName()+":"+pagereq );    

    WikiPage wikipage      = wikiContext.getPage();
    WikiPage latestversion = wiki.getPage( pagereq );

    String delete = wiki.safeGetParameter( request, "delete" );
    String deleteall = wiki.safeGetParameter( request, "delete-all" );

    if( latestversion == null )
    {
        latestversion = wikiContext.getPage();
    }

    AuthorizationManager mgr = wiki.getAuthorizationManager();
    UserProfile currentUser  = wikiContext.getCurrentUser();

    if( !mgr.checkPermission( wikiContext.getPage(),
                              currentUser,
                              new DeletePermission() ) )
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

    //
    //  Set the response type before we branch.
    //

    response.setContentType("text/html; charset="+wiki.getContentEncoding() );

    if( deleteall != null )
    {
        if (log.isInfoEnabled()) {
            log.info("Deleting page "+pagereq+". User="+request.getRemoteUser()+", host="+request.getRemoteAddr() );
        }

        wiki.deletePage( pagereq );
        response.sendRedirect(wikiContext.getViewURL(pagereq));
        return;
    }
    else if( delete != null )
    {
        if (log.isInfoEnabled()) {
            log.info("Deleting a range of pages from "+pagereq);
        }
        
        for( Enumeration params = request.getParameterNames(); params.hasMoreElements(); )
        {
            String paramName = (String)params.nextElement();
            
            if( paramName.startsWith("delver") )
            {
                int version = Integer.parseInt( paramName.substring(7) );
                
                WikiPage p = wiki.getPage( pagereq, version );
                
                if (log.isDebugEnabled()) {
                    log.debug("Deleting version "+version);
                }
                wiki.deleteVersion( p );
            }
        }
        
        response.sendRedirect(wiki.getURL( WikiContext.INFO, pagereq, null, false ));
        return; 
    }

    // FIXME: not so.
    String contentPage = wiki.getTemplateManager().findJSP( pageContext,
                                                            wikiContext.getTemplate(),
                                                            "EditTemplate.jsp" );
%>

<wiki:Include page="<%=contentPage%>" />

<%
    NDC.pop();
    NDC.remove();
%>
