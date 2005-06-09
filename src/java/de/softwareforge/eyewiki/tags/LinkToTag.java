package de.softwareforge.eyewiki.tags;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;


import de.softwareforge.eyewiki.WikiConstants;
import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiPage;
import de.softwareforge.eyewiki.attachment.Attachment;


/**
 * Writes a link to a Wiki page.  Body of the link becomes the actual text. The link is written
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
 * <li>
 * template - Which template should we link to.
 * </li>
 * </ul>
 *
 *
 * @author Janne Jalkanen
 *
 * @since 2.0
 */
public class LinkToTag
        extends WikiLinkTag
{
    /** DOCUMENT ME! */
    private String m_version = null;

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getVersion()
    {
        return m_version;
    }

    /**
     * DOCUMENT ME!
     *
     * @param arg DOCUMENT ME!
     */
    public void setVersion(String arg)
    {
        m_version = arg;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    public int doWikiStartTag()
            throws IOException
    {
        String pageName = m_pageName;
        boolean isattachment = false;

        if (m_pageName == null)
        {
            WikiPage p = m_wikiContext.getPage();

            if (p != null)
            {
                pageName = p.getName();

                isattachment = (p instanceof Attachment);
            }
            else
            {
                return SKIP_BODY;
            }
        }

        JspWriter out = pageContext.getOut();
        String url;
        String linkclass;

        if (isattachment)
        {
            url = m_wikiContext.getURL(
                    WikiContext.ATTACH, pageName,
                    (getVersion() != null)
                    ? ("version=" + getVersion())
                    : null);
            linkclass = WikiConstants.CSS_LINK_ATTACHMENT;
        }
        else
        {
            StringBuffer params = new StringBuffer();

            if (getVersion() != null)
            {
                params.append("version=" + getVersion());
            }

            if (getTemplate() != null)
            {
                params.append(((params.length() > 0)
                    ? "&amp;"
                    : "") + "skin=" + getTemplate());
            }

            url = m_wikiContext.getURL(WikiContext.VIEW, pageName, params.toString());
            linkclass = WikiConstants.CSS_WIKICONTENT;
        }

        switch (m_format)
        {
        case ANCHOR:
            StringBuffer sb = new StringBuffer("<a ");
            if (linkclass != null)
            {
                sb.append("class=\"" + linkclass + "\"");
            }
            sb.append("href=\"" + url + "\">");
            
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
