/*
   JSPWiki - a JSP-based WikiWiki clone.

   Copyright (C) 2001-2003 Janne Jalkanen (Janne.Jalkanen@iki.fi)

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
package com.ecyrd.jspwiki.filters;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;

import com.ecyrd.jspwiki.WikiContext;
import com.ecyrd.jspwiki.WikiEngine;
import com.ecyrd.jspwiki.WikiException;
import com.ecyrd.jspwiki.WikiProperties;
import com.ecyrd.jspwiki.util.ClassUtil;
import com.ecyrd.jspwiki.util.PriorityList;

import org.xml.sax.AttributeList;
import org.xml.sax.HandlerBase;
import org.xml.sax.InputSource;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;

import uk.co.wilson.xml.MinML;


/**
 * Manages the page filters.  Page filters are components that can be executed at certain places:
 * 
 * <ul>
 * <li>
 * Before the page is translated into HTML.
 * </li>
 * <li>
 * After the page has been translated into HTML.
 * </li>
 * <li>
 * Before the page is saved.
 * </li>
 * <li>
 * After the page has been saved.
 * </li>
 * </ul>
 * 
 * Using page filters allows you to modify the page data on-the-fly, and do things like adding your
 * own custom WikiMarkup.
 * 
 * <p>
 * The initial page filter configuration is kept in a file called "filters.xml".  The format is
 * really very simple:
 * <pre>
 *  <?xml version="1.0"?>
 *   <pagefilters>
 *     <filter>
 *      <class>com.ecyrd.jspwiki.filters.ProfanityFilter</class>
 *    </filter>
 *     <filter>
 *      <class>com.ecyrd.jspwiki.filters.TestFilter</class>
 *       <param>
 *        <name>foobar</name>
 *        <value>Zippadippadai</value>
 *      </param>
 *       <param>
 *        <name>blatblaa</name>
 *        <value>5</value>
 *      </param>
 *     </filter>
 *  </pagefilters>
 *  </pre>
 * The &lt;filter> -sections define the filters.  For more information, please see the
 * PageFilterConfiguration page in the JSPWiki distribution.
 * </p>
 *
 * @author Janne Jalkanen
 */
