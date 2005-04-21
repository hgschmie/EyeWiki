/*
    JSPWiki - a JSP-based WikiWiki clone.

    Copyright (C) 2003 Janne Jalkanen (Janne.Jalkanen@iki.fi)

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation; either version 2.1 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package com.ecyrd.jspwiki.plugin;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ecyrd.jspwiki.PageLock;
import com.ecyrd.jspwiki.WikiConstants;
import com.ecyrd.jspwiki.WikiContext;
import com.ecyrd.jspwiki.WikiEngine;
import com.ecyrd.jspwiki.manager.PageManager;


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

    public ListLocksPlugin(WikiEngine engine)
    {
        this.engine = engine;
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

        PageManager mgr = engine.getPageManager();
        List locks = mgr.getActiveLocks();

        result.append("<table class=\"" + WikiConstants.CSS_WIKICONTENT + "\">\n");
        result.append("<tr class=\"" + WikiConstants.CSS_WIKICONTENT + "\">\n");
        result.append("<th class=\"" + WikiConstants.CSS_WIKICONTENT + "\">Page</th>");
        result.append("<th class=\"" + WikiConstants.CSS_WIKICONTENT + "\">Locked by</th>");
        result.append("<th class=\"" + WikiConstants.CSS_WIKICONTENT + "\">Acquired</th>");
        result.append("<th class=\"" + WikiConstants.CSS_WIKICONTENT + "\">Expires</th>\n");
        result.append("</tr>");

        if (locks.size() == 0)
        {
            result.append("<tr class=\"" + WikiConstants.CSS_WIKICONTENT + "\">");
            result.append("<td  class=\"" + WikiConstants.CSS_WIKICONTENT + "\" colspan=\"4\">No locks exist currently.</td></tr>\n");
        }
        else
        {
            for (Iterator i = locks.iterator(); i.hasNext();)
            {
                PageLock lock = (PageLock) i.next();

                result.append("<tr class=\"" + WikiConstants.CSS_WIKICONTENT + "\">");
                result.append("<td class=\"" + WikiConstants.CSS_WIKICONTENT + "\">" + lock.getPage().getName() + "</td>");
                result.append("<td class=\"" + WikiConstants.CSS_WIKICONTENT + "\">" + lock.getLocker() + "</td>");
                result.append("<td class=\"" + WikiConstants.CSS_WIKICONTENT + "\">" + lock.getAcquisitionTime() + "</td>");
                result.append("<td class=\"" + WikiConstants.CSS_WIKICONTENT + "\">" + lock.getExpiryTime() + "</td>");
                result.append("</tr>\n");
            }
        }

        result.append("</table>");

        return result.toString();
    }
}
