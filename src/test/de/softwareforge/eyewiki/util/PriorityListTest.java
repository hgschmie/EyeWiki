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

import de.softwareforge.eyewiki.util.PriorityList;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class PriorityListTest
        extends TestCase
{
    /**
     * Creates a new PriorityListTest object.
     *
     * @param s DOCUMENT ME!
     */
    public PriorityListTest(String s)
    {
        super(s);
    }

    /**
     * DOCUMENT ME!
     */
    public void testInsert()
    {
        PriorityList p = new PriorityList();

        p.add("One", 1);
        p.add("Two", 2);

        assertEquals("size", 2, p.size());

        assertEquals("Two", "Two", p.get(0));
        assertEquals("One", "One", p.get(1));
    }

    /**
     * Check that the priority in case two items are the same priority is "first goes first".
     */
    public void testInsertSame()
    {
        PriorityList p = new PriorityList();

        p.add("One", 1);
        p.add("Two", 1);

        assertEquals("size", 2, p.size());

        assertEquals("One", "One", p.get(0));
        assertEquals("Two", "Two", p.get(1));
    }

    /**
     * DOCUMENT ME!
     */
    public void testInsertSame2()
    {
        PriorityList p = new PriorityList();

        p.add("One", 1);
        p.add("Two", 2);
        p.add("Three", 3);

        assertEquals("size", 3, p.size());

        assertEquals("Three", "Three", p.get(0));
        assertEquals("Two", "Two", p.get(1));
        assertEquals("One", "One", p.get(2));

        p.add("TwoTwo", 2);

        assertEquals("2: size", 4, p.size());

        assertEquals("2: Three", "Three", p.get(0));
        assertEquals("2: Two", "Two", p.get(1));
        assertEquals("2: TwoTwo", "TwoTwo", p.get(2));
        assertEquals("2: One", "One", p.get(3));
    }

    /**
     * DOCUMENT ME!
     */
    public void testInsertSame3()
    {
        PriorityList p = new PriorityList();

        p.add("One", 1);
        p.add("Two", 2);
        p.add("Two2", 2);
        p.add("Two3", 2);
        p.add("Three", 3);

        assertEquals("size", 5, p.size());

        assertEquals("Three", "Three", p.get(0));
        assertEquals("Two", "Two", p.get(1));
        assertEquals("Two2", "Two2", p.get(2));
        assertEquals("Two3", "Two3", p.get(3));
        assertEquals("One", "One", p.get(4));

        p.add("TwoTwo", 2);

        assertEquals("2: size", 6, p.size());

        assertEquals("2: Three", "Three", p.get(0));
        assertEquals("2: Two", "Two", p.get(1));
        assertEquals("2: Two2", "Two2", p.get(2));
        assertEquals("2: Two3", "Two3", p.get(3));
        assertEquals("2: TwoTwo", "TwoTwo", p.get(4));
        assertEquals("2: One", "One", p.get(5));
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Test suite()
    {
        return new TestSuite(PriorityListTest.class);
    }
}
