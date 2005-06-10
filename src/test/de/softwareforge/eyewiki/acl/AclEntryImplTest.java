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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


import de.softwareforge.eyewiki.acl.AclEntryImpl;
import de.softwareforge.eyewiki.auth.permissions.CommentPermission;
import de.softwareforge.eyewiki.auth.permissions.EditPermission;
import de.softwareforge.eyewiki.auth.permissions.ViewPermission;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class AclEntryImplTest
        extends TestCase
{
    /** DOCUMENT ME! */
    AclEntryImpl m_ae;

    /**
     * Creates a new AclEntryImplTest object.
     *
     * @param s DOCUMENT ME!
     */
    public AclEntryImplTest(String s)
    {
        super(s);
    }

    /**
     * DOCUMENT ME!
     */
    public void setUp()
    {
        m_ae = new AclEntryImpl();
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
    public void testAddPermission()
    {
        m_ae.addPermission(new ViewPermission());

        assertTrue("no permission", m_ae.checkPermission(new ViewPermission()));
        assertFalse("permission found", m_ae.checkPermission(new EditPermission()));
    }

    /**
     * DOCUMENT ME!
     */
    public void testAddPermission2()
    {
        m_ae.addPermission(new ViewPermission());
        m_ae.addPermission(new EditPermission());

        assertTrue("no editpermission", m_ae.checkPermission(new EditPermission()));
        assertTrue("no viewpermission", m_ae.checkPermission(new ViewPermission()));
    }

    /**
     * DOCUMENT ME!
     */
    public void testAddPermission3()
    {
        m_ae.addPermission(new CommentPermission());

        assertFalse("has editpermission", m_ae.checkPermission(new EditPermission()));
    }

    /**
     * DOCUMENT ME!
     */
    public void testAddPermission4()
    {
        m_ae.addPermission(new EditPermission());

        assertTrue("has not commentpermission", m_ae.checkPermission(new CommentPermission()));
    }

    /**
     * DOCUMENT ME!
     */
    public void testRemPermission()
    {
        m_ae.addPermission(new ViewPermission());
        m_ae.addPermission(new EditPermission());

        assertTrue("no editpermission", m_ae.checkPermission(new EditPermission()));
        assertTrue("no viewpermission", m_ae.checkPermission(new ViewPermission()));

        m_ae.removePermission(new EditPermission());

        assertFalse("editperm found", m_ae.checkPermission(new EditPermission()));
        assertTrue("viewperm disappeared", m_ae.checkPermission(new ViewPermission()));
    }

    /**
     * DOCUMENT ME!
     */
    public void testDefaults()
    {
        assertFalse("negative", m_ae.isNegative());
        assertFalse("elements", m_ae.permissions().hasMoreElements());
        assertNull("principal", m_ae.getPrincipal());
    }

    /**
     * DOCUMENT ME!
     */
    public void testNegative()
    {
        m_ae.setNegativePermissions();

        assertTrue("not negative", m_ae.isNegative());
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Test suite()
    {
        return new TestSuite(AclEntryImplTest.class);
    }
}
