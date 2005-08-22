package de.softwareforge.eyewiki;

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

import java.util.Collection;
import java.util.Iterator;

/**
 * Utilities for tests.
 */
public class Util
{
    /**
     * Check that a collection contains the required string.
     *
     * @param container DOCUMENT ME!
     * @param captive DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    static public boolean collectionContains(Collection container, String captive)
    {
        Iterator i = container.iterator();

        while (i.hasNext())
        {
            Object cap = i.next();

            if (cap instanceof String && captive.equals(cap))
            {
                return (true);
            }
        }

        return (false);
    }
}
