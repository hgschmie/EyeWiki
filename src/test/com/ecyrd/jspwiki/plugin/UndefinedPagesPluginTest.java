package com.ecyrd.jspwiki.plugin;

import org.apache.commons.configuration.PropertiesConfiguration;

import com.ecyrd.jspwiki.TestEngine;
import com.ecyrd.jspwiki.WikiContext;
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
public class UndefinedPagesPluginTest
        extends TestCase
{
    /** DOCUMENT ME! */
    PropertiesConfiguration conf = new PropertiesConfiguration();

    /** DOCUMENT ME! */
    TestEngine engine;

    /** DOCUMENT ME! */
    WikiContext context;

    /** DOCUMENT ME! */
    PluginManager manager;

    /**
     * Creates a new UndefinedPagesPluginTest object.
     *
     * @param s DOCUMENT ME!
     */
    public UndefinedPagesPluginTest(String s)
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

        engine.saveText("TestPage", "Reference to [Foobar].");
        engine.saveText("Foobar", "Reference to [Foobar2], [Foobars]");

        context = new WikiContext(engine, new WikiPage("TestPage"));
        manager = new PluginManager(conf);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void tearDown()
            throws Exception
    {
        TestEngine.deleteTestPage("TestPage");
        TestEngine.deleteTestPage("Foobar");
        TestEngine.emptyWorkDir();
    }

    private String wikitize(String s)
    {
        return engine.textToHTML(context, s);
    }

    /**
     * Tests that only correct undefined links are found. We also check against plural forms here,
     * which should not be listed as non-existant.
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testSimpleUndefined()
            throws Exception
    {
        WikiContext context2 = new WikiContext(engine, new WikiPage("Foobar"));

        String res =
            manager.execute(context2, "{INSERT com.ecyrd.jspwiki.plugin.UndefinedPagesPlugin");

        String exp = "[Foobar 2]\\\\";

        assertEquals(wikitize(exp), res);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Test suite()
    {
        return new TestSuite(UndefinedPagesPluginTest.class);
    }
}
