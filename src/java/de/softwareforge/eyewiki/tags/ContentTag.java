package de.softwareforge.eyewiki.tags;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;


import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.providers.ProviderException;
import de.softwareforge.eyewiki.util.TextUtil;


/**
 * Is used as a "super include" tag, which can include the proper context based on the wikicontext.
 *
 * @author Janne Jalkanen
 *
 * @since 2.2
 */
public class ContentTag
        extends WikiTagBase
{
    /** DOCUMENT ME! */
    private static Map c_defaultMappings;

    /**
     * Lists the default pages for each JSP page.  It first contains the name of the context and
     * then the page which should be included:
     * <pre>
     *  public static final String[] DEFAULT_JSP_PAGES = {
     *     WikiContext.VIEW,     "PageContent.jsp",
     *     WikiContext.DIFF,     "DiffContent.jsp", ...
     *  </pre>
     * A Property object is built using TextUtil.createProperties();
     *
     * @see TextUtil.createProperties()
     */
    private static final String [] DEFAULT_JSP_PAGES = {
        WikiContext.VIEW,     "PageContent.jsp",
        WikiContext.DIFF,     "DiffContent.jsp",
        WikiContext.INFO,     "InfoContent.jsp",
        WikiContext.PREVIEW,  "PreviewContent.jsp",
        WikiContext.CONFLICT, "ConflictContent.jsp",
        WikiContext.FIND,     "FindContent.jsp",
        WikiContext.PREFS,    "PreferencesContent.jsp",
        WikiContext.ERROR,    "DisplayMessage.jsp",
        WikiContext.EDIT,     "EditContent.jsp",
        WikiContext.COMMENT,  "CommentContent.jsp"
    };

    static
    {
        c_defaultMappings = TextUtil.createMap(null, DEFAULT_JSP_PAGES);
    }

    /** DOCUMENT ME! */
    private Map m_mappings = new HashMap(c_defaultMappings);

    /**
     * DOCUMENT ME!
     *
     * @param s DOCUMENT ME!
     */
    public void setView(String s)
    {
        m_mappings.put(WikiContext.VIEW, s);
    }

    /**
     * DOCUMENT ME!
     *
     * @param s DOCUMENT ME!
     */
    public void setDiff(String s)
    {
        m_mappings.put(WikiContext.DIFF, s);
    }

    /**
     * DOCUMENT ME!
     *
     * @param s DOCUMENT ME!
     */
    public void setInfo(String s)
    {
        m_mappings.put(WikiContext.INFO, s);
    }

    /**
     * DOCUMENT ME!
     *
     * @param s DOCUMENT ME!
     */
    public void setPreview(String s)
    {
        m_mappings.put(WikiContext.PREVIEW, s);
    }

    /**
     * DOCUMENT ME!
     *
     * @param s DOCUMENT ME!
     */
    public void setConflict(String s)
    {
        m_mappings.put(WikiContext.CONFLICT, s);
    }

    /**
     * DOCUMENT ME!
     *
     * @param s DOCUMENT ME!
     */
    public void setFind(String s)
    {
        m_mappings.put(WikiContext.FIND, s);
    }

    /**
     * DOCUMENT ME!
     *
     * @param s DOCUMENT ME!
     */
    public void setPrefs(String s)
    {
        m_mappings.put(WikiContext.PREFS, s);
    }

    /**
     * DOCUMENT ME!
     *
     * @param s DOCUMENT ME!
     */
    public void setError(String s)
    {
        m_mappings.put(WikiContext.ERROR, s);
    }

    /**
     * DOCUMENT ME!
     *
     * @param s DOCUMENT ME!
     */
    public void setEdit(String s)
    {
        m_mappings.put(WikiContext.EDIT, s);
    }

    /**
     * DOCUMENT ME!
     *
     * @param s DOCUMENT ME!
     */
    public void setComment(String s)
    {
        m_mappings.put(WikiContext.COMMENT, s);
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
            String jspPage = (String) m_mappings.get(m_wikiContext.getRequestContext());

            if (jspPage == null)
            {
                throw new JspException(
                    "This template uses <wiki:Content/> in an unsupported context: "
                    + m_wikiContext.getRequestContext());
            }

            String page =
                m_wikiContext.getEngine().getTemplateManager().findJSP(
                    pageContext, m_wikiContext.getTemplate(), jspPage);
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
