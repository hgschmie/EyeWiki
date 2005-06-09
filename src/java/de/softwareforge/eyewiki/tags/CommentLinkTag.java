package de.softwareforge.eyewiki.tags;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

import de.softwareforge.eyewiki.WikiConstants;
import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiPage;


/**
 * Writes a comment link.  Body of the link becomes the link text.
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
 * </ul>
 *
 *
 * @author Janne Jalkanen
 *
 * @since 2.0
 */
public class CommentLinkTag
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
        WikiPage page = null;
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
                    .append(getCommentURL(pageName))
                    .append("\">");

            out.print(sb.toString());
            break;

        case URL:
            out.print(getCommentURL(pageName));

            break;

        default:
            break;
        }

        return EVAL_BODY_INCLUDE;
    }

    private String getCommentURL(String pageName)
    {
        return m_wikiContext.getURL(WikiContext.COMMENT, pageName);
    }
}
