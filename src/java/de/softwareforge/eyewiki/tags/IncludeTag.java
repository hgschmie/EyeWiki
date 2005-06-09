package de.softwareforge.eyewiki.tags;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;

import de.softwareforge.eyewiki.providers.ProviderException;


/**
 * Includes an another JSP page, making sure that we actually pass the WikiContext correctly.
 *
 * @author Janne Jalkanen
 *
 * @since 2.0
 */

// FIXME: Perhaps unnecessary?
public class IncludeTag
        extends WikiTagBase
{
    /** DOCUMENT ME! */
    protected String m_page;

    /**
     * DOCUMENT ME!
     *
     * @param page DOCUMENT ME!
     */
    public void setPage(String page)
    {
        m_page = page;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getPage()
    {
        return m_page;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     * @throws ProviderException DOCUMENT ME!
     */
    public final int doWikiStartTag()
            throws IOException, ProviderException
    {
        // WikiEngine engine = m_wikiContext.getEngine();
        return SKIP_BODY;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws JspException DOCUMENT ME!
     */
    public final int doEndTag()
            throws JspException
    {
        try
        {
            String page =
                m_wikiContext.getEngine().getTemplateManager().findJSP(
                    pageContext, m_wikiContext.getTemplate(), m_page);
            pageContext.include(page);
        }
        catch (ServletException e)
        {
            log.warn(
                "Including failed, got a servlet exception from sub-page. "
                + "Rethrowing the exception to the JSP engine.", e);
            throw new JspException(e.getMessage());
        }
        catch (IOException e)
        {
            log.warn(
                "I/O exception - probably the connection was broken. "
                + "Rethrowing the exception to the JSP engine.", e);
            throw new JspException(e.getMessage());
        }

        return EVAL_PAGE;
    }
}
