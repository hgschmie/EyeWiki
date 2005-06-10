package de.softwareforge.eyewiki.acl;

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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


import de.softwareforge.eyewiki.acl.AccessControlList;
import de.softwareforge.eyewiki.acl.AclEntryImpl;
import de.softwareforge.eyewiki.acl.AclImpl;
import de.softwareforge.eyewiki.auth.AllGroup;
import de.softwareforge.eyewiki.auth.UserProfile;
import de.softwareforge.eyewiki.auth.WikiGroup;
import de.softwareforge.eyewiki.auth.permissions.CommentPermission;
import de.softwareforge.eyewiki.auth.permissions.CreatePermission;
import de.softwareforge.eyewiki.auth.permissions.EditPermission;
import de.softwareforge.eyewiki.auth.permissions.ViewPermission;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class AclImplTest
        extends TestCase
{
    /** DOCUMENT ME! */
    AclImpl m_acl;

    /** DOCUMENT ME! */
    AclImpl m_aclGroup;

    /**
     * Creates a new AclImplTest object.
     *
     * @param s DOCUMENT ME!
     */
    public AclImplTest(String s)
    {
        super(s);
    }

    /**
     * We setup the following rules: Alice = may view Bob   = may view, may edit Charlie = may
     * view, may NOT edit Dave = may view, may NOT edit, may create groupAcl: FooGroup = Alice,
     * Bob - may edit BarGroup = Bob, Charlie - may NOT edit
     */
    public void setUp()
    {
        m_acl = new AclImpl();
        m_aclGroup = new AclImpl();

        //  User 1
        UserProfile u_alice = new UserProfile();
        u_alice.setName("Alice");

        UserProfile u_bob = new UserProfile();
        u_bob.setName("Bob");

        UserProfile u_charlie = new UserProfile();
        u_charlie.setName("Charlie");

        UserProfile u_dave = new UserProfile();
        u_dave.setName("Dave");

        //  ALLOW VIEW
        AclEntry ae = new AclEntryImpl();
        ae.addPermission(new ViewPermission());
        ae.setPrincipal(u_alice);

        //  DENY EDIT
        AclEntry ae2 = new AclEntryImpl();
        ae2.addPermission(new EditPermission());
        ae2.setNegativePermissions();
        ae2.setPrincipal(u_charlie);

        AclEntry ae2b = new AclEntryImpl();
        ae2b.addPermission(new ViewPermission());
        ae2b.setPrincipal(u_charlie);

        //  ALLOW VIEW, EDIT
        AclEntry ae3 = new AclEntryImpl();
        ae3.addPermission(new ViewPermission());
        ae3.addPermission(new EditPermission());
        ae3.setPrincipal(u_bob);

        // ALLOW VIEW, CREATE, DENY EDIT
        AclEntry ae4 = new AclEntryImpl();
        ae4.addPermission(new ViewPermission());
        ae4.addPermission(new CreatePermission());
        ae4.setPrincipal(u_dave);

        AclEntry ae4b = new AclEntryImpl();
        ae4b.addPermission(new EditPermission());
        ae4b.setNegativePermissions();
        ae4b.setPrincipal(u_dave);

        m_acl.addEntry(null, ae);
        m_acl.addEntry(null, ae2);
        m_acl.addEntry(null, ae2b);
        m_acl.addEntry(null, ae3);
        m_acl.addEntry(null, ae4);
        m_acl.addEntry(null, ae4b);

        //  Groups
        WikiGroup group1 = new WikiGroup();
        group1.addMember(u_alice);
        group1.addMember(u_bob);
        group1.setName("FooGroup");

        WikiGroup group2 = new WikiGroup();
        group2.addMember(u_bob);
        group2.addMember(u_charlie);
        group2.setName("BarGroup");

        AclEntry ag1 = new AclEntryImpl();
        ag1.addPermission(new EditPermission());
        ag1.setPrincipal(group1);

        AclEntry ag2 = new AclEntryImpl();
        ag2.addPermission(new EditPermission());
        ag2.setNegativePermissions();
        ag2.setPrincipal(group2);

        m_aclGroup.addEntry(null, ag1); // allow edit FooGroup
        m_aclGroup.addEntry(null, ag2); // deny  edit BarGroup
        m_aclGroup.addEntry(null, ae); // allow view Alice
        m_aclGroup.addEntry(null, ae2); // deny  edit Charlie
    }

    /**
     * DOCUMENT ME!
     */
    public void tearDown()
    {
    }

    /**
     * DOCUMENT ME!
     */
    public void testAlice()
    {
        UserProfile wup = new UserProfile();
        wup.setName("Alice");

        assertTrue("view", m_acl.checkPermission(wup, new ViewPermission()));
        assertFalse("edit", m_acl.checkPermission(wup, new EditPermission()));
        assertFalse("comment", m_acl.checkPermission(wup, new CommentPermission()));

        assertEquals("edit none", AclImpl.NONE, m_acl.findPermission(wup, new EditPermission()));

        assertEquals(
            "comment none", AclImpl.NONE, m_acl.findPermission(wup, new CommentPermission()));

        assertEquals("view allow", AclImpl.ALLOW, m_acl.findPermission(wup, new ViewPermission()));
    }

    /**
     * DOCUMENT ME!
     */
    public void testBob()
    {
        UserProfile wup = new UserProfile();
        wup.setName("Bob");

        assertTrue("view", m_acl.checkPermission(wup, new ViewPermission()));
        assertTrue("edit", m_acl.checkPermission(wup, new EditPermission()));
        assertTrue("comment", m_acl.checkPermission(wup, new CommentPermission()));

        assertEquals("view allow", AclImpl.ALLOW, m_acl.findPermission(wup, new ViewPermission()));
        assertEquals("edit allow", AclImpl.ALLOW, m_acl.findPermission(wup, new EditPermission()));
        assertEquals(
            "comment allow", AclImpl.ALLOW, m_acl.findPermission(wup, new CommentPermission()));
    }

    /**
     * DOCUMENT ME!
     */
    public void testCharlie()
    {
        UserProfile wup = new UserProfile();
        wup.setName("Charlie");

        assertTrue("view", m_acl.checkPermission(wup, new ViewPermission()));
        assertFalse("edit", m_acl.checkPermission(wup, new EditPermission()));

        assertEquals("view allow", AclImpl.ALLOW, m_acl.findPermission(wup, new ViewPermission()));

        assertEquals("edit deny", AclImpl.DENY, m_acl.findPermission(wup, new EditPermission()));
    }

    /**
     * DOCUMENT ME!
     */
    public void testDave()
    {
        UserProfile wup = new UserProfile();
        wup.setName("Dave");

        assertTrue("view", m_acl.checkPermission(wup, new ViewPermission()));
        assertTrue("create", m_acl.checkPermission(wup, new CreatePermission()));
        assertFalse("edit", m_acl.checkPermission(wup, new EditPermission()));

        assertEquals("view allow", AclImpl.ALLOW, m_acl.findPermission(wup, new ViewPermission()));

        assertEquals(
            "create allow", AclImpl.ALLOW, m_acl.findPermission(wup, new CreatePermission()));

        assertEquals("edit deny", AclImpl.DENY, m_acl.findPermission(wup, new EditPermission()));
    }

    /**
     * DOCUMENT ME!
     */
    public void testFooGroup()
    {
        UserProfile wup = new UserProfile();
        wup.setName("Alice");

        assertEquals(
            "view allow Alice", AclImpl.ALLOW, m_aclGroup.findPermission(wup, new ViewPermission()));

        assertEquals(
            "edit allow Alice", AclImpl.ALLOW, m_aclGroup.findPermission(wup, new EditPermission()));

        wup.setName("Bob");

        assertEquals(
            "allow edit Bob", AclImpl.ALLOW, m_aclGroup.findPermission(wup, new EditPermission()));

        assertEquals(
            "allow view Bob", AclImpl.NONE, m_aclGroup.findPermission(wup, new ViewPermission()));

        wup.setName("Charlie");

        assertEquals(
            "deny edit Charlie", AclImpl.DENY, m_aclGroup.findPermission(wup, new EditPermission()));

        assertEquals(
            "Default view Charlie", AclImpl.NONE,
            m_aclGroup.findPermission(wup, new ViewPermission()));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testAllGroup()
            throws Exception
    {
        UserProfile wup = new UserProfile();
        wup.setName("Alice");

        WikiGroup group1 = new AllGroup();
        group1.setName("All");

        AclEntry ae = new AclEntryImpl();
        ae.setPrincipal(group1);
        ae.addPermission(new ViewPermission());
        ae.addPermission(new EditPermission());

        AccessControlList acl = new AclImpl();
        acl.addEntry(null, ae);

        assertEquals("no view!", AclImpl.ALLOW, acl.findPermission(wup, new ViewPermission()));
        assertEquals("no edit!", AclImpl.ALLOW, acl.findPermission(wup, new EditPermission()));
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Test suite()
    {
        return new TestSuite(AclImplTest.class);
    }
}
