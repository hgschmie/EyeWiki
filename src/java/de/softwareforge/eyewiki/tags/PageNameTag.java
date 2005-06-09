package de.softwareforge.eyewiki.tags;

import java.io.IOException;


import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.WikiPage;
import de.softwareforge.eyewiki.attachment.Attachment;


/**
 * Returns the currently requested page name.
 *
 * @author Janne Jalkanen
 *
 * @since 2.0
 */
public class PageNameTag
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
        WikiPage page = m_wikiContext.getPage();

        if (page != null)
        {
            if (page instanceof Attachment)
            {
                pageContext.getOut().print(((Attachment) page).getFileName());
            }
            else
            {
                pageContext.getOut().print(engine.beautifyTitle(page.getName()));
            }
        }

        return SKIP_BODY;
    }
}