public class FilterManager
        extends HandlerBase
        implements WikiProperties
{
    /** DOCUMENT ME! */
    private static final Logger log = Logger.getLogger(WikiEngine.class);

    /** DOCUMENT ME! */
    public static final String DEFAULT_XMLFILE = "/filters.xml";

    /** DOCUMENT ME! */
    private PriorityList m_pageFilters = new PriorityList();

    /*
     *  The XML parsing part.  We use a simple SAX1 parser; we do not at this
     *  point need anything more complicated.
     */

    /** DOCUMENT ME! */
    private String filterName = null;

    /** DOCUMENT ME! */
    private Properties filterProperties = new Properties();

    /** DOCUMENT ME! */
    private boolean parsingFilters = false;

    /** DOCUMENT ME! */
    private String lastReadCharacters = null;

    /** DOCUMENT ME! */
    private String lastReadParamName = null;

    /** DOCUMENT ME! */
    private String lastReadParamValue = null;

    /**
     * Creates a new FilterManager object.
     *
     * @param engine DOCUMENT ME!
     * @param conf DOCUMENT ME!
     *
     * @throws WikiException DOCUMENT ME!
     */
    public FilterManager(WikiEngine engine, Configuration conf)
            throws WikiException
    {
        initialize(engine, conf);
    }

    /**
     * Adds a page filter to the queue.  The priority defines in which order the page filters are
     * run, the highest priority filters go in the queue first.
     * 
     * <p>
     * In case two filters have the same priority, their execution order is the insertion order.
     * </p>
     *
     * @param f PageFilter to add
     * @param priority The priority in which position to add it in.
     *
     * @throws IllegalArgumentException If the PageFilter is null or invalid.
     *
     * @since 2.1.44.
     */
    public void addPageFilter(PageFilter f, int priority)
    {
        if (f == null)
        {
            throw new IllegalArgumentException(
                "Attempt to provide a null filter - this should never happen.  Please check your configuration (or if you're a developer, check your own code.)");
        }

        m_pageFilters.add(f, priority);
    }

    private void initPageFilter(String className, Properties props)
    {
        try
        {
            int priority = 0; // FIXME: Currently fixed.

            Class cl = ClassUtil.findClass("com.ecyrd.jspwiki.filters", className);

            PageFilter filter = (PageFilter) cl.newInstance();

            filter.initialize(props);

            addPageFilter(filter, priority);

            if (log.isInfoEnabled())
            {
                log.info("Added page filter " + cl.getName() + " with priority " + priority);
            }
        }
        catch (ClassNotFoundException e)
        {
            log.error("Unable to find the filter class: " + className);
        }
        catch (InstantiationException e)
        {
            log.error("Cannot create filter class: " + className);
        }
        catch (IllegalAccessException e)
        {
            log.error("You are not allowed to access class: " + className);
        }
        catch (ClassCastException e)
        {
            log.error("Suggested class is not a PageFilter: " + className);
        }
        catch (FilterException e)
        {
            log.error("Filter " + className + " failed to initialize itself.", e);
        }
    }

    /**
     * Initializes the filters from an XML file.
     *
     * @param engine DOCUMENT ME!
     * @param conf DOCUMENT ME!
     *
     * @throws WikiException DOCUMENT ME!
     */
    public void initialize(WikiEngine engine, Configuration conf)
            throws WikiException
    {
        InputStream xmlStream = null;
        String xmlFile = conf.getString(PROP_FILTERXML, PROP_FILTERXML_DEFAULT);

        try
        {
            if (log.isDebugEnabled())
            {
                log.debug("Attempting to load property file " + xmlFile);
            }

            xmlStream = new FileInputStream(new File(xmlFile));

            if (xmlStream == null)
            {
                if (log.isInfoEnabled())
                {
                    log.info(
                        "Cannot find property file for filters (this is okay, expected to find it as: '"
                        + xmlFile + "')");
                }

                return;
            }

            Parser parser = new MinML(); // FIXME: Should be settable

            parser.setDocumentHandler(this);
            parser.setErrorHandler(this);

            parser.parse(new InputSource(xmlStream));
        }
        catch (FileNotFoundException fnf)
        {
            if (log.isInfoEnabled())
            {
                log.info("Could not open " + xmlFile + ". No filters are defined.");
            }
        }
        catch (IOException e)
        {
            log.error("Unable to read property file", e);
        }
        catch (SAXException e)
        {
            log.error("Problem in the XML file", e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param name DOCUMENT ME!
     * @param atts DOCUMENT ME!
     */
    public void startElement(String name, AttributeList atts)
    {
        if ("pagefilters".equals(name))
        {
            parsingFilters = true;
        }
        else if (parsingFilters)
        {
            if ("filter".equals(name))
            {
                filterName = null;
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param name DOCUMENT ME!
     */
    public void endElement(String name)
    {
        if ("pagefilters".equals(name))
        {
            parsingFilters = false;
        }
        else if (parsingFilters)
        {
            if ("filter".equals(name))
            {
                initPageFilter(filterName, filterProperties);
            }
            else if ("class".equals(name))
            {
                filterName = lastReadCharacters;
            }
            else if ("param".equals(name))
            {
                filterProperties.setProperty(lastReadParamName, lastReadParamValue);
            }
            else if ("name".equals(name))
            {
                lastReadParamName = lastReadCharacters;
            }
            else if ("value".equals(name))
            {
                lastReadParamValue = lastReadCharacters;
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param ch DOCUMENT ME!
     * @param start DOCUMENT ME!
     * @param length DOCUMENT ME!
     */
    public void characters(char [] ch, int start, int length)
    {
        lastReadCharacters = new String(ch, start, length);
    }

    /**
     * Does the filtering before a translation.
     *
     * @param context DOCUMENT ME!
     * @param pageData DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws FilterException DOCUMENT ME!
     */
    public String doPreTranslateFiltering(WikiContext context, String pageData)
            throws FilterException
    {
        for (Iterator i = m_pageFilters.iterator(); i.hasNext();)
        {
            PageFilter f = (PageFilter) i.next();

            pageData = f.preTranslate(context, pageData);
        }

        return pageData;
    }

    /**
     * Does the filtering after HTML translation.
     *
     * @param context DOCUMENT ME!
     * @param pageData DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws FilterException DOCUMENT ME!
     */
    public String doPostTranslateFiltering(WikiContext context, String pageData)
            throws FilterException
    {
        for (Iterator i = m_pageFilters.iterator(); i.hasNext();)
        {
            PageFilter f = (PageFilter) i.next();

            pageData = f.postTranslate(context, pageData);
        }

        return pageData;
    }

    /**
     * Does the filtering before a save to the page repository.
     *
     * @param context DOCUMENT ME!
     * @param pageData DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws FilterException DOCUMENT ME!
     */
    public String doPreSaveFiltering(WikiContext context, String pageData)
            throws FilterException
    {
        for (Iterator i = m_pageFilters.iterator(); i.hasNext();)
        {
            PageFilter f = (PageFilter) i.next();

            pageData = f.preSave(context, pageData);
        }

        return pageData;
    }

    /**
     * Does the page filtering after the page has been saved.
     *
     * @param context DOCUMENT ME!
     * @param pageData DOCUMENT ME!
     *
     * @throws FilterException DOCUMENT ME!
     */
    public void doPostSaveFiltering(WikiContext context, String pageData)
            throws FilterException
    {
        for (Iterator i = m_pageFilters.iterator(); i.hasNext();)
        {
            PageFilter f = (PageFilter) i.next();

            f.postSave(context, pageData);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public List getFilterList()
    {
        return m_pageFilters;
    }
}
