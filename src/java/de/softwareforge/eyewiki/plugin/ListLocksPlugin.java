package de.softwareforge.eyewiki.plugin;

/*
 * ========================================================================
 *
 * eyeWiki - a WikiWiki clone written in Java
 *
 * ========================================================================
 *
 * Copyright (C) 2005 Henning Schmiedehausen <henning@software-forge.com>
 *
 * based on
 *
 * JSPWiki - a JSP-based WikiWiki clone.
 * Copyright (C) 2002-2005 Janne Jalkanen (Janne.Jalkanen@iki.fi)
 *
 * ========================================================================
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * ========================================================================
 */

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.softwareforge.eyewiki.PageLock;
import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.manager.PageManager;

/**
 * This is a plugin for the administrator: It allows him to see in a single glance who is editing what.
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

    /** DOCUMENT ME! */
    protected final PageManager pageManager;

    /**
     * Creates a new ListLocksPlugin object.
     *
     * @param engine DOCUMENT ME!
     * @param pageManager DOCUMENT ME!
     */
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

                result.append("<tr><td>").append(lock.getPage().getName()).append("</td><td>").append(lock.getLocker())
                      .append("</td><td>").append(lock.getAcquisitionTime()).append("</td><td>").append(lock.getExpiryTime())
                      .append("</td></tr>\n");
            }
        }

        result.append("</table>");

        return result.toString();
    }
}
