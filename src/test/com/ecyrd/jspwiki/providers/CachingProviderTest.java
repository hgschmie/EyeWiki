
package com.ecyrd.jspwiki.providers;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringReader;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.PropertyConfigurator;

import com.ecyrd.jspwiki.FileUtil;
import com.ecyrd.jspwiki.TestEngine;
import com.ecyrd.jspwiki.WikiPage;
import com.ecyrd.jspwiki.WikiProperties;

public class CachingProviderTest extends TestCase
{
    public CachingProviderTest( String s )
    {
        super( s );
    }

    public void setUp()
        throws Exception
    {
        TestEngine.emptyWorkDir();

        PropertiesConfiguration conf2 = new PropertiesConfiguration();

        conf2.load( TestEngine.findTestProperties() );
        PropertyConfigurator.configure(ConfigurationConverter.getProperties(conf2));
    }

    public void tearDown()
    	throws Exception
    {
        TestEngine.emptyWorkDir();
        TestEngine.deleteTestPage("Testi");
    }

    /**
     *  Checks that at startup we call the provider once, and once only.
     */
    public void testInitialization()
        throws Exception
    {
        PropertiesConfiguration conf = new PropertiesConfiguration();
        conf.load( TestEngine.findTestProperties() );

        conf.setProperty( "jspwiki.usePageCache", "true" );
        conf.setProperty( "jspwiki.pageProvider", "com.ecyrd.jspwiki.providers.CounterProvider" );
        conf.setProperty( "jspwiki.cachingProvider.capacity", "100" );

        TestEngine engine = new TestEngine( conf );

        CounterProvider p = (CounterProvider)((CachingProvider)engine.getPageManager().getProvider()).getRealProvider();

        assertEquals("init", 1, p.m_initCalls);
        assertEquals("getAllPages", 1, p.m_getAllPagesCalls);
        assertEquals("pageExists", 0, p.m_pageExistsCalls);
        assertEquals("getPage", 2, p.m_getPageCalls); // These two are for non-existant pages (with and without s)
        assertEquals("getPageText", 4, p.m_getPageTextCalls);

        WikiPage wp = engine.getPage( "Foo" );

        assertEquals("pageExists2", 0, p.m_pageExistsCalls);
        assertEquals("getPage2", 2, p.m_getPageCalls);
    }

    public void testSneakyAdd()
        throws Exception
    {
        PropertiesConfiguration conf = new PropertiesConfiguration();
        conf.load( TestEngine.findTestProperties() );

        conf.setProperty( "jspwiki.cachingProvider.cacheCheckInterval", "2" );
        
        TestEngine engine = new TestEngine( conf );
        
        String dir = conf.getString(WikiProperties.PROP_PAGEDIR );
        
        File f = new File( dir, "Testi.txt" );
        String content = "[fuufaa]";
        
        PrintWriter out = new PrintWriter( new FileWriter(f) );
        FileUtil.copyContents( new StringReader(content), out );
        out.close();
        
        Thread.sleep( 4000L ); // Make sure we wait long enough
        
        WikiPage p = engine.getPage( "Testi" );
        assertNotNull( "page did not exist?", p );
        
        String text = engine.getText( "Testi");
        assertEquals("text", "[fuufaa]", text );
        
        // TODO: ReferenceManager check as well
    }
    
    public static Test suite()
    {
        return new TestSuite( CachingProviderTest.class );
    }

}
