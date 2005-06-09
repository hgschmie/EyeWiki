package de.softwareforge.eyewiki.tags;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import de.softwareforge.eyewiki.SearchResult;
import de.softwareforge.eyewiki.WikiContext;


/**
 * Iterates through Search result results.
 *
 * <P>
 * <B>Attributes</B>
 * </p>
 *
 * <UL>
 * <li>
 * max = how many search results should be shown.
 * </li>
 * </ul>
 *
 *
 * @author Janne Jalkanen
 *
 * @since 2.0
 */

// FIXME: Shares MUCH too much in common with IteratorTag.  Must refactor.
public class SearchResultIteratorTag
        extends IteratorTag
{
    /** DOCUMENT ME! */
    private int m_maxItems;

    /** DOCUMENT ME! */
    private int m_count = 0;

    /**
     * DOCUMENT ME!
     *
     * @param arg DOCUMENT ME!
     */
    public void setMaxItems(String arg)
    {
        m_maxItems = Integer.parseInt(arg);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public final int doStartTag()
    {
        //
        //  Do lazy eval if the search results have not been set.
        //
        if (m_iterator == null)
        {
            Collection searchresults =
                (Collection) pageContext.getAttribute("searchresults", PageContext.REQUEST_SCOPE);
            setList(searchresults);
        }

        m_count = 0;
        m_wikiContext =
            (WikiContext) pageContext.getAttribute(
                WikiTagBase.ATTR_CONTEXT, PageContext.REQUEST_SCOPE);

        return nextResult();
    }

    private int nextResult()
    {
        if ((m_iterator != null) && m_iterator.hasNext() && (m_count++ < m_maxItems))
        {
            SearchResult r = (SearchResult) m_iterator.next();

            WikiContext context = (WikiContext) m_wikiContext.clone();
            context.setPage(r.getPage());
            pageContext.setAttribute(WikiTagBase.ATTR_CONTEXT, context, PageContext.REQUEST_SCOPE);
            pageContext.setAttribute(getId(), r);

            return EVAL_BODY_AGAIN;
        }

        return SKIP_BODY;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int doAfterBody()
    {
        if (bodyContent != null)
        {
            try
            {
                JspWriter out = getPreviousOut();
                out.print(bodyContent.getString());
                bodyContent.clearBody();
            }
            catch (IOException e)
            {
                log.error("Unable to get inner tag text", e);

                // FIXME: throw something?
            }
        }

        return nextResult();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int doEndTag()
    {
        m_iterator = null;

        return super.doEndTag();
    }
}
