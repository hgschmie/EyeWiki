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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.lang.time.DateFormatUtils;

import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiEngine;

import org.jdom.Element;
import org.jdom.Namespace;

/**
 * DOCUMENT ME!
 *
 * @author jalkanen
 *
 * @since
 */
public class DirectoryItem
        extends DavItem
{
    /** DOCUMENT ME! */
    private String m_name;

    /**
     * Creates a new DirectoryItem object.
     *
     * @param engine DOCUMENT ME!
     * @param name DOCUMENT ME!
     */
    public DirectoryItem(WikiEngine engine, String name)
    {
        super(engine);
        m_name = name;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Collection getPropertySet()
    {
        ArrayList ts = new ArrayList();
        Namespace davns = Namespace.getNamespace("DAV:");

        ts.add(new Element("resourcetype", davns).addContent(new Element("collection", davns)));

        Element txt = new Element("displayname", davns);
        txt.setText(m_name);
        ts.add(txt);

        ts.add(new Element("getcontentlength", davns).setText("0"));
        ts.add(new Element("getlastmodified", davns).setText(DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format(new Date())));

        return ts;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getHref()
    {
        String davurl = "dav" + (m_name.equals("/") ? "" : "/") + m_name; //FIXME: Fixed, should determine from elsewhere

        if (!davurl.endsWith("/"))
        {
            davurl += "/";
        }

        return m_engine.getURL(WikiContext.NONE, davurl, null, true);
    }

    /**
     * DOCUMENT ME!
     *
     * @param di DOCUMENT ME!
     */
    public void addDavItem(DavItem di)
    {
        m_items.add(di);
    }
}
