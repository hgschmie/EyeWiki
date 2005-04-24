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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.Startable;
import org.picocontainer.defaults.ConstantParameter;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.ecyrd.jspwiki.WikiContext;
import com.ecyrd.jspwiki.WikiEngine;
import com.ecyrd.jspwiki.WikiProperties;
import com.ecyrd.jspwiki.util.PriorityList;


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
    private Properties filterProperties = new Properties();

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
                    log.info("Could not load "
                            + xmlFile + ", no filters are configured.");
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

    public synchronized void start()
    {
        // If our filters are good, they implement the regular life cycle...
        filterContainer.start();

        for (Iterator it = filterContainer.getComponentInstances().iterator(); it.hasNext(); )
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

    public synchronized void stop()
    {
        setStarted(false);
    }

    protected void setStarted(final boolean started)
    {
        this.started = started;
    }

    public boolean isStarted()
    {
        return started;
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
    public void addPageFilter(PageFilter f)
    {
        if (f == null)
        {
            throw new IllegalArgumentException(
                    "Attempt to provide a null filter - this should never happen. "
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

        for (Iterator it = m_pageFilters.iterator(); it.hasNext(); )
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

    private void registerFilter(String filterName, Properties filterProperties)
            throws SAXException
    {
        try
        {
            Class filterClass = Class.forName(filterName);

            if (filterProperties != null && filterProperties.size() > 0)
            {
                Parameter [] propParameter = new Parameter[] { new ConstantParameter(filterProperties) };
                filterContainer.registerComponentImplementation(filterName, filterClass, propParameter);
            }
            else
            {
                filterContainer.registerComponentImplementation(filterName, filterClass);
            }
        }
        catch (Exception e)
        {
            throw new SAXException("While registering " + filterName + " with the Filter container:",  e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param name DOCUMENT ME!
     * @param atts DOCUMENT ME!
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
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param name DOCUMENT ME!
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
                registerFilter(filterName, filterProperties);
            }
            else if ("class".equals(qName))
            {
                filterName = lastReadCharacters.toString();
            }
            else if ("param".equals(qName))
            {
                filterProperties.setProperty(lastReadParamName, lastReadParamValue);
            }
            else if ("name".equals(qName))
            {
                lastReadParamName = lastReadCharacters.toString();
            }
            else if ("value".equals(qName))
            {
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

    /*
    private static FilterComponentAdapter
            extends ConstructorInjectionComponentAdapter
    {
        private FilterComponentAdapter(final String className, final Properties props)
        {
            Parameter [] propParameter = new Parameter[1];
            propParameter[0] = new ConstantParameter(props);
            super(className, className, propParameter);
        }
    }
    */
}

