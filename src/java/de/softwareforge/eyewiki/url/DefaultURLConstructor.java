package de.softwareforge.eyewiki.url;

/*
 * ========================================================================
 *
 * eyeWiki - a WikiWiki clone written in Java
 *
 * ========================================================================
 *
 * Copyright (C) 2005 Henning Schmiedehausen <henning@software-forge.com>
 *
 * based on
 *
 * JSPWiki - a JSP-based WikiWiki clone.
 * Copyright (C) 2002-2005 Janne Jalkanen (Janne.Jalkanen@iki.fi)
 *
 * ========================================================================
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * ========================================================================
 */

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;

import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.WikiProperties;
import de.softwareforge.eyewiki.exception.InternalWikiException;
import de.softwareforge.eyewiki.util.TextUtil;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class DefaultURLConstructor
        implements URLConstructor
{
    /** DOCUMENT ME! */
    protected WikiEngine engine;

    /** Are URL styles relative or absolute? */
    protected boolean m_useRelativeURLStyle = true;

    /**
     * Creates a new DefaultURLConstructor object.
     *
     * @param engine DOCUMENT ME!
     * @param conf DOCUMENT ME!
     */
    public DefaultURLConstructor(final WikiEngine engine, final Configuration conf)
    {
        this.engine = engine;

        m_useRelativeURLStyle =
            "relative".equals(conf.getString(WikiProperties.PROP_REFSTYLE, WikiProperties.PROP_REFSTYLE_DEFAULT));
    }

    /**
     * DOCUMENT ME!
     *
     * @param baseptrn DOCUMENT ME!
     * @param name DOCUMENT ME!
     * @param absolute DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected final String doReplacement(String baseptrn, String name, boolean absolute)
    {
        String baseurl = "";

        if (absolute || !m_useRelativeURLStyle)
        {
            baseurl = engine.getBaseURL();
        }

        baseptrn = StringUtils.replace(baseptrn, "%u", baseurl);
        baseptrn = StringUtils.replace(baseptrn, "%U", engine.getBaseURL());
        baseptrn = StringUtils.replace(baseptrn, "%n", engine.encodeName(name));

        return baseptrn;
    }

    /**
     * Returns the pattern used for each URL style.
     *
     * @param context
     * @param name
     *
     * @return A pattern for replacement.
     *
     * @throws InternalWikiException DOCUMENT ME!
     */
    public static String getURLPattern(String context, String name)
    {
        if (context.equals(WikiContext.VIEW))
        {
            if (name == null)
            {
                return "%uWiki.jsp"; // FIXME
            }

            return "%uWiki.jsp?page=%n";
        }
        else if (context.equals(WikiContext.EDIT))
        {
            return "%uEdit.jsp?page=%n";
        }
        else if (context.equals(WikiContext.ATTACH))
        {
            return "%uattach/%n";
        }
        else if (context.equals(WikiContext.INFO))
        {
            return "%uPageInfo.jsp?page=%n";
        }
        else if (context.equals(WikiContext.DIFF))
        {
            return "%uDiff.jsp?page=%n";
        }
        else if (context.equals(WikiContext.NONE))
        {
            return "%u%n";
        }
        else if (context.equals(WikiContext.UPLOAD))
        {
            return "%uUpload.jsp?page=%n";
        }
        else if (context.equals(WikiContext.COMMENT))
        {
            return "%uComment.jsp?page=%n";
        }
        else if (context.equals(WikiContext.LOGIN))
        {
            return "%uLogin.jsp?page=%n";
        }

        else if (context.equals(WikiContext.ERROR))
        {
            return "%uError.jsp";
        }

        throw new InternalWikiException("Requested unsupported context " + context);
    }

    /**
     * Constructs the actual URL based on the context.
     *
     * @param context DOCUMENT ME!
     * @param name DOCUMENT ME!
     * @param absolute DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private String makeURL(String context, String name, boolean absolute)
    {
        if (context.equals(WikiContext.VIEW))
        {
            if (name == null)
            {
                return makeURL("%uWiki.jsp", "", absolute); // FIXME
            }
        }

        return doReplacement(getURLPattern(context, name), name, absolute);
    }

    /**
     * Constructs the URL with a bunch of parameters.
     *
     * @param context DOCUMENT ME!
     * @param name DOCUMENT ME!
     * @param absolute DOCUMENT ME!
     * @param parameters If null or empty, no parameters are added.
     *
     * @return DOCUMENT ME!
     */
    public String makeURL(String context, String name, boolean absolute, String parameters)
    {
        if (StringUtils.isNotEmpty(parameters))
        {
            if (context.equals(WikiContext.ATTACH))
            {
                parameters = "?" + parameters;
            }
            else
            {
                parameters = "&amp;" + parameters;
            }
        }
        else
        {
            parameters = "";
        }

        return makeURL(context, name, absolute) + parameters;
    }

    /**
     * Should parse the "page" parameter from the actual request.
     *
     * @param context DOCUMENT ME!
     * @param request DOCUMENT ME!
     * @param encoding DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws UnsupportedEncodingException DOCUMENT ME!
     */
    public String parsePage(String context, HttpServletRequest request, String encoding)
            throws UnsupportedEncodingException
    {
        String pagereq = engine.safeGetParameter(request, "page");

        if (context.equals(WikiContext.ATTACH))
        {
            pagereq = parsePageFromURL(request, encoding);
        }

        return pagereq;
    }

    /**
     * Takes the name of the page from the request URI. The initial slash is also removed.  If there is no page, returns null.
     *
     * @param request DOCUMENT ME!
     * @param encoding DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws UnsupportedEncodingException DOCUMENT ME!
     */
    public static String parsePageFromURL(HttpServletRequest request, String encoding)
            throws UnsupportedEncodingException
    {
        String name = request.getPathInfo();

        if ((name == null) || (name.length() <= 1))
        {
            return null;
        }
        else if (name.charAt(0) == '/')
        {
            name = name.substring(1);
        }

        //
        //  This is required, because by default all URLs are handled
        //  as Latin1, even if they are really UTF-8.
        //
        return TextUtil.urlDecode(name, encoding);
    }

    /**
     * This method is not needed for the DefaultURLConstructor.
     *
     * @param request DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @since
     */
    public String getForwardPage(HttpServletRequest request)
    {
        return request.getPathInfo();
    }
}
