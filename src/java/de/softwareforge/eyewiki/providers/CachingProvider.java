/*
  JSPWiki - a JSP-based WikiWiki clone.

  Copyright (C) 2001-2005 Janne Jalkanen (Janne.Jalkanen@iki.fi)

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
package de.softwareforge.eyewiki.providers;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import org.picocontainer.PicoContainer;
import org.picocontainer.Startable;

import com.opensymphony.oscache.base.Cache;
import com.opensymphony.oscache.base.NeedsRefreshException;
import com.opensymphony.oscache.base.algorithm.LRUCache;
import com.opensymphony.oscache.base.events.CacheEntryEvent;
import com.opensymphony.oscache.base.events.CacheEntryEventListener;
import com.opensymphony.oscache.base.events.CacheGroupEvent;
import com.opensymphony.oscache.base.events.CachePatternEvent;
import com.opensymphony.oscache.base.events.CachewideEvent;

import de.softwareforge.eyewiki.QueryItem;
import de.softwareforge.eyewiki.SearchMatcher;
import de.softwareforge.eyewiki.SearchResult;
import de.softwareforge.eyewiki.SearchResultComparator;
import de.softwareforge.eyewiki.WikiConstants;
import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.WikiPage;
import de.softwareforge.eyewiki.WikiProperties;
import de.softwareforge.eyewiki.WikiProvider;
import de.softwareforge.eyewiki.exception.NoRequiredPropertyException;
import de.softwareforge.eyewiki.util.TextUtil;


/**
 * Provides a caching page provider.  This class rests on top of a real provider class and provides
 * a cache to speed things up.  Only if the cache copy of the page text has expired, we fetch it
 * from the provider.
 *
 * <p>
 * This class also detects if someone has modified the page externally, not through JSPWiki
 * routines, and throws the proper RepositoryModifiedException.
 * </p>
 *
 * <p>
 * Heavily based on ideas by Chris Brooking.
 * </p>
 *
 * <p>
 * Since 2.1.52 uses the OSCache library from OpenSymphony.
 * </p>
 *
 * <p>
 * Since 2.1.100 uses the Apache Lucene library to help in searching.
 * </p>
 *
 * @author Janne Jalkanen
 *
 * @see RepositoryModifiedException
 * @since 1.6.4
 */

