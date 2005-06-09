package de.softwareforge.eyewiki.tags;

import java.io.IOException;


import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.WikiPage;
import de.softwareforge.eyewiki.attachment.AttachmentManager;


/**
 * Includes body if page has attachments.
 *
 * @author Janne Jalkanen
 *
 * @since 2.0
 */
public class HasAttachmentsTag
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
        AttachmentManager mgr = engine.getAttachmentManager();

        if ((page != null) && mgr.attachmentsEnabled())
        {
            if (mgr.hasAttachments(page))
            {
                return EVAL_BODY_INCLUDE;
            }
        }

        return SKIP_BODY;
    }
}
