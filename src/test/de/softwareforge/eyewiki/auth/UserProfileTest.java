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

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.log4j.PropertyConfigurator;

import de.softwareforge.eyewiki.TestEngine;
import de.softwareforge.eyewiki.auth.UserProfile;
import de.softwareforge.eyewiki.util.TextUtil;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests the UserProfile class.
 *
 * @author Janne Jalkanen
 */
public class UserProfileTest
        extends TestCase
{
    /**
     * Creates a new UserProfileTest object.
     *
     * @param s DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public UserProfileTest(String s)
            throws Exception
    {
        super(s);

        Configuration conf = TestEngine.getConfiguration("/eyewiki_auth.properties");
        PropertyConfigurator.configure(ConfigurationConverter.getProperties(conf));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void setUp()
            throws Exception
    {
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
    public void testEquals()
    {
        UserProfile p = new UserProfile();
        UserProfile p2 = new UserProfile();

        p.setName("Alice");
        p2.setName("Bob");

        assertFalse(p.equals(p2));
    }

    /**
     * DOCUMENT ME!
     */
    public void testEquals2()
    {
        UserProfile p = new UserProfile();
        UserProfile p2 = new UserProfile();

        p.setName("Alice");
        p2.setName("Alice");

        assertTrue(p.equals(p2));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testStringRepresentation()
            throws Exception
    {
        UserProfile p = UserProfile.parseStringRepresentation("username=JanneJalkanen");

        assertEquals("name", "JanneJalkanen", p.getName());
    }

    /**
     * Sometimes not all servlet containers offer you correctly decoded cookies.  Reported by KalleKivimaa.
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testBrokenStringRepresentation()
            throws Exception
    {
        UserProfile p = UserProfile.parseStringRepresentation("username%3DJanneJalkanen");

        assertEquals("name", "JanneJalkanen", p.getName());
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testUTFStringRepresentation()
            throws Exception
    {
        UserProfile p = new UserProfile();

        p.setName("M��m��");

        String s = p.getStringRepresentation();

        UserProfile p2 = UserProfile.parseStringRepresentation(s);
        assertEquals("name", "M��m��", p2.getName());
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testUTFURLStringRepresentation()
            throws Exception
    {
        UserProfile p = UserProfile.parseStringRepresentation("username=" + TextUtil.urlEncodeUTF8("M��m��"));

        assertEquals("name", "M��m��", p.getName());
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Test suite()
    {
        return new TestSuite(UserProfileTest.class);
    }
}
