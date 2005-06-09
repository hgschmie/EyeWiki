/*
    WikiForms - a WikiPage FORM handler for JSPWiki.

    Copyright (C) 2003 BaseN.

    JSPWiki Copyright (C) 2002 Janne Jalkanen (Janne.Jalkanen@iki.fi)

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published
    by the Free Software Foundation; either version 2.1 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program; if not, write to the Free Software
*/
package de.softwareforge.eyewiki.util;

import java.util.Map;

import de.softwareforge.eyewiki.util.FormUtil;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class FormUtilTest
        extends TestCase
{
    /**
     * Creates a new FormUtilTest object.
     *
     * @param s DOCUMENT ME!
     */
    public FormUtilTest(String s)
    {
        super(s);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Test suite()
    {
        return new TestSuite(FormUtilTest.class);
    }

    public void testNullReq()
    {
        Map params = FormUtil.requestToMap(null, "foo");

        assertNotNull("No params Map returned!", params);
        assertEquals("Found a parameter in an empty request!", 0 , params.size());
    }
}

