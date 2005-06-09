package de.softwareforge.eyewiki.tags;

import java.io.IOException;


/**
 * Writes the eyewiki.baseURL property.
 *
 * @author Janne Jalkanen
 *
 * @since 2.2
 */
public class BaseURLTag
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
        pageContext.getOut().print(m_wikiContext.getEngine().getBaseURL());

        return SKIP_BODY;
    }
}
