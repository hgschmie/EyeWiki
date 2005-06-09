package de.softwareforge.eyewiki.tags;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;

import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.WikiPage;


/**
 * Writes difference between two pages using a HTML table.  If there is no difference, includes the
 * body.
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
public class InsertDiffTag
        extends WikiTagBase
{
    /** DOCUMENT ME! */
    public static final String ATTR_OLDVERSION = "insertdiff.old";

    /** DOCUMENT ME! */
    public static final String ATTR_NEWVERSION = "insertdiff.new";

    /** DOCUMENT ME! */
    protected String m_pageName;

    /**
     * DOCUMENT ME!
     *
     * @param page DOCUMENT ME!
     */
    public void setPage(String page)
    {
        m_pageName = page;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getPage()
    {
        return m_pageName;
    }

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
        WikiEngine engine = m_wikiContext.getEngine();
        WikiPage page;

        if (m_pageName == null)
        {
            page = m_wikiContext.getPage();
        }
        else
        {
            page = engine.getPage(m_pageName);
        }

        Integer vernew =
            (Integer) pageContext.getAttribute(ATTR_NEWVERSION, PageContext.REQUEST_SCOPE);
        Integer verold =
            (Integer) pageContext.getAttribute(ATTR_OLDVERSION, PageContext.REQUEST_SCOPE);

        if (log.isInfoEnabled())
        {
            log.info("Request diff between version " + verold + " and " + vernew);
        }

        if (page != null)
        {
            JspWriter out = pageContext.getOut();

            String diff =
                engine.getDiff(page.getName(), vernew.intValue(), verold.intValue(), true);

            if (StringUtils.isEmpty(diff))
            {
                return EVAL_BODY_INCLUDE;
            }

            out.write(diff);
        }

        return SKIP_BODY;
    }
}
