
package com.ecyrd.jspwiki.providers;

import junit.framework.*;
import java.io.*;
import java.util.*;

import org.apache.log4j.*;

import com.ecyrd.jspwiki.*;

public class CachingProviderTest extends TestCase
{
    public CachingProviderTest( String s )
    {
        super( s );
    }

    public void setUp()
        throws Exception
    {
        Properties props2 = new Properties();

        props2.load( TestEngine.findTestProperties() );
        PropertyConfigurator.configure(props2);
    }

    public void tearDown()
    {
    }

    /**
     *  Checks that at startup we call the provider once, and once only.
     */
    public void testInitialization()
        throws Exception
    {
        Properties props = new Properties();
        props.load( TestEngine.findTestProperties() );

        props.setProperty( "jspwiki.usePageCache", "true" );
        props.setProperty( "jspwiki.pageProvider", "com.ecyrd.jspwiki.providers.CounterProvider" );
        props.setProperty( "jspwiki.cachingProvider.capacity", "100" );

        TestEngine engine = new TestEngine( props );

        CounterProvider p = (CounterProvider)((CachingProvider)engine.getPageManager().getProvider()).getRealProvider();

        assertEquals("init", 1, p.m_initCalls);
        assertEquals("getAllPages", 1, p.m_getAllPagesCalls);
        assertEquals("pageExists", 0, p.m_pageExistsCalls);
        assertEquals("getPage", 0, p.m_getPageCalls);
        assertEquals("getPageText", 4, p.m_getPageTextCalls);

        WikiPage wp = engine.getPage( "Foo" );

        assertEquals("pageExists2", 0, p.m_pageExistsCalls);
        assertEquals("getPage2", 0, p.m_getPageCalls);
    }

    public static Test suite()
    {
        return new TestSuite( CachingProviderTest.class );
    }

}
