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
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.WikiPage;
import de.softwareforge.eyewiki.WikiProperties;
import de.softwareforge.eyewiki.attachment.Attachment;
import de.softwareforge.eyewiki.filters.BasicPageFilter;
import de.softwareforge.eyewiki.filters.PageFilter;
import de.softwareforge.eyewiki.providers.ProviderException;
import de.softwareforge.eyewiki.providers.WikiPageProvider;

import org.picocontainer.Startable;

/*
  BUGS

  - if a wikilink is added to a page, then removed, RefMan still thinks that
  the page refers to the wikilink page. Hm.

  - if a page is deleted, gets very confused.

  - Serialization causes page attributes to be missing, when InitializablePlugins
  are not executed properly.  Thus, serialization should really also mark whether
  a page is serializable or not...
*/
/*
   A word about synchronizing:

   I expect this object to be accessed in three situations:
   - when a WikiEngine is created and it scans its wikipages
   - when the WE saves a page
   - when a JSP page accesses one of the WE's ReferenceManagers
   to display a list of (un)referenced pages.

   So, access to this class is fairly rare, and usually triggered by
   user interaction. OTOH, the methods in this class use their storage
   objects intensively (and, sorry to say, in an unoptimized manner =).
   My deduction: using unsynchronized HashMaps etc and syncing methods
   or code blocks is preferrable to using slow, synced storage objects.
   We don't have iterative code here, so I'm going to use synced methods
   for now.

   Please contact me if you notice problems with ReferenceManager, and
   especially with synchronization, or if you have suggestions about
   syncing.

   ebu@memecry.net
*/

/**
 * Keeps track of wikipage references:
 *
 * <UL>
 * <li>
 * What pages a given page refers to
 * </li>
 * <li>
 * What pages refer to a given page
 * </li>
 * </ul>
 *
 * This is a quick'n'dirty approach without any finesse in storage and searching algorithms; we trust java.util..
 *
 * <P>
 * This class contains two HashMaps, m_refersTo and m_referredBy. The first is indexed by WikiPage names and contains a Collection
 * of all WikiPages the page refers to. (Multiple references are not counted, naturally.) The second is indexed by WikiPage names
 * and contains a Set of all pages that refer to the indexing page. (Notice - the keys of both Maps should be kept in sync.)
 * </p>
 *
 * <P>
 * When a page is added or edited, its references are parsed, a Collection is received, and we crudely replace anything previous
 * with this new Collection. We then check each referenced page name and make sure they know they are referred to by the new page.
 * </p>
 *
 * <P>
 * Based on this information, we can perform non-optimal searches for e.g. unreferenced pages, top ten lists, etc.
 * </p>
 *
 * <P>
 * The owning class must take responsibility of filling in any pre-existing information, probably by loading each and every
 * WikiPage and calling this class to update the references when created.
 * </p>
 *
 * @author <a hef="mailto:ebu@memecry.net">ebu</a>
 *
 * @since 1.6.1
 */
