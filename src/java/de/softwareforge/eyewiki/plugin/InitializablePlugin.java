package de.softwareforge.eyewiki.plugin;

import java.util.Map;

import de.softwareforge.eyewiki.WikiContext;


/**
 * If a plugin defines this interface, it is called during eyeWiki initialization, if it occurs on
 * a page.
 *
 * @author Janne Jalkanen
 *
 * @since 2.2
 */
public interface InitializablePlugin
{
    /**
     * The initialization routine.  The context is to a Wiki page, and
     * the parameters are exactly like in the execute()-routine.
     * However, this routine is not expected to return anything,
     * as any output will be discarded.
     *
     * @param context DOCUMENT ME!
     * @param params DOCUMENT ME!
     *
     * @throws PluginException DOCUMENT ME!
     */
    void initialize(WikiContext context, Map params)
            throws PluginException;
}
