package com.ecyrd.jspwiki.providers;

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
        TestSuite suite = new TestSuite("Providers suite");

        suite.addTest(FileSystemProviderTest.suite());
        suite.addTest(RCSFileProviderTest.suite());
        suite.addTest(VersioningFileProviderTest.suite());
        suite.addTest(BasicAttachmentProviderTest.suite());
        suite.addTest(CachingProviderTest.suite());

        return suite;
    }
}
