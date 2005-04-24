/*
   JSPWiki - a JSP-based WikiWiki clone.

   Copyright (C) 2001-2003 Janne Jalkanen (Janne.Jalkanen@iki.fi)

   This program is free software; you can redistribute it and/or modify
   it under the terms of the GNU Lesser General Public License as published by
   the Free Software Foundation; either version 2.1 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General Public License
   along with this program; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
package com.ecyrd.jspwiki.providers;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import org.picocontainer.PicoContainer;

import com.ecyrd.jspwiki.QueryItem;
import com.ecyrd.jspwiki.WikiConstants;
import com.ecyrd.jspwiki.WikiEngine;
import com.ecyrd.jspwiki.WikiPage;
import com.ecyrd.jspwiki.WikiProvider;
import com.ecyrd.jspwiki.attachment.Attachment;
import com.ecyrd.jspwiki.exception.NoRequiredPropertyException;
import com.opensymphony.oscache.base.Cache;
import com.opensymphony.oscache.base.NeedsRefreshException;


/**
 * Provides a caching attachment provider.  This class rests on top of a real provider class and
 * provides a cache to speed things up.  Only the Attachment objects are cached; the actual
 * attachment contents are fetched always from the provider.
 *
 * @author Janne Jalkanen
 *
 * @since 2.1.64.
 */

