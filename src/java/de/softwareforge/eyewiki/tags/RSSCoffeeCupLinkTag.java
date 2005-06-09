package de.softwareforge.eyewiki.tags;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

import de.softwareforge.eyewiki.WikiEngine;


/**
 * Writes an image link to the RSS file with the Coffee Cup for Userland aggregation.
 *
 * @author Janne Jalkanen
 *
 * @since 2.0
 */
public class RSSCoffeeCupLinkTag
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
            JspWriter out = pageContext.getOut();
            out.print(
                "<a href=\"http://127.0.0.1:5335/system/pages/subscriptions/?url=" + rssURL + "\">");
            out.print("<img src=\"" + engine.getBaseURL() + "images/xmlCoffeeCup.png\"");
            out.print("border=\"0\" title=\"" + getTitle() + "\"/>");
            out.print("</a>");
        }

        return SKIP_BODY;
    }
}
