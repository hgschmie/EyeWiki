package com.ecyrd.jspwiki.url;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;


/**
 * An utility class for creating URLs for different purposes.
 */
public interface URLConstructor
{
    /**
     * Constructs the URL with a bunch of parameters.
     *
     * @param context DOCUMENT ME!
     * @param name DOCUMENT ME!
     * @param absolute DOCUMENT ME!
     * @param parameters DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    String makeURL(String context, String name, boolean absolute, String parameters);

    /**
     * Should parse the "page" parameter from the actual request.
     *
     * @param context DOCUMENT ME!
     * @param request DOCUMENT ME!
     * @param encoding DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    String parsePage(String context, HttpServletRequest request, String encoding)
            throws IOException;

    /**
     *  Returns information which JSP page should continue handling
     *  this type of request.
     *  
     * @param context
     * @return "Wiki.jsp", "PageInfo.jsp", etc.  Just return the name,
     *         JSPWiki will figure out the page.
     */
    String getForwardPage(HttpServletRequest request);
}