// FIXME: Do we need to clear the cache entry if we get an NRE and the attachment is not there?
// FIXME: We probably clear the cache a bit too aggressively in places.
// FIXME: Does not yet react well to external cache changes.  Should really use custom
//        EntryRefreshPolicy for that.
public class CachingAttachmentProvider
        implements WikiAttachmentProvider
{
    /** DOCUMENT ME! */
    private static final Logger log = Logger.getLogger(CachingAttachmentProvider.class);

    /** DOCUMENT ME! */
    private WikiAttachmentProvider m_provider;

    /**
     * The cache contains Collection objects which contain Attachment objects. The key is the
     * parent wiki page name (String).
     */
    private Cache m_cache;

    /** DOCUMENT ME! */
    private long m_cacheMisses = 0;

    /** DOCUMENT ME! */
    private long m_cacheHits = 0;

    /** DOCUMENT ME! */
    private int m_refreshPeriod = 60 * 10; // 10 minutes at the moment

    /**
     * DOCUMENT ME!
     *
     * @param engine DOCUMENT ME!
     *
     * @throws NoRequiredPropertyException DOCUMENT ME!
     * @throws IOException DOCUMENT ME!
     * @throws IllegalArgumentException DOCUMENT ME!
     */
    public CachingAttachmentProvider(WikiEngine engine)
            throws NoRequiredPropertyException, IOException
    {
        log.debug("Initing CachingAttachmentProvider");

        //
        //  Construct an unlimited cache.
        //
        m_cache = new Cache(true, false, false);

        PicoContainer container = engine.getComponentContainer();
        m_provider = (WikiAttachmentProvider) container.getComponentInstance(WikiConstants.REAL_ATTACHMENT_PROVIDER);
    }

    /**
     * DOCUMENT ME!
     *
     * @param att DOCUMENT ME!
     * @param data DOCUMENT ME!
     *
     * @throws ProviderException DOCUMENT ME!
     * @throws IOException DOCUMENT ME!
     */
    public void putAttachmentData(Attachment att, InputStream data)
            throws ProviderException, IOException
    {
        m_provider.putAttachmentData(att, data);
        m_cache.flushEntry(att.getParentName());
    }

    /**
     * DOCUMENT ME!
     *
     * @param att DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws ProviderException DOCUMENT ME!
     * @throws IOException DOCUMENT ME!
     */
    public InputStream getAttachmentData(Attachment att)
            throws ProviderException, IOException
    {
        return m_provider.getAttachmentData(att);
    }

    /**
     * DOCUMENT ME!
     *
     * @param page DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws ProviderException DOCUMENT ME!
     */
    public Collection listAttachments(WikiPage page)
            throws ProviderException
    {
        if (log.isDebugEnabled())
        {
            log.debug("Listing attachments for " + page);
        }

        try
        {
            Collection c = (Collection) m_cache.getFromCache(page.getName(), m_refreshPeriod);

            if (c != null)
            {
                if (log.isDebugEnabled())
                {
                    log.debug("LIST from cache, " + page.getName() + ", size=" + c.size());
                }

                m_cacheHits++;

                return c;
            }

            if (log.isDebugEnabled())
            {
                log.debug("list NOT in cache, " + page.getName());
            }

            c = refresh(page);
        }
        catch (NeedsRefreshException nre)
        {
            try
            {
                Collection c = refresh(page);

                return c;
            }
            catch (ProviderException ex)
            {
                // Make sure to avoid possible deadlock with locked cache entry
                m_cache.cancelUpdate(page.getName());

                log.warn("Provider failed, returning cached content");

                return (Collection) nre.getCacheContent();
            }
        }

        return new ArrayList();
    }

    /**
     * DOCUMENT ME!
     *
     * @param query DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Collection findAttachments(QueryItem [] query)
    {
        return m_provider.findAttachments(query);
    }

    /**
     * DOCUMENT ME!
     *
     * @param timestamp DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws ProviderException DOCUMENT ME!
     */
    public List listAllChanged(Date timestamp)
            throws ProviderException
    {
        // FIXME: Should cache
        return m_provider.listAllChanged(timestamp);
    }

    /**
     * Simply goes through the collection and attempts to locate the given attachment of that name.
     *
     * @param c DOCUMENT ME!
     * @param name DOCUMENT ME!
     *
     * @return null, if no such attachment was in this collection.
     */
    private Attachment findAttachmentFromCollection(Collection c, String name)
    {
        for (Iterator i = c.iterator(); i.hasNext();)
        {
            Attachment att = (Attachment) i.next();

            if (name.equals(att.getFileName()))
            {
                return att;
            }
        }

        return null;
    }

    /**
     * Refreshes the cache content and updates counters.
     *
     * @param page DOCUMENT ME!
     *
     * @return The newly fetched object from the provider.
     *
     * @throws ProviderException DOCUMENT ME!
     */
    private Collection refresh(final WikiPage page)
            throws ProviderException
    {
        m_cacheMisses++;

        Collection c = m_provider.listAttachments(page);
        m_cache.putInCache(page.getName(), c);

        return c;
    }

    /**
     * DOCUMENT ME!
     *
     * @param page DOCUMENT ME!
     * @param name DOCUMENT ME!
     * @param version DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws ProviderException DOCUMENT ME!
     */
    public Attachment getAttachmentInfo(WikiPage page, String name, int version)
            throws ProviderException
    {
        if (log.isDebugEnabled())
        {
            log.debug(
                "Getting attachments for " + page + ", name=" + name + ", version=" + version);
        }

        //
        //  We don't cache previous versions
        //
        if (version != WikiProvider.LATEST_VERSION)
        {
            if (log.isDebugEnabled())
            {
                log.debug("...we don't cache old versions");
            }

            return m_provider.getAttachmentInfo(page, name, version);
        }

        try
        {
            Collection c = (Collection) m_cache.getFromCache(page.getName(), m_refreshPeriod);

            if (c == null)
            {
                if (log.isDebugEnabled())
                {
                    log.debug("...wasn't in the cache");
                }

                c = refresh(page);

                if (c == null)
                {
                    return null; // No such attachment
                }
            }
            else
            {
                if (log.isDebugEnabled())
                {
                    log.debug("...FOUND in the cache");
                }

                m_cacheHits++;
            }

            return findAttachmentFromCollection(c, name);
        }
        catch (NeedsRefreshException nre)
        {
            log.debug("...needs refresh");

            Collection c = null;

            try
            {
                c = refresh(page);
            }
            catch (ProviderException ex)
            {
                // Make sure to avoid possible deadlock with locked cache entry
                m_cache.cancelUpdate(page.getName());

                log.warn("Provider failed, returning cached content");

                c = (Collection) nre.getCacheContent();
            }

            if (c != null)
            {
                return findAttachmentFromCollection(c, name);
            }
        }

        return null;
    }

    /**
     * Returns version history.  Each element should be an Attachment.
     *
     * @param att DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public List getVersionHistory(Attachment att)
    {
        return m_provider.getVersionHistory(att);
    }

    /**
     * DOCUMENT ME!
     *
     * @param att DOCUMENT ME!
     *
     * @throws ProviderException DOCUMENT ME!
     */
    public void deleteVersion(Attachment att)
            throws ProviderException
    {
        // This isn't strictly speaking correct, but it does not really matter
        m_cache.putInCache(att.getParentName(), null);
        m_provider.deleteVersion(att);
    }

    /**
     * DOCUMENT ME!
     *
     * @param att DOCUMENT ME!
     *
     * @throws ProviderException DOCUMENT ME!
     */
    public void deleteAttachment(Attachment att)
            throws ProviderException
    {
        m_cache.putInCache(att.getParentName(), null);
        m_provider.deleteAttachment(att);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public synchronized String getProviderInfo()
    {
        return ("Real provider: " + m_provider.getClass().getName() + "<br />Cache misses: "
        + m_cacheMisses + "<br />Cache hits: " + m_cacheHits);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public WikiAttachmentProvider getRealProvider()
    {
        return m_provider;
    }
}
