package de.softwareforge.eyewiki;

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
