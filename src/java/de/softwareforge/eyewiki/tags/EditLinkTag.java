package de.softwareforge.eyewiki.tags;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

import de.softwareforge.eyewiki.WikiConstants;
import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.WikiPage;


/**
 * Writes an edit link.  Body of the link becomes the link text.
 *
 * <P>
 * <B>Attributes</B>
 * </p>
 *
 * <UL>
 * <li>
 * page - Page name to refer to.  Default is the current page.
 * </li>
 * <li>
 * format - Format, either "anchor" or "url".
 * </li>
 * <li>
 * version - Version number of the page to refer to.  Possible values are "this", meaning the
 * version of the current page; or a version number.  Default is always to point at the latest
 * version of the page.
 * </li>
 * </ul>
 *
 *
 * @author Janne Jalkanen
 *
 * @since 2.0
 */
public class EditLinkTag
        extends WikiLinkTag
{
    /** DOCUMENT ME! */
    private String m_version = null;

    /**
     * DOCUMENT ME!
     *
     * @param vers DOCUMENT ME!
     */
    public void setVersion(String vers)
    {
        m_version = vers;
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
        WikiPage page = null;
        String versionString = "";
        String pageName = null;

        //
        //  Determine the page and the link.
        //
        if (m_pageName == null)
        {
            page = m_wikiContext.getPage();

            if (page == null)
            {
                // You can't call this on the page itself anyways.
                return SKIP_BODY;
            }
            else
            {
                pageName = page.getName();
            }
        }
        else
        {
            pageName = m_pageName;
        }

        //
        //  Determine the latest version, if the version attribute is "this".
        //
        if (m_version != null)
        {
            if ("this".equalsIgnoreCase(m_version))
            {
                if (page == null)
                {
                    // No page, so go fetch according to page name.
                    page = engine.getPage(m_pageName);
                }

                if (page != null)
                {
                    versionString = "version=" + page.getVersion();
                }
            }
            else
            {
                versionString = "version=" + m_version;
            }
        }

        //
        //  Finally, print out the correct link, according to what
        //  user commanded.
        //
        JspWriter out = pageContext.getOut();

        switch (m_format)
        {
        case ANCHOR:
            StringBuffer sb = new StringBuffer("<a class=\"")
                    .append(WikiConstants.CSS_WIKICONTENT)
                    .append("\" href=\"")
                    .append(m_wikiContext.getURL(WikiContext.EDIT, pageName, versionString))
                    .append("\">");

            out.print(sb.toString());
            break;

        case URL:
            out.print(m_wikiContext.getURL(WikiContext.EDIT, pageName, versionString));

            break;

        default:
            break;
        }

        return EVAL_BODY_INCLUDE;
    }
}
