package de.softwareforge.eyewiki.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.log4j.Logger;

import de.softwareforge.eyewiki.WikiContext;


/**
 * Converts the body text into HTML content.
 *
 * @author Janne Jalkanen
 *
 * @since 2.0
 */
public class TranslateTag
        extends BodyTagSupport
{
    /** DOCUMENT ME! */
    private static final Logger log = Logger.getLogger(TranslateTag.class);

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws JspException DOCUMENT ME!
     */
    public final int doAfterBody()
            throws JspException
    {
        try
        {
            WikiContext context =
                (WikiContext) pageContext.getAttribute(
                    WikiTagBase.ATTR_CONTEXT, PageContext.REQUEST_SCOPE);
            BodyContent bc = getBodyContent();
            String wikiText = bc.getString();
            bc.clearBody();

            String result = context.getEngine().textToHTML(context, wikiText);

            getPreviousOut().write(result);
        }
        catch (Exception e)
        {
            log.error("Tag failed", e);
            throw new JspException("Tag failed, check logs: " + e.getMessage());
        }

        return SKIP_BODY;
    }
}
