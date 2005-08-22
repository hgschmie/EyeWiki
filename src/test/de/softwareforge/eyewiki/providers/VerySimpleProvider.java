package de.softwareforge.eyewiki.providers;

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
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.apache.commons.configuration.Configuration;

import de.softwareforge.eyewiki.QueryItem;
import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.WikiPage;
import de.softwareforge.eyewiki.providers.ProviderException;
import de.softwareforge.eyewiki.providers.WikiPageProvider;

/**
 * This is a simple provider that is used by some of the tests.  It has some specific behaviours, like it always contains a single
 * page.
 */
public class VerySimpleProvider
        implements WikiPageProvider
{
    /** This provider has only a single page, when you ask a list of all pages. */
    public static final String PAGENAME = "foo";

    /** The name of the page list. */
    public static final String AUTHOR = "default-author";

    /** The last request is stored here. */
    public String m_latestReq = null;

    /** The version number of the last request is stored here. */
    public int m_latestVers = -123989;

    /**
     * DOCUMENT ME!
     *
     * @param engine DOCUMENT ME!
     * @param conf DOCUMENT ME!
     */
    public VerySimpleProvider(WikiEngine engine, Configuration conf)
    {
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getProviderInfo()
    {
        return "Very Simple Provider.";
    }

    /**
     * DOCUMENT ME!
     *
     * @param page DOCUMENT ME!
     * @param text DOCUMENT ME!
     *
     * @throws ProviderException DOCUMENT ME!
     */
    public void putPageText(WikiPage page, String text)
            throws ProviderException
    {
    }

    /**
     * Always returns true.
     *
     * @param page DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean pageExists(String page)
    {
        return true;
    }

    /**
     * Always returns null.
     *
     * @param query DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Collection findPages(QueryItem [] query)
    {
        return null;
    }

    /**
     * Returns always a valid WikiPage.
     *
     * @param page DOCUMENT ME!
     * @param version DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public WikiPage getPageInfo(String page, int version)
    {
        m_latestReq = page;
        m_latestVers = version;

        WikiPage p = new WikiPage(page);
        p.setVersion(5);
        p.setAuthor(AUTHOR);
        p.setLastModified(new Date(0L));

        return p;
    }

    /**
     * Returns a single page.
     *
     * @return DOCUMENT ME!
     */
    public Collection getAllPages()
    {
        Vector v = new Vector();
        v.add(getPageInfo(PAGENAME, 5));

        return v;
    }

    /**
     * Returns the same as getAllPages().
     *
     * @param date DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Collection getAllChangedSince(Date date)
    {
        return getAllPages();
    }

    /**
     * Always returns 1.
     *
     * @return DOCUMENT ME!
     */
    public int getPageCount()
    {
        return 1;
    }

    /**
     * Always returns an empty list.
     *
     * @param page DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public List getVersionHistory(String page)
    {
        return new Vector();
    }

    /**
     * Stores the page and version into public fields of this class, then returns an empty string.
     *
     * @param page DOCUMENT ME!
     * @param version DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getPageText(String page, int version)
    {
        m_latestReq = page;
        m_latestVers = version;

        return "";
    }

    /**
     * DOCUMENT ME!
     *
     * @param page DOCUMENT ME!
     * @param version DOCUMENT ME!
     */
    public void deleteVersion(String page, int version)
    {
    }

    /**
     * DOCUMENT ME!
     *
     * @param page DOCUMENT ME!
     */
    public void deletePage(String page)
    {
    }
}