public class ReferenceManager
        implements Startable
{
    /** DOCUMENT ME! */
    private static Logger log = Logger.getLogger(ReferenceManager.class);

    /** DOCUMENT ME! */
    private static final String SERIALIZATION_FILE = "refmgr.ser";

    /**
     * Maps page wikiname to a Collection of pages it refers to. The Collection must contain Strings. The Collection may contain
     * names of non-existing pages.
     */
    private Map m_refersTo;

    /**
     * Maps page wikiname to a Set of referring pages. The Set must contain Strings. Non-existing pages (a reference exists, but
     * not a file for the page contents) may have an empty Set in m_referredBy.
     */
    private Map m_referredBy;

    /** The WikiEngine that owns this object. */
    private final WikiEngine engine;

    /** DOCUMENT ME! */
    private boolean m_matchEnglishPlurals = WikiProperties.PROP_MATCHPLURALS_DEFAULT;

    /** Is the Manager started? */
    private boolean started = false;

    /**
     * Builds a new ReferenceManager.
     *
     * @param engine The WikiEngine to which this is meeting.
     * @param conf DOCUMENT ME!
     */
    public ReferenceManager(final WikiEngine engine, final Configuration conf)
    {
        m_refersTo = new HashMap();
        m_referredBy = new HashMap();
        this.engine = engine;

        m_matchEnglishPlurals = conf.getBoolean(WikiProperties.PROP_MATCHPLURALS, WikiProperties.PROP_MATCHPLURALS_DEFAULT);
    }

    /**
     * Initializes the reference manager. Scans all existing WikiPages for internal links and adds them to the ReferenceManager
     * object.
     */
    public synchronized void start()
    {
        try
        {
            Collection pages = new ArrayList();

            pages.addAll(engine.getPageManager().getAllPages());
            pages.addAll(engine.getAttachmentManager().getAllAttachments());

            initialize(pages);
        }
        catch (ProviderException e)
        {
            log.fatal("Page or Attachment Provider is unable to list its pages", e);
        }

        engine.getFilterManager().addPageFilter(new ReferenceManagerFilter());

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
     * Does a full reference update.
     *
     * @param page DOCUMENT ME!
     *
     * @throws ProviderException DOCUMENT ME!
     */
    private void updatePageReferences(WikiPage page)
            throws ProviderException
    {
        String content = engine.getPageManager().getPageText(page.getName(), WikiPageProvider.LATEST_VERSION);
        Collection links = engine.scanWikiLinks(page, content);
        Collection attachments = engine.getAttachmentManager().listAttachments(page);

        for (Iterator atti = attachments.iterator(); atti.hasNext();)
        {
            links.add(((Attachment) (atti.next())).getName());
        }

        updateReferences(page.getName(), links);
    }

    /**
     * Initializes the entire reference manager with the initial set of pages from the collection.
     *
     * @param pages A collection of all pages you want to be included in the reference count.
     *
     * @throws ProviderException DOCUMENT ME!
     *
     * @since 2.2
     */
    private void initialize(Collection pages)
            throws ProviderException
    {
        if (log.isDebugEnabled())
        {
            log.debug("Initializing new ReferenceManager with " + pages.size() + " initial pages.");
        }

        long start = System.currentTimeMillis();
        log.info("Starting cross reference scan of WikiPages");

        //
        //  First, try to serialize old data from disk.  If that fails,
        //  we'll go and update the entire reference lists (which'll take
        //  time)
        //
        try
        {
            long saved = unserializeFromDisk();

            //
            //  Now we must check if any of the pages have been changed
            //  while we were in the electronic la-la-land, and update
            //  the references for them.
            //
            for (Iterator it = pages.iterator(); it.hasNext();)
            {
                WikiPage page = (WikiPage) it.next();

                // refresh everything but attachments.
                if (page instanceof Attachment)
                {
                    continue;
                }

                // Refresh with the latest copy
                page = engine.getPage(page.getName());

                if (page.getLastModified() == null)
                {
                    log.fatal("Provider returns null lastModified.  Please submit a bug report.");
                }
                else if (page.getLastModified().getTime() > saved)
                {
                    updatePageReferences(page);
                }
            }
        }
        catch (Exception e)
        {
            if (log.isInfoEnabled())
            {
                log.info("Unable to unserialize old refmgr information, rebuilding database: " + e.getMessage());
            }

            buildKeyLists(pages);

            // Scan the existing pages from disk and update references in the manager.
            for (Iterator it = pages.iterator(); it.hasNext();)
            {
                WikiPage page = (WikiPage) it.next();

                if (page instanceof Attachment)
                {
                    // We cannot build a reference list from the contents
                    // of attachments, so we skip them.
                    continue;
                }

                updatePageReferences(page);
            }

            serializeToDisk();
        }

        if (log.isInfoEnabled())
        {
            log.info("Cross reference scan done (" + (System.currentTimeMillis() - start) + " ms)");
        }
    }

    /**
     * Reads the serialized data from the disk back to memory. Returns the date when the data was last written on disk
     *
     * @return DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     * @throws ClassNotFoundException DOCUMENT ME!
     */
    private synchronized long unserializeFromDisk()
            throws IOException, ClassNotFoundException
    {
        ObjectInputStream in = null;
        long saved = 0L;

        try
        {
            long start = System.currentTimeMillis();

            File f = new File(engine.getWorkDir(), SERIALIZATION_FILE);

            in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(f)));

            saved = in.readLong();
            m_refersTo = (Map) in.readObject();
            m_referredBy = (Map) in.readObject();

            in.close();

            long finish = System.currentTimeMillis();

            if (log.isDebugEnabled())
            {
                log.debug("Read serialized data successfully in " + (finish - start) + "ms");
            }
        }
        finally
        {
            IOUtils.closeQuietly(in);
        }

        return saved;
    }

    /**
     * Serializes hashmaps to disk.  The format is private, don't touch it.
     */
    private synchronized void serializeToDisk()
    {
        ObjectOutputStream out = null;

        try
        {
            long start = System.currentTimeMillis();

            File f = new File(engine.getWorkDir(), SERIALIZATION_FILE);

            out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(f)));

            out.writeLong(System.currentTimeMillis()); // Timestamp
            out.writeObject(m_refersTo);
            out.writeObject(m_referredBy);

            out.close();

            long finish = System.currentTimeMillis();

            if (log.isDebugEnabled())
            {
                log.debug("serialization done - took " + (finish - start) + "ms");
            }
        }
        catch (IOException e)
        {
            log.error("Unable to serialize!");
            IOUtils.closeQuietly(out);
        }
    }

    /**
     * Updates the referred pages of a new or edited WikiPage. If a refersTo entry for this page already exists, it is removed and
     * a new one is built from scratch. Also calls updateReferredBy() for each referenced page.
     *
     * <P>
     * This is the method to call when a new page has been created and we want to a) set up its references and b) notify the
     * referred pages of the references. Use this method during run-time.
     * </p>
     *
     * @param page Name of the page to update.
     * @param references A Collection of Strings, each one pointing to a page this page references.
     */
    public synchronized void updateReferences(String page, Collection references)
    {
        //
        // Create a new entry in m_refersTo.
        //
        Collection oldRefTo = (Collection) m_refersTo.get(page);
        m_refersTo.remove(page);
        m_refersTo.put(page, references);

        //
        //  We know the page exists, since it's making references somewhere.
        //  If an entry for it didn't exist previously in m_referredBy, make
        //  sure one is added now.
        //
        if (!m_referredBy.containsKey(page))
        {
            m_referredBy.put(page, new TreeSet());
        }

        //
        //  Get all pages that used to be referred to by 'page' and
        //  remove that reference. (We don't want to try to figure out
        //  which particular references were removed...)
        //
        cleanReferredBy(page, oldRefTo);

        //
        //  Notify all referred pages of their referinesshoodicity.
        //
        Iterator it = references.iterator();

        while (it.hasNext())
        {
            String referredPageName = (String) it.next();
            updateReferredBy(referredPageName, page);
        }
    }

    /**
     * Returns the refers-to list. For debugging.
     *
     * @return DOCUMENT ME!
     */
    protected Map getRefersTo()
    {
        return (m_refersTo);
    }

    /**
     * Returns the referred-by list. For debugging.
     *
     * @return DOCUMENT ME!
     */
    protected Map getReferredBy()
    {
        return (m_referredBy);
    }

    /**
     * Cleans the 'referred by' list, removing references by 'referrer' to any other page. Called after 'referrer' is removed.
     *
     * @param referrer DOCUMENT ME!
     * @param oldReferred DOCUMENT ME!
     */
    private void cleanReferredBy(String referrer, Collection oldReferred)
    {
        // Two ways to go about this. One is to look up all pages previously
        // referred by referrer and remove referrer from their lists, and let
        // the update put them back in (except possibly removed ones).
        // The other is to get the old referred to list, compare to the new,
        // and tell the ones missing in the latter to remove referrer from
        // their list. Hm. We'll just try the first for now. Need to come
        // back and optimize this a bit.
        if (oldReferred == null)
        {
            return;
        }

        Iterator it = oldReferred.iterator();

        while (it.hasNext())
        {
            String referredPage = (String) it.next();
            Set oldRefBy = (Set) m_referredBy.get(referredPage);

            if (oldRefBy != null)
            {
                oldRefBy.remove(referrer);
            }

            // If the page is referred to by no one AND it doesn't even
            // exist, we might just as well forget about this entry.
            // It will be added again elsewhere if new references appear.
            if (((oldRefBy == null) || oldRefBy.isEmpty()) && !engine.pageExists(referredPage))
            {
                m_referredBy.remove(referredPage);
            }
        }
    }

    /**
     * When initially building a ReferenceManager from scratch, call this method BEFORE calling updateReferences() with a full list
     * of existing page names. It builds the refersTo and referredBy key lists, thus enabling updateReferences() to function
     * correctly.
     *
     * <P>
     * This method should NEVER be called after initialization. It clears all mappings from the reference tables.
     * </p>
     *
     * @param pages a Collection containing WikiPage objects.
     */
    private synchronized void buildKeyLists(Collection pages)
    {
        m_refersTo.clear();
        m_referredBy.clear();

        if (pages == null)
        {
            return;
        }

        Iterator it = pages.iterator();

        try
        {
            while (it.hasNext())
            {
                WikiPage page = (WikiPage) it.next();

                // We add a non-null entry to referredBy to indicate the referred page exists
                m_referredBy.put(page.getName(), new TreeSet());

                // Just add a key to refersTo; the keys need to be in sync with referredBy.
                m_refersTo.put(page.getName(), null);
            }
        }
        catch (ClassCastException e)
        {
            log.fatal("Invalid collection entry in ReferenceManager.buildKeyLists().", e);
        }
    }

    /**
     * Marks the page as referred to by the referrer. If the page does not exist previously, nothing is done. (This means that some
     * page, somewhere, has a link to a page that does not exist.)
     *
     * <P>
     * This method is NOT synchronized. It should only be referred to from within a synchronized method, or it should be made
     * synced if necessary.
     * </p>
     *
     * @param page DOCUMENT ME!
     * @param referrer DOCUMENT ME!
     */
    private void updateReferredBy(String page, String referrer)
    {
        // We're not really interested in first level self-references.
        if (page.equals(referrer))
        {
            return;
        }

        Set referrers = (Set) m_referredBy.get(page);

        // Even if 'page' has not been created yet, it can still be referenced.
        // This requires we don't use m_referredBy keys when looking up missing
        // pages, of course.
        if (referrers == null)
        {
            referrers = new TreeSet();
            m_referredBy.put(page, referrers);
        }

        referrers.add(referrer);
    }

    /**
     * Finds all unreferenced pages. This requires a linear scan through m_referredBy to locate keys with null or empty values.
     *
     * @return DOCUMENT ME!
     */
    public synchronized Collection findUnreferenced()
    {
        ArrayList unref = new ArrayList();

        Set keys = m_referredBy.keySet();
        Iterator it = keys.iterator();

        while (it.hasNext())
        {
            String key = (String) it.next();

            //Set refs = (Set) m_referredBy.get( key );
            Set refs = getReferenceList(m_referredBy, key);

            if ((refs == null) || refs.isEmpty())
            {
                unref.add(key);
            }
        }

        return unref;
    }

    /**
     * Finds all references to non-existant pages. This requires a linear scan through m_refersTo values; each value must have a
     * corresponding key entry in the reference Maps, otherwise such a page has never been created.
     *
     * <P>
     * Returns a Collection containing Strings of unreferenced page names. Each non-existant page name is shown only once - we
     * don't return information on who referred to it.
     * </p>
     *
     * @return DOCUMENT ME!
     */
    public synchronized Collection findUncreated()
    {
        TreeSet uncreated = new TreeSet();

        // Go through m_refersTo values and check that m_refersTo has the corresponding keys.
        // We want to reread the code to make sure our HashMaps are in sync...
        Collection allReferences = m_refersTo.values();
        Iterator it = allReferences.iterator();

        while (it.hasNext())
        {
            Collection refs = (Collection) it.next();

            if (refs != null)
            {
                Iterator rit = refs.iterator();

                while (rit.hasNext())
                {
                    String aReference = (String) rit.next();

                    if (!engine.pageExists(aReference))
                    {
                        uncreated.add(aReference);
                    }
                }
            }
        }

        return uncreated;
    }

    /**
     * Searches for the given page in the given Map.
     *
     * @param coll DOCUMENT ME!
     * @param pagename DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private Set getReferenceList(Map coll, String pagename)
    {
        Set refs = (Set) coll.get(pagename);

        if (((refs == null) || (refs.size() == 0)) && m_matchEnglishPlurals)
        {
            if (pagename.endsWith("s"))
            {
                refs = (Set) coll.get(pagename.substring(0, pagename.length() - 1));
            }
            else
            {
                refs = (Set) coll.get(pagename + "s");
            }
        }

        return refs;
    }

    /**
     * Find all pages that refer to this page. Returns null if the page does not exist or is not referenced at all, otherwise
     * returns a collection containing page names (String) that refer to this one.
     *
     * @param pagename DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public synchronized Collection findReferrers(String pagename)
    {
        Set refs = getReferenceList(m_referredBy, pagename);

        if ((refs == null) || refs.isEmpty())
        {
            return null;
        }
        else
        {
            return refs;
        }
    }

    /*
     * ========================================================================
     *
     * Page Filter methods
     *
     * ========================================================================
     */
    public class ReferenceManagerFilter
            extends BasicPageFilter
            implements PageFilter
    {
        /**
         * Creates a new ReferenceManagerFilter object.
         */
        private ReferenceManagerFilter()
        {
            super(null);
        }

        /**
         * After the page has been saved, updates the reference lists.
         *
         * @param context DOCUMENT ME!
         * @param content DOCUMENT ME!
         *
         * @throws IllegalArgumentException DOCUMENT ME!
         */
        public void postSave(WikiContext context, String content)
        {
            if (!isStarted())
            {
                throw new IllegalArgumentException("Called postSave() before start()!");
            }

            WikiPage page = context.getPage();

            updateReferences(page.getName(), engine.scanWikiLinks(page, content));

            serializeToDisk();
        }

        /**
         * DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        public boolean isVisible()
        {
            return false;
        }

        /**
         * DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        public int getPriority()
        {
            return PageFilter.MIN_PRIORITY;
        }
    }
}
