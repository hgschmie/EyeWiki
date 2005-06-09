package de.softwareforge.eyewiki;

import java.util.ArrayList;
import java.util.Collection;


/**
 * Just a simple class collecting all of the links that come in.
 *
 * @author Janne Jalkanen
 */
public class LinkCollector
        implements StringTransmutator
{
    /** DOCUMENT ME! */
    private ArrayList m_items = new ArrayList();

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public final Collection getLinks()
    {
        return m_items;
    }

    /**
     * DOCUMENT ME!
     *
     * @param context DOCUMENT ME!
     * @param in DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public final String mutate(final WikiContext context, final String in)
    {
        m_items.add(in);

        return in;
    }
}
