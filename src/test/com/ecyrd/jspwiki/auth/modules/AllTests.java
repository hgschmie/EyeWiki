package com.ecyrd.jspwiki.auth.modules;

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
        TestSuite suite = new TestSuite("auth package modules tests");

        suite.addTest(WikiDatabaseTest.suite());
        suite.addTest(PageAuthorizerTest.suite());

        return suite;
    }
}
