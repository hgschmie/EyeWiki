package com.ecyrd.jspwiki.manager;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.log4j.PropertyConfigurator;

import com.ecyrd.jspwiki.TestEngine;
import com.ecyrd.jspwiki.providers.CachingProvider;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class PageManagerTest
        extends TestCase
{
    /** DOCUMENT ME! */
    Configuration conf = null;

    /** DOCUMENT ME! */
    TestEngine engine;

    /**
     * Creates a new PageManagerTest object.
     *
     * @param s DOCUMENT ME!
     */
    public PageManagerTest(String s)
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
    public void testPageCacheExists()
            throws Exception
    {
        conf.setProperty("jspwiki.usePageCache", Boolean.TRUE);

        PageManager m = new PageManager(engine, conf);

        assertTrue(m.getProvider() instanceof CachingProvider);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testPageCacheNotInUse()
            throws Exception
    {
        conf.setProperty("jspwiki.usePageCache", Boolean.FALSE);

        PageManager m = new PageManager(engine, conf);

        assertTrue(!(m.getProvider() instanceof CachingProvider));
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Test suite()
    {
        return new TestSuite(PageManagerTest.class);
    }
}