// FIXME: Keeps a list of all WikiPages in memory - should cache them too.
// FIXME: Synchronization is a bit inconsistent in places.
// FIXME: A part of the stuff is now redundant, since we could easily use the text cache
//        for a lot of things.  RefactorMe.
public class CachingProvider
        implements WikiPageProvider, WikiProperties, Startable
{
    /** DOCUMENT ME! */
    private static final Logger log = Logger.getLogger(CachingProvider.class);

    /** DOCUMENT ME! */
    private static final String OSCACHE_ALGORITHM = LRUCache.class.getName();

    /** DOCUMENT ME! */
    private static final String LUCENE_DIR = "lucene";

    // Number of page updates before we optimize the index.

    /** DOCUMENT ME! */
    public static final int LUCENE_OPTIMIZE_COUNT = 10;

    /** DOCUMENT ME! */
    private static final String LUCENE_ID = "id";

    /** DOCUMENT ME! */
    private static final String LUCENE_PAGE_CONTENTS = "contents";

    /** DOCUMENT ME! */
    private WikiPageProvider m_provider;

    /** DOCUMENT ME! */
    private Cache m_cache;

    /** DOCUMENT ME! */
    private Cache m_negCache; // Cache for holding non-existing pages

    /** DOCUMENT ME! */
    private Cache m_textCache;

    /** DOCUMENT ME! */
    private Cache m_historyCache;

    /** DOCUMENT ME! */
    private long m_cacheMisses = 0;

    /** DOCUMENT ME! */
    private long m_cacheHits = 0;

    /** DOCUMENT ME! */
    private long m_historyCacheMisses = 0;

    /** DOCUMENT ME! */
    private long m_historyCacheHits = 0;

    /** DOCUMENT ME! */
    private int m_expiryPeriod = PROP_CACHECHECKINTERVAL_DEFAULT;

    /** This can be very long, as normally all modifications are noticed in an earlier stage. */
    private int m_pageContentExpiryPeriod = 24 * 60 * 60;

    // FIXME: This MUST be cached somehow.

    /** DOCUMENT ME! */
    private boolean m_gotall = false;

    // Lucene data, if used.

    /** DOCUMENT ME! */
    private boolean m_useLucene = false;

    private String m_analyzerClass = null;

    /** DOCUMENT ME! */
    private String m_luceneDirectory = null;

    /** DOCUMENT ME! */
    private int m_updateCount = 0;

    /** DOCUMENT ME! */
    private Thread m_luceneUpdateThread = null;

    /** DOCUMENT ME! */
    private Vector m_updates = new Vector(); // Vector because multi-threaded.

    /** DOCUMENT ME! */
    private CacheItemCollector m_allCollector = new CacheItemCollector();

    /** The Wiki Engine */
    private final WikiEngine m_engine;

    /**
     * DOCUMENT ME!
     *
     * @param engine DOCUMENT ME!
     * @param conf DOCUMENT ME!
     *
     * @throws NoRequiredPropertyException DOCUMENT ME!
     * @throws IOException DOCUMENT ME!
     * @throws IllegalArgumentException DOCUMENT ME!
     */
    public CachingProvider(WikiEngine engine, Configuration conf)
            throws NoRequiredPropertyException, IOException
    {
        log.debug("Initing CachingProvider");

        this.m_engine = engine;

        //
        //  Cache consistency checks
        //
        m_expiryPeriod = conf.getInt(PROP_CACHECHECKINTERVAL, PROP_CACHECHECKINTERVAL_DEFAULT);

        if (log.isDebugEnabled())
        {
            log.debug("Cache expiry period is " + m_expiryPeriod + " s");
        }

        //
        //  Text cache capacity
        //
        int capacity = conf.getInt(PROP_CACHECAPACITY, PROP_CACHECAPACITY_DEFAULT);

        if (log.isDebugEnabled())
        {
            log.debug("Cache capacity " + capacity + " pages.");
        }

        m_cache = new Cache(true, false, false);
        m_cache.addCacheEventListener(m_allCollector, CacheEntryEventListener.class);

        m_negCache = new Cache(true, false, false);

        m_textCache = new Cache(true, false, false, false, OSCACHE_ALGORITHM, capacity);

        m_historyCache = new Cache(true, false, false, false, OSCACHE_ALGORITHM, capacity);

        PicoContainer container = m_engine.getComponentContainer();
        m_provider = (WikiPageProvider) container.getComponentInstance(WikiConstants.REAL_PAGE_PROVIDER);

        //
        // See if we're using Lucene, and if so, ensure that its
        // index directory is up to date.
        //
        m_useLucene = conf.getBoolean(PROP_USE_LUCENE, PROP_USE_LUCENE_DEFAULT);

        m_analyzerClass = conf.getString(PROP_LUCENE_ANALYZER, PROP_LUCENE_ANALYZER_DEFAULT);
    }

    public synchronized void start()
    {
        if (m_useLucene)
        {
            initLucene();
        }
    }

    public synchronized void stop()
    {
        // GNDN
    }

    private Analyzer getLuceneAnalyzer()
            throws ClassNotFoundException,
                   InstantiationException,
                   IllegalAccessException
    {
        Analyzer analyzer = (Analyzer) Class.forName(m_analyzerClass).newInstance();
        return analyzer;
    }
    
    /**
     * Waits first for a little while before starting to go through the Lucene "pages that need
     * updating".
     */
    private void startLuceneUpdateThread()
    {
        m_luceneUpdateThread =
                new Thread(
                        new Runnable()
                        {
                            public void run()
                            {
                                // FIXME: This is a kludge - JSPWiki should somehow report
                                //        that init phase is complete.
                                try
                                {
                                    Thread.sleep(60000L);
                                }
                                catch (InterruptedException e)
                                {
                                }

                                while (true)
                                {
                                    while (m_updates.size() > 0)
                                    {
                                        Object [] pair = (Object []) m_updates.remove(0);
                                        WikiPage page = (WikiPage) pair[0];
                                        String text = (String) pair[1];
                                        updateLuceneIndex(page, text);
                                    }

                                    try
                                    {
                                        Thread.sleep(500);
                                    }
                                    catch (InterruptedException e)
                                    {
                                    }
                                }
                            }
                        });
        m_luceneUpdateThread.start();
    }

    private void luceneIndexPage(WikiPage page, String text, IndexWriter writer)
            throws IOException
    {
        // make a new, empty document
        Document doc = new Document();

        // Raw name is the keyword we'll use to refer to this document for updates.
        doc.add(Field.Keyword(LUCENE_ID, page.getName()));

        // Body text is indexed, but not stored in doc. We add in the
        // title text as well to make sure it gets considered.
        doc.add(
                Field.Text(
                        LUCENE_PAGE_CONTENTS,
                        new StringReader(text + " " + TextUtil.beautifyString(page.getName()))));
        writer.addDocument(doc);
    }

    /**
     * Attempts to fetch the given page information from the cache.  If the page is not in there,
     * checks the real provider.
     *
     * @param name The page name to look for
     *
     * @return The WikiPage, or null, if the page does not exist
     *
     * @throws ProviderException If something failed
     * @throws RepositoryModifiedException If the page exists, but has been changed in the
     *         repository
     */
    private WikiPage getPageInfoFromCache(String name)
            throws ProviderException
    {
        try
        {
            WikiPage item = (WikiPage) m_cache.getFromCache(name, m_expiryPeriod);

            if (item != null)
            {
                return item;
            }

            return null;
        }
        catch (NeedsRefreshException e)
        {
            WikiPage cached = (WikiPage) e.getCacheContent();

            // int version = (cached != null) ? cached.getVersion() : WikiPageProvider.LATEST_VERSION;
            WikiPage refreshed = m_provider.getPageInfo(name, WikiPageProvider.LATEST_VERSION);

            if ((refreshed == null) && (cached != null))
            {
                //  Page has been removed evilly by a goon from outer space
                if (log.isDebugEnabled())
                {
                    log.debug("Page " + name + " has been removed externally.");
                }

                m_cache.putInCache(name, null);
                m_textCache.putInCache(name, null);
                m_historyCache.putInCache(name, null);

                // We cache a page miss
                m_negCache.putInCache(name, name);

                if (m_useLucene)
                {
                    deleteFromLucene(new WikiPage(name));
                }

                throw new RepositoryModifiedException("Removed: " + name, name);
            }
            else if (cached == null)
            {
                // The page did not exist in the first place
                if (refreshed != null)
                {
                    // We must now add it
                    m_cache.putInCache(name, refreshed);

                    // Requests for this page are now no longer denied
                    m_negCache.putInCache(name, null);

                    throw new RepositoryModifiedException("Added: " + name, name);

                    // return refreshed;
                }
                else
                {
                    // Cache page miss
                    m_negCache.putInCache(name, name);
                    m_cache.cancelUpdate(name);
                }
            }
            else if (cached.getVersion() != refreshed.getVersion())
            {
                //  The newest version has been deleted, but older versions still remain
                if (log.isDebugEnabled())
                {
                    log.debug("Page " + cached.getName() + " newest version deleted, reloading...");
                }

                m_cache.putInCache(name, refreshed);

                // Requests for this page are now no longer denied
                m_negCache.putInCache(name, null);

                m_textCache.flushEntry(name);
                m_historyCache.flushEntry(name);

                return refreshed;
            }
            else if (
                    Math.abs(
                            refreshed.getLastModified().getTime()
                            - cached.getLastModified().getTime()) > 1000L)
            {
                //  Yes, the page has been modified externally and nobody told us
                if (log.isInfoEnabled())
                {
                    log.info("Page " + cached.getName() + " changed, reloading...");
                }

                m_cache.putInCache(name, refreshed);

                // Requests for this page are now no longer denied
                m_negCache.putInCache(name, null);
                m_textCache.flushEntry(name);
                m_historyCache.flushEntry(name);

                throw new RepositoryModifiedException("Modified: " + name, name);
            }
            else
            {
                // Refresh the cache by putting the same object back
                m_cache.putInCache(name, cached);

                // Requests for this page are now no longer denied
                m_negCache.putInCache(name, null);
            }

            return cached;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param pageName DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean pageExists(String pageName)
    {
        //
        //  First, check the negative cache if we've seen it before
        //
        try
        {
            String isNonExistant = (String) m_negCache.getFromCache(pageName, m_expiryPeriod);

            if (isNonExistant != null)
            {
                return false; // No such page
            }
        }
        catch (NeedsRefreshException e)
        {
            // OSCache 2.1 locks the Entry which leads to a deadlock. We must unlock the entry
            // if there is no entry yet in there.
            m_negCache.cancelUpdate(pageName);

            // Let's just check if the page exists in the normal way
        }

        WikiPage p = null;

        try
        {
            p = getPageInfoFromCache(pageName);
        }
        catch (RepositoryModifiedException e)
        {
            // The repository was modified, we need to check now if the page was removed or
            // added.
            // TODO: This information would be available in the exception, but we would
            //       need to subclass.
            try
            {
                p = getPageInfoFromCache(pageName);
            }
            catch (Exception ex)
            {
                return false;
            } // This should not happen
        }
        catch (ProviderException e)
        {
            if (log.isInfoEnabled())
            {
                log.info("Provider failed while trying to check if page exists: " + pageName);
            }

            return false;
        }

        //
        //  A null item means that the page either does not
        //  exist, or has not yet been cached; a non-null
        //  means that the page does exist.
        //
        if (p != null)
        {
            return true;
        }

        //
        //  If we have a list of all pages in memory, then any page
        //  not in the cache must be non-existent.
        //
        //  FIXME: There's a problem here; if someone modifies the
        //         repository by adding a page outside JSPWiki,
        //         we won't notice it.
        if (m_gotall)
        {
            return false;
        }

        //
        //  We could add the page to the cache here as well,
        //  but in order to understand whether that is a
        //  good thing or not we would need to analyze
        //  the JSPWiki calling patterns extensively.  Presumably
        //  it would be a good thing if pageExists() is called
        //  many times before the first getPageText() is called,
        //  and the whole page is cached.
        //
        return m_provider.pageExists(pageName);
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
     * @throws RepositoryModifiedException If the page has been externally modified.
     */
    public String getPageText(String pageName, int version)
            throws ProviderException
    {
        String result = null;

        if (version == WikiPageProvider.LATEST_VERSION)
        {
            result = getTextFromCache(pageName);
        }
        else
        {
            WikiPage p = getPageInfoFromCache(pageName);

            //
            //  Or is this the latest version fetched by version number?
            //
            if ((p != null) && (p.getVersion() == version))
            {
                result = getTextFromCache(pageName);
            }
            else
            {
                result = m_provider.getPageText(pageName, version);
            }
        }

        return result;
    }

    /**
     * Adds a page-text pair to the lucene update queue.  Safe to call always - if lucene is not
     * used, does nothing.
     *
     * @param page DOCUMENT ME!
     * @param text DOCUMENT ME!
     */
    private void addToLuceneQueue(WikiPage page, String text)
    {
        if (m_useLucene && (page != null))
        {
            // Add work item to m_updates queue.
            Object [] pair = new Object[2];
            pair[0] = page;
            pair[1] = text;
            m_updates.add(pair);

            if (log.isDebugEnabled())
            {
                log.debug("Scheduling page " + page.getName() + " for index update");
            }
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
     * @throws RepositoryModifiedException If the page has been externally modified.
     */
    private String getTextFromCache(String pageName)
            throws ProviderException
    {
        String text;

        WikiPage page = getPageInfoFromCache(pageName);

        try
        {
            text = (String) m_textCache.getFromCache(pageName, m_pageContentExpiryPeriod);

            if (text == null)
            {
                if (page != null)
                {
                    text = m_provider.getPageText(pageName, WikiPageProvider.LATEST_VERSION);

                    m_textCache.putInCache(pageName, text);

                    addToLuceneQueue(page, text);

                    m_cacheMisses++;
                }
                else
                {
                    return null;
                }
            }
            else
            {
                m_cacheHits++;
            }
        }
        catch (NeedsRefreshException e)
        {
            if (pageExists(pageName))
            {
                text = m_provider.getPageText(pageName, WikiPageProvider.LATEST_VERSION);

                m_textCache.putInCache(pageName, text);

                addToLuceneQueue(page, text);

                m_cacheMisses++;
            }
            else
            {
                m_textCache.putInCache(pageName, null);

                return null; // No page exists
            }
        }

        return text;
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
        synchronized (this)
        {
            addToLuceneQueue(page, text);

            m_provider.putPageText(page, text);

            page.setLastModified(new Date());

            // Refresh caches properly

            m_cache.flushEntry(page.getName());
            m_textCache.flushEntry(page.getName());
            m_historyCache.flushEntry(page.getName());
            m_negCache.flushEntry(page.getName());
            
            // Refresh caches
            try
            {
                getPageInfoFromCache(page.getName());
            }
            catch(RepositoryModifiedException e)
            {
            } // Expected
        }
    }

    private synchronized void updateLuceneIndex(WikiPage page, String text)
    {
        IndexWriter writer = null;

        if (log.isDebugEnabled())
        {
            log.debug("Updating Lucene index for page '" + page.getName() + "'...");
        }

        try
        {
            deleteFromLucene(page);

            // Now add back the new version.
            writer = new IndexWriter(m_luceneDirectory, getLuceneAnalyzer(), false);
            luceneIndexPage(page, text, writer);
            m_updateCount++;

            if (m_updateCount >= LUCENE_OPTIMIZE_COUNT)
            {
                writer.optimize();
                m_updateCount = 0;
            }
        }
        catch (IOException e)
        {
            log.error("Unable to update page '" + page.getName() + "' from Lucene index", e);
        }
        catch(Exception e)
        {
            log.error("Unexpected Lucene exception - please check configuration!", e);
        }
        finally
        {
            try
            {
                if (writer != null)
                {
                    writer.close();
                }
            }
            catch (IOException ioe)
            {
                log.error("Could not update Lucene Index for " + page.getName(), ioe);
            }
        }

        if (log.isDebugEnabled())
        {
            log.debug("Done updating Lucene index for page '" + page.getName() + "'.");
        }
    }

    private void deleteFromLucene(WikiPage page)
    {
        IndexReader reader = null;

        try
        {
            // Must first remove existing version of page.
            reader = IndexReader.open(m_luceneDirectory);
            reader.delete(new Term(LUCENE_ID, page.getName()));
        }
        catch (IOException e)
        {
            log.error("Unable to update page '" + page.getName() + "' from Lucene index", e);
        }
        finally
        {
            try
            {
                if (reader != null)
                {
                    reader.close();
                }
            }
            catch (IOException ioe)
            {
                log.error("Could not delete Lucene Index for " + page.getName(), ioe);
            }
        }
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
        if (m_gotall)
        {
            return m_allCollector.getAllItems();
        }

        // Make sure that all pages are in the cache.
        synchronized (this)
        {
            Collection all = m_provider.getAllPages();

            for (Iterator i = all.iterator(); i.hasNext();)
            {
                WikiPage p = (WikiPage) i.next();

                m_cache.putInCache(p.getName(), p);

                // Requests for this page are now no longer denied
                m_negCache.putInCache(p.getName(), null);
            }

            m_gotall = true;
            return all;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param date DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Collection getAllChangedSince(Date date)
    {
        return m_provider.getAllChangedSince(date);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws ProviderException DOCUMENT ME!
     */
    public int getPageCount()
            throws ProviderException
    {
        return m_provider.getPageCount();
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
        //
        //  If the provider is a fast searcher, then
        //  just pass this request through.
        //
        if (m_provider instanceof FastSearch)
        {
            return m_provider.findPages(query);
        }

        TreeSet res = new TreeSet(new SearchResultComparator());
        SearchMatcher matcher = new SearchMatcher(query);

        Collection allPages = null;

        try
        {
            if (m_useLucene)
            {
                // To keep the scoring mechanism the same, we'll only use Lucene to determine which pages to score.
                allPages = searchLucene(query);
            }
            else
            {
                allPages = getAllPages();
            }
        }
        catch (ProviderException pe)
        {
            log.error("Unable to retrieve page list", pe);

            return (null);
        }

        Iterator it = allPages.iterator();

        while (it.hasNext())
        {
            try
            {
                WikiPage page = (WikiPage) it.next();

                if (page != null)
                {
                    String pageName = page.getName();
                    String pageContent = getTextFromCache(pageName);
                    SearchResult comparison = matcher.matchPageContent(pageName, pageContent);

                    if (comparison != null)
                    {
                        res.add(comparison);
                    }
                }
            }
            catch (RepositoryModifiedException rme)
            {
                log.error("Repository has been modified!", rme);
            }
            catch (ProviderException pe)
            {
                log.error("Unable to retrieve page from cache", pe);
            }
            catch (IOException ioe)
            {
                log.error("Failed to search page", ioe);
            }
        }

        return res;
    }

    /**
     * DOCUMENT ME!
     *
     * @param queryTerms
     *
     * @return Collection of WikiPage items for the pages that Lucene claims will match the search.
     */
    private Collection searchLucene(QueryItem [] queryTerms)
    {
        Searcher searcher = null;

        try
        {
            searcher = new IndexSearcher(m_luceneDirectory);

            BooleanQuery query = new BooleanQuery();

            for (int curr = 0; curr < queryTerms.length; curr++)
            {
                QueryItem queryTerm = queryTerms[curr];

                if (queryTerm.getWord().indexOf(' ') >= 0)
                { // this is a phrase search

                    StringTokenizer tok = new StringTokenizer(queryTerm.getWord());

                    while (tok.hasMoreTokens())
                    {
                        // Just find pages with the words, so that included stop words don't mess up search.
                        String word = tok.nextToken();
                        query.add(
                                new TermQuery(new Term(LUCENE_PAGE_CONTENTS, word)),
                                queryTerm.getType() == QueryItem.REQUIRED,
                                queryTerm.getType() == QueryItem.FORBIDDEN);
                    }

                    /* Since we're not using Lucene to score, no reason to use PhraseQuery, which removes stop words.
                       PhraseQuery phraseQ = new PhraseQuery();
                       StringTokenizer tok = new StringTokenizer(queryTerm.word);
                       while (tok.hasMoreTokens()) {
                       String word = tok.nextToken();
                       phraseQ.add(new Term(LUCENE_PAGE_CONTENTS, word));
                       }
                       query.add(phraseQ,
                       queryTerm.type == QueryItem.REQUIRED,
                       queryTerm.type == QueryItem.FORBIDDEN);
                    */
                }
                else
                { // single word query
                    query.add(
                            new TermQuery(new Term(LUCENE_PAGE_CONTENTS, queryTerm.getWord())),
                            queryTerm.getType() == QueryItem.REQUIRED, queryTerm.getType() == QueryItem.FORBIDDEN);
                }
            }

            Hits hits = searcher.search(query);

            ArrayList list = new ArrayList(hits.length());

            for (int curr = 0; curr < hits.length(); curr++)
            {
                Document doc = hits.doc(curr);
                String pageName = doc.get(LUCENE_ID);
                WikiPage result = getPageInfo(pageName, WikiPageProvider.LATEST_VERSION);

                if (result != null)
                {
                    list.add(result);
                }
                else
                {
                    log.error(
                            "Lucene found a result page '" + pageName
                            + "' that could not be loaded, removing from Lucene cache");
                    deleteFromLucene(new WikiPage(pageName));
                }
            }

            return list;
        }
        catch (Exception e)
        {
            log.error("Failed during Lucene search", e);

            return Collections.EMPTY_LIST;
        }
        finally
        {
            try
            {
                if (searcher != null)
                {
                    searcher.close();
                }
            }
            catch (IOException ioe)
            {
                log.error("Could not search in Lucene Index", ioe);
            }
        }
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
     * @throws RepositoryModifiedException DOCUMENT ME!
     */
    public WikiPage getPageInfo(String pageName, int version)
            throws ProviderException
    {
        WikiPage cached = getPageInfoFromCache(pageName);

        int latestcached = (cached != null)
                ? cached.getVersion()
                : Integer.MIN_VALUE;

        if ((version == WikiPageProvider.LATEST_VERSION) || (version == latestcached))
        {
            if (cached == null)
            {
                WikiPage data = m_provider.getPageInfo(pageName, version);

                if (data != null)
                {
                    m_cache.putInCache(pageName, data);

                    // Requests for this page are now no longer denied
                    m_negCache.putInCache(pageName, null);
                }

                return data;
            }

            return cached;
        }
        else
        {
            // We do not cache old versions.
            return m_provider.getPageInfo(pageName, version);
        }
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
    public List getVersionHistory(String page)
            throws ProviderException
    {
        List history = null;

        try
        {
            history = (List) m_historyCache.getFromCache(page, m_expiryPeriod);

            if (log.isDebugEnabled())
            {
                log.debug("History cache hit for page " + page);
            }

            m_historyCacheHits++;
        }
        catch (NeedsRefreshException e)
        {
            history = m_provider.getVersionHistory(page);

            m_historyCache.putInCache(page, history);

            if (log.isDebugEnabled())
            {
                log.debug("History cache miss for page " + page);
            }

            m_historyCacheMisses++;
        }

        return history;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public synchronized String getProviderInfo()
    {
        return ("Real provider: " + m_provider.getClass().getName() + "<br />Cache misses: "
                + m_cacheMisses + "<br />Cache hits: " + m_cacheHits + "<br />History cache hits: "
                + m_historyCacheHits + "<br />History cache misses: " + m_historyCacheMisses
                + "<br />Cache consistency checks: " + m_expiryPeriod + "s" + "<br />Lucene enabled: "
                + (m_useLucene
                        ? "yes"
                        : "no"));
    }

    /**
     * DOCUMENT ME!
     *
     * @param pageName DOCUMENT ME!
     * @param version DOCUMENT ME!
     *
     * @throws ProviderException DOCUMENT ME!
     */
    public void deleteVersion(String pageName, int version)
            throws ProviderException
    {
        //
        //  Luckily, this is such a rare operation it is okay
        //  to synchronize against the whole thing.
        //
        synchronized (this)
        {
            WikiPage cached = getPageInfoFromCache(pageName);

            int latestcached = (cached != null)
                    ? cached.getVersion()
                    : Integer.MIN_VALUE;

            //
            //  If we have this version cached, remove from cache.
            //
            if ((version == WikiPageProvider.LATEST_VERSION) || (version == latestcached))
            {
                m_cache.flushEntry(pageName);
                m_textCache.putInCache(pageName, null);
                m_historyCache.putInCache(pageName, null);
            }

            m_provider.deleteVersion(pageName, version);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param pageName DOCUMENT ME!
     *
     * @throws ProviderException DOCUMENT ME!
     */
    public void deletePage(String pageName)
            throws ProviderException
    {
        //
        //  See note in deleteVersion().
        //
        synchronized (this)
        {
            if (m_useLucene)
            {
                deleteFromLucene(getPageInfo(pageName, WikiPageProvider.LATEST_VERSION));
            }

            m_cache.putInCache(pageName, null);
            m_textCache.putInCache(pageName, null);
            m_historyCache.putInCache(pageName, null);
            m_negCache.putInCache(pageName, pageName);
            m_provider.deletePage(pageName);
        }
    }

    /**
     * Returns the actual used provider.
     *
     * @return DOCUMENT ME!
     *
     * @since 2.0
     */
    public WikiPageProvider getRealProvider()
    {
        return m_provider;
    }

    private void initLucene()
    {
        m_luceneDirectory = m_engine.getWorkDir() + File.separator + LUCENE_DIR;

        // FIXME: Just to be simple for now, we will do full reindex
        // only if no files are in lucene directory.
        File dir = new File(m_luceneDirectory);

        if (log.isInfoEnabled())
        {
            log.info("Lucene enabled, cache will be in: " + dir.getAbsolutePath());
        }

        try
        {
            if (!dir.exists())
            {
                dir.mkdirs();
            }

            if (!dir.exists() || !dir.canWrite() || !dir.canRead())
            {
                log.error("Cannot write to Lucene directory, disabling Lucene: " + dir.getAbsolutePath());
                throw new IOException("Invalid Lucene directory.");
            }
                
            String[] filelist = dir.list();
                
            if (filelist == null)
            {
                throw new IOException("Invalid Lucene directory: cannot produce listing: "+dir.getAbsolutePath());
            }
                
            if (filelist.length == 0)
            {
                //
                //  No files? Reindex!
                //
                Date start = new Date();
                IndexWriter writer = null;

                log.info("Starting Lucene reindexing, this can take a couple minutes...");

                //
                //  Do lock recovery, in case JSPWiki was shut down forcibly
                //
                Directory luceneDir = FSDirectory.getDirectory(dir, false);
                    
                if (IndexReader.isLocked(luceneDir))
                {
                    log.info("JSPWiki was shut down while Lucene was indexing - unlocking now.");
                    IndexReader.unlock(luceneDir);
                }
                    
                try
                {
                    writer = new IndexWriter(m_luceneDirectory, getLuceneAnalyzer(), true);

                    Collection allPages = getAllPages();

                    for (Iterator iterator = allPages.iterator(); iterator.hasNext();)
                    {
                        WikiPage page = (WikiPage) iterator.next();
                        String text = getPageText(page.getName(), WikiProvider.LATEST_VERSION);
                        luceneIndexPage(page, text, writer);
                    }

                    writer.optimize();
                }
                finally
                {
                    if (writer != null)
                    {
                        writer.close();
                    }
                }

                Date end = new Date();

                if (log.isInfoEnabled())
                {
                    log.info(
                            "Full Lucene index finished in " + (end.getTime() - start.getTime())
                            + " milliseconds.");
                }
            }
            else
            {
                log.info("Files found in Lucene directory, not reindexing.");
            }
        }
        catch (NoClassDefFoundError e)
        {
            log.info("Lucene libraries do not exist - not using Lucene.");
            m_useLucene = false;
        }
        catch (IOException e)
        {
            log.error("Problem while creating Lucene index - not using Lucene.", e);
            m_useLucene = false;
        }
        catch (ProviderException e)
        {
            log.error("Problem reading pages while creating Lucene index (JSPWiki won't start.)", e);
            throw new IllegalArgumentException("unable to create Lucene index");
        }
        catch(ClassNotFoundException e)
        {
            log.error("Illegal Analyzer specified:", e);
            m_useLucene = false;
        }
        catch(Exception e)
        {
            log.error("Unable to start lucene", e);
            m_useLucene = false;
        }

        startLuceneUpdateThread();
    }


    /**
     * This is a simple class that keeps a list of all WikiPages that we have in memory.  Because
     * the OSCache cannot give us a list of all pages currently in cache, we'll have to check
     * this.
     *
     * @author jalkanen
     *
     * @since
     */
    private static class CacheItemCollector
            implements CacheEntryEventListener
    {
        /** DOCUMENT ME! */
        private TreeSet m_allItems = new TreeSet();

        /**
         * DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        public Set getAllItems()
        {
            return m_allItems;
        }

        /**
         * DOCUMENT ME!
         *
         * @param arg0 DOCUMENT ME!
         */
        public void cacheEntryAdded(CacheEntryEvent arg0)
        {
            WikiPage item = (WikiPage) arg0.getEntry().getContent();

            if (item != null)
            {
                // Item added or replaced.
                m_allItems.add(item);
            }
        }

        /**
         * DOCUMENT ME!
         *
         * @param arg0 DOCUMENT ME!
         */
        public void cacheEntryRemoved(CacheEntryEvent arg0)
        {
            // Removed item
            // FIXME: If the page system is changed during this time, we'll just fail gracefully
                
            try
            {
                for(Iterator i = m_allItems.iterator(); i.hasNext();)
                {
                    WikiPage p = (WikiPage)i.next();
                    
                    if (p.getName().equals(arg0.getKey()))
                    {
                        i.remove();
                        break;
                    }
                }
            }
            catch(Exception e)
            {
            }
        }

        /**
         * DOCUMENT ME!
         *
         * @param arg0 DOCUMENT ME!
         */
        public void cacheEntryFlushed(CacheEntryEvent arg0)
        {
            cacheEntryRemoved(arg0);
        }

        /**
         * DOCUMENT ME!
         *
         * @param arg0 DOCUMENT ME!
         */
        public void cacheEntryUpdated(CacheEntryEvent arg0)
        {
            WikiPage item = (WikiPage) arg0.getEntry().getContent();

            if (item != null)
            {
                cacheEntryAdded(arg0);
            }
            else
            {
                cacheEntryRemoved(arg0);
            }
        }

        /**
         * DOCUMENT ME!
         *
         * @param arg0 DOCUMENT ME!
         */
        public void cacheGroupFlushed(CacheGroupEvent arg0)
        {
        }

        /**
         * DOCUMENT ME!
         *
         * @param arg0 DOCUMENT ME!
         */
        public void cachePatternFlushed(CachePatternEvent arg0)
        {
        }

        /**
         * DOCUMENT ME!
         *
         * @param arg0 DOCUMENT ME!
         */
        public void cacheFlushed(CachewideEvent arg0)
        {
        }
    }
}
