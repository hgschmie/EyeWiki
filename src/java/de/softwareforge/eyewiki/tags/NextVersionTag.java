package de.softwareforge.eyewiki.tags;

import java.io.IOException;

import de.softwareforge.eyewiki.WikiPage;


/**
 * Writes the version number of the next version of the page.
 *
 * @author Janne Jalkanen
 *
 * @since 2.2
 */
public class NextVersionTag
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

        if (version == -1)
        {
            version = -1;
        }
        else
        {
            version++;
        }

        pageContext.getOut().print(version);

        return SKIP_BODY;
    }
}
