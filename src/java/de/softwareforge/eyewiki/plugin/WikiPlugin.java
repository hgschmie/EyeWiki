package de.softwareforge.eyewiki.plugin;

import java.util.Map;

import de.softwareforge.eyewiki.WikiContext;


/**
 * Defines an interface for plugins.  Any instance of a wiki plugin
 * should implement this interface.
 *
 * @author Janne Jalkanen
 */
public interface WikiPlugin
{
    /**
     * This is the main entry point for any plugin.  The parameters are parsed, and a special
     * parameter called "_body" signifies the name of the plugin body, i.e. the part of the plugin
     * that is not a parameter of the form "key=value".  This has been separated using an empty
     * line.
     *
     * <P>
     * Note that it is preferred that the plugin returns XHTML-compliant HTML (i.e. close all tags,
     * use &lt;br /&gt; instead of &lt;br&gt;, etc.
     * </p>
     *
     * @param context The current WikiContext.
     * @param params A Map which contains key-value pairs.  Any parameter that the user has
     *        specified on the wiki page will contain String-String parameters, but it is possible
     *        that at some future date, eyeWiki will give you other things that are not Strings.
     *
     * @return HTML, ready to be included into the rendered page.
     *
     * @throws PluginException In case anything goes wrong.
     */
    String execute(WikiContext context, Map params)
            throws PluginException;
}
