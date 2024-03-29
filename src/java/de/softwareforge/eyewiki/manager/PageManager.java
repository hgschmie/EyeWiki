package de.softwareforge.eyewiki.manager;

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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.softwareforge.eyewiki.PageLock;
import de.softwareforge.eyewiki.QueryItem;
import de.softwareforge.eyewiki.WikiConstants;
import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.WikiException;
import de.softwareforge.eyewiki.WikiPage;
import de.softwareforge.eyewiki.WikiProperties;
import de.softwareforge.eyewiki.providers.ProviderException;
import de.softwareforge.eyewiki.providers.RepositoryModifiedException;
import de.softwareforge.eyewiki.providers.WikiPageProvider;

import org.picocontainer.PicoContainer;
import org.picocontainer.Startable;

/**
 * Manages the WikiPages.  This class functions as an unified interface towards the page providers. It handles initialization and
 * management of the providers, and provides utility methods for accessing the contents.
 *
 * @author Janne Jalkanen
 *
 * @since 2.0
 */

// FIXME: This class currently only functions just as an extra layer over providers,
//        complicating things.  We need to move more provider-specific functionality
//        from WikiEngine (which is too big now) into this class.
public final class PageManager
        implements WikiProperties, Startable
{
    /** DOCUMENT ME! */
    private static final Logger log = Logger.getLogger(PageManager.class);

    /** DOCUMENT ME! */
    private final WikiPageProvider m_provider;

    /** DOCUMENT ME! */
    private HashMap m_pageLocks = new HashMap();

    /** DOCUMENT ME! */
    private final WikiEngine m_engine;

    /** The expiry time.  Default is 60 minutes. */
    private int m_expiryTime = 60;

    /** Is the Manager started? */
    private boolean started = false;

    /**
     * Creates a new PageManager.
     *
     * @param engine DOCUMENT ME!
     * @param conf DOCUMENT ME!
     *
     * @throws WikiException If anything goes wrong, you get this.
     */
    public PageManager(WikiEngine engine, Configuration conf)
            throws WikiException
    {
        m_engine = engine;

        m_expiryTime = conf.getInt(PROP_LOCKEXPIRY, PROP_LOCKEXPIRY_DEFAULT);

        // Don't do this over c'tor injection, because we might have more than just one WikiPageProvider
        // instance around (PageProvider and (in case of a caching provider) RealPageProvider)
        PicoContainer container = m_engine.getComponentContainer();
        m_provider = (WikiPageProvider) container.getComponentInstance(WikiConstants.PAGE_PROVIDER);

        if (log.isDebugEnabled())
        {
            log.debug("Initializing page provider class " + m_provider);
        }
    }

    /**
     * DOCUMENT ME!
     */
    public synchronized void start()
    {
        //
        //  Start the lock reaper.
        //
        new LockReaper().start();
        setStarted(true);
    }

    /**
     * DOCUMENT ME!
     */
    public synchronized void stop()
    {
        setStarted(false);
    }

    /**
     * DOCUMENT ME!
     *
     * @param started DOCUMENT ME!
     */
    protected void setStarted(final boolean started)
    {
        this.started = started;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isStarted()
    {
        return started;
    }

    /**
     * Returns the page provider currently in use.
     *
     * @return DOCUMENT ME!
     */
    public WikiPageProvider getProvider()
    {
        return m_provider;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws ProviderException DOCUMENT ME!
     */
    public Collection getAllPages()
            throws ProviderException
    {
        return m_provider.getAllPages();
    }

    /**
     * Fetches the page text from the repository.  This method also does some sanity checks, like checking for the pageName
     * validity, etc.  Also, if the page repository has been modified externally, it is smart enough to handle such occurrences.
     *
     * @param pageName DOCUMENT ME!
     * @param version DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws ProviderException DOCUMENT ME!
     */
    public String getPageText(String pageName, int version)
            throws ProviderException
    {
        if (StringUtils.isEmpty(pageName))
        {
            throw new ProviderException("Illegal page name");
        }

        String text = null;

        try
        {
            text = m_provider.getPageText(pageName, version);
        }
        catch (RepositoryModifiedException e)
        {
            //
            //  This only occurs with the latest version.
            //
            if (log.isInfoEnabled())
            {
                log.info("Repository has been modified externally while fetching page " + pageName);
            }

            //
            //  Empty the references and yay, it shall be recalculated
            //
            //WikiPage p = new WikiPage( pageName );
            WikiPage p = m_provider.getPageInfo(pageName, version);

            m_engine.updateReferences(p);

            text = m_provider.getPageText(pageName, version);
        }

        return text;
    }

    /**
     * DOCUMENT ME!
     *
     * @param page DOCUMENT ME!
     * @param content DOCUMENT ME!
     *
     * @throws ProviderException DOCUMENT ME!
     */
    public void putPageText(WikiPage page, String content)
            throws ProviderException
    {
        if ((page == null) || StringUtils.isEmpty(page.getName()))
        {
            throw new ProviderException("Illegal page name");
        }

        m_provider.putPageText(page, content);
    }

    /**
     * Locks page for editing.  Note, however, that the PageManager will in no way prevent you from actually editing this page; the
     * lock is just for information.
     *
     * @param page DOCUMENT ME!
     * @param user DOCUMENT ME!
     *
     * @return null, if page could not be locked.
     */
    public PageLock lockPage(WikiPage page, String user)
    {
        PageLock lock = null;

        synchronized (m_pageLocks)
        {
            lock = (PageLock) m_pageLocks.get(page.getName());

            if (lock == null)
            {
                //
                //  Lock is available, so make a lock.
                //
                Date d = new Date();
                lock = new PageLock(page, user, d, new Date(d.getTime() + (m_expiryTime * 60 * 1000L)));

                m_pageLocks.put(page.getName(), lock);

                if (log.isDebugEnabled())
                {
                    log.debug("Locked page " + page.getName() + " for " + user);
                }
            }
            else
            {
                if (log.isDebugEnabled())
                {
                    log.debug("Page " + page.getName() + " already locked by " + lock.getLocker());
                }

                lock = null; // Nothing to return
            }
        }

        return lock;
    }

    /**
     * Marks a page free to be written again.  If there has not been a lock, will fail quietly.
     *
     * @param lock A lock acquired in lockPage().  Safe to be null.
     */
    public void unlockPage(PageLock lock)
    {
        if (lock == null)
        {
            return;
        }

        synchronized (m_pageLocks)
        {
            String pageName = lock.getPage().getName();
            m_pageLocks.remove(pageName);

            if (log.isDebugEnabled())
            {
                log.debug("Unlocked page " + pageName);
            }
        }
    }

    /**
     * Returns the current lock owner of a page.  If the page is not locked, will return null.
     *
     * @param page DOCUMENT ME!
     *
     * @return Current lock.
     */
    public PageLock getCurrentLock(WikiPage page)
    {
        PageLock lock = null;

        synchronized (m_pageLocks)
        {
            lock = (PageLock) m_pageLocks.get(page.getName());
        }

        return lock;
    }

    /**
     * Returns a list of currently applicable locks.  Note that by the time you get the list, the locks may have already expired,
     * so use this only for informational purposes.
     *
     * @return List of PageLock objects, detailing the locks.  If no locks exist, returns an empty list.
     *
     * @since 2.0.22.
     */
    public List getActiveLocks()
    {
        ArrayList result = new ArrayList();

        synchronized (m_pageLocks)
        {
            for (Iterator i = m_pageLocks.values().iterator(); i.hasNext();)
            {
                result.add(i.next());
            }
        }

        return result;
    }

    /**
     * DOCUMENT ME!
     *
     * @param query DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Collection findPages(QueryItem [] query)
    {
        return m_provider.findPages(query);
    }

    /**
     * DOCUMENT ME!
     *
     * @param pageName DOCUMENT ME!
     * @param version DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws ProviderException DOCUMENT ME!
     */
    public WikiPage getPageInfo(String pageName, int version)
            throws ProviderException
    {
        if (StringUtils.isEmpty(pageName))
        {
            throw new ProviderException("Illegal page name");
        }

        WikiPage page = null;

        try
        {
            page = m_provider.getPageInfo(pageName, version);
        }
        catch (RepositoryModifiedException e)
        {
            //
            //  This only occurs with the latest version.
            //
            if (log.isInfoEnabled())
            {
                log.info("Repository has been modified externally while fetching info for " + pageName);
            }

            WikiPage p = new WikiPage(pageName);

            m_engine.updateReferences(p);

            page = m_provider.getPageInfo(pageName, version);
        }

        return page;
    }

    /**
     * Gets a version history of page.  Each element in the returned List is a WikiPage.
     *
     * <P></p>
     *
     * @param pageName DOCUMENT ME!
     *
     * @return If the page does not exist, returns null, otherwise a List of WikiPages.
     *
     * @throws ProviderException DOCUMENT ME!
     */
    public List getVersionHistory(String pageName)
            throws ProviderException
    {
        if (pageExists(pageName))
        {
            return m_provider.getVersionHistory(pageName);
        }

        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getProviderDescription()
    {
        return m_provider.getProviderInfo();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getTotalPageCount()
    {
        try
        {
            return m_provider.getAllPages().size();
        }
        catch (ProviderException e)
        {
            log.error("Unable to count pages: ", e);

            return -1;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param pageName DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws ProviderException DOCUMENT ME!
     */
    public boolean pageExists(String pageName)
            throws ProviderException
    {
        if (StringUtils.isEmpty(pageName))
        {
            throw new ProviderException("Illegal page name");
        }

        return m_provider.pageExists(pageName);
    }

    /**
     * Deletes only a specific version of a WikiPage.
     *
     * @param page DOCUMENT ME!
     *
     * @throws ProviderException DOCUMENT ME!
     */
    public void deleteVersion(WikiPage page)
            throws ProviderException
    {
        m_provider.deleteVersion(page.getName(), page.getVersion());

        // FIXME: Update RefMgr
    }

    /**
     * Deletes an entire page, all versions, all traces.
     *
     * @param page DOCUMENT ME!
     *
     * @throws ProviderException DOCUMENT ME!
     */
    public void deletePage(WikiPage page)
            throws ProviderException
    {
        m_provider.deletePage(page.getName());

        // FIXME: Update RefMgr
    }

    /**
     * This is a simple reaper thread that runs roughly every minute or so (it's not really that important, as long as it runs),
     * and removes all locks that have expired.
     */
    private class LockReaper
            extends Thread
    {
        /**
         * DOCUMENT ME!
         */
        public void run()
        {
            while (true)
            {
                try
                {
                    Thread.sleep(60 * 1000L);

                    synchronized (m_pageLocks)
                    {
                        Collection entries = m_pageLocks.values();

                        Date now = new Date();

                        for (Iterator i = entries.iterator(); i.hasNext();)
                        {
                            PageLock p = (PageLock) i.next();

                            if (now.after(p.getExpiryTime()))
                            {
                                i.remove();

                                if (log.isDebugEnabled())
                                {
                                    log.debug("Reaped lock: " + p.getPage().getName() + " by " + p.getLocker() + ", acquired "
                                        + p.getAcquisitionTime() + ", and expired " + p.getExpiryTime());
                                }
                            }
                        }
                    }
                }
                catch (Throwable t)
                {
                    log.warn("While reaping logs: ", t);
                }
            }
        }
    }
}
