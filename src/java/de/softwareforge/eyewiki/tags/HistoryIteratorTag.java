package de.softwareforge.eyewiki.tags;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import org.apache.log4j.Logger;


import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.WikiPage;
import de.softwareforge.eyewiki.providers.ProviderException;


/**
 * Iterates through tags.
 *
 * <P>
 * <B>Attributes</B>
 * </p>
 *
 * <UL>
 * <li>
 * page - Page name to refer to.  Default is the current page.
 * </li>
 * </ul>
 *
 *
 * @author Janne Jalkanen
 *
 * @since 2.0
 */

// FIXME: Too much in common with IteratorTag - REFACTOR
public class HistoryIteratorTag
        extends IteratorTag
{
    /** DOCUMENT ME! */
    private static final Logger log = Logger.getLogger(HistoryIteratorTag.class);

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public final int doStartTag()
    {
        m_wikiContext =
            (WikiContext) pageContext.getAttribute(
                WikiTagBase.ATTR_CONTEXT, PageContext.REQUEST_SCOPE);

        WikiEngine engine = m_wikiContext.getEngine();
        WikiPage page;

        page = m_wikiContext.getPage();

        try
        {
            if ((page != null) && engine.pageExists(page))
            {
                Collection versions = engine.getVersionHistory(page.getName());

                if (versions == null)
                {
                    // There is no history
                    return SKIP_BODY;
                }

                m_iterator = versions.iterator();

                if (m_iterator.hasNext())
                {
                    WikiContext context = (WikiContext) m_wikiContext.clone();
                    context.setPage((WikiPage) m_iterator.next());
                    pageContext.setAttribute(
                        WikiTagBase.ATTR_CONTEXT, context, PageContext.REQUEST_SCOPE);
                    pageContext.setAttribute(getId(), context.getPage());
                }
                else
                {
                    return SKIP_BODY;
                }
            }

            return EVAL_BODY_AGAIN;
        }
        catch (ProviderException e)
        {
            log.fatal("Provider failed while trying to iterator through history", e);

            // FIXME: THrow something.
        }

        return SKIP_BODY;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public final int doAfterBody()
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

        if ((m_iterator != null) && m_iterator.hasNext())
        {
            WikiContext context = (WikiContext) m_wikiContext.clone();
            context.setPage((WikiPage) m_iterator.next());
            pageContext.setAttribute(WikiTagBase.ATTR_CONTEXT, context, PageContext.REQUEST_SCOPE);
            pageContext.setAttribute(getId(), context.getPage());

            return EVAL_BODY_AGAIN;
        }
        else
        {
            return SKIP_BODY;
        }
    }
}
