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
package com.ecyrd.jspwiki.providers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ecyrd.jspwiki.QueryItem;
import com.ecyrd.jspwiki.SearchMatcher;
import com.ecyrd.jspwiki.SearchResult;
import com.ecyrd.jspwiki.SearchResultComparator;
import com.ecyrd.jspwiki.WikiEngine;
import com.ecyrd.jspwiki.WikiPage;
import com.ecyrd.jspwiki.WikiProperties;
import com.ecyrd.jspwiki.WikiProvider;
import com.ecyrd.jspwiki.exception.InternalWikiException;
import com.ecyrd.jspwiki.exception.NoRequiredPropertyException;
import com.ecyrd.jspwiki.util.FileUtil;
import com.ecyrd.jspwiki.util.TextUtil;


/**
 * Provides a simple directory based repository for Wiki pages.
 *
 * <P>
 * All files have ".txt" appended to make life easier for those who insist on using Windows or
 * other software which makes assumptions on the files contents based on its name.
 * </p>
 *
 * <p>
 * This class functions as a superclass to all file based providers.
 * </p>
 *
 * @author Janne Jalkanen
 *
 * @since 2.1.21.
 */
public abstract class AbstractFileProvider
        implements WikiPageProvider
{
    /** DOCUMENT ME! */
    private static final Logger log = Logger.getLogger(AbstractFileProvider.class);

    /**
     * All files should have this extension to be recognized as JSPWiki files. We default to .txt,
     * because that is probably easiest for Windows users, and guarantees correct handling.
     */
    public static final String FILE_EXT = ".txt";

    /** DOCUMENT ME! */
    private String m_pageDirectory = null;

    /** DOCUMENT ME! */
    protected String m_encoding;

    /**
     * DOCUMENT ME!
     *
     * @param engine DOCUMENT ME!
     * @param conf DOCUMENT ME!
     *
     * @throws NoRequiredPropertyException If the specified page directory does not exist.
     * @throws IOException In case the specified page directory is a file, not a directory.
     */
    public void initialize(WikiEngine engine, Configuration conf)
            throws NoRequiredPropertyException, IOException
    {
        log.debug("Initing FileSystemProvider");
        m_pageDirectory = engine.getPageDir();

        if (m_pageDirectory == null)
        {
            throw new NoRequiredPropertyException(
                "File based providers need a " + "page directory but none was found. Aborting!",
                WikiProperties.PROP_PAGEDIR);
        }

        m_encoding =
            conf.getString(WikiProperties.PROP_ENCODING, WikiProperties.PROP_ENCODING_DEFAULT);

        if (log.isInfoEnabled())
        {
            log.info("Wikipages are read from '" + m_pageDirectory + "'");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    String getPageDirectory()
    {
        return m_pageDirectory;
    }

    /**
     * This makes sure that the queried page name is still readable by the file system.
     *
     * @param pagename DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected String mangleName(String pagename)
    {
        pagename = TextUtil.urlEncode(pagename, m_encoding);

        pagename = StringUtils.replace(pagename, "/", "%2F");

        return pagename;
    }

    /**
     * This makes the reverse of mangleName.
     *
     * @param filename DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws InternalWikiException DOCUMENT ME!
     */
    protected String unmangleName(String filename)
    {
        // The exception should never happen.
        try
        {
            return TextUtil.urlDecode(filename, m_encoding);
        }
        catch (UnsupportedEncodingException e)
        {
            throw new InternalWikiException("Faulty encoding; should never happen");
        }
    }

    /**
     * Finds a Wiki page from the page repository.
     *
     * @param page DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected File findPage(String page)
    {
        return new File(m_pageDirectory, mangleName(page) + FILE_EXT);
    }

    /**
     * DOCUMENT ME!
     *
     * @param page DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean pageExists(String page)
    {
        File pagefile = findPage(page);

        return pagefile.exists();
    }

    /**
     * This implementation just returns the current version, as filesystem does not provide
     * versioning information for now.
     *
     * @param page DOCUMENT ME!
     * @param version DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws ProviderException DOCUMENT ME!
     */
    public String getPageText(String page, int version)
            throws ProviderException
    {
        return getPageText(page);
    }

    /**
     * Read the text directly from the correct file.
     *
     * @param page DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private String getPageText(String page)
    {
        String result = null;
        InputStream in = null;

        File pagedata = findPage(page);

        if (pagedata.exists())
        {
            if (pagedata.canRead())
            {
                try
                {
                    in = new FileInputStream(pagedata);
                    result = FileUtil.readContents(in, m_encoding);
                }
                catch (IOException e)
                {
                    log.error("Failed to read", e);
                }
                finally
                {
                    IOUtils.closeQuietly(in);
                }
            }
            else
            {
                log.warn(
                    "Failed to read page '" + page + "' from '" + pagedata.getAbsolutePath()
                    + "', possibly a permissions problem");
            }
        }
        else
        {
            // This is okay.
            if (log.isInfoEnabled())
            {
                log.info("New page '" + page + "'");
            }
        }

        return result;
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
        File file = findPage(page.getName());
        PrintWriter out = null;

        try
        {
            out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), m_encoding));

            out.print(text);
        }
        catch (IOException e)
        {
            log.error("Saving failed");
        }
        finally
        {
            IOUtils.closeQuietly(out);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws ProviderException DOCUMENT ME!
     * @throws InternalWikiException DOCUMENT ME!
     */
    public Collection getAllPages()
            throws ProviderException
    {
        log.debug("Getting all pages...");

        ArrayList set = new ArrayList();

        File wikipagedir = new File(m_pageDirectory);

        File [] wikipages = wikipagedir.listFiles(new WikiFileFilter());

        if (wikipages == null)
        {
            throw new InternalWikiException("Page directory does not exist");
        }

        for (int i = 0; i < wikipages.length; i++)
        {
            String wikiname = wikipages[i].getName();
            int cutpoint = wikiname.lastIndexOf(FILE_EXT);

            WikiPage page =
                getPageInfo(
                    unmangleName(wikiname.substring(0, cutpoint)), WikiPageProvider.LATEST_VERSION);

            if (page == null)
            {
                // This should not really happen.
                // FIXME: Should we throw an exception here?
                log.error(
                    "Page " + wikiname
                    + " was found in directory listing, but could not be located individually.");

                continue;
            }

            set.add(page);
        }

        return set;
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
        return new ArrayList(); // FIXME
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getPageCount()
    {
        File wikipagedir = new File(m_pageDirectory);

        File [] wikipages = wikipagedir.listFiles(new WikiFileFilter());

        return wikipages.length;
    }

    /**
     * Iterates through all WikiPages, matches them against the given query, and returns a
     * Collection of SearchResult objects.
     *
     * @param query DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Collection findPages(QueryItem [] query)
    {
        File wikipagedir = new File(m_pageDirectory);
        TreeSet res = new TreeSet(new SearchResultComparator());
        SearchMatcher matcher = new SearchMatcher(query);

        File [] wikipages = wikipagedir.listFiles(new WikiFileFilter());

        for (int i = 0; i < wikipages.length; i++)
        {
            FileInputStream input = null;

            // log.debug("Searching page "+wikipages[i].getPath() );
            String filename = wikipages[i].getName();
            int cutpoint = filename.lastIndexOf(FILE_EXT);
            String wikiname = filename.substring(0, cutpoint);

            wikiname = unmangleName(wikiname);

            try
            {
                input = new FileInputStream(wikipages[i]);

                String pagetext = FileUtil.readContents(input, m_encoding);
                SearchResult comparison = matcher.matchPageContent(wikiname, pagetext);

                if (comparison != null)
                {
                    res.add(comparison);
                }
            }
            catch (IOException e)
            {
                log.error("Failed to read " + filename, e);
            }
            finally
            {
                IOUtils.closeQuietly(input);
            }
        }

        return res;
    }

    /**
     * Always returns the latest version, since FileSystemProvider does not support versioning.
     *
     * @param page DOCUMENT ME!
     * @param version DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws ProviderException DOCUMENT ME!
     */
    public WikiPage getPageInfo(String page, int version)
            throws ProviderException
    {
        File file = findPage(page);

        if (!file.exists())
        {
            return null;
        }

        WikiPage p = new WikiPage(page);
        p.setLastModified(new Date(file.lastModified()));

        return p;
    }

    /**
     * The FileSystemProvider provides only one version.
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
        ArrayList list = new ArrayList();

        list.add(getPageInfo(page, WikiPageProvider.LATEST_VERSION));

        return list;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getProviderInfo()
    {
        return "";
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
        if (version == WikiProvider.LATEST_VERSION)
        {
            File f = findPage(pageName);

            f.delete();
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
        File f = findPage(pageName);

        f.delete();
    }

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Revision$
     */
    public class WikiFileFilter
            implements FilenameFilter
    {
        /**
         * DOCUMENT ME!
         *
         * @param dir DOCUMENT ME!
         * @param name DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        public boolean accept(File dir, String name)
        {
            return name.endsWith(FILE_EXT);
        }
    }
}
