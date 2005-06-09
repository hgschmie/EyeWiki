package de.softwareforge.eyewiki.stress;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.configuration.Configuration;


import de.softwareforge.eyewiki.TestEngine;
import de.softwareforge.eyewiki.util.FileUtil;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class SpeedTest
        extends TestCase
{
    /** DOCUMENT ME! */
    private static int ITERATIONS = 100;

    /** DOCUMENT ME! */
    public static final String NAME1 = "Test1";

    /** DOCUMENT ME! */
    Configuration conf = null;

    /** DOCUMENT ME! */
    TestEngine engine;

    /**
     * Creates a new SpeedTest object.
     *
     * @param s DOCUMENT ME!
     */
    public SpeedTest(String s)
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
        conf = TestEngine.getConfiguration("/eyewiki_rcs.properties");
        
        engine = new TestEngine(conf);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void tearDown()
            throws Exception
    {
        engine.cleanup();
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testSpeed1()
            throws Exception
    {
        InputStream is = getClass().getResourceAsStream("/TextFormattingRules.txt");
        Reader in = new InputStreamReader(is, "ISO-8859-1");
        StringWriter out = new StringWriter();
        Benchmark mark = new Benchmark();

        FileUtil.copyContents(in, out);

        engine.saveText(NAME1, out.toString());

        mark.start();

        for (int i = 0; i < ITERATIONS; i++)
        {
            String txt = engine.getHTML(NAME1);
            assertTrue(0 != txt.length());
        }

        mark.stop();

        System.out.println(
            ITERATIONS + " pages took " + mark.getDurationMs() + " ms (="
            + (mark.getDurationMs() / ITERATIONS) + " ms/page)");
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testSpeed2()
            throws Exception
    {
        InputStream is = getClass().getResourceAsStream("/TestPlugins.txt");
        Reader in = new InputStreamReader(is, "ISO-8859-1");
        StringWriter out = new StringWriter();
        Benchmark mark = new Benchmark();

        FileUtil.copyContents(in, out);

        engine.saveText(NAME1, out.toString());

        mark.start();

        for (int i = 0; i < ITERATIONS; i++)
        {
            String txt = engine.getHTML(NAME1);
            assertTrue(0 != txt.length());
        }

        mark.stop();

        System.out.println(
            ITERATIONS + " plugin pages took " + mark.getDurationMs() + " ms (="
            + (mark.getDurationMs() / ITERATIONS) + " ms/page)");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Test suite()
    {
        return new TestSuite(SpeedTest.class);
    }
}
