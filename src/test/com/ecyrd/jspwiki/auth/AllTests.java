package com.ecyrd.jspwiki.auth;

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
        TestSuite suite = new TestSuite("auth package tests");

        suite.addTest(UserProfileTest.suite());
        suite.addTest(WikiGroupTest.suite());
        suite.addTest(AuthorizationManagerTest.suite());
        suite.addTest(com.ecyrd.jspwiki.auth.modules.AllTests.suite());

        return suite;
    }
}
