package com.ecyrd.jspwiki;

import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.PropertyConfigurator;

import com.ecyrd.jspwiki.providers.CachingProvider;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


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
    PropertiesConfiguration conf = new PropertiesConfiguration();

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
        conf.load(TestEngine.findTestProperties());
        PropertyConfigurator.configure(ConfigurationConverter.getProperties(conf));
        engine = new TestEngine(conf);
    }

    /**
     * DOCUMENT ME!
     */
    public void tearDown()
    {
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
