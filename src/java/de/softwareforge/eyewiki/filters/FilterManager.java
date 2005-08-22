package de.softwareforge.eyewiki.filters;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.WikiProperties;
import de.softwareforge.eyewiki.util.PriorityList;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.Startable;
import org.picocontainer.defaults.ConstantParameter;
import org.picocontainer.defaults.DefaultPicoContainer;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

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
 * Using page filters allows you to modify the page data on-the-fly, and do things like adding your own custom WikiMarkup.
 *
 * <p>
 * The initial page filter configuration is kept in a file called "filters.xml".  The format is really very simple:
 * <pre>
 *  <?xml version="1.0"?>
 *   <pagefilters>
 *     <filter>
 *      <class>de.softwareforge.eyewiki.filters.ProfanityFilter</class>
 *    </filter>
 *     <filter>
 *      <class>de.softwareforge.eyewiki.filters.TestFilter</class>
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
 * The &lt;filter> -sections define the filters.  For more information, please see the PageFilterConfiguration page in the eyeWiki
 * distribution.
 * </p>
 *
 * @author Janne Jalkanen
 */
public class FilterManager
        extends DefaultHandler
        implements WikiProperties, Startable
{
    /** DOCUMENT ME! */
    private static final Logger log = Logger.getLogger(FilterManager.class);

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
    private Configuration filterConf = null;

    /** DOCUMENT ME! */
    private boolean parsingFilters = false;

    /** DOCUMENT ME! */
    private StringBuffer lastReadCharacters = null;

    /** DOCUMENT ME! */
    private String lastReadParamName = null;

    /** DOCUMENT ME! */
    private String lastReadParamValue = null;

    /** The SAX Parser Factory */
    private static SAXParserFactory saxFactory = null;

    /** Is the manager started? */
    private boolean started = false;

    /** The Container to manage the Filters */
    private final MutablePicoContainer filterContainer;

    static
    {
        saxFactory = SAXParserFactory.newInstance();
        saxFactory.setValidating(false);
        saxFactory.setNamespaceAware(false);
    }

    /**
     * Creates a new FilterManager object.
     *
     * @param engine DOCUMENT ME!
     * @param conf DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public FilterManager(WikiEngine engine, Configuration conf)
            throws Exception
    {
        filterContainer = new DefaultPicoContainer(engine.getComponentContainer());

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
                    log.info("Could not load " + xmlFile + ", no filters are configured.");
                }

                return;
            }

            SAXParser parser = saxFactory.newSAXParser();
            parser.parse(new InputSource(xmlStream), this);
        }
        catch (FileNotFoundException fnf)
        {
            if (log.isInfoEnabled())
            {
                log.info("Could not open " + xmlFile + ". No filters are configured.");
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    public synchronized void start()
    {
        // If our filters are good, they implement the regular life cycle...
        filterContainer.start();

        for (Iterator it = filterContainer.getComponentInstances().iterator(); it.hasNext();)
        {
            PageFilter pageFilter = (PageFilter) it.next();

            addPageFilter(pageFilter);

            if (log.isInfoEnabled())
            {
                log.info("Added page filter " + pageFilter.getClass().getName() + " with priority " + pageFilter.getPriority());
            }
        }

        setStarted(true);
    }

    /**
     * DOCUMENT ME!
     */
    public synchronized void stop()
    {
        setStarted(false);
    }

    /**
     * DOCUMENT ME!
     *
     * @param started DOCUMENT ME!
     */
    protected void setStarted(final boolean started)
    {
        this.started = started;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isStarted()
    {
        return started;
    }

    /**
     * Adds a page filter to the queue.  The priority defines in which order the page filters are run, the highest priority filters
     * go in the queue first.
     *
     * <p>
     * In case two filters have the same priority, their execution order is the insertion order.
     * </p>
     *
     * @param f PageFilter to add
     *
     * @throws IllegalArgumentException If the PageFilter is null or invalid.
     *
     * @since 2.1.44.
     */
    public void addPageFilter(PageFilter f)
    {
        if (f == null)
        {
            throw new IllegalArgumentException("Attempt to provide a null filter - this should never happen. "
                + "Please check your configuration (or if you're a developer, check your own code.)");
        }

        m_pageFilters.add(f, f.getPriority());
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
     * Returns all Filters from the FilterManager
     *
     * @return All registered Filters
     */
    public List getFilterList()
    {
        return m_pageFilters;
    }

    /**
     * Returns all visible Filters from the FilterManager
     *
     * @return All registered and visible Filters
     */
    public List getVisibleFilterList()
    {
        List l = new ArrayList(m_pageFilters.size());

        for (Iterator it = m_pageFilters.iterator(); it.hasNext();)
        {
            PageFilter filter = (PageFilter) it.next();

            if (filter.isVisible())
            {
                l.add(filter);
            }
        }

        return l;
    }

    /*
     * ========================================================================
     *
     * The XML Parser Code
     *
     * ========================================================================
     */
    private void registerFilter(String filterName, Configuration filterConf)
            throws SAXException
    {
        try
        {
            Class filterClass = Class.forName(filterName);

            Parameter [] confParameter = new Parameter [] { new ConstantParameter(filterConf) };
            filterContainer.registerComponentImplementation(filterName, filterClass, confParameter);
        }
        catch (Exception e)
        {
            throw new SAXException("While registering " + filterName + " with the Filter container:", e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param namespace DOCUMENT ME!
     * @param name DOCUMENT ME!
     * @param qName DOCUMENT ME!
     * @param atts DOCUMENT ME!
     *
     * @throws SAXException DOCUMENT ME!
     */
    public void startElement(String namespace, String name, String qName, Attributes atts)
            throws SAXException
    {
        lastReadCharacters = new StringBuffer();

        if ("pagefilters".equals(qName))
        {
            parsingFilters = true;
        }
        else if (parsingFilters)
        {
            if ("filter".equals(qName))
            {
                filterName = null;
                filterConf = new PropertiesConfiguration();
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param namespace DOCUMENT ME!
     * @param name DOCUMENT ME!
     * @param qName DOCUMENT ME!
     *
     * @throws SAXException DOCUMENT ME!
     * @throws IllegalArgumentException DOCUMENT ME!
     */
    public void endElement(String namespace, String name, String qName)
            throws SAXException
    {
        if ("pagefilters".equals(qName))
        {
            parsingFilters = false;
        }
        else if (parsingFilters)
        {
            if ("filter".equals(qName))
            {
                registerFilter(filterName, filterConf);
            }
            else if ("class".equals(qName))
            {
                filterName = lastReadCharacters.toString();
            }
            else if ("param".equals(qName))
            {
                if (StringUtils.isNotEmpty(lastReadParamName))
                {
                    filterConf.setProperty(lastReadParamName, lastReadParamValue);
                }
            }
            else if ("name".equals(qName))
            {
                lastReadParamName = lastReadCharacters.toString();
            }
            else if ("value".equals(qName))
            {
                if (StringUtils.isEmpty(lastReadParamName))
                {
                    throw new IllegalArgumentException("Found Parameter Value before Parameter name!");
                }

                lastReadParamValue = lastReadCharacters.toString();
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
        lastReadCharacters.append(ch, start, length);
    }
}
