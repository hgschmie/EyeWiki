package com.ecyrd.jspwiki.auth.modules;

import java.util.Properties;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.ecyrd.jspwiki.TestEngine;
import com.ecyrd.jspwiki.auth.AuthorizationManager;
import com.ecyrd.jspwiki.auth.UserProfile;
import com.ecyrd.jspwiki.auth.permissions.WikiPermission;

public class PageAuthorizerTest
    extends TestCase
{
    TestEngine m_engine;

    public PageAuthorizerTest( String s )
    {
        super( s );
    }

    public void setUp()
        throws Exception
    {
        Properties props = new Properties();
        props.load( TestEngine.findTestProperties() );
        
        m_engine = new TestEngine(props);

        String text1 = "Foobar.\n\n[{SET defaultpermissions='ALLOW EDIT Charlie;DENY VIEW Bob'}]\n\nBlood.";
        String text2 = "Foo";

        m_engine.saveText( "DefaultPermissions", text1 );
        m_engine.saveText( "TestPage", text2 );
    }

    public void tearDown()
    {
        m_engine.deletePage( "DefaultPermissions" );
        m_engine.deletePage( "TestPage" );
    }

    public void testDefaultPermissions()
    {
        AuthorizationManager mgr = m_engine.getAuthorizationManager();

        UserProfile wup = new UserProfile();
        wup.setName( "Charlie" );
        wup.setLoginStatus( UserProfile.PASSWORD );

        assertTrue( "Charlie", mgr.checkPermission( m_engine.getPage( "TestPage" ),
                                                    wup,
                                                    WikiPermission.newInstance( "edit" ) ) );

        wup.setName( "Bob" );
        assertTrue( "Bob", mgr.checkPermission( m_engine.getPage( "TestPage" ),
                                                wup,
                                                WikiPermission.newInstance( "view" ) ) );


                                                    
    }


    public static Test suite()
    {
        return new TestSuite( PageAuthorizerTest.class );
    }

}
