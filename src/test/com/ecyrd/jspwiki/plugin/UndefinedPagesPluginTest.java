
package com.ecyrd.jspwiki.plugin;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.configuration.PropertiesConfiguration;

import com.ecyrd.jspwiki.TestEngine;
import com.ecyrd.jspwiki.WikiContext;
import com.ecyrd.jspwiki.WikiPage;

public class UndefinedPagesPluginTest extends TestCase
{
    PropertiesConfiguration conf = new PropertiesConfiguration();
    TestEngine engine;
    WikiContext context;
    PluginManager manager;
    
    public UndefinedPagesPluginTest( String s )
    {
        super( s );
    }

    public void setUp()
        throws Exception
    {
        conf.load( TestEngine.findTestProperties() );

        engine = new TestEngine(conf);

        engine.saveText( "TestPage", "Reference to [Foobar]." );
        engine.saveText( "Foobar", "Reference to [Foobar2], [Foobars]" );

        context = new WikiContext( engine, new WikiPage("TestPage") );
        manager = new PluginManager( conf );
    }

    public void tearDown()
    	throws Exception
    {
        TestEngine.deleteTestPage( "TestPage" );
        TestEngine.deleteTestPage( "Foobar" );
        TestEngine.emptyWorkDir();
    }

    private String wikitize( String s )
    {
        return engine.textToHTML( context, s );
    }

    /**
     *  Tests that only correct undefined links are found.
     *  We also check against plural forms here, which should not
     *  be listed as non-existant.
     */
    public void testSimpleUndefined()
        throws Exception
    {
        WikiContext context2 = new WikiContext( engine, new WikiPage("Foobar") );

        String res = manager.execute( context2,
                                      "{INSERT com.ecyrd.jspwiki.plugin.UndefinedPagesPlugin");

        String exp = "[Foobar 2]\\\\";

        assertEquals( wikitize(exp), res );
    }

    public static Test suite()
    {
        return new TestSuite( UndefinedPagesPluginTest.class );
    }
}
