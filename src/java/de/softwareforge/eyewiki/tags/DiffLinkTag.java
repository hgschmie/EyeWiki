package de.softwareforge.eyewiki.tags;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

import de.softwareforge.eyewiki.WikiConstants;
import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.WikiPage;
import de.softwareforge.eyewiki.WikiProvider;


/**
 * Writes a diff link.  Body of the link becomes the link text.
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
 * version - The older of these versions.  May be an integer to signify a version number, or the
 * text "latest" to signify the latest version. If not specified, will default to "latest".  May
 * also be "previous" to signify a version prior to this particular version.
 * </li>
 * <li>
 * newVersion - The newer of these versions.  Can also be "latest", or "previous".  Defaults to
 * "latest".
 * </li>
 * </ul>
 *
 * If the page does not exist, this tag will fail silently, and not evaluate its body contents.
 *
 * @author Janne Jalkanen
 *
 * @since 2.0
 */
public class DiffLinkTag
        extends WikiLinkTag
{
    /** DOCUMENT ME! */
    public static final String VER_LATEST = "latest";

    /** DOCUMENT ME! */
    public static final String VER_PREVIOUS = "previous";

    /** DOCUMENT ME! */
    public static final String VER_CURRENT = "current";

    /** DOCUMENT ME! */
    private String m_version = VER_LATEST;

    /** DOCUMENT ME! */
    private String m_newVersion = VER_LATEST;

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public final String getVersion()
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
     */
    public final String getNewVersion()
    {
        return m_newVersion;
    }

    /**
     * DOCUMENT ME!
     *
     * @param arg DOCUMENT ME!
     */
    public void setNewVersion(String arg)
    {
        m_newVersion = arg;
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

        int r1 = 0;
        int r2 = 0;

        //
        //  In case the page does not exist, we fail silently.
        //
        if (!engine.pageExists(pageName))
        {
            return SKIP_BODY;
        }

        if (VER_LATEST.equals(getVersion()))
        {
            WikiPage latest = engine.getPage(pageName, WikiProvider.LATEST_VERSION);

            r1 = latest.getVersion();
        }
        else if (VER_PREVIOUS.equals(getVersion()))
        {
            r1 = m_wikiContext.getPage().getVersion() - 1;
            r1 = (r1 < 1)
                ? 1
                : r1;
        }
        else if (VER_CURRENT.equals(getVersion()))
        {
            r1 = m_wikiContext.getPage().getVersion();
        }
        else
        {
            r1 = Integer.parseInt(getVersion());
        }

        if (VER_LATEST.equals(getNewVersion()))
        {
            WikiPage latest = engine.getPage(pageName, WikiProvider.LATEST_VERSION);

            r2 = latest.getVersion();
        }
        else if (VER_PREVIOUS.equals(getNewVersion()))
        {
            r2 = m_wikiContext.getPage().getVersion() - 1;
            r2 = (r2 < 1)
                ? 1
                : r2;
        }
        else if (VER_CURRENT.equals(getNewVersion()))
        {
            r2 = m_wikiContext.getPage().getVersion();
        }
        else
        {
            r2 = Integer.parseInt(getNewVersion());
        }

        String url = m_wikiContext.getURL(WikiContext.DIFF, pageName, "r1=" + r1 + "&amp;r2=" + r2);

        switch (m_format)
        {
        case ANCHOR:

            StringBuffer sb = new StringBuffer("<a class=\"")
                    .append(WikiConstants.CSS_WIKICONTENT)
                    .append("\" href=\"")
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
