package de.softwareforge.eyewiki.tags;

import java.io.IOException;

import de.softwareforge.eyewiki.WikiPage;


/**
 * Outputs the version number of the previous version of this page.
 *
 * @author Janne Jalkanen
 *
 * @since 2.2
 */
public class PreviousVersionTag
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
        WikiPage page = m_wikiContext.getPage();

        int version = page.getVersion();

        version--;

        if (version > 0)
        {
            pageContext.getOut().print(version);
        }

        return SKIP_BODY;
    }
}
