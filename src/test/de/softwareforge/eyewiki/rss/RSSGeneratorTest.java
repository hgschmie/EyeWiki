package de.softwareforge.eyewiki.rss;

import java.util.Date;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.configuration.Configuration;


import de.softwareforge.eyewiki.TestEngine;
import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.plugin.WeblogEntryPlugin;
import de.softwareforge.eyewiki.plugin.WeblogPlugin;
import de.softwareforge.eyewiki.rss.RSSGenerator;

/**
 *  @author jalkanen
 *
 *  @since 
 */
public class RSSGeneratorTest
        extends TestCase
{
    /** DOCUMENT ME! */
    Configuration conf = null;

    TestEngine m_testEngine;
    
    public RSSGeneratorTest( String arg0 )
    {
        super( arg0 );
    }

    protected void setUp() throws Exception
    {
        conf = TestEngine.getConfiguration("/eyewiki_rss.properties");

        conf.setProperty( WikiEngine.PROP_BASEURL, "http://localhost/" );
        m_testEngine = new TestEngine(conf);
    }

    protected void tearDown() throws Exception
    {
        m_testEngine.cleanup();
    }

    public void testBlogRSS()
        throws Exception
    {
        WeblogEntryPlugin plugin = (WeblogEntryPlugin) m_testEngine.getPluginManager().findPlugin("WeblogEntryPlugin");
        m_testEngine.saveText( "TestBlog", "Foo1" );
        
        assertNotNull("No Weblog Entry Plugin found", plugin);

        String newPage = plugin.getNewEntryPage("TestBlog" );
        m_testEngine.saveText( newPage, "!Title1\r\nFoo" );
                
        newPage = plugin.getNewEntryPage("TestBlog" );
        m_testEngine.saveText( newPage, "!Title2\r\n__Bar__" );
        
        RSSGenerator gen = m_testEngine.getRSSGenerator();
        
        assertNotNull("No RSS Generator found", gen);
        
        WikiContext context = new WikiContext( m_testEngine, m_testEngine.getPage("TestBlog") );
        
        WeblogPlugin blogplugin = (WeblogPlugin) m_testEngine.getPluginManager().findPlugin("WeblogPlugin");
        
        assertNotNull("No Weblog Plugin found", blogplugin);

        List entries = blogplugin.findBlogEntries("TestBlog",
                                                   new Date(0),
                                                   new Date(Long.MAX_VALUE) );
        
        String blog = gen.generateBlogRSS( context, entries );
        
        assertTrue( "has Foo", blog.indexOf("<description>Foo</description>") != -1 );
        assertTrue( "has proper Bar", blog.indexOf("&lt;b&gt;Bar&lt;/b&gt;") != -1 );
    }
    
    public static Test suite()
    {
        return new TestSuite( RSSGeneratorTest.class );
    }

}
