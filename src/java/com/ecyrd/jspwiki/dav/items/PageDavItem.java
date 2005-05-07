/*
 * (C) Janne Jalkanen 2005
 *
 */
package com.ecyrd.jspwiki.dav.items;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang.time.DateFormatUtils;

import org.jdom.Element;
import org.jdom.Namespace;

import com.ecyrd.jspwiki.WikiContext;
import com.ecyrd.jspwiki.WikiEngine;
import com.ecyrd.jspwiki.WikiPage;


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
     * @see com.ecyrd.jspwiki.dav.DavItem#getPropertySet(int)
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
