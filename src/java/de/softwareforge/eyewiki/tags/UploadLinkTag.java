package de.softwareforge.eyewiki.tags;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

import de.softwareforge.eyewiki.WikiConstants;
import de.softwareforge.eyewiki.WikiContext;


/**
 * Writes a link to the upload page.  Body of the link becomes the actual text. The link is written
 * regardless to whether the page exists or not.
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
 * format - either "anchor" or "url" to output either an &lt;A&gt;... or just the HREF part of one.
 * </li>
 * </ul>
 *
 *
 * @author Janne Jalkanen
 *
 * @since 2.0
 */
public class UploadLinkTag
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
        String pageName = m_pageName;

        if (m_pageName == null)
        {
            if (m_wikiContext.getPage() != null)
            {
                pageName = m_wikiContext.getPage().getName();
            }
            else
            {
                return SKIP_BODY;
            }
        }

        JspWriter out = pageContext.getOut();

        String url = m_wikiContext.getURL(WikiContext.UPLOAD, pageName);

        switch (m_format)
        {
        case ANCHOR:
            StringBuffer sb = new StringBuffer("<a class=\"")
                    .append(WikiConstants.CSS_WIKICONTENT)
                    .append("\" target=\"_new\" href=\"")
                    .append(url)
                    .append("\">");

            out.print(sb.toString());

            break;

        case URL:
            out.print(url);

            break;

        default:
            break;
        }

        return EVAL_BODY_INCLUDE;
    }
}
