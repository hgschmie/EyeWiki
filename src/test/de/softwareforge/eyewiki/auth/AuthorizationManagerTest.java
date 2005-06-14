package de.softwareforge.eyewiki.auth;


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
import java.security.acl.AclEntry;
import java.security.acl.Permission;

import java.util.Enumeration;

import org.apache.commons.configuration.Configuration;

import de.softwareforge.eyewiki.TestEngine;
import de.softwareforge.eyewiki.WikiPage;
import de.softwareforge.eyewiki.acl.AccessControlList;
import de.softwareforge.eyewiki.attachment.Attachment;
import de.softwareforge.eyewiki.auth.AuthorizationManager;
import de.softwareforge.eyewiki.auth.UserProfile;
import de.softwareforge.eyewiki.auth.permissions.CommentPermission;
import de.softwareforge.eyewiki.auth.permissions.DeletePermission;
import de.softwareforge.eyewiki.auth.permissions.EditPermission;
import de.softwareforge.eyewiki.auth.permissions.UploadPermission;
import de.softwareforge.eyewiki.auth.permissions.ViewPermission;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests the AuthorizationManager class.
 *
 * @author Janne Jalkanen
 */
public class AuthorizationManagerTest
        extends TestCase
{
    /** DOCUMENT ME! */
    private AuthorizationManager m_manager;

    /** DOCUMENT ME! */
    private TestEngine m_engine;

    /**
     * Creates a new AuthorizationManagerTest object.
     *
     * @param s DOCUMENT ME!
     */
    public AuthorizationManagerTest(String s)
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
        m_manager = m_engine.getAuthorizationManager();
    }

    /**
     * DOCUMENT ME!
     */
    public void tearDown()
    {
        m_engine.cleanup();
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testSimplePermissions()
            throws Exception
    {
        String src = "[{DENY edit Guest}] [{ALLOW edit FooBar}]";

        m_engine.saveText("Test", src);

        WikiPage p = m_engine.getPage("Test");
        UserProfile wup = new UserProfile();
        wup.setLoginStatus(UserProfile.PASSWORD);
        wup.setName("FooBar");

        System.out.println(printPermissions(p));

        assertTrue("read 1", m_manager.checkPermission(p, wup, new ViewPermission()));
        assertTrue("edit 1", m_manager.checkPermission(p, wup, new EditPermission()));

        wup.setName("GobbleBlat");
        assertTrue("read 2", m_manager.checkPermission(p, wup, new ViewPermission()));
        assertFalse("edit 2", m_manager.checkPermission(p, wup, new EditPermission()));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testNamedPermissions()
            throws Exception
    {
        String src = "[{ALLOW edit NamedGuest}] [{DENY edit Guest}] ";

        m_engine.saveText("Test", src);

        WikiPage p = m_engine.getPage("Test");

        UserProfile wup = new UserProfile();
        wup.setName("FooBar");

        assertFalse("edit 1", m_manager.checkPermission(p, wup, new EditPermission()));

        wup.setLoginStatus(UserProfile.COOKIE);

        assertTrue("edit 2", m_manager.checkPermission(p, wup, new EditPermission()));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testAttachmentPermissions()
            throws Exception
    {
        String src = "[{ALLOW edit NamedGuest}] [{DENY edit Guest}] ";

        m_engine.saveText("Test", src);

        Attachment att = new Attachment("Test", "foobar.jpg");

        UserProfile wup = new UserProfile();
        wup.setName("FooBar");

        assertFalse("edit 1", m_manager.checkPermission(att, wup, new UploadPermission()));

        wup.setLoginStatus(UserProfile.COOKIE);

        assertTrue("edit 2", m_manager.checkPermission(att, wup, new UploadPermission()));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testAttachmentPermissions2()
            throws Exception
    {
        String src = "[{ALLOW upload FooBar}] [{ALLOW view Guest}] ";

        m_engine.saveText("Test", src);

        Attachment att = new Attachment("Test", "foobar.jpg");

        UserProfile wup = new UserProfile();
        wup.setLoginStatus(UserProfile.PASSWORD);
        wup.setName("FooBar");

        assertTrue("download", m_manager.checkPermission(att, wup, "view"));

        assertTrue("upload", m_manager.checkPermission(att, wup, "upload"));
    }

    /**
     * An user should not be allowed to simply set their name in the cookie and be allowed access.
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testNamedPermissions2()
            throws Exception
    {
        String src = "[{ALLOW edit FooBar}] [{DENY edit Guest}] ";

        m_engine.saveText("Test", src);

        WikiPage p = m_engine.getPage("Test");

        UserProfile wup = new UserProfile();
        wup.setName("FooBar");

        assertFalse("edit 1", m_manager.checkPermission(p, wup, new EditPermission()));

        wup.setLoginStatus(UserProfile.COOKIE);

        assertFalse("edit 2", m_manager.checkPermission(p, wup, new EditPermission()));

        wup.setLoginStatus(UserProfile.CONTAINER);

        assertTrue("edit 3", m_manager.checkPermission(p, wup, new EditPermission()));

        wup.setLoginStatus(UserProfile.PASSWORD);

        assertTrue("edit 4", m_manager.checkPermission(p, wup, new EditPermission()));
    }

    /**
     * An user should not be allowed to simply set their name in the cookie and be allowed access (this time with group data).
     *
     * @throws Exception DOCUMENT ME!
     */

    /*
     * TODO: Fix this test

    public void testNamedPermissions3()
        throws Exception
    {
        String src = "[{ALLOW edit FooGroup}] [{DENY edit Guest}] ";

        m_engine.saveText( "Test", src );

        m_engine.saveText( "FooGroup", "[{SET members=FooBar}]" );

        WikiPage p = m_engine.getPage("Test");

        UserProfile wup = new UserProfile();
        wup.setName( "FooBar" );

        assertFalse( "edit 1", m_manager.checkPermission( p, wup, new EditPermission() ) );

        wup.setLoginStatus( UserProfile.COOKIE );

        assertFalse( "edit 2", m_manager.checkPermission( p, wup, new EditPermission() ) );

        wup.setLoginStatus( UserProfile.CONTAINER );

        assertTrue( "edit 3", m_manager.checkPermission( p, wup, new EditPermission() ) );

        wup.setLoginStatus( UserProfile.PASSWORD );

        assertTrue( "edit 4", m_manager.checkPermission( p, wup, new EditPermission() ) );
    }
    */

    /**
     * A superuser should be allowed permissions.
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testAdminPermissions()
            throws Exception
    {
        String src = "[{DENY view Guest}] [{DENY edit Guest}] ";

        m_engine.saveText("Test", src);

        WikiPage p = m_engine.getPage("Test");

        UserProfile wup = new UserProfile();
        wup.setLoginStatus(UserProfile.CONTAINER);
        wup.setName("AdminGroup");

        assertTrue("edit 1", m_manager.checkPermission(p, wup, new EditPermission()));
        assertTrue("view 1", m_manager.checkPermission(p, wup, new ViewPermission()));
        assertTrue("delete 1", m_manager.checkPermission(p, wup, new DeletePermission()));
        assertTrue("comment 1", m_manager.checkPermission(p, wup, new CommentPermission()));

        wup.setName("NobodyHere");

        assertFalse("view 2", m_manager.checkPermission(p, wup, new ViewPermission()));
    }

    /**
     * Also, anyone in the supergroup should be allowed all permissions.
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testAdminPermissions2()
            throws Exception
    {
        String src = "[{DENY view Guest}] [{DENY edit Guest}] ";

        m_engine.saveText("Test", src);

        src = "[{SET members=FooBar}]";

        m_engine.saveText("AdminGroup", src);

        WikiPage p = m_engine.getPage("Test");

        UserProfile wup = new UserProfile();
        wup.setLoginStatus(UserProfile.PASSWORD);
        wup.setName("FooBar");

        assertTrue("edit 1", m_manager.checkPermission(p, wup, new EditPermission()));
        assertTrue("view 1", m_manager.checkPermission(p, wup, new ViewPermission()));
        assertTrue("delete 1", m_manager.checkPermission(p, wup, new DeletePermission()));
        assertTrue("comment 1", m_manager.checkPermission(p, wup, new CommentPermission()));

        wup.setName("NobodyHere");

        assertFalse("view 2", m_manager.checkPermission(p, wup, new ViewPermission()));
    }

    /**
     * A superuser should be allowed permissions, but not if he's not logged in.
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testAdminPermissionsNoLogin()
            throws Exception
    {
        String src = "[{DENY view Guest}] [{DENY edit Guest}] ";

        m_engine.saveText("Test", src);

        WikiPage p = m_engine.getPage("Test");

        m_engine.saveText("AdminGroup", "[{SET members=Hobble}]");

        UserProfile wup = new UserProfile();
        wup.setName("Hobble");

        assertFalse("edit 1", m_manager.checkPermission(p, wup, new EditPermission()));
        assertFalse("view 1", m_manager.checkPermission(p, wup, new ViewPermission()));
        assertFalse("delete 1", m_manager.checkPermission(p, wup, new DeletePermission()));
        assertFalse("comment 1", m_manager.checkPermission(p, wup, new CommentPermission()));
    }

    /**
     * From Paul Downes.
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testFunnyPermissions()
            throws Exception
    {
        String src = "[{DENY edit Guest}]\n[{ALLOW edit NamedGuest}]\n";

        m_engine.saveText("Test", src);

        WikiPage p = m_engine.getPage("Test");

        UserProfile wup = new UserProfile();
        wup.setName("Foogor");

        assertFalse("guest edit", m_manager.checkPermission(p, wup, new EditPermission()));

        wup.setLoginStatus(UserProfile.COOKIE);

        assertTrue("namedguest edit", m_manager.checkPermission(p, wup, new EditPermission()));
    }

    /**
     * From Paul Downes.
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testFunnyPermissions2()
            throws Exception
    {
        String src = "[{ALLOW edit Guest}]\n[{DENY edit Guest}]\n";

        m_engine.saveText("Test", src);

        WikiPage p = m_engine.getPage("Test");

        UserProfile wup = new UserProfile();
        wup.setName("Foogor");

        assertTrue("guest edit", m_manager.checkPermission(p, wup, new EditPermission()));

        wup.setLoginStatus(UserProfile.COOKIE);

        assertTrue("namedguest edit", m_manager.checkPermission(p, wup, new EditPermission()));
    }

    /**
     * From Paul Downes.
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testFunnyPermissions3()
            throws Exception
    {
        String src = "[{ALLOW edit Guest}]\n[{DENY view Guest}]\n";

        m_engine.saveText("Test", src);

        WikiPage p = m_engine.getPage("Test");

        UserProfile wup = new UserProfile();
        wup.setName("Foogor");

        assertFalse("guest edit", m_manager.checkPermission(p, wup, new ViewPermission()));

        assertTrue("view", m_manager.checkPermission(p, wup, new EditPermission()));
    }

    /**
     * Returns a string representation of the permissions of the page.
     *
     * @param p DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public static String printPermissions(WikiPage p)
            throws Exception
    {
        StringBuffer sb = new StringBuffer();

        AccessControlList acl = p.getAcl();

        sb.append("page = " + p.getName() + "\n");

        if (acl != null)
        {
            for (Enumeration e = acl.entries(); e.hasMoreElements();)
            {
                AclEntry entry = (AclEntry) e.nextElement();

                sb.append("  user = " + entry.getPrincipal().getName() + ": ");

                if (entry.isNegative())
                {
                    sb.append("NEG");
                }

                sb.append("(");

                for (Enumeration perms = entry.permissions(); perms.hasMoreElements();)
                {
                    Permission perm = (Permission) perms.nextElement();
                    sb.append(perm.toString());
                }

                sb.append(")\n");
            }
        }
        else
        {
            sb.append("  no permissions\n");
        }

        return sb.toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Test suite()
    {
        return new TestSuite(AuthorizationManagerTest.class);
    }
}
