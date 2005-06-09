package de.softwareforge.eyewiki.tags;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.jsp.PageContext;


/**
 * Includes the body content, if there are any search results.
 *
 * @author Janne Jalkanen
 *
 * @since 2.0
 */
public class SearchResultsTag
        extends WikiTagBase
{
    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    public final int doWikiStartTag()
            throws IOException
    {
        Collection list =
            (Collection) pageContext.getAttribute("searchresults", PageContext.REQUEST_SCOPE);

        if (list != null)
        {
            return EVAL_BODY_INCLUDE;
        }

        return SKIP_BODY;
    }
}
