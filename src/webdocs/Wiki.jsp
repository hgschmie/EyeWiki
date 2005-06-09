<%@ page import="org.apache.log4j.*" %>
<%@ page import="de.softwareforge.eyewiki.*" %>
<%@ page import="de.softwareforge.eyewiki.tags.WikiTagBase" %>
<%@ page import="de.softwareforge.eyewiki.auth.permissions.ViewPermission" %>
<%@ page import="de.softwareforge.eyewiki.auth.*" %>
<%@ page errorPage="/Error.jsp" %>
<%@ taglib uri="/WEB-INF/tld/jspwiki.tld" prefix="wiki" %>
<%! 
    public void jspInit()
    {
        wiki = WikiEngine.getInstance( getServletConfig() );
    }
    Logger log = Logger.getLogger("JSPWiki"); 
    WikiEngine wiki;

%><%

    WikiContext wikiContext = wiki.createContext( request, WikiContext.VIEW );
    String pagereq = wikiContext.getPage().getName();

    NDC.push( wiki.getApplicationName()+":"+pagereq );

    if (log.isInfoEnabled())
    {
        String remoteUser = request.getRemoteUser();

        if (remoteUser == null)
        {
            log.info("Anonymous request for page '"+pagereq+"' from "+request.getRemoteAddr()  );
        }
        else
        {
            log.info("Request for page '"+pagereq+"' from "+request.getRemoteAddr()+" by " + remoteUser);
        }
    }

    String redirect = wiki.getRedirectURL( wikiContext );

    if( redirect != null )
    {
        response.sendRedirect( redirect );
        return;
    }

    AuthorizationManager mgr = wiki.getAuthorizationManager();
    UserProfile currentUser  = wiki.getUserManager().getUserProfile( request );

    if( !mgr.checkPermission( wikiContext.getPage(),
                              currentUser,
                              new ViewPermission() ) )
    {
        if( mgr.isStrictLogins() )
        {
            if (log.isInfoEnabled()) {
                log.info("User "+currentUser.getName()+" has no access - redirecting to login page.");
            }
            String msg = "Unknown user or password.<br>Please try again.";
            session.setAttribute( "msg", msg );
            String pageurl = wiki.encodeName( pagereq );
            response.sendRedirect( wiki.getBaseURL()+"Login.jsp?page="+pageurl );
            return;
        }
        else
        {
            //
            //  Do a bit of sanity check here.  FIXME: Should this be somewhere else?
            //
            if( pagereq.equals("LoginError") ) 
            {
                throw new WikiSecurityException("Looped config detected - you must not prevent view access to page LoginError AND have strictLogins set to true!");
            }

            if (log.isInfoEnabled()) {
                log.info("User "+currentUser.getName()+" has no access - displaying message.");
            }
            response.sendRedirect(wikiContext.getViewURL("LoginError") );
        }
    }

    pageContext.setAttribute( WikiTagBase.ATTR_CONTEXT,
                              wikiContext,
                              PageContext.REQUEST_SCOPE );

    //
    //  Alright, then start responding.
    //

    response.setContentType("text/html; charset="+wiki.getContentEncoding() );

    String contentPage = wiki.getTemplateManager().findJSP( pageContext,
                                                            wikiContext.getTemplate(),
                                                            "ViewTemplate.jsp" );

%><wiki:Include page="<%=contentPage%>" /><%

    NDC.pop();
    NDC.remove();
%>

