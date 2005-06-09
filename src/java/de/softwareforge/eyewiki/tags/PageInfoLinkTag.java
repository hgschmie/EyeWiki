package de.softwareforge.eyewiki.tags;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

import de.softwareforge.eyewiki.WikiConstants;
import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.WikiPage;


/**
 * Writes a link to the Wiki PageInfo.  Body of the link becomes the actual text.
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

// FIXME: Refactor together with LinkToTag and EditLinkTag.
public class PageInfoLinkTag
        extends WikiLinkTag
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
        WikiEngine engine = m_wikiContext.getEngine();
        String pageName = m_pageName;

        if (m_pageName == null)
        {
            WikiPage p = m_wikiContext.getPage();

            if (p != null)
            {
                pageName = p.getName();
            }
            else
            {
                return SKIP_BODY;
            }
        }

        if (engine.pageExists(pageName))
        {
            JspWriter out = pageContext.getOut();

            String url = m_wikiContext.getURL(WikiContext.INFO, pageName);

            switch (m_format)
            {
            case ANCHOR:
                out.print("<a class=\"" + WikiConstants.CSS_LINK_PAGEINFO + "\" href=\"" + url + "\">");

                break;

            case URL:
                out.print(url);

                break;

            default:
                break;
            }

            return EVAL_BODY_INCLUDE;
        }

        return SKIP_BODY;
    }
}
