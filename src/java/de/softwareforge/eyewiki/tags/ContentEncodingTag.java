package de.softwareforge.eyewiki.tags;

import java.io.IOException;

import de.softwareforge.eyewiki.WikiEngine;


/**
 * Returns the app name.
 *
 * @author Janne Jalkanen
 *
 * @since 2.0
 */
public class ContentEncodingTag
        extends WikiTagBase
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

        pageContext.getOut().print(engine.getContentEncoding());

        return SKIP_BODY;
    }
}
