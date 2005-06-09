/*
 * (C) Janne Jalkanen 2005
 *
 */
package de.softwareforge.eyewiki.dav.items;

import de.softwareforge.eyewiki.WikiEngine;


/**
 * DOCUMENT ME!
 *
 * @author jalkanen
 *
 * @since
 */
public class TopLevelDavItem
        extends DirectoryItem
{
    /**
     * Creates a new TopLevelDavItem object.
     *
     * @param engine DOCUMENT ME!
     */
    public TopLevelDavItem(WikiEngine engine)
    {
        super(engine, "/");
        addDavItem(new DirectoryItem(engine, "raw"));
        addDavItem(new DirectoryItem(engine, "html"));
    }
}
