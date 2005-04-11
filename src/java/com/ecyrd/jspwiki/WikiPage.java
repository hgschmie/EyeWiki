/*
    JSPWiki - a JSP-based WikiWiki clone.

    Copyright (C) 2001 Janne Jalkanen (Janne.Jalkanen@iki.fi)

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
package com.ecyrd.jspwiki;

import java.util.Date;
import java.util.HashMap;

import com.ecyrd.jspwiki.acl.AccessControlList;
import com.ecyrd.jspwiki.providers.WikiPageProvider;


/**
 * Simple wrapper class for the Wiki page attributes.  The Wiki page content is moved around in
 * Strings, though.
 */

// FIXME: We need to rethink how metadata is being used - probably the
//        author, date, etc. should also be part of the metadata.  We also
//        need to figure out the metadata lifecycle.
public class WikiPage
        implements Cloneable, Comparable
{
    /** "Summary" is a short summary of the page.  It is a String. */
    public static final String DESCRIPTION = "summary";

    /** DOCUMENT ME! */
    public static final String ALIAS = "alias";

    /** DOCUMENT ME! */
    public static final String REDIRECT = "redirect";

    /** DOCUMENT ME! */
    public static final String SIZE = "size";

    /** DOCUMENT ME! */
    private String m_name;

    /** DOCUMENT ME! */
    private Date m_lastModified;

    /** DOCUMENT ME! */
    private long m_fileSize = -1;

    /** DOCUMENT ME! */
    private int m_version = WikiPageProvider.LATEST_VERSION;

    /** DOCUMENT ME! */
    private String m_author = null;

    /** DOCUMENT ME! */
    private HashMap m_attributes = new HashMap();

    /** DOCUMENT ME! */
    private AccessControlList m_accessList = null;

    /** DOCUMENT ME! */
    private boolean m_hasMetadata = false;

    /**
     * Creates a new WikiPage object.
     *
     * @param name DOCUMENT ME!
     */
    public WikiPage(String name)
    {
        m_name = name;
    }

    /**
     * Needed for clone()'ing
     */
    private WikiPage()
    {
    }

    /**
     * Needed for clone()'ing
     */
    private void setName(String name)
    {
        this.m_name = name;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getName()
    {
        return m_name;
    }

    /**
     * A WikiPage may have a number of attributes, which might or might not be available. Typically
     * attributes are things that do not need to be stored with the wiki page to the page
     * repository, but are generated on-the-fly.  A provider is not required to save them, but
     * they can do that if they really want.
     *
     * @param key The key using which the attribute is fetched
     *
     * @return The attribute.  If the attribute has not been set, returns null.
     */
    public Object getAttribute(String key)
    {
        return m_attributes.get(key);
    }

    /**
     * Sets an metadata attribute.
     *
     * @param key DOCUMENT ME!
     * @param attribute DOCUMENT ME!
     */
    public void setAttribute(String key, Object attribute)
    {
        m_attributes.put(key, attribute);
    }

    /**
     * Removes an attribute from the page, if it exists.
     *
     * @param key DOCUMENT ME!
     *
     * @return If the attribute existed, returns the object.
     *
     * @since 2.1.111
     */
    public Object removeAttribute(String key)
    {
        return m_attributes.remove(key);
    }

    /**
     * Returns the date when this page was last modified.
     *
     * @return DOCUMENT ME!
     */
    public Date getLastModified()
    {
        return new Date((m_lastModified != null) ? m_lastModified.getTime() : 0);
    }

    /**
     * DOCUMENT ME!
     *
     * @param date DOCUMENT ME!
     */
    public void setLastModified(Date date)
    {
        m_lastModified = new Date(date.getTime());
    }

    /**
     * DOCUMENT ME!
     *
     * @param version DOCUMENT ME!
     */
    public void setVersion(int version)
    {
        m_version = version;
    }

    /**
     * Returns the version that this WikiPage instance represents.
     *
     * @return DOCUMENT ME!
     */
    public int getVersion()
    {
        return m_version;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @since 2.1.109
     */
    public long getSize()
    {
        return (m_fileSize);
    }

    /**
     * DOCUMENT ME!
     *
     * @param size DOCUMENT ME!
     *
     * @since 2.1.109
     */
    public void setSize(long size)
    {
        m_fileSize = size;
    }

    /**
     * Returns the AccessControlList for this page.  May return null, in case there is no ACL
     * defined for this page, or it has not yet been received.
     *
     * @return DOCUMENT ME!
     */
    public AccessControlList getAcl()
    {
        return m_accessList;
    }

    /**
     * DOCUMENT ME!
     *
     * @param acl DOCUMENT ME!
     */
    public void setAcl(AccessControlList acl)
    {
        m_accessList = acl;
    }

    /**
     * DOCUMENT ME!
     *
     * @param author DOCUMENT ME!
     */
    public void setAuthor(String author)
    {
        m_author = author;
    }

    /**
     * Returns author name, or null, if no author has been defined.
     *
     * @return DOCUMENT ME!
     */
    public String getAuthor()
    {
        return m_author;
    }

    /**
     * This method will remove all metadata from the page.
     */
    public void invalidateMetadata()
    {
        m_hasMetadata = false;
        setAcl(null);
        m_attributes.clear();
    }

    /**
     * Returns true, if the page has valid metadata, i.e. it has been parsed.
     *
     * @return DOCUMENT ME!
     */
    public boolean hasMetadata()
    {
        return m_hasMetadata;
    }

    /**
     * DOCUMENT ME!
     */
    public void setHasMetadata()
    {
        m_hasMetadata = true;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String toString()
    {
        return "WikiPage [" + m_name + ",ver=" + m_version + ",mod=" + m_lastModified + "]";
    }

    /**
     * Creates a deep clone of a WikiPage.  Strings are not cloned, since they're immutable.
     *
     * @return DOCUMENT ME!
     */
    public Object clone()
    {
        WikiPage p = null;
        
        try
        {
            p = (WikiPage) super.clone();
        }
        catch (CloneNotSupportedException cne)
        {
            throw new RuntimeException("Could not clone WikiPage", cne);
        }
        p.setName(m_name);
        p.setAuthor(m_author);
        p.setVersion(m_version);
        p.setLastModified(m_lastModified);
        return p;
    }

    /**
     * DOCUMENT ME!
     *
     * @param o DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int compareTo(Object o)
    {
        int res = 0;

        if (o instanceof WikiPage)
        {
            WikiPage c = (WikiPage) o;

            res = this.getName().compareTo(c.getName());

            if (res == 0)
            {
                res = this.getVersion() - c.getVersion();
            }
        }

        return res;
    }
}
