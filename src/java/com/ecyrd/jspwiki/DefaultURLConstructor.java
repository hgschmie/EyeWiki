package com.ecyrd.jspwiki;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;

import com.ecyrd.jspwiki.util.TextUtil;

public class DefaultURLConstructor
    implements URLConstructor
{
    protected WikiEngine m_engine;
    private String m_viewURLPattern = "%uWiki.jsp?page=%n";

    /** Are URL styles relative or absolute? */
    protected boolean          m_useRelativeURLStyle = true;

    public void initialize( WikiEngine engine, 
                            Configuration conf)
    {
        m_engine = engine;

        m_useRelativeURLStyle = "relative".equals( conf.getString(
                                                           WikiProperties.PROP_REFSTYLE,
                                                           WikiProperties.PROP_REFSTYLE_DEFAULT));
    }

    protected final String doReplacement( String baseptrn, String name, boolean absolute )
    {
        String baseurl = "";

        if( absolute || !m_useRelativeURLStyle ) baseurl = m_engine.getBaseURL();

        baseptrn = StringUtils.replace( baseptrn, "%u", baseurl );
        baseptrn = StringUtils.replace( baseptrn, "%U", m_engine.getBaseURL() );
        baseptrn = StringUtils.replace( baseptrn, "%n", m_engine.encodeName(name) );

        return baseptrn;
    }

    /**
     *  Constructs the actual URL based on the context.
     */
    private String makeURL( String context,
                            String name,
                            boolean absolute )
    {
        if( context.equals(WikiContext.VIEW) )
        {
            if( name == null ) return makeURL("%uWiki.jsp","",absolute); // FIXME
            return doReplacement( m_viewURLPattern, name, absolute );
        }
        else if( context.equals(WikiContext.EDIT) )
        {
            return doReplacement( "%uEdit.jsp?page=%n", name, absolute );
        }
        else if( context.equals(WikiContext.ATTACH) )
        {
            return doReplacement( "%uattach/%n", name, absolute );
        }
        else if( context.equals(WikiContext.INFO) )
        {
            return doReplacement( "%uPageInfo.jsp?page=%n", name, absolute );
        }
        else if( context.equals(WikiContext.DIFF) )
        {
            return doReplacement( "%uDiff.jsp?page=%n", name, absolute );
        }
        else if( context.equals(WikiContext.NONE) )
        {
            return doReplacement( "%u%n", name, absolute );
        }
        else if( context.equals(WikiContext.UPLOAD) )
        {
            return doReplacement( "%uUpload.jsp?page=%n", name, absolute ); 
        }
        else if( context.equals(WikiContext.COMMENT) )
        {
            return doReplacement( "%uComment.jsp?page=%n", name, absolute ); 
        }
        else if( context.equals(WikiContext.ERROR) )
        {
            return doReplacement( "%uError.jsp", name, absolute );
        }
        throw new InternalWikiException("Requested unsupported context "+context);
    }

    /**
     *  Constructs the URL with a bunch of parameters.
     *  @param parameters If null or empty, no parameters are added.
     */
    public String makeURL( String context,
                           String name,
                           boolean absolute,
                           String parameters )
    {
        if( parameters != null && parameters.length() > 0 )
        {            
            if( context.equals(WikiContext.ATTACH) )
            {
                parameters = "?"+parameters;
            }
            parameters = "&amp;"+parameters;
        }
        else
        {
            parameters = "";
        }
        return makeURL( context, name, absolute )+parameters;
    }

    /**
     *  Should parse the "page" parameter from the actual
     *  request.
     */
    public String parsePage( String context,
                             HttpServletRequest request,
                             String encoding )
        throws UnsupportedEncodingException
    {
        String pagereq = m_engine.safeGetParameter( request, "page" );

        if( context.equals(WikiContext.ATTACH) )
        {
            pagereq = parsePageFromURL( request, encoding );

            if( pagereq != null ) pagereq = TextUtil.urlDecodeUTF8(pagereq);
        }

        return pagereq;
    }

    /**
     *  Takes the name of the page from the request URI.
     *  The initial slash is also removed.  If there is no page,
     *  returns null.
     */
    public static String parsePageFromURL( HttpServletRequest request,
                                           String encoding )
        throws UnsupportedEncodingException
    {
        String name = request.getPathInfo();

        if( name == null || name.length() <= 1 )
        {
            return null;
        }
        else if( name.charAt(0) == '/' )
        {
            name = name.substring(1);
        }
       
        name = new String(name.getBytes("ISO-8859-1"),
                          encoding );

        return name;
    }

}
