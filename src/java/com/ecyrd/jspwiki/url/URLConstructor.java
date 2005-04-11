package com.ecyrd.jspwiki.url;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.configuration.Configuration;

import com.ecyrd.jspwiki.WikiEngine;


/**
 * An utility class for creating URLs for different purposes.
 */
public interface URLConstructor
{
    /**
     * Initializes.  Note that the engine is not fully initialized at this point, so don't do
     * anything fancy here - use lazy init, if you have to.
     *
     * @param engine DOCUMENT ME!
     * @param conf DOCUMENT ME!
     */
    void initialize(WikiEngine engine, Configuration conf);

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
}
