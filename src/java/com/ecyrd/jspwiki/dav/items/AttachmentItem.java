/*
 * (C) Janne Jalkanen 2005
 *
 */
package com.ecyrd.jspwiki.dav.items;

import java.util.Collection;

import com.ecyrd.jspwiki.WikiContext;
import com.ecyrd.jspwiki.WikiEngine;
import com.ecyrd.jspwiki.attachment.Attachment;


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
     * @see com.ecyrd.jspwiki.dav.items.PageDavItem#getHref()
     */
    public String getHref()
    {
        return m_engine.getURL(WikiContext.NONE, "dav/raw/" + m_page.getName(), null, true);
    }
}
