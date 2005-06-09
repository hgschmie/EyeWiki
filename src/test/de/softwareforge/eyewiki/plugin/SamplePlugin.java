package de.softwareforge.eyewiki.plugin;

import java.util.Map;

import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.plugin.PluginException;
import de.softwareforge.eyewiki.plugin.WikiPlugin;


/**
 * Implements a simple plugin that just returns its text.
 *
 * <P>
 * Parameters: text - text to return. Any _body content gets appended between brackets.
 * </p>
 *
 * @author Janne Jalkanen
 */
public class SamplePlugin
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
        StringBuffer sb = new StringBuffer();

        String text = (String) params.get("text");

        if (text != null)
        {
            sb.append(text);
        }

        String body = (String) params.get("_body");

        if (body != null)
        {
            sb.append(" (" + body.replace('\n', '+') + ")");
        }

        return sb.toString();
    }
}
