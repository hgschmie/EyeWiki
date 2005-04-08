package com.ecyrd.jspwiki.plugin;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class AllTests
        extends TestCase
{
    /**
     * Creates a new AllTests object.
     *
     * @param s DOCUMENT ME!
     */
    public AllTests(String s)
    {
        super(s);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Test suite()
    {
        TestSuite suite = new TestSuite("JSPWiki plugins");

        suite.addTest(PluginManagerTest.suite());
        suite.addTest(ReferringPagesPluginTest.suite());
        suite.addTest(CounterPluginTest.suite());
        suite.addTest(UndefinedPagesPluginTest.suite());

        return suite;
    }
}
