package de.softwareforge.eyewiki.xmlrpc;

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

import java.io.UnsupportedEncodingException;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.xmlrpc.XmlRpcException;

import de.softwareforge.eyewiki.LinkCollector;
import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.WikiPage;
import de.softwareforge.eyewiki.attachment.Attachment;
import de.softwareforge.eyewiki.auth.AuthorizationManager;
import de.softwareforge.eyewiki.auth.UserProfile;
import de.softwareforge.eyewiki.auth.permissions.ViewPermission;
import de.softwareforge.eyewiki.util.TextUtil;

/**
 * Provides handlers for all RPC routines.
 *
 * @author Janne Jalkanen
 *
 * @since 1.6.6
 */

// We could use WikiEngine directly, but because of introspection it would
// show just too many methods to be safe.
public class RPCHandler
        extends AbstractRPCHandler
{
    /** DOCUMENT ME! */
    private static final Logger log = Logger.getLogger(RPCHandler.class);

    /**
     * DOCUMENT ME!
     *
     * @param engine DOCUMENT ME!
     */
    public void initialize(WikiEngine engine)
    {
        super.initialize(engine);
    }

    /**
     * Converts Java string into RPC string.
     *
     * @param src DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private String toRPCString(String src)
    {
        return TextUtil.urlEncodeUTF8(src);
    }

    /**
     * Converts RPC string (UTF-8, url encoded) into Java string.
     *
     * @param src DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private String fromRPCString(String src)
    {
        return TextUtil.urlDecodeUTF8(src);
    }

    /**
     * Transforms a Java string into UTF-8.
     *
     * @param src DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private byte [] toRPCBase64(String src)
    {
        try
        {
            return src.getBytes("UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            //
            //  You shouldn't be running eyeWiki on a platform that does not
            //  use UTF-8.  We revert to platform default, so that the other
            //  end might have a chance of getting something.
            //
            log.fatal("Platform does not support UTF-8, reverting to platform default");

            return src.getBytes();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getApplicationName()
    {
        return toRPCString(m_engine.getApplicationName());
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Vector getAllPages()
    {
        Collection pages = m_engine.getRecentChanges();
        Vector result = new Vector();

        for (Iterator i = pages.iterator(); i.hasNext();)
        {
            WikiPage p = (WikiPage) i.next();

            if (!(p instanceof Attachment))
            {
                result.add(toRPCString(p.getName()));
            }
        }

        return result;
    }

    /**
     * Encodes a single wiki page info into a Hashtable.
     *
     * @param page DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected Hashtable encodeWikiPage(WikiPage page)
    {
        Hashtable ht = new Hashtable();

        ht.put("name", toRPCString(page.getName()));

        Date d = page.getLastModified();

        //
        //  Here we reset the DST and TIMEZONE offsets of the
        //  calendar.  Unfortunately, I haven't thought of a better
        //  way to ensure that we're getting the proper date
        //  from the XML-RPC thingy, except to manually adjust the date.
        //
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.add(Calendar.MILLISECOND,
            -(cal.get(Calendar.ZONE_OFFSET) + (cal.getTimeZone().inDaylightTime(d) ? cal.get(Calendar.DST_OFFSET) : 0)));

        ht.put("lastModified", cal.getTime());
        ht.put("version", new Integer(page.getVersion()));

        if (page.getAuthor() != null)
        {
            ht.put("author", toRPCString(page.getAuthor()));
        }

        return ht;
    }

    /**
     * DOCUMENT ME!
     *
     * @param since DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Vector getRecentChanges(Date since)
    {
        Collection pages = m_engine.getRecentChanges();
        Vector result = new Vector();

        Calendar cal = Calendar.getInstance();
        cal.setTime(since);

        //
        //  Convert UTC to our time.
        //
        cal.add(Calendar.MILLISECOND,
            (cal.get(Calendar.ZONE_OFFSET) + (cal.getTimeZone().inDaylightTime(since) ? cal.get(Calendar.DST_OFFSET) : 0)));
        since = cal.getTime();

        for (Iterator i = pages.iterator(); i.hasNext();)
        {
            WikiPage page = (WikiPage) i.next();

            if (page.getLastModified().after(since) && !(page instanceof Attachment))
            {
                result.add(encodeWikiPage(page));
            }
        }

        return result;
    }

    /**
     * Simple helper method, turns the incoming page name into normal Java string, then checks page condition.
     *
     * @param pagename Page Name as an RPC string (URL-encoded UTF-8)
     *
     * @return Real page name, as Java string.
     *
     * @throws XmlRpcException if there is something wrong with the page.
     */
    private String parsePageCheckCondition(String pagename)
            throws XmlRpcException
    {
        pagename = fromRPCString(pagename);

        if (!m_engine.pageExists(pagename))
        {
            throw new XmlRpcException(ERR_NOPAGE, "No such page '" + pagename + "' found, o master.");
        }

        AuthorizationManager mgr = m_engine.getAuthorizationManager();
        UserProfile currentUser = new UserProfile(); // This should be a guest.

        if (!mgr.checkPermission(m_engine.getPage(pagename), currentUser, new ViewPermission()))
        {
            throw new XmlRpcException(ERR_NOPERMISSION, "No permission to view page " + pagename + ", o master");
        }

        return pagename;
    }

    /**
     * DOCUMENT ME!
     *
     * @param pagename DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws XmlRpcException DOCUMENT ME!
     */
    public Hashtable getPageInfo(String pagename)
            throws XmlRpcException
    {
        pagename = parsePageCheckCondition(pagename);

        return encodeWikiPage(m_engine.getPage(pagename));
    }

    /**
     * DOCUMENT ME!
     *
     * @param pagename DOCUMENT ME!
     * @param version DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws XmlRpcException DOCUMENT ME!
     */
    public Hashtable getPageInfoVersion(String pagename, int version)
            throws XmlRpcException
    {
        pagename = parsePageCheckCondition(pagename);

        return encodeWikiPage(m_engine.getPage(pagename, version));
    }

    /**
     * DOCUMENT ME!
     *
     * @param pagename DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws XmlRpcException DOCUMENT ME!
     */
    public byte [] getPage(String pagename)
            throws XmlRpcException
    {
        pagename = parsePageCheckCondition(pagename);

        String text = m_engine.getPureText(pagename, -1);

        return toRPCBase64(text);
    }

    /**
     * DOCUMENT ME!
     *
     * @param pagename DOCUMENT ME!
     * @param version DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws XmlRpcException DOCUMENT ME!
     */
    public byte [] getPageVersion(String pagename, int version)
            throws XmlRpcException
    {
        pagename = parsePageCheckCondition(pagename);

        return toRPCBase64(m_engine.getPureText(pagename, version));
    }

    /**
     * DOCUMENT ME!
     *
     * @param pagename DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws XmlRpcException DOCUMENT ME!
     */
    public byte [] getPageHTML(String pagename)
            throws XmlRpcException
    {
        pagename = parsePageCheckCondition(pagename);

        return toRPCBase64(m_engine.getHTML(pagename));
    }

    /**
     * DOCUMENT ME!
     *
     * @param pagename DOCUMENT ME!
     * @param version DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws XmlRpcException DOCUMENT ME!
     */
    public byte [] getPageHTMLVersion(String pagename, int version)
            throws XmlRpcException
    {
        pagename = parsePageCheckCondition(pagename);

        return toRPCBase64(m_engine.getHTML(pagename, version));
    }

    /**
     * DOCUMENT ME!
     *
     * @param pagename DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws XmlRpcException DOCUMENT ME!
     */
    public Vector listLinks(String pagename)
            throws XmlRpcException
    {
        pagename = parsePageCheckCondition(pagename);

        WikiPage page = m_engine.getPage(pagename);
        String pagedata = m_engine.getPureText(page);

        LinkCollector localCollector = new LinkCollector();
        LinkCollector extCollector = new LinkCollector();
        LinkCollector attCollector = new LinkCollector();

        WikiContext context = new WikiContext(m_engine, page);
        context.setVariable(WikiEngine.PROP_REFSTYLE, "absolute");

        m_engine.textToHTML(context, pagedata, localCollector, extCollector, attCollector);

        Vector result = new Vector();

        //
        //  Add local links.
        //
        for (Iterator i = localCollector.getLinks().iterator(); i.hasNext();)
        {
            String link = (String) i.next();
            Hashtable ht = new Hashtable();
            ht.put("page", toRPCString(link));
            ht.put("type", LINK_LOCAL);

            //
            //  FIXME: This is a kludge.  The link format should really be queried
            //  from the TranslatorReader itself.  Also, the link format should probably
            //  have information on whether the page exists or not.
            //
            //
            //  FIXME: The current link collector interface is not very good, since
            //  it causes this.
            //
            if (m_engine.pageExists(link))
            {
                ht.put("href", context.getURL(WikiContext.VIEW, link));
            }
            else
            {
                ht.put("href", context.getURL(WikiContext.EDIT, link));
            }

            result.add(ht);
        }

        //
        // Add links to inline attachments
        //
        for (Iterator i = attCollector.getLinks().iterator(); i.hasNext();)
        {
            String link = (String) i.next();

            Hashtable ht = new Hashtable();

            ht.put("page", toRPCString(link));
            ht.put("type", LINK_LOCAL);
            ht.put("href", context.getURL(WikiContext.ATTACH, link));

            result.add(ht);
        }

        //
        // External links don't need to be changed into XML-RPC strings,
        // simply because URLs are by definition ASCII.
        //
        for (Iterator i = extCollector.getLinks().iterator(); i.hasNext();)
        {
            String link = (String) i.next();

            Hashtable ht = new Hashtable();

            ht.put("page", link);
            ht.put("type", LINK_EXTERNAL);
            ht.put("href", link);

            result.add(ht);
        }

        return result;
    }
}
