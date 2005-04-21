<%@ page import="org.apache.log4j.*" %>
<%@ page import="com.ecyrd.jspwiki.*" %>
<%@ page import="com.ecyrd.jspwiki.plugin.*" %>
<%@ page errorPage="/Error.jsp" %>
<%@ taglib uri="/WEB-INF/tld/jspwiki.tld" prefix="wiki" %>
<%! 
    public void jspInit()
    {
        wiki = WikiEngine.getInstance( getServletConfig() );
    }

    WikiEngine wiki;
%><%
    WikiContext wikiContext = wiki.createContext( request, WikiContext.EDIT );
    String pagereq = wikiContext.getPage().getName();

    NDC.push( wiki.getApplicationName()+":"+pagereq );
    
    String specialpage = wiki.getSpecialPageReference( pagereq );
    String newEntry = "PluginUnconfigured";

    if( specialpage != null )
    {
        // FIXME: Do Something Else
        newEntry = specialpage;
    }
    else
    {
        PluginManager pluginManager = wiki.getPluginManager();

        if (pluginManager != null)
        {
            WeblogEntryPlugin p = (WeblogEntryPlugin) pluginManager.findPlugin("WeblogEntryPlugin");
    
            if (p != null)
            {
                newEntry = wikiContext.getURL(WikiContext.EDIT, p.getNewEntryPage(pagereq ));
            }
        }
    }

    NDC.pop();
    NDC.remove();

    response.sendRedirect(newEntry);
%>

