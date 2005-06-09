package de.softwareforge.eyewiki.xmlrpc;

import de.softwareforge.eyewiki.WikiEngine;


/**
 * Any wiki RPC handler should implement this so that they can be properly initialized and
 * recognized by eyeWiki.
 *
 * @author Janne Jalkanen
 *
 * @since 2.1.7
 */
public interface WikiRPCHandler
{
    /**
     * DOCUMENT ME!
     *
     * @param engine DOCUMENT ME!
     */
    void initialize(WikiEngine engine);
}
