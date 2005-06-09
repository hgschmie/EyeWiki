package de.softwareforge.eyewiki.tags;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.jsp.PageContext;


/**
 * Outputs the size of the search results list, if it contains any items. Otherwise outputs an
 * empty string.
 *
 * @author Janne Jalkanen
 *
 * @since 2.0
 */
public class SearchResultsSizeTag
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
            pageContext.getOut().print(list.size());
        }

        return SKIP_BODY;
    }
}
