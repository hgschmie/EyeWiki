
package com.ecyrd.jspwiki.auth.modules;

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
        TestSuite suite = new TestSuite("auth package modules tests");

        suite.addTest( WikiDatabaseTest.suite() );
        suite.addTest( PageAuthorizerTest.suite() );


        return suite;
    }
}
