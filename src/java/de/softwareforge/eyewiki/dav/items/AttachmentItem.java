package de.softwareforge.eyewiki.dav.items;

import java.util.Collection;


import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.attachment.Attachment;


/**
 * DOCUMENT ME!
 *
 * @author jalkanen
 *
 * @since
 */
public class AttachmentItem
        extends PageDavItem
{
    /**
     * DOCUMENT ME!
     *
     * @param engine
     * @param att
     */
    public AttachmentItem(WikiEngine engine, Attachment att)
    {
        super(engine, att);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Collection getPropertySet()
    {
        Collection props = getCommonProperties();

        return props;
    }

    /* (non-Javadoc)
     * @see de.softwareforge.eyewiki.dav.items.PageDavItem#getHref()
     */
    public String getHref()
    {
        return m_engine.getURL(WikiContext.NONE, "dav/raw/" + m_page.getName(), null, true);
    }
}
