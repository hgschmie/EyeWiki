package de.softwareforge.eyewiki.tags;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

import de.softwareforge.eyewiki.WikiConstants;
import de.softwareforge.eyewiki.WikiEngine;


/**
 * Writes an image link to the RSS file.
 *
 * @author Janne Jalkanen
 *
 * @since 2.0
 */
public class RSSImageLinkTag
        extends WikiTagBase
{
    /** DOCUMENT ME! */
    protected String m_title;

    /**
     * DOCUMENT ME!
     *
     * @param title DOCUMENT ME!
     */
    public void setTitle(String title)
    {
        m_title = title;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getTitle()
    {
        return m_title;
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

        String rssURL = engine.getGlobalRSSURL();

        if (rssURL != null)
        {
            StringBuffer sb = new StringBuffer("<div class=\"")
                    .append(WikiConstants.CSS_RSS)
                    .append("\"><a class=\"")
                    .append(WikiConstants.CSS_RSS)
                    .append("\" href=\"")
                    .append(rssURL)
                    .append("\"><img class=\"")
                    .append(WikiConstants.CSS_RSS)
                    .append("\" src=\"")
                    .append(engine.getBaseURL())
                    .append("images/xml.png\" alt=\"[RSS]\" title=\"")
                    .append(getTitle())
                    .append("\"/></a></div>");

            JspWriter out = pageContext.getOut();
            out.print(sb.toString());
        }

        return SKIP_BODY;
    }
}
