package de.softwareforge.eyewiki.auth.modules;

import org.apache.commons.configuration.Configuration;

import de.softwareforge.eyewiki.TestEngine;
import de.softwareforge.eyewiki.auth.AuthorizationManager;
import de.softwareforge.eyewiki.auth.UserProfile;
import de.softwareforge.eyewiki.auth.permissions.WikiPermission;

/*
 * ========================================================================
 *
 * eyeWiki - a WikiWiki clone written in Java
 *
 * ========================================================================
 *
 * Copyright (C) 2005 Henning Schmiedehausen <henning@software-forge.com>
 *
 * based on
 *
 * JSPWiki - a JSP-based WikiWiki clone.
 * Copyright (C) 2002-2005 Janne Jalkanen (Janne.Jalkanen@iki.fi)
 *
 * ========================================================================
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * ========================================================================
 */
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
        Configuration conf = TestEngine.getConfiguration("/eyewiki_auth.properties");

        m_engine = new TestEngine(conf);

        String text1 = "Foobar.\n\n[{SET defaultpermissions='ALLOW EDIT Charlie;DENY VIEW Bob'}]\n\nBlood.";
        String text2 = "Foo";

        m_engine.saveText("DefaultPermissions", text1);
        m_engine.saveText("TestPage", text2);
    }

    /**
     * DOCUMENT ME!
     */
    public void tearDown()
    {
        m_engine.cleanup();
    }

    // TODO: Fix this test
    public void testDefaultPermissions()
    {
        AuthorizationManager mgr = m_engine.getAuthorizationManager();

        UserProfile wup = new UserProfile();
        wup.setName("Charlie");
        wup.setLoginStatus(UserProfile.PASSWORD);

        assertTrue("Charlie", mgr.checkPermission(m_engine.getPage("TestPage"), wup, WikiPermission.newInstance("edit")));

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
