
package com.ecyrd.jspwiki.auth;

import java.io.IOException;
import java.util.Properties;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.PropertyConfigurator;

import com.ecyrd.jspwiki.TestEngine;
import com.ecyrd.jspwiki.TextUtil;

/**
 *  Tests the UserProfile class.
 *  @author Janne Jalkanen
 */
public class UserProfileTest extends TestCase
{
    public UserProfileTest( String s )
    {
        super( s );
        Properties props = new Properties();
        try
        {
            props.load( TestEngine.findTestProperties() );
            PropertyConfigurator.configure(props);
        }
        catch( IOException e ) {}
    }

    public void setUp()
        throws Exception
    {
    }

    public void tearDown()
    {
    }

    public void testEquals()
    {
        UserProfile p = new UserProfile();
        UserProfile p2 = new UserProfile();

        p.setName("Alice");
        p2.setName("Bob");

        assertFalse( p.equals( p2 ) );
    }

    public void testEquals2()
    {
        UserProfile p = new UserProfile();
        UserProfile p2 = new UserProfile();

        p.setName("Alice");
        p2.setName("Alice");

        assertTrue( p.equals( p2 ) );
    }

    public void testStringRepresentation()
        throws Exception
    {
        UserProfile p = UserProfile.parseStringRepresentation("username=JanneJalkanen");

        assertEquals( "name", "JanneJalkanen",p.getName() );
    }

    /**
     *  Sometimes not all servlet containers offer you correctly
     *  decoded cookies.  Reported by KalleKivimaa.
     */
    public void testBrokenStringRepresentation()
        throws Exception
    {
        UserProfile p = UserProfile.parseStringRepresentation("username%3DJanneJalkanen");

        assertEquals( "name", "JanneJalkanen",p.getName() );
    }

    public void testUTFStringRepresentation()
        throws Exception
    {
        UserProfile p = new UserProfile();

        p.setName("M��m��");
        String s = p.getStringRepresentation();

        UserProfile p2 = UserProfile.parseStringRepresentation( s );
        assertEquals( "name", "M��m��", p2.getName() );
    }

    public void testUTFURLStringRepresentation()
        throws Exception
    {
        UserProfile p = UserProfile.parseStringRepresentation("username="+TextUtil.urlEncodeUTF8("M��m��"));

        assertEquals( "name", "M��m��",p.getName() );
    }


    public static Test suite()
    {
        return new TestSuite( UserProfileTest.class );
    }
}
