package de.softwareforge.eyewiki.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.log4j.Logger;

import de.softwareforge.eyewiki.WikiContext;


/**
 * Base class for eyeWiki tags.  You do not necessarily have to derive from this class, since this
 * does some initialization.
 *
 * <P>
 * This tag is only useful if you're having an "empty" tag, with no body content.
 * </p>
 *
 * @author Janne Jalkanen
 *
 * @since 2.0
 */
public abstract class WikiTagBase
        extends TagSupport
{
    /** DOCUMENT ME! */
    public static final String ATTR_CONTEXT = "eyewiki.context";

    /** DOCUMENT ME! */
    protected Logger log = Logger.getLogger(this.getClass());

    /** DOCUMENT ME! */
    protected WikiContext m_wikiContext;

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws JspException DOCUMENT ME!
     */
    public int doStartTag()
            throws JspException
    {
        try
        {
            m_wikiContext =
                (WikiContext) pageContext.getAttribute(ATTR_CONTEXT, PageContext.REQUEST_SCOPE);

            if (m_wikiContext == null)
            {
                throw new JspException("WikiContext may not be NULL - serious internal problem!");
            }

            return doWikiStartTag();
        }
        catch (Exception e)
        {
            log.error("Tag failed", e);
            throw new JspException("Tag failed, check logs: " + e.getMessage());
        }
    }

    /**
     * This method is allowed to do pretty much whatever he wants. We then catch all mistakes.
     *
     * @return DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public abstract int doWikiStartTag()
            throws Exception;

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws JspException DOCUMENT ME!
     */
    public int doEndTag()
            throws JspException
    {
        return EVAL_PAGE;
    }
}
