package de.softwareforge.eyewiki.plugin;

import java.util.Map;

import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.plugin.PluginException;
import de.softwareforge.eyewiki.plugin.WikiPlugin;


/**
 * Implements a simple plugin that just returns a piece of Javascript
 *
 * <P>
 * Parameters: text - text to return.
 * </p>
 *
 * @author Janne Jalkanen
 */
public class JavaScriptPlugin
        implements WikiPlugin
{
    /**
     * DOCUMENT ME!
     *
     * @param engine DOCUMENT ME!
     *
     * @throws PluginException DOCUMENT ME!
     */
    public void initialize(WikiEngine engine)
            throws PluginException
    {
    }

    /**
     * DOCUMENT ME!
     *
     * @param context DOCUMENT ME!
     * @param params DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws PluginException DOCUMENT ME!
     */
    public String execute(WikiContext context, Map params)
            throws PluginException
    {
        return "<script language=\"JavaScript\"><!--\nfoo='';\n--></script>\n";
    }
}
