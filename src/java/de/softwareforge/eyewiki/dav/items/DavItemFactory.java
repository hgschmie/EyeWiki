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
import java.util.Iterator;

import org.apache.log4j.Logger;

import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.WikiPage;
import de.softwareforge.eyewiki.dav.DavContext;
import de.softwareforge.eyewiki.providers.ProviderException;

/**
 * DOCUMENT ME!
 *
 * @author jalkanen
 *
 * @since
 */
public class DavItemFactory
{
    /** DOCUMENT ME! */
    private WikiEngine m_engine;

    /** DOCUMENT ME! */
    Logger log = Logger.getLogger(DavItemFactory.class);

    /**
     * Creates a new DavItemFactory object.
     *
     * @param engine DOCUMENT ME!
     */
    public DavItemFactory(WikiEngine engine)
    {
        m_engine = engine;
    }

    private DavItem getRawItem(DavContext dc)
    {
        String pagename = dc.m_page;

        if ((pagename == null) || (pagename.length() == 0))
        {
            DirectoryItem di = new DirectoryItem(m_engine, dc.m_davcontext);

            try
            {
                Collection c = m_engine.getPageManager().getAllPages();

                for (Iterator i = c.iterator(); i.hasNext();)
                {
                    WikiPage p = (WikiPage) i.next();

                    PageDavItem dip = new PageDavItem(m_engine, p);

                    di.addDavItem(dip);
                }
            }
            catch (ProviderException e)
            {
                log.error("Failed to get page list", e);

                return null;
            }

            return di;
        }
        else
        {
            if (pagename.endsWith(".txt"))
            {
                pagename = pagename.substring(0, pagename.length() - 4);

                WikiPage p = m_engine.getPage(pagename);

                if (p != null)
                {
                    PageDavItem di = new PageDavItem(m_engine, p);

                    return di;
                }
            }

            // TODO: add attachments
        }

        return null;
    }

    private DavItem getHTMLItem(DavContext dc)
    {
        String pagename = dc.m_page;

        if ((pagename == null) || (pagename.length() == 0))
        {
            DirectoryItem di = new DirectoryItem(m_engine, dc.m_davcontext);

            try
            {
                Collection c = m_engine.getPageManager().getAllPages();

                for (Iterator i = c.iterator(); i.hasNext();)
                {
                    WikiPage p = (WikiPage) i.next();

                    HTMLPageDavItem dip = new HTMLPageDavItem(m_engine, p);

                    di.addDavItem(dip);
                }
            }
            catch (ProviderException e)
            {
                log.error("Failed to get page list", e);

                return null;
            }

            return di;
        }
        else
        {
            if (pagename.endsWith(".html"))
            {
                pagename = pagename.substring(0, pagename.length() - 5);

                WikiPage p = m_engine.getPage(pagename);

                if (p != null)
                {
                    HTMLPageDavItem di = new HTMLPageDavItem(m_engine, p);

                    return di;
                }
            }

            // TODO: add attachments
        }

        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param dc DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public DavItem newItem(DavContext dc)
    {
        if (dc.m_davcontext.length() == 0)
        {
            return new TopLevelDavItem(m_engine);
        }
        else if (dc.m_davcontext.equals("raw"))
        {
            return getRawItem(dc);
        }
        else if (dc.m_davcontext.equals("html"))
        {
            return getHTMLItem(dc);
        }

        return null;
    }
}
