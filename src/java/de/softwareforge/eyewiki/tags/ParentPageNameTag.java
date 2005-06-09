package de.softwareforge.eyewiki.tags;

import java.io.IOException;


import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.WikiPage;
import de.softwareforge.eyewiki.attachment.Attachment;


/**
 * Returns the parent of the currently requested page.  Weblog entries are recognized as subpages
 * of the weblog page.
 *
 * @author Janne Jalkanen
 *
 * @since 2.0
 */
public class ParentPageNameTag
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
                pageContext.getOut().print(
                    engine.beautifyTitle(((Attachment) page).getParentName()));
            }
            else
            {
                String name = page.getName();

                int entrystart = name.indexOf("_blogentry_");

                if (entrystart != -1)
                {
                    name = name.substring(0, entrystart);
                }

                int commentstart = name.indexOf("_comments_");

                if (commentstart != -1)
                {
                    name = name.substring(0, commentstart);
                }

                pageContext.getOut().print(engine.beautifyTitle(name));
            }
        }

        return SKIP_BODY;
    }
}
