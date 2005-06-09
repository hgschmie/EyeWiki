package de.softwareforge.eyewiki.tags;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.log4j.Logger;

import de.softwareforge.eyewiki.WikiContext;


/**
 * This is a class that provides the same services as the WikiTagBase, but this time it works for
 * the BodyTagSupport base class.
 *
 * @author jalkanen
 */
public abstract class WikiBodyTag
        extends BodyTagSupport
{
    /** DOCUMENT ME! */
    private static final Logger log = Logger.getLogger(WikiBodyTag.class);

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
                (WikiContext) pageContext.getAttribute(
                    WikiTagBase.ATTR_CONTEXT, PageContext.REQUEST_SCOPE);

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
     * A local stub for doing tags.  This is just called after the local variables have been set.
     *
     * @return As doStartTag()
     *
     * @throws JspException
     * @throws IOException
     */
    public abstract int doWikiStartTag()
            throws JspException, IOException;
}
