package de.softwareforge.eyewiki.tags;

import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.log4j.Logger;


import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.plugin.PluginManager;


/**
 * Inserts any Wiki plugin.  The body of the tag becomes then the body for the plugin.
 *
 * <P>
 * <B>Attributes</B>
 * </p>
 *
 * <UL>
 * <li>
 * plugin - name of the plugin you want to insert.
 * </li>
 * <li>
 * args   - An argument string for the tag.
 * </li>
 * </ul>
 *
 *
 * @author Janne Jalkanen
 *
 * @since 2.0
 */
public class PluginTag
        extends BodyTagSupport
{
    /** DOCUMENT ME! */
    private static final Logger log = Logger.getLogger(PluginTag.class);

    /** DOCUMENT ME! */
    private String m_plugin;

    /** DOCUMENT ME! */
    private String m_args;

    /** DOCUMENT ME! */
    protected WikiContext m_wikiContext;

    /**
     * DOCUMENT ME!
     *
     * @param p DOCUMENT ME!
     */
    public void setPlugin(String p)
    {
        m_plugin = p;
    }

    /**
     * DOCUMENT ME!
     *
     * @param a DOCUMENT ME!
     */
    public void setArgs(String a)
    {
        m_args = a;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws JspException DOCUMENT ME!
     */
    public int doAfterBody()
            throws JspException
    {
        try
        {
            m_wikiContext =
                (WikiContext) pageContext.getAttribute(
                    WikiTagBase.ATTR_CONTEXT, PageContext.REQUEST_SCOPE);

            WikiEngine engine = m_wikiContext.getEngine();

            PluginManager pm = engine.getPluginManager();

            Map argmap = pm.parseArgs(m_args);

            BodyContent bc = getBodyContent();

            if (bc != null)
            {
                argmap.put("_body", bc.getString());
            }

            String result = pm.execute(m_wikiContext, m_plugin, argmap);

            pageContext.getOut().write(result);

            return SKIP_BODY;
        }
        catch (Exception e)
        {
            log.error("Failed to insert plugin", e);
            throw new JspException("Tag failed, check logs: " + e.getMessage());
        }
    }
}
