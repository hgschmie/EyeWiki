package com.ecyrd.jspwiki.auth.modules;

import org.apache.commons.configuration.PropertiesConfiguration;

import com.ecyrd.jspwiki.TestEngine;
import com.ecyrd.jspwiki.auth.AuthorizationManager;
import com.ecyrd.jspwiki.auth.UserProfile;
import com.ecyrd.jspwiki.auth.permissions.WikiPermission;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class PageAuthorizerTest
        extends TestCase
{
    /** DOCUMENT ME! */
    TestEngine m_engine;

    /**
     * Creates a new PageAuthorizerTest object.
     *
     * @param s DOCUMENT ME!
     */
    public PageAuthorizerTest(String s)
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
        PropertiesConfiguration conf = new PropertiesConfiguration();
        conf.load(TestEngine.findTestProperties());

        m_engine = new TestEngine(conf);

        String text1 =
            "Foobar.\n\n[{SET defaultpermissions='ALLOW EDIT Charlie;DENY VIEW Bob'}]\n\nBlood.";
        String text2 = "Foo";

        m_engine.saveText("DefaultPermissions", text1);
        m_engine.saveText("TestPage", text2);
    }

    /**
     * DOCUMENT ME!
     */
    public void tearDown()
    {
        TestEngine.deleteTestPage("DefaultPermissions");
        TestEngine.deleteTestPage("TestPage");
    }

    // TODO: Fix this test
    public void testDefaultPermissions()
    {
        AuthorizationManager mgr = m_engine.getAuthorizationManager();

        UserProfile wup = new UserProfile();
        wup.setName("Charlie");
        wup.setLoginStatus(UserProfile.PASSWORD);

        assertTrue(
            "Charlie",
            mgr.checkPermission(
                m_engine.getPage("TestPage"), wup, WikiPermission.newInstance("edit")));

        /*
        wup.setName( "Bob" );
        assertTrue( "Bob", mgr.checkPermission( m_engine.getPage( "TestPage" ),
                                                wup,
                                                WikiPermission.newInstance( "view" ) ) );
         */
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Test suite()
    {
        return new TestSuite(PageAuthorizerTest.class);
    }
}
