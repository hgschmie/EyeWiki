/*
   JSPWiki - a JSP-based WikiWiki clone.

   Copyright (C) 2001-2002 Janne Jalkanen (Janne.Jalkanen@iki.fi)

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
package com.ecyrd.jspwiki.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ecyrd.jspwiki.PageLock;
import com.ecyrd.jspwiki.QueryItem;
import com.ecyrd.jspwiki.WikiEngine;
import com.ecyrd.jspwiki.WikiException;
import com.ecyrd.jspwiki.WikiPage;
import com.ecyrd.jspwiki.WikiProperties;
import com.ecyrd.jspwiki.exception.NoRequiredPropertyException;
import com.ecyrd.jspwiki.providers.CachingProvider;
import com.ecyrd.jspwiki.providers.ProviderException;
import com.ecyrd.jspwiki.providers.RepositoryModifiedException;
import com.ecyrd.jspwiki.providers.WikiPageProvider;
import com.ecyrd.jspwiki.util.ClassUtil;


/**
 * Manages the WikiPages.  This class functions as an unified interface towards the page providers.
 * It handles initialization and management of the providers, and provides utility methods for
 * accessing the contents.
 *
 * @author Janne Jalkanen
 *
 * @since 2.0
 */

// FIXME: This class currently only functions just as an extra layer over providers,
//        complicating things.  We need to move more provider-specific functionality
//        from WikiEngine (which is too big now) into this class.
public class PageManager
        implements WikiProperties
{
    /** DOCUMENT ME! */
    private static final Logger log = Logger.getLogger(PageManager.class);

    /** DOCUMENT ME! */
    private WikiPageProvider m_provider;

    /** DOCUMENT ME! */
    private HashMap m_pageLocks = new HashMap();

    /** DOCUMENT ME! */
    private WikiEngine m_engine;

    /** The expiry time.  Default is 60 minutes. */
    private int m_expiryTime = 60;

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
        String classname;

        m_engine = engine;

        boolean useCache = conf.getBoolean(PROP_USECACHE, PROP_USECACHE_DEFAULT);

        m_expiryTime = conf.getInt(PROP_LOCKEXPIRY, PROP_LOCKEXPIRY_DEFAULT);

        //
        //  If user wants to use a cache, then we'll use the CachingProvider.
        //
        if (useCache)
        {
            classname = CachingProvider.class.getName();
        }
        else
        {
            classname = conf.getString(PROP_CLASS_PAGEPROVIDER, PROP_CLASS_PAGEPROVIDER_DEFAULT);
        }

        try
        {
            Class providerclass = ClassUtil.findClass(DEFAULT_PROVIDER_CLASS_PREFIX, classname);

            m_provider = (WikiPageProvider) providerclass.newInstance();

            if (log.isDebugEnabled())
            {
                log.debug("Initializing page provider class " + m_provider);
            }

            m_provider.initialize(m_engine, conf);
        }
        catch (ClassNotFoundException e)
        {
            log.error("Unable to locate provider class " + classname, e);
            throw new WikiException("no provider class");
        }
        catch (InstantiationException e)
        {
            log.error("Unable to create provider class " + classname, e);
            throw new WikiException("faulty provider class");
        }
        catch (IllegalAccessException e)
        {
            log.error("Illegal access to provider class " + classname, e);
            throw new WikiException("illegal provider class");
        }
        catch (NoRequiredPropertyException e)
        {
            log.error("Provider did not found a property it was looking for: " + e.getMessage(), e);
            throw e; // Same exception works.
        }
        catch (IOException e)
        {
            log.error(
                "An I/O exception occurred while trying to create a new page provider: "
                + classname, e);
            throw new WikiException("Unable to start page provider: " + e.getMessage());
        }

        //
        //  Start the lock reaper.
        //
        new LockReaper().start();
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
     * Fetches the page text from the repository.  This method also does some sanity checks, like
     * checking for the pageName validity, etc.  Also, if the page repository has been modified
     * externally, it is smart enough to handle such occurrences.
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
     * Locks page for editing.  Note, however, that the PageManager will in no way prevent you from
     * actually editing this page; the lock is just for information.
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
                lock =
                    new PageLock(
                        page, user, d, new Date(d.getTime() + (m_expiryTime * 60 * 1000L)));

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
     * Returns a list of currently applicable locks.  Note that by the time you get the list, the
     * locks may have already expired, so use this only for informational purposes.
     *
     * @return List of PageLock objects, detailing the locks.  If no locks exist, returns an empty
     *         list.
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
                log.info(
                    "Repository has been modified externally while fetching info for " + pageName);
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
     * This is a simple reaper thread that runs roughly every minute or so (it's not really that
     * important, as long as it runs), and removes all locks that have expired.
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
                                    log.debug(
                                        "Reaped lock: " + p.getPage().getName() + " by "
                                        + p.getLocker() + ", acquired " + p.getAcquisitionTime()
                                        + ", and expired " + p.getExpiryTime());
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
