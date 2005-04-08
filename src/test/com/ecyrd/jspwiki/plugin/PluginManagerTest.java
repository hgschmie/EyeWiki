package com.ecyrd.jspwiki.plugin;

import org.apache.commons.configuration.PropertiesConfiguration;

import com.ecyrd.jspwiki.TestEngine;
import com.ecyrd.jspwiki.WikiContext;
import com.ecyrd.jspwiki.WikiEngine;
import com.ecyrd.jspwiki.WikiPage;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class PluginManagerTest
        extends TestCase
{
    /** DOCUMENT ME! */
    public static final String NAME1 = "Test1";

    /** DOCUMENT ME! */
    PropertiesConfiguration conf = new PropertiesConfiguration();

    /** DOCUMENT ME! */
    WikiEngine engine;

    /** DOCUMENT ME! */
    WikiContext context;

    /** DOCUMENT ME! */
    PluginManager manager;

    /**
     * Creates a new PluginManagerTest object.
     *
     * @param s DOCUMENT ME!
     */
    public PluginManagerTest(String s)
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

        engine = new TestEngine(conf);
        context = new WikiContext(engine, new WikiPage("testpage"));
        manager = new PluginManager(conf);
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
    public void testSimpleInsert()
            throws Exception
    {
        String res =
            manager.execute(
                context, "{INSERT com.ecyrd.jspwiki.plugin.SamplePlugin WHERE text=foobar}");

        assertEquals("foobar", res);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testSimpleInsertNoPackage()
            throws Exception
    {
        String res = manager.execute(context, "{INSERT SamplePlugin WHERE text=foobar}");

        assertEquals("foobar", res);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testSimpleInsertNoPackage2()
            throws Exception
    {
        conf.setProperty(PluginManager.PROP_CLASS_PLUGIN_SEARCHPATH, "com.foo");

        PluginManager m = new PluginManager(conf);
        String res = m.execute(context, "{INSERT SamplePlugin2 WHERE text=foobar}");

        assertEquals("foobar", res);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testSimpleInsertNoPackage3()
            throws Exception
    {
        conf.setProperty(PluginManager.PROP_CLASS_PLUGIN_SEARCHPATH, "com.foo");

        PluginManager m = new PluginManager(conf);
        String res = m.execute(context, "{INSERT SamplePlugin3 WHERE text=foobar}");

        assertEquals("foobar", res);
    }

    /**
     * Check that in all cases com.ecyrd.jspwiki.plugin is searched.
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testSimpleInsertNoPackage4()
            throws Exception
    {
        conf.setProperty(PluginManager.PROP_CLASS_PLUGIN_SEARCHPATH, "com.foo,blat.blaa");

        PluginManager m = new PluginManager(conf);
        String res = m.execute(context, "{INSERT SamplePlugin WHERE text=foobar}");

        assertEquals("foobar", res);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testSimpleInsert2()
            throws Exception
    {
        String res =
            manager.execute(
                context,
                "{INSERT   com.ecyrd.jspwiki.plugin.SamplePlugin  WHERE   text = foobar2, moo=blat}");

        assertEquals("foobar2", res);
    }

    /**
     * Missing closing brace
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testSimpleInsert3()
            throws Exception
    {
        String res =
            manager.execute(
                context,
                "{INSERT   com.ecyrd.jspwiki.plugin.SamplePlugin  WHERE   text = foobar2, moo=blat");

        assertEquals("foobar2", res);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testQuotedArgs()
            throws Exception
    {
        String res = manager.execute(context, "{INSERT SamplePlugin WHERE text='this is a space'}");

        assertEquals("this is a space", res);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testQuotedArgs2()
            throws Exception
    {
        String res =
            manager.execute(context, "{INSERT SamplePlugin WHERE text='this \\'is a\\' space'}");

        assertEquals("this 'is a' space", res);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testNumberArgs()
            throws Exception
    {
        String res = manager.execute(context, "{INSERT SamplePlugin WHERE text=15}");

        assertEquals("15", res);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testNoInsert()
            throws Exception
    {
        String res = manager.execute(context, "{SamplePlugin WHERE text=15}");

        assertEquals("15", res);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Test suite()
    {
        return new TestSuite(PluginManagerTest.class);
    }
}
