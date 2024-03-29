package de.softwareforge.eyewiki.manager;

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

import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.jsp.PageContext;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.opensymphony.oscache.base.Cache;
import com.opensymphony.oscache.base.NeedsRefreshException;

import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiEngine;

/**
 * This class takes care of managing eyeWiki templates.
 *
 * @author Janne Jalkanen
 *
 * @since 2.1.62
 */
public class TemplateManager
{
    /** The default directory for the properties. */
    public static final String DIRECTORY = "templates";

    /** DOCUMENT ME! */
    public static final String DEFAULT_TEMPLATE = "default";

    /** Name of the file that contains the properties. */
    public static final String PROPERTYFILE = "template.properties";

    /** DOCUMENT ME! */
    private static final Logger log = Logger.getLogger(TemplateManager.class);

    /** DOCUMENT ME! */
    private WikiEngine m_engine;

    /** DOCUMENT ME! */
    private Cache m_propertyCache;

    /**
     * Creates a new TemplateManager object.
     *
     * @param engine DOCUMENT ME!
     * @param conf DOCUMENT ME!
     */
    public TemplateManager(WikiEngine engine, Configuration conf)
    {
        m_engine = engine;

        //
        //  Uses the unlimited cache.
        //
        m_propertyCache = new Cache(true, false, false);
    }

    /**
     * Check the existence of a template.
     *
     * @param templateName DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */

    // FIXME: Does not work yet
    public boolean templateExists(String templateName)
    {
        ServletContext context = m_engine.getServletContext();

        InputStream in = context.getResourceAsStream(getPath(templateName) + "ViewTemplate.jsp");

        if (in != null)
        {
            IOUtils.closeQuietly(in);

            return true;
        }

        return false;
    }

    /**
     * An utility method for finding a JSP page.  It searches only under either current context or by the absolute name.
     *
     * @param pageContext DOCUMENT ME!
     * @param name DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String findJSP(PageContext pageContext, String name)
    {
        ServletContext sContext = pageContext.getServletContext();

        InputStream is = sContext.getResourceAsStream(name);

        if (is == null)
        {
            String defname = makeFullJSPName(DEFAULT_TEMPLATE, removeTemplatePart(name));
            is = sContext.getResourceAsStream(defname);

            if (is != null)
            {
                name = defname;
            }
            else
            {
                name = null;
            }
        }

        IOUtils.closeQuietly(is);

        return name;
    }

    /**
     * Removes the template part of a name.
     *
     * @param name DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private String removeTemplatePart(final String name)
    {
        int idx = name.indexOf('/');

        if (idx != -1)
        {
            idx = name.indexOf('/', idx); // Find second "/"

            if (idx != -1)
            {
                return name.substring(idx + 1);
            }
        }

        return name;
    }

    private String makeFullJSPName(final String template, final String name)
    {
        StringBuffer sb = new StringBuffer("/").append(DIRECTORY).append("/").append(template).append("/").append(name);

        return sb.toString();
    }

    /**
     * Attempts to locate a JSP page under the given template.  If that template does not exist, or the page does not exist under
     * that template, will attempt to locate a similarly named file under the default template.
     *
     * @param pageContext DOCUMENT ME!
     * @param template DOCUMENT ME!
     * @param name DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String findJSP(PageContext pageContext, String template, String name)
    {
        ServletContext sContext = pageContext.getServletContext();

        if (name.charAt(0) == '/')
        {
            // This is already a full path
            return findJSP(pageContext, name);
        }

        String fullname = makeFullJSPName(template, name);
        InputStream is = sContext.getResourceAsStream(fullname);

        if (is == null)
        {
            String defname = makeFullJSPName(DEFAULT_TEMPLATE, name);
            is = sContext.getResourceAsStream(defname);

            if (is != null)
            {
                fullname = defname;
            }
            else
            {
                fullname = null;
            }
        }

        IOUtils.closeQuietly(is);

        return fullname;
    }

    /**
     * Returns a property, as defined in the template.  The evaluation is lazy, i.e. the properties are not loaded until the
     * template is actually used for the first time.
     *
     * @param context DOCUMENT ME!
     * @param key DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getTemplateProperty(WikiContext context, String key)
    {
        String template = context.getTemplate();

        try
        {
            Properties props = (Properties) m_propertyCache.getFromCache(template, -1);

            if (props == null)
            {
                try
                {
                    props = getTemplateProperties(template);

                    m_propertyCache.putInCache(template, props);
                }
                catch (IOException e)
                {
                    log.warn("IO Exception while reading template properties", e);

                    return null;
                }
            }

            return props.getProperty(key);
        }
        catch (NeedsRefreshException ex)
        {
            // Avoid deadlock
            m_propertyCache.cancelUpdate(template);

            // FIXME
            return null;
        }
    }

    private static String getPath(final String template)
    {
        StringBuffer sb = new StringBuffer("/").append(DIRECTORY).append("/").append(template).append("/");

        return sb.toString();
    }

    /**
     * Always returns a valid property map.
     *
     * @param templateName DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    private Properties getTemplateProperties(String templateName)
            throws IOException
    {
        Properties p = new Properties();

        ServletContext context = m_engine.getServletContext();

        InputStream propertyStream = context.getResourceAsStream(getPath(templateName) + PROPERTYFILE);

        if (propertyStream != null)
        {
            p.load(propertyStream);

            propertyStream.close();
        }
        else
        {
            if (log.isDebugEnabled())
            {
                log.debug("Template '" + templateName + "' does not have a propertyfile '" + PROPERTYFILE + "'.");
            }
        }

        return p;
    }
}
