package de.softwareforge.eyewiki.attachment;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.softwareforge.eyewiki.TranslatorReader;
import de.softwareforge.eyewiki.WikiConstants;
import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.WikiPage;
import de.softwareforge.eyewiki.WikiProperties;
import de.softwareforge.eyewiki.WikiProvider;
import de.softwareforge.eyewiki.manager.ReferenceManager;
import de.softwareforge.eyewiki.providers.ProviderException;
import de.softwareforge.eyewiki.providers.WikiAttachmentProvider;

import org.picocontainer.PicoContainer;

/**
 * Provides facilities for handling attachments.  All attachment handling goes through this class.
 *
 * <p>
 * The AttachmentManager provides a facade towards the current WikiAttachmentProvider that is in use.  It is created by the
 * WikiEngine as a singleton object, and can be requested through the WikiEngine.
 * </p>
 *
 * @author Janne Jalkanen
 *
 * @since 1.9.28
 */
public class AttachmentManager
        implements WikiProperties
{
    /** DOCUMENT ME! */
    private static final Logger log = Logger.getLogger(AttachmentManager.class);

    /** DOCUMENT ME! */
    private final WikiAttachmentProvider m_provider;

    /** DOCUMENT ME! */
    private final WikiEngine m_engine;

    /** The Reference Manager used for Attachments */
    private final ReferenceManager m_referenceManager;

    /**
     * Creates a new AttachmentManager.  Note that creation will never fail, but it's quite likely that attachments do not
     * function.
     *
     * <p>
     * <b>DO NOT CREATE</b> an AttachmentManager on your own, unless you really know what you're doing.  Just use
     * WikiEngine.getAttachmentManager() if you're making a module for eyeWiki.
     * </p>
     *
     * @param engine The wikiengine that owns this attachment manager.
     * @param referenceManager A list of properties from which the AttachmentManager will seek its configuration.  Typically this
     *        is the "eyewiki.properties".
     */
    public AttachmentManager(final WikiEngine engine, final ReferenceManager referenceManager)
    {
        m_engine = engine;
        m_referenceManager = referenceManager;

        PicoContainer container = m_engine.getComponentContainer();

        m_provider = (WikiAttachmentProvider) container.getComponentInstance(WikiConstants.ATTACHMENT_PROVIDER);
    }

    /**
     * Returns true, if attachments are enabled and running.
     *
     * @return DOCUMENT ME!
     */
    public boolean attachmentsEnabled()
    {
        return m_provider != null;
    }

    /**
     * Gets info on a particular attachment, latest version.
     *
     * @param name A full attachment name.
     *
     * @return Attachment, or null, if no such attachment exists.
     *
     * @throws ProviderException If something goes wrong.
     */
    public Attachment getAttachmentInfo(String name)
            throws ProviderException
    {
        return getAttachmentInfo(name, WikiProvider.LATEST_VERSION);
    }

    /**
     * Gets info on a particular attachment with the given version.
     *
     * @param name A full attachment name.
     * @param version A version number.
     *
     * @return Attachment, or null, if no such attachment or version exists.
     *
     * @throws ProviderException If something goes wrong.
     */
    public Attachment getAttachmentInfo(String name, int version)
            throws ProviderException
    {
        if (name == null)
        {
            return null;
        }

        return getAttachmentInfo(null, name, version);
    }

    /**
     * Figures out the full attachment name from the context and attachment name.
     *
     * @param context The current WikiContext
     * @param attachmentname The file name of the attachment.
     *
     * @return Attachment, or null, if no such attachment exists.
     *
     * @throws ProviderException If something goes wrong.
     */
    public Attachment getAttachmentInfo(WikiContext context, String attachmentname)
            throws ProviderException
    {
        return getAttachmentInfo(context, attachmentname, WikiProvider.LATEST_VERSION);
    }

    /**
     * Figures out the full attachment name from the context and attachment name.
     *
     * @param context The current WikiContext
     * @param attachmentname The file name of the attachment.
     * @param version A particular version.
     *
     * @return Attachment, or null, if no such attachment or version exists.
     *
     * @throws ProviderException If something goes wrong.
     */
    public Attachment getAttachmentInfo(WikiContext context, String attachmentname, int version)
            throws ProviderException
    {
        if (m_provider == null)
        {
            return (null);
        }

        WikiPage currentPage = null;

        if (context != null)
        {
            currentPage = context.getPage();
        }

        //
        //  Figure out the parent page of this attachment.  If we can't find it,
        //  we'll assume this refers directly to the attachment.
        //
        int cutpt = attachmentname.lastIndexOf('/');

        if (cutpt != -1)
        {
            String parentPage = attachmentname.substring(0, cutpt);
            parentPage = TranslatorReader.cleanLink(parentPage);
            attachmentname = attachmentname.substring(cutpt + 1);

            currentPage = m_engine.getPage(parentPage);
        }

        //
        //  If the page cannot be determined, we cannot possibly find the
        //  attachments.
        //
        if ((currentPage == null) || StringUtils.isEmpty(currentPage.getName()))
        {
            return null;
        }

        return m_provider.getAttachmentInfo(currentPage, attachmentname, version);
    }

    /**
     * Returns the list of attachments associated with a given wiki page. If there are no attachments, returns an empty Collection.
     *
     * @param wikipage The wiki page from which you are seeking attachments for.
     *
     * @return a valid collection of attachments.
     *
     * @throws ProviderException DOCUMENT ME!
     */
    public Collection listAttachments(WikiPage wikipage)
            throws ProviderException
    {
        if (m_provider == null)
        {
            return new ArrayList();
        }

        return m_provider.listAttachments(wikipage);
    }

    /**
     * Returns true, if the page has any attachments at all.  This is a convinience method.
     *
     * @param wikipage The wiki page from which you are seeking attachments for.
     *
     * @return True, if the page has attachments, else false.
     */
    public boolean hasAttachments(WikiPage wikipage)
    {
        try
        {
            return listAttachments(wikipage).size() > 0;
        }
        catch (Exception e)
        {
            log.error("While fetching an attachment: ", e);
        }

        return false;
    }

    /**
     * Finds an attachment from the repository as a stream.
     *
     * @param att Attachment
     *
     * @return An InputStream to read from.  May return null, if attachments are disabled.
     *
     * @throws IOException DOCUMENT ME!
     * @throws ProviderException DOCUMENT ME!
     */
    public InputStream getAttachmentStream(Attachment att)
            throws IOException, ProviderException
    {
        if (m_provider == null)
        {
            return (null);
        }

        return m_provider.getAttachmentData(att);
    }

    /**
     * Stores an attachment that lives in the given file. If the attachment did not exist previously, this method will create it.
     * If it did exist, it stores a new version.
     *
     * @param att Attachment to store this under.
     * @param source A file to read from.
     *
     * @throws IOException If writing the attachment failed.
     * @throws ProviderException If something else went wrong.
     */
    public void storeAttachment(Attachment att, File source)
            throws IOException, ProviderException
    {
        FileInputStream in = null;

        try
        {
            in = new FileInputStream(source);
            storeAttachment(att, in);
        }
        finally
        {
            IOUtils.closeQuietly(in);
        }
    }

    /**
     * Stores an attachment directly from a stream. If the attachment did not exist previously, this method will create it.  If it
     * did exist, it stores a new version.
     *
     * @param att Attachment to store this under.
     * @param in InputStream from which the attachment contents will be read.
     *
     * @throws IOException If writing the attachment failed.
     * @throws ProviderException If something else went wrong.
     */
    public void storeAttachment(Attachment att, InputStream in)
            throws IOException, ProviderException
    {
        if (m_provider == null)
        {
            return;
        }

        m_provider.putAttachmentData(att, in);

        m_referenceManager.updateReferences(att.getName(), new java.util.Vector());

        m_engine.updateReferences(new WikiPage(att.getParentName()));
    }

    /**
     * Returns a list of versions of the attachment.
     *
     * @param attachmentName A fully qualified name of the attachment.
     *
     * @return A list of Attachments.  May return null, if attachments are disabled.
     *
     * @throws ProviderException If the provider fails for some reason.
     */
    public List getVersionHistory(String attachmentName)
            throws ProviderException
    {
        if (m_provider == null)
        {
            return (null);
        }

        Attachment att = getAttachmentInfo((WikiContext) null, attachmentName);

        if (att != null)
        {
            return m_provider.getVersionHistory(att);
        }

        return null;
    }

    /**
     * Returns a collection of Attachments, containing each and every attachment that is in this Wiki.
     *
     * @return A collection of attachments.  If attachments are disabled, will return an empty collection.
     *
     * @throws ProviderException DOCUMENT ME!
     */
    public Collection getAllAttachments()
            throws ProviderException
    {
        if (attachmentsEnabled())
        {
            return m_provider.listAllChanged(new Date(0L));
        }

        return new ArrayList();
    }

    /**
     * Returns the current attachment provider.
     *
     * @return The current provider.  May be null, if attachments are disabled.
     */
    public WikiAttachmentProvider getCurrentProvider()
    {
        return m_provider;
    }

    /**
     * Deletes the given attachment version.
     *
     * @param att DOCUMENT ME!
     *
     * @throws ProviderException DOCUMENT ME!
     */
    public void deleteVersion(Attachment att)
            throws ProviderException
    {
        m_provider.deleteVersion(att);
    }

    /**
     * Deletes all versions of the given attachment.
     *
     * @param att DOCUMENT ME!
     *
     * @throws ProviderException DOCUMENT ME!
     */
    public void deleteAttachment(Attachment att)
            throws ProviderException
    {
        m_provider.deleteAttachment(att);
    }
}
