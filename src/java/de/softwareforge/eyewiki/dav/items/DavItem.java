package de.softwareforge.eyewiki.dav.items;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import de.softwareforge.eyewiki.WikiEngine;

/**
 * DOCUMENT ME!
 *
 * @author jalkanen
 *
 * @since
 */
public abstract class DavItem
{
    /** DOCUMENT ME! */
    protected WikiEngine m_engine;

    /** DOCUMENT ME! */
    protected ArrayList m_items = new ArrayList();

    /**
     * Creates a new DavItem object.
     *
     * @param engine DOCUMENT ME!
     */
    protected DavItem(WikiEngine engine)
    {
        m_engine = engine;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract Collection getPropertySet();

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract String getHref();

    /**
     * DOCUMENT ME!
     *
     * @param depth DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Iterator iterator(int depth)
    {
        ArrayList list = new ArrayList();

        if (depth == 0)
        {
            list.add(this);
        }
        else if (depth == 1)
        {
            list.add(this);
            list.addAll(m_items);
        }
        else if (depth == -1)
        {
            list.add(this);

            for (Iterator i = m_items.iterator(); i.hasNext();)
            {
                DavItem di = (DavItem) i.next();

                for (Iterator j = di.iterator(-1); i.hasNext();)
                {
                    list.add(j.next());
                }
            }
        }

        return list.iterator();
    }
}
