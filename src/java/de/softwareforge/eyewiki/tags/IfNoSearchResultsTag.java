package de.softwareforge.eyewiki.tags;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.jsp.PageContext;


/**
 * If there have been no search results, then outputs the body text.
 *
 * @author Janne Jalkanen
 *
 * @since 2.0
 */
public class IfNoSearchResultsTag
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

        if ((list == null) || (list.size() == 0))
        {
            return EVAL_BODY_INCLUDE;
        }

        return SKIP_BODY;
    }
}
