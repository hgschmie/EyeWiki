package de.softwareforge.eyewiki.tags;

import java.io.IOException;

import de.softwareforge.eyewiki.WikiPage;


/**
 * Writes the version of the current page.  If this is marked as the current version, then includes
 * body as text instead of version number.
 *
 * @author Janne Jalkanen
 *
 * @since 2.0
 */
public class PageVersionTag
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

        if (page != null)
        {
            int version = page.getVersion();

            if (version > 0)
            {
                pageContext.getOut().print(Integer.toString(version));

                return SKIP_BODY;
            }
        }

        return EVAL_BODY_INCLUDE;
    }
}
