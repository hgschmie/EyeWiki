package de.softwareforge.eyewiki.plugin;

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

import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.oro.text.GlobCompiler;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Matcher;

import de.softwareforge.eyewiki.WikiContext;

/**
 * Denounces a link by removing it from any search engine.  The bots are listed in com/ecyrd/eyewiki/plugin/denounce.properties.
 *
 * @author Janne Jalkanen
 *
 * @since 2.1.40.
 */
public class Denounce
        implements WikiPlugin
{
    /** DOCUMENT ME! */
    private static Logger log = Logger.getLogger(Denounce.class);

    /** DOCUMENT ME! */
    public static final String PARAM_LINK = "link";

    /** DOCUMENT ME! */
    public static final String PARAM_TEXT = "text";

    /** DOCUMENT ME! */
    public static final String PROPERTYFILE = "com/ecyrd/eyewiki/plugin/denounce.properties";

    /** DOCUMENT ME! */
    public static final String PROP_AGENTPATTERN = "denounce.agentpattern.";

    /** DOCUMENT ME! */
    public static final String PROP_HOSTPATTERN = "denounce.hostpattern.";

    /** DOCUMENT ME! */
    public static final String PROP_REFERERPATTERN = "denounce.refererpattern.";

    /** DOCUMENT ME! */
    public static final String PROP_DENOUNCETEXT = "denounce.denouncetext";

    /** DOCUMENT ME! */
    private static ArrayList c_refererPatterns = new ArrayList();

    /** DOCUMENT ME! */
    private static ArrayList c_agentPatterns = new ArrayList();

    /** DOCUMENT ME! */
    private static ArrayList c_hostPatterns = new ArrayList();

    /** DOCUMENT ME! */
    private static String c_denounceText = "";

    /**
     * Prepares the different patterns for later use.  Compiling is (probably) expensive, so we do it statically at class load
     * time.
     */
    static
    {
        try
        {
            PatternCompiler compiler = new GlobCompiler();
            ClassLoader loader = Denounce.class.getClassLoader();

            InputStream in = loader.getResourceAsStream(PROPERTYFILE);

            if (in == null)
            {
                throw new IOException("No property file found! (Check the installation, it should be there.)");
            }

            Properties props = new Properties();
            props.load(in);

            c_denounceText = props.getProperty(PROP_DENOUNCETEXT, c_denounceText);

            for (Enumeration e = props.propertyNames(); e.hasMoreElements();)
            {
                String name = (String) e.nextElement();

                try
                {
                    if (name.startsWith(PROP_REFERERPATTERN))
                    {
                        c_refererPatterns.add(compiler.compile(props.getProperty(name)));
                    }
                    else if (name.startsWith(PROP_AGENTPATTERN))
                    {
                        c_agentPatterns.add(compiler.compile(props.getProperty(name)));
                    }
                    else if (name.startsWith(PROP_HOSTPATTERN))
                    {
                        c_hostPatterns.add(compiler.compile(props.getProperty(name)));
                    }
                }
                catch (MalformedPatternException ex)
                {
                    log.error("Malformed URL pattern in " + PROPERTYFILE + ": " + props.getProperty(name), ex);
                }
            }

            if (log.isDebugEnabled())
            {
                log.debug("Added " + c_refererPatterns.size() + c_agentPatterns.size() + c_hostPatterns.size()
                    + " crawlers to denounce list.");
            }
        }
        catch (IOException e)
        {
            log.error("Unable to load URL patterns from " + PROPERTYFILE, e);
        }
        catch (Exception e)
        {
            log.error("Unable to initialize Denounce plugin", e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param context DOCUMENT ME!
     * @param params DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws PluginException DOCUMENT ME!
     */
    public String execute(WikiContext context, Map params)
            throws PluginException
    {
        String link = (String) params.get(PARAM_LINK);
        String text = (String) params.get(PARAM_TEXT);
        boolean linkAllowed = true;

        if (link == null)
        {
            throw new PluginException("Denounce: No parameter " + PARAM_LINK + " defined!");
        }

        HttpServletRequest request = context.getHttpRequest();

        if (request != null)
        {
            linkAllowed = !matchHeaders(request);
        }

        if (text == null)
        {
            text = link;
        }

        if (linkAllowed)
        {
            // FIXME: Should really call TranslatorReader
            return "<a href=\"" + link + "\">" + text + "</a>";
        }

        return c_denounceText;
    }

    /**
     * Returns true, if the path is found among the referers.
     *
     * @param list DOCUMENT ME!
     * @param path DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private boolean matchPattern(List list, String path)
    {
        PatternMatcher matcher = new Perl5Matcher();

        for (Iterator i = list.iterator(); i.hasNext();)
        {
            if (matcher.matches(path, (Pattern) i.next()))
            {
                return true;
            }
        }

        return false;
    }

    // FIXME: Should really return immediately when a match is found.
    private boolean matchHeaders(HttpServletRequest request)
    {
        //
        //  User Agent
        //
        String userAgent = request.getHeader("User-Agent");

        if ((userAgent != null) && matchPattern(c_agentPatterns, userAgent))
        {
            if (log.isDebugEnabled())
            {
                log.debug("Matched user agent " + userAgent + " for denounce.");
            }

            return true;
        }

        //
        //  Referrer header
        //
        String refererPath = request.getHeader("Referer");

        if ((refererPath != null) && matchPattern(c_refererPatterns, refererPath))
        {
            if (log.isDebugEnabled())
            {
                log.debug("Matched referer " + refererPath + " for denounce.");
            }

            return true;
        }

        //
        //  Host
        //
        String host = request.getRemoteHost();

        if ((host != null) && matchPattern(c_hostPatterns, host))
        {
            if (log.isDebugEnabled())
            {
                log.debug("Matched host " + host + " for denounce.");
            }

            return true;
        }

        return false;
    }
}
