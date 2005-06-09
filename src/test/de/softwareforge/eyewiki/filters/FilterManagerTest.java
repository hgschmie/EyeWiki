package de.softwareforge.eyewiki.filters;

import java.util.Iterator;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.log4j.PropertyConfigurator;


import de.softwareforge.eyewiki.TestEngine;
import de.softwareforge.eyewiki.filters.FilterManager;
import de.softwareforge.eyewiki.filters.PageFilter;
import de.softwareforge.eyewiki.filters.ProfanityFilter;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class FilterManagerTest
        extends TestCase
{
    /** DOCUMENT ME! */
    Configuration conf = null;

    /** DOCUMENT ME! */
    TestEngine engine;

    /**
     * Creates a new FilterManagerTest object.
     *
     * @param s DOCUMENT ME!
     */
    public FilterManagerTest(String s)
    {
        super(s);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void setUp()
            throws Exception
    {
        conf = TestEngine.getConfiguration();
        PropertyConfigurator.configure(ConfigurationConverter.getProperties(conf));
        engine = new TestEngine(conf);
    }

    /**
     * DOCUMENT ME!
     */
    public void tearDown()
    {
        engine.cleanup();
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testInitFilters()
            throws Exception
    {
        FilterManager m = engine.getFilterManager();

        List l = m.getVisibleFilterList();

        assertEquals("Wrong number of filters", 2, l.size());

        Iterator i = l.iterator();
        PageFilter f1 = (PageFilter) i.next();

        assertTrue("Not a Profanityfilter", f1 instanceof ProfanityFilter);

        PageFilter f2 = (PageFilter) i.next();

        assertTrue("Not a Testfilter", f2 instanceof TestFilter);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testInitParams()
            throws Exception
    {
        FilterManager m = engine.getFilterManager();

        List l = m.getVisibleFilterList();

        Iterator i = l.iterator();
        PageFilter f1 = (PageFilter) i.next();
        TestFilter f2 = (TestFilter) i.next();

        Configuration conf = f2.getConfiguration();

        assertEquals("no foobar", "Zippadippadai", conf.getString("foobar"));

        assertEquals("no blatblaa", "5", conf.getString("blatblaa"));
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Test suite()
    {
        return new TestSuite(FilterManagerTest.class);
    }
}