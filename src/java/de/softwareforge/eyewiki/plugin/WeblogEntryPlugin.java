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

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;


import de.softwareforge.eyewiki.PageLock;
import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.WikiPage;
import de.softwareforge.eyewiki.manager.PageManager;
import de.softwareforge.eyewiki.providers.ProviderException;


/**
 * Builds a simple weblog.
 *
 * @since 1.9.21
 */
public class WeblogEntryPlugin
        implements WikiPlugin
{
    /** DOCUMENT ME! */
    private static Logger log = Logger.getLogger(WeblogEntryPlugin.class);

    /** DOCUMENT ME! */
    public static final int MAX_BLOG_ENTRIES = 10000; // Just a precaution.

    /** DOCUMENT ME! */
    public static final String PARAM_ENTRYTEXT = "entrytext";

    private final WikiEngine engine;

    private final PageManager pageManager;

    public WeblogEntryPlugin(final WikiEngine engine, final PageManager pageManager)
    {
        this.engine = engine;
        this.pageManager = pageManager;
    }

    /**
     * DOCUMENT ME!
     *
     * @param engine DOCUMENT ME!
     * @param blogName DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws ProviderException DOCUMENT ME!
     */
    public String getNewEntryPage(String blogName)
            throws ProviderException
    {
        SimpleDateFormat fmt = new SimpleDateFormat(WeblogPlugin.DEFAULT_DATEFORMAT);
        String today = fmt.format(new Date());

        int entryNum = findFreeEntry(pageManager, blogName, today);

        return WeblogPlugin.makeEntryPage(blogName, today, "" + entryNum);
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
        String weblogName = context.getPage().getName();

        StringBuffer sb = new StringBuffer();

        String entryText = (String) params.get(PARAM_ENTRYTEXT);

        if (entryText == null)
        {
            entryText = "New entry";
        }

        // FIXME: Generate somehow else.
        // String blogPage = getNewEntryPage(engine, weblogName);
        //sb.append("<a href=\""+engine.getEditURL(blogPage)+"\">New entry</a>");

        sb.append(
            "<a href=\"" + engine.getBaseURL() + "NewBlogEntry.jsp?page="
            + engine.encodeName(weblogName) + "\">" + entryText + "</a>");

        return sb.toString();
    }

    private int findFreeEntry(PageManager mgr, String baseName, String date)
            throws ProviderException
    {
        Collection everyone = mgr.getAllPages();
        int max = 0;

        String startString = WeblogPlugin.makeEntryPage(baseName, date, "");

        for (Iterator i = everyone.iterator(); i.hasNext();)
        {
            WikiPage p = (WikiPage) i.next();

            if (p.getName().startsWith(startString))
            {
                try
                {
                    String probableId = p.getName().substring(startString.length());

                    int id = Integer.parseInt(probableId);

                    if (id > max)
                    {
                        max = id;
                    }
                }
                catch (NumberFormatException e)
                {
                    if (log.isDebugEnabled())
                    {
                        log.debug("Was not a log entry: " + p.getName());
                    }
                }
            }
        }

        //
        //  Find the first page that has no page lock.
        //
        int idx = max + 1;

        while (idx < MAX_BLOG_ENTRIES)
        {
            WikiPage page =
                new WikiPage(WeblogPlugin.makeEntryPage(baseName, date, Integer.toString(idx)));
            PageLock lock = mgr.getCurrentLock(page);

            if (lock == null)
            {
                break;
            }

            idx++;
        }

        return idx;
    }
}
