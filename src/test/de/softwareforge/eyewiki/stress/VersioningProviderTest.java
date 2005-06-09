package de.softwareforge.eyewiki.stress;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.configuration.Configuration;


import de.softwareforge.eyewiki.TestEngine;
import de.softwareforge.eyewiki.WikiPage;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class VersioningProviderTest
        extends TestCase
{
    /** DOCUMENT ME! */
    public static final String NAME1 = "Test1";

    /** DOCUMENT ME! */
    Configuration conf = null;

    /** DOCUMENT ME! */
    TestEngine engine;

    /**
     * Creates a new VersioningProviderTest object.
     *
     * @param s DOCUMENT ME!
     */
    public VersioningProviderTest(String s)
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
        conf = TestEngine.getConfiguration("/eyewiki_vers.properties");

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

        // +2 comes from \r\n.
        assertEquals("wrong text", maxver + 2, engine.getText(NAME1).length());
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Test suite()
    {
        return new TestSuite(VersioningProviderTest.class);
    }
}
