/*
   JSPWiki - a JSP-based WikiWiki clone.

   Copyright (C) 2001-2005 Janne Jalkanen (Janne.Jalkanen@iki.fi)

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

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.apache.ecs.xhtml.table;
import org.apache.ecs.xhtml.td;
import org.apache.ecs.xhtml.th;
import org.apache.ecs.xhtml.tr;
import org.apache.log4j.Logger;

import com.ecyrd.jspwiki.SearchResult;
import com.ecyrd.jspwiki.WikiConstants;
import com.ecyrd.jspwiki.WikiContext;
import com.ecyrd.jspwiki.WikiEngine;


/**
 * DOCUMENT ME!
 *
 * @author jalkanen
 *
 * @since
 */
public class Search
        implements WikiPlugin
{
    /** DOCUMENT ME! */
    private static final Logger log = Logger.getLogger(Search.class);

    /** DOCUMENT ME! */
    public static final String PARAM_QUERY = "query";

    /** DOCUMENT ME! */
    public static final String PARAM_SET = "set";

    /** DOCUMENT ME! */
    public static final String DEFAULT_SETNAME = "_defaultSet";

    /** DOCUMENT ME! */
    public static final String PARAM_MAX = "max";

    /* (non-Javadoc)
     * @see com.ecyrd.jspwiki.plugin.WikiPlugin#execute(com.ecyrd.jspwiki.WikiContext, java.util.Map)
     */
    public String execute(WikiContext context, Map params)
            throws PluginException
    {
        int maxItems = Integer.MAX_VALUE;
        Collection results = null;

        String queryString = (String) params.get(PARAM_QUERY);
        String set = (String) params.get(PARAM_SET);
        String max = (String) params.get(PARAM_MAX);

        if (set == null)
        {
            set = DEFAULT_SETNAME;
        }

        if (max != null)
        {
            maxItems = Integer.parseInt(max);
        }

        if (queryString == null)
        {
            results = (Collection) context.getVariable(set);
        }
        else
        {
            results = doBasicQuery(context, queryString);
            context.setVariable(set, results);
        }

        String res = "";

        if (results != null)
        {
            res = renderResults(results, context, maxItems);
        }

        return res;
    }

    private Collection doBasicQuery(WikiContext context, String query)
    {
        if (log.isInfoEnabled())
        {
            log.info("Searching for string " + query);
        }

        Collection list = context.getEngine().findPages(query);

        return list;
    }

    private String renderResults(Collection results, WikiContext context, int maxItems)
    {
        WikiEngine engine = context.getEngine();
        table t = new table();
        t.setClass(WikiConstants.CSS_SEARCH);

        tr row = new tr();
        t.addElement(row);

        th searchHeader = new th();
        searchHeader.setClass(WikiConstants.CSS_SEARCH);
        searchHeader.addElement("Page");
        row.addElement(searchHeader);
        
        th scoreHeader = new th();
        scoreHeader.setClass(WikiConstants.CSS_SEARCHSCORE);
        scoreHeader.addElement("Score");
        row.addElement(scoreHeader);

        int idx = 0;

        for (Iterator i = results.iterator(); i.hasNext() && (idx++ <= maxItems);)
        {
            SearchResult sr = (SearchResult) i.next();
            row = new tr();

            td name = new td();
            name.setClass(WikiConstants.CSS_SEARCH);
            name.addElement(
                "<a class=\"" + WikiConstants.CSS_SEARCH + "\" href=\"" + context.getURL(WikiContext.VIEW, sr.getPage().getName()) + "\">"
                + engine.beautifyTitle(sr.getPage().getName()) + "</a>");
            row.addElement(name);

            td score = new td();
            score.setClass(WikiConstants.CSS_SEARCHSCORE);
            score.addElement("" + sr.getScore());
            row.addElement(score);

            t.addElement(row);
        }

        if (results.isEmpty())
        {
            row = new tr();
            td result = new td();
            result.setColSpan(2).setClass(WikiConstants.CSS_SEARCH);
            result.addElement("No results");
            row.addElement(result);
            t.addElement(row);
        }

        return t.toString();
    }
}
