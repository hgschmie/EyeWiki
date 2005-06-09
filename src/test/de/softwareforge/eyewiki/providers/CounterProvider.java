package de.softwareforge.eyewiki.providers;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;

import de.softwareforge.eyewiki.QueryItem;
import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.WikiPage;
import de.softwareforge.eyewiki.providers.ProviderException;
import de.softwareforge.eyewiki.providers.WikiPageProvider;


/**
 * A provider who counts the hits to different parts.
 */
public class CounterProvider
        implements WikiPageProvider
{
    /** DOCUMENT ME! */
    static Logger log = Logger.getLogger(CounterProvider.class);

    /** DOCUMENT ME! */
    public int m_getPageCalls = 0;

    /** DOCUMENT ME! */
    public int m_pageExistsCalls = 0;

    /** DOCUMENT ME! */
    public int m_getPageTextCalls = 0;

    /** DOCUMENT ME! */
    public int m_getAllPagesCalls = 0;

    /** DOCUMENT ME! */
    public int m_initCalls = 0;

    /** DOCUMENT ME! */
    WikiPage [] m_pages =
        {
            new WikiPage("Foo"),
            new WikiPage("Bar"),
            new WikiPage("Blat"),
            new WikiPage("Blaa")
        };

    /** DOCUMENT ME! */
    String defaultText = "[Foo], [Bar], [Blat], [Blah]";

    /**
     * DOCUMENT ME!
     *
     * @param engine DOCUMENT ME!
     * @param conf DOCUMENT ME!
     */
    public CounterProvider(WikiEngine engine, Configuration conf)
    {
        m_initCalls++;

        for (int i = 0; i < m_pages.length; i++)
        {
            m_pages[i].setAuthor("Unknown");
            m_pages[i].setLastModified(new Date(0L));
            m_pages[i].setVersion(1);
        }
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
     * DOCUMENT ME!
     *
     * @param page DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean pageExists(String page)
    {
        m_pageExistsCalls++;

        //System.out.println("PAGE="+page);
        //TestEngine.trace();
        return findPage(page) != null;
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
        return null;
    }

    private WikiPage findPage(String page)
    {
        for (int i = 0; i < m_pages.length; i++)
        {
            if (m_pages[i].getName().equals(page))
            {
                return m_pages[i];
            }
        }

        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param page DOCUMENT ME!
     * @param version DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public WikiPage getPageInfo(String page, int version)
    {
        m_getPageCalls++;

        //System.out.println("GETPAGEINFO="+page);
        //TestEngine.trace();
        WikiPage p = findPage(page);

        return p;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Collection getAllPages()
    {
        m_getAllPagesCalls++;

        Vector v = new Vector();

        for (int i = 0; i < m_pages.length; i++)
        {
            v.add(m_pages[i]);
        }

        return v;
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
        return new Vector();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getPageCount()
    {
        return m_pages.length;
    }

    /**
     * DOCUMENT ME!
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
     * DOCUMENT ME!
     *
     * @param page DOCUMENT ME!
     * @param version DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getPageText(String page, int version)
    {
        m_getPageTextCalls++;

        return defaultText;
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
