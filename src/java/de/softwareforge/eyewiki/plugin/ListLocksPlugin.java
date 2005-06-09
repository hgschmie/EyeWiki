package de.softwareforge.eyewiki.plugin;

import java.util.Iterator;
import java.util.List;
import java.util.Map;


import de.softwareforge.eyewiki.PageLock;
import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.manager.PageManager;


/**
 * This is a plugin for the administrator: It allows him to see in a single glance who is editing
 * what.
 *
 * @author Janne Jalkanen
 *
 * @since 2.0.22.
 */
public class ListLocksPlugin
        implements WikiPlugin
{
    /** DOCUMENT ME! */
    protected final WikiEngine engine;

    protected final PageManager pageManager;

    public ListLocksPlugin(WikiEngine engine, PageManager pageManager)
    {
        this.engine = engine;
        this.pageManager = pageManager;
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
        StringBuffer result = new StringBuffer();

        List locks = pageManager.getActiveLocks();

        result.append("<table>\n<tr>\n<th>Page</th><th>Locked by</th><th>Acquired</th><th>Expires</th>\n</tr>");

        if (locks.size() == 0)
        {
            result.append("<tr><td colspan=\"4\">No locks exist currently.</td></tr>\n");
        }
        else
        {
            for (Iterator i = locks.iterator(); i.hasNext();)
            {
                PageLock lock = (PageLock) i.next();

                result.append("<tr><td>")
                .append(lock.getPage().getName())
                .append("</td><td>").append(lock.getLocker())
        		.append("</td><td>").append(lock.getAcquisitionTime())
        		.append("</td><td>").append(lock.getExpiryTime())
        		.append("</td></tr>\n");
            }
        }

        result.append("</table>");

        return result.toString();
    }
}
