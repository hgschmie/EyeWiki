package de.softwareforge.eyewiki.dav.items;

/*
 * ========================================================================
 *
 * eyeWiki - a WikiWiki clone written in Java
 *
 * ========================================================================
 *
 * Copyright (C) 2005 Henning Schmiedehausen <henning@software-forge.com>
 *
 * based on
 *
 * JSPWiki - a JSP-based WikiWiki clone.
 * Copyright (C) 2002-2005 Janne Jalkanen (Janne.Jalkanen@iki.fi)
 *
 * ========================================================================
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * ========================================================================
 */

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
