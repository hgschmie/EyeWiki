package de.softwareforge.eyewiki.plugin;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.apache.ecs.xhtml.table;
import org.apache.ecs.xhtml.td;
import org.apache.ecs.xhtml.th;
import org.apache.ecs.xhtml.tr;
import org.apache.log4j.Logger;

import de.softwareforge.eyewiki.SearchResult;
import de.softwareforge.eyewiki.WikiConstants;
import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiEngine;


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

    /** DOCUMENT ME! */
    protected final WikiEngine engine;

    public Search(WikiEngine engine)
    {
        this.engine = engine;
    }

    /* (non-Javadoc)
     * @see de.softwareforge.eyewiki.plugin.WikiPlugin#execute(de.softwareforge.eyewiki.WikiContext, java.util.Map)
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
            results = doBasicQuery(queryString);
            context.setVariable(set, results);
        }

        String res = "";

        if (results != null)
        {
            res = renderResults(results, context, maxItems);
        }

        return res;
    }

    private Collection doBasicQuery(String query)
    {
        if (log.isInfoEnabled())
        {
            log.info("Searching for string " + query);
        }

        Collection list = engine.findPages(query);

        return list;
    }

    private String renderResults(Collection results, WikiContext context, int maxItems)
    {
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
