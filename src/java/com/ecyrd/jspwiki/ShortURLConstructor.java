/*
   JSPWiki - a JSP-based WikiWiki clone.

   Copyright (C) 2001-2005 Janne Jalkanen (Janne.Jalkanen@iki.fi)

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
*/
package com.ecyrd.jspwiki;

import java.io.UnsupportedEncodingException;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;

import com.ecyrd.jspwiki.util.TextUtil;


/**
 * Provides a way to do short URLs of the form /wiki/PageName.
 *
 * @author jalkanen
 *
 * @since 2.2
 */
public class ShortURLConstructor
    extends DefaultURLConstructor
{
    /** DOCUMENT ME! */
    static Logger log = Logger.getLogger(ShortURLConstructor.class);

    /** DOCUMENT ME! */
    private String m_urlPrefix = "";

    /**
     * DOCUMENT ME!
     *
     * @param engine DOCUMENT ME!
     * @param conf DOCUMENT ME!
     */
    public void initialize(WikiEngine engine, Configuration conf)
    {
        super.initialize(engine, conf);

        m_urlPrefix = conf.getString(WikiProperties.PROP_SHORTURL_PREFIX, null);

        if (m_urlPrefix == null)
        {
            String baseurl = engine.getBaseURL();

            try
            {
                URL url = new URL(baseurl);

                String path = url.getPath();

                m_urlPrefix = path + "wiki/";

                if (log.isInfoEnabled())
                {
                    log.info(
                        "Short URL prefix path=" + m_urlPrefix + " (You can use "
                        + WikiProperties.PROP_SHORTURL_PREFIX + " to override this)");
                }
            }
            catch (MalformedURLException e)
            {
                log.error("Malformed base URL!");
            }
        }
    }

    /**
     * Constructs the actual URL based on the context.
     *
     * @param context DOCUMENT ME!
     * @param name DOCUMENT ME!
     * @param absolute DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws InternalWikiException DOCUMENT ME!
     */
    private String makeURL(String context, String name, boolean absolute)
    {
        String viewurl = m_urlPrefix + "%n";

        if (absolute)
        {
            viewurl = "%uwiki/%n";
        }

        if (context.equals(WikiContext.VIEW))
        {
            if (name == null)
            {
                return makeURL("%u", "", absolute); // FIXME
            }

            return doReplacement(viewurl, name, absolute);
        }
        else if (context.equals(WikiContext.EDIT))
        {
            return doReplacement(viewurl + "?do=Edit", name, absolute);
        }
        else if (context.equals(WikiContext.ATTACH))
        {
            return doReplacement("%Uattach/%n", name, absolute);
        }
        else if (context.equals(WikiContext.INFO))
        {
            return doReplacement(viewurl + "?do=PageInfo", name, absolute);
        }
        else if (context.equals(WikiContext.DIFF))
        {
            return doReplacement(viewurl + "?do=Diff", name, absolute);
        }
        else if (context.equals(WikiContext.NONE))
        {
            return doReplacement("%U%n", name, absolute);
        }
        else if (context.equals(WikiContext.UPLOAD))
        {
            return doReplacement(viewurl + "?do=Upload", name, absolute);
        }
        else if (context.equals(WikiContext.COMMENT))
        {
            return doReplacement(viewurl + "?do=Comment", name, absolute);
        }
        else if (context.equals(WikiContext.ERROR))
        {
            return doReplacement("%UError.jsp", name, absolute);
        }

        throw new InternalWikiException("Requested unsupported context " + context);
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
        if ((parameters != null) && (parameters.length() > 0))
        {
            if (context.equals(WikiContext.ATTACH) || context.equals(WikiContext.VIEW))
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
        String pagereq = m_engine.safeGetParameter(request, "page");

        if (pagereq == null)
        {
            pagereq = parsePageFromURL(request, encoding);

            if (pagereq != null)
            {
                pagereq = TextUtil.urlDecodeUTF8(pagereq);
            }
        }

        return pagereq;
    }
}
