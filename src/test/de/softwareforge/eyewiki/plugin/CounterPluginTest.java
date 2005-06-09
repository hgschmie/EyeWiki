package de.softwareforge.eyewiki.plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import javax.servlet.ServletException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.configuration.Configuration;


import de.softwareforge.eyewiki.TestEngine;
import de.softwareforge.eyewiki.TranslatorReader;
import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiPage;
import de.softwareforge.eyewiki.exception.NoRequiredPropertyException;
import de.softwareforge.eyewiki.plugin.PluginManager;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class CounterPluginTest
        extends TestCase
{
    /** DOCUMENT ME! */
    Configuration conf = null;

    /** DOCUMENT ME! */
    TestEngine testEngine;

    /** DOCUMENT ME! */
    WikiContext context;

    /** DOCUMENT ME! */
    PluginManager manager;

    /**
     * Creates a new CounterPluginTest object.
     *
     * @param s DOCUMENT ME!
     */
    public CounterPluginTest(String s)
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

        testEngine = new TestEngine(conf);
    }

    /**
     * DOCUMENT ME!
     */
    public void tearDown()
    {
        testEngine.cleanup();
    }

    private String translate(String src)
            throws IOException, NoRequiredPropertyException, ServletException
    {
        WikiContext context = new WikiContext(testEngine, new WikiPage("TestPage"));
        Reader r = new TranslatorReader(context, new BufferedReader(new StringReader(src)));
        StringWriter out = new StringWriter();
        int c;

        while ((c = r.read()) != -1)
        {
            out.write(c);
        }

        return out.toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testSimpleCount()
            throws Exception
    {
        String src = "[{Counter}], [{Counter}]";

        assertEquals("1, 2", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testSimpleVar()
            throws Exception
    {
        String src = "[{Counter}], [{Counter}], [{$counter}]";

        assertEquals("1, 2, 2", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testTwinVar()
            throws Exception
    {
        String src = "[{Counter}], [{Counter name=aa}], [{$counter-aa}]";

        assertEquals("1, 1, 1", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Test suite()
    {
        return new TestSuite(CounterPluginTest.class);
    }
}
