
package com.ecyrd.jspwiki;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.PropertyConfigurator;

import com.ecyrd.jspwiki.providers.CachingProvider;

public class PageManagerTest extends TestCase
{
    PropertiesConfiguration conf = new PropertiesConfiguration();

    TestEngine engine;

    public PageManagerTest( String s )
    {
        super( s );
    }

    public void setUp()
        throws Exception
    {
        conf.load( TestEngine.findTestProperties() );
        PropertyConfigurator.configure(ConfigurationConverter.getProperties(conf));
        engine = new TestEngine(conf);
    }

    public void tearDown()
    {
    }

    public void testPageCacheExists()
        throws Exception
    {
        conf.setProperty( "jspwiki.usePageCache", Boolean.TRUE);
        PageManager m = new PageManager( engine,conf);

        assertTrue( m.getProvider() instanceof CachingProvider );
    }

    public void testPageCacheNotInUse()
        throws Exception
    {
        conf.setProperty( "jspwiki.usePageCache", Boolean.FALSE);
        PageManager m = new PageManager( engine, conf);

        assertTrue( !(m.getProvider() instanceof CachingProvider) );
    }

    public static Test suite()
    {
        return new TestSuite( PageManagerTest.class );
    }

}
