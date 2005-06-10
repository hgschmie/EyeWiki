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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang.time.DateFormatUtils;

import org.jdom.Element;
import org.jdom.Namespace;

import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.WikiPage;


/**
 * DOCUMENT ME!
 *
 * @author jalkanen
 *
 * @since
 */
public class PageDavItem
        extends DavItem
{
    /**
     * DOCUMENT ME!
     */
    protected WikiPage m_page;

    /**
     * DOCUMENT ME!
     */
    protected Namespace m_dcns = Namespace.getNamespace("dc", "http://purl.org/dc/elements/1.1/");

    /**
     * DOCUMENT ME!
     */
    protected Namespace m_davns = Namespace.getNamespace("DAV:");

    /**
         *
         */
    public PageDavItem(WikiEngine engine, WikiPage page)
    {
        super(engine);
        m_page = page;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected Collection getCommonProperties()
    {
        ArrayList set = new ArrayList();

        set.add(new Element("resourcetype", m_davns));
        set.add(new Element("creator", m_dcns).setText(m_page.getAuthor()));
        set.add(
            new Element("getlastmodified", m_davns).setText(
                DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format(m_page.getLastModified())));
        set.add(new Element("displayname", m_davns).setText(m_page.getName()));

        return set;
    }

    /* (non-Javadoc)
     * @see de.softwareforge.eyewiki.dav.DavItem#getPropertySet(int)
     */
    public Collection getPropertySet()
    {
        Collection set = getCommonProperties();

        String txt = m_engine.getPureText(m_page);

        try
        {
            byte [] txtBytes = txt.getBytes("UTF-8");
            set.add(
                new Element("getcontentlength", m_davns).setText(Long.toString(txtBytes.length)));
            set.add(
                new Element("getcontenttype", m_davns).setText("text/plain; charset=\"UTF-8\""));
        }
        catch (UnsupportedEncodingException e)
        {
        } // Should never happen

        return set;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getHref()
    {
        return m_engine.getURL(
            WikiContext.NONE, "dav/raw/" + m_page.getName() + ".txt", null, true);
    }
}
