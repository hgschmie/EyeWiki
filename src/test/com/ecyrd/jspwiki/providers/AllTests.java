
package com.ecyrd.jspwiki.providers;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllTests extends TestCase
{
    public AllTests( String s )
    {
        super( s );
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite("Providers suite");

        suite.addTest( FileSystemProviderTest.suite() );
        suite.addTest( RCSFileProviderTest.suite() );
        suite.addTest( VersioningFileProviderTest.suite() );
        suite.addTest( BasicAttachmentProviderTest.suite() );
        suite.addTest( CachingProviderTest.suite() );

        return suite;
    }
}
