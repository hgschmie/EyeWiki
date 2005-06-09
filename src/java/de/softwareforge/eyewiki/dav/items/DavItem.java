/*
 * (C) Janne Jalkanen 2005
 *
 */
package de.softwareforge.eyewiki.dav.items;

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
    /**
     * DOCUMENT ME!
     */
    protected WikiEngine m_engine;

    /**
     * DOCUMENT ME!
     */
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
