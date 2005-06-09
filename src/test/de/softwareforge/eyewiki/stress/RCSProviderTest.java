package de.softwareforge.eyewiki.stress;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.configuration.Configuration;


import de.softwareforge.eyewiki.TestEngine;
import de.softwareforge.eyewiki.WikiPage;


/**
 * Does de.softwareforge.eyewiki.stress testing on the RCSProvider.
 */
public class RCSProviderTest
        extends TestCase
{
    /** DOCUMENT ME! */
    public static final String NAME1 = "Test1";

    /** DOCUMENT ME! */
    Configuration conf = null;

    /** DOCUMENT ME! */
    TestEngine engine;

    /**
     * Creates a new RCSProviderTest object.
     *
     * @param s DOCUMENT ME!
     */
    public RCSProviderTest(String s)
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
        conf = TestEngine.getConfiguration("/jspwiki_rcs.properties");

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
     * Bug report by Anon: RCS under Windows 2k:
     * <PRE>
     * In getPageInfo of RCSFileProvider:
     *  Problem:
     *  With a longer rlog result, the break clause in the last "else if"
     * breaks out of the reading loop before all the lines in the full
     * rlog have been read in. This causes the process.wait() to hang.
     *  Suggested quick fix:
     *  Always read all the contents of the rlog, even if it is slower.
     * </PRE>
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testMillionChanges()
            throws Exception
    {
        String text = "";
        String name = NAME1;
        int maxver = 200; // Save 200 versions.
        Benchmark mark = new Benchmark();

        mark.start();

        for (int i = 0; i < maxver; i++)
        {
            text = text + ".";
            engine.saveText(name, text);
        }

        mark.stop();

        System.out.println("Benchmark: " + mark.toString(200) + " pages/second");

        WikiPage pageinfo = engine.getPage(NAME1);

        assertEquals("wrong version", maxver, pageinfo.getVersion());

        // +2 comes from \r\n at the end of each file.
        assertEquals("wrong text", maxver + 2, engine.getText(NAME1).length());
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Test suite()
    {
        return new TestSuite(RCSProviderTest.class);
    }
}