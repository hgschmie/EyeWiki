
package com.ecyrd.jspwiki;

import junit.framework.*;
import java.util.*;

import org.apache.log4j.*;

import com.ecyrd.jspwiki.providers.*;

public class PageManagerTest extends TestCase
{
    Properties props = new Properties();

    TestEngine engine;

    public PageManagerTest( String s )
    {
        super( s );
    }

    public void setUp()
        throws Exception
    {
        props.load( TestEngine.findTestProperties() );
        PropertyConfigurator.configure(props);
        engine = new TestEngine(props);
    }

    public void tearDown()
    {
    }

    public void testPageCacheExists()
        throws Exception
    {
        props.setProperty( "jspwiki.usePageCache", "true" );
        PageManager m = new PageManager( engine, props );

        assertTrue( m.getProvider() instanceof CachingProvider );
    }

    public void testPageCacheNotInUse()
        throws Exception
    {
        props.setProperty( "jspwiki.usePageCache", "false" );
        PageManager m = new PageManager( engine, props );

        assertTrue( !(m.getProvider() instanceof CachingProvider) );
    }

    public static Test suite()
    {
        return new TestSuite( PageManagerTest.class );
    }

}
