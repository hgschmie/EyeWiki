<%@ page import="org.apache.log4j.*" %>
<%@ page import="de.softwareforge.eyewiki.*" %>
<%@ page import="de.softwareforge.eyewiki.tags.WikiTagBase" %>
<%@ page import="de.softwareforge.eyewiki.plugin.VotePlugin" %>
<%@ page import="de.softwareforge.eyewiki.plugin.PluginManager" %>
<%@ page import="org.apache.commons.lang.BooleanUtils" %>

<%@ page errorPage="/Error.jsp" %>

<%@ taglib uri="/WEB-INF/tld/eyewiki.tld" prefix="wiki" %>

<%! 
    public void jspInit()
    {
        wiki = WikiEngine.getInstance( getServletConfig() );
    }

    Logger log = Logger.getLogger("eyeWiki"); 
    WikiEngine wiki;
%><%

    WikiContext wikiContext = wiki.createContext( request, WikiContext.VIEW );
    String pagereq = wikiContext.getPage().getName();
    String vote    = request.getParameter("vote");

    NDC.push( wiki.getApplicationName()+":"+pagereq );
    
    if (log.isInfoEnabled()) {
        log.info("Vote '"+pagereq+"' from "+request.getRemoteAddr()+" by "+request.getRemoteUser() );
    }


    pageContext.setAttribute( WikiTagBase.ATTR_CONTEXT,
                              wikiContext,
                              PageContext.REQUEST_SCOPE );

    PluginManager pluginManager = wiki.getPluginManager();

    if (pluginManager != null)
    {
        VotePlugin plugin = (VotePlugin) pluginManager.findPlugin("VotePlugin");

        plugin.vote( wikiContext, BooleanUtils.toBoolean(vote) ? 1 : -1 );
    }

    NDC.pop();
    NDC.remove();

    response.sendRedirect( wiki.getBaseURL()+"Wiki.jsp?page=VoteOk" );
%>

