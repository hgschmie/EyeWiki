<%@ page isErrorPage="true" %>
<%@ page import="org.apache.log4j.*" %>
<%@ page import="com.ecyrd.jspwiki.*" %>
<%@ page import="com.ecyrd.jspwiki.util.*" %>
<%@ page import="com.ecyrd.jspwiki.tags.WikiTagBase" %>
<%@ taglib uri="/WEB-INF/tld/jspwiki.tld" prefix="wiki" %>
<%! 
    public void jspInit()
    {
        wiki = WikiEngine.getInstance( getServletConfig() );
    }
    Logger log = Logger.getLogger("JSPWiki"); 
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

   <h3 class="errorpage">JSPWiki has detected an error</h3>

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

