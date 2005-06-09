package de.softwareforge.eyewiki.providers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import java.util.List;


import de.softwareforge.eyewiki.QueryItem;
import de.softwareforge.eyewiki.WikiPage;
import de.softwareforge.eyewiki.WikiProvider;
import de.softwareforge.eyewiki.attachment.Attachment;


/**
 * Defines an attachment provider - a class which is capable of saving binary data as attachments.
 *
 * <P>
 * The difference between this class and WikiPageProvider is that there PageProviders handle
 * Unicode text, whereas we handle binary data. While there are quite a lot of similarities in how
 * we handle things, many providers can really use just one.  In addition, since binary files can
 * be really large, we rely on Input/OutputStreams.
 * </p>
 *
 * @author Erik Bunn
 * @author Janne Jalkanen
 */
public interface WikiAttachmentProvider
        extends WikiProvider
{
    /**
     * Put new attachment data.
     *
     * @param att DOCUMENT ME!
     * @param data DOCUMENT ME!
     *
     * @throws ProviderException DOCUMENT ME!
     * @throws IOException DOCUMENT ME!
     */
    void putAttachmentData(Attachment att, InputStream data)
            throws ProviderException, IOException;

    /**
     * Get attachment data.
     *
     * @param att DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws ProviderException DOCUMENT ME!
     * @throws IOException DOCUMENT ME!
     */
    InputStream getAttachmentData(Attachment att)
            throws ProviderException, IOException;

    /**
     * Lists all attachments attached to a page.
     *
     * @param page DOCUMENT ME!
     *
     * @return A collection of Attachment objects.  May be empty, but never null.
     *
     * @throws ProviderException DOCUMENT ME!
     */
    Collection listAttachments(WikiPage page)
            throws ProviderException;

    /**
     * Finds attachments based on the query.
     *
     * @param query DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    Collection findAttachments(QueryItem [] query);

    /**
     * Lists changed attachments since given date.  Can also be used to fetch a list of all pages.
     *
     * <P>
     * This is different from WikiPageProvider, where you basically get a list of all pages, then
     * sort them locally.  However, since some providers can be more efficient in locating
     * recently changed files (like any database) than our non-optimized Java code, it makes more
     * sense to fetch the whole list this way.
     * </p>
     *
     * <P>
     * To get all files, call this with Date(0L);
     * </p>
     *
     * @param timestamp List all files from this date onward.
     *
     * @return A List of Attachment objects, in most-recently-changed first order.
     *
     * @throws ProviderException DOCUMENT ME!
     */
    List listAllChanged(Date timestamp)
            throws ProviderException;

    /**
     * Returns info about an attachment.
     *
     * @param page DOCUMENT ME!
     * @param name DOCUMENT ME!
     * @param version DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws ProviderException DOCUMENT ME!
     */
    Attachment getAttachmentInfo(WikiPage page, String name, int version)
            throws ProviderException;

    /**
     * Returns version history.  Each element should be an Attachment.
     *
     * @param att DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    List getVersionHistory(Attachment att);

    /**
     * Removes a specific version from the repository.  The implementations should really do no
     * more security checks, since that is the domain of the AttachmentManager.  Just delete it as
     * efficiently as you can.
     *
     * @param att Attachment to be removed.  The version field is checked, and thus only that
     *        version is removed.
     *
     * @throws ProviderException If the attachment cannot be removed for some reason.
     *
     * @since 2.0.19.
     */
    void deleteVersion(Attachment att)
            throws ProviderException;

    /**
     * Removes an entire page from the repository.  The implementations should really do no more
     * security checks, since that is the domain of the AttachmentManager.  Just delete it as
     * efficiently as you can.  You should also delete any auxiliary files and directories that
     * belong to this attachment, IF they were created by this provider.
     *
     * @param att Attachment to delete.
     *
     * @throws ProviderException If the page could not be removed for some reason.
     *
     * @since 2.0.17.
     */
    void deleteAttachment(Attachment att)
            throws ProviderException;
}
