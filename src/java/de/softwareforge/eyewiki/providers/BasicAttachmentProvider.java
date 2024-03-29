package de.softwareforge.eyewiki.providers;

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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import de.softwareforge.eyewiki.PageTimeComparator;
import de.softwareforge.eyewiki.QueryItem;
import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.WikiPage;
import de.softwareforge.eyewiki.WikiProperties;
import de.softwareforge.eyewiki.WikiProvider;
import de.softwareforge.eyewiki.attachment.Attachment;
import de.softwareforge.eyewiki.exception.NoRequiredPropertyException;
import de.softwareforge.eyewiki.util.FileUtil;
import de.softwareforge.eyewiki.util.TextUtil;

/**
 * Provides basic, versioning attachments.
 * <PRE>
 *   Structure is as follows:
 *      attachment_dir/
 *         ThisPage/
 *            attachment.doc/
 *               attachment.properties
 *               1.doc
 *               2.doc
 *               3.doc
 *            picture.png/
 *               attachment.properties
 *               1.png
 *               2.png
 *         ThatPage/
 *            picture.png/
 *               attachment.properties
 *               1.png
 *   </PRE>
 * The names of the directories will be URLencoded.
 *
 * <p>
 * "attachment.properties" consists of the following items:
 *
 * <UL>
 * <li>
 * 1.author = author name for version 1 (etc)
 * </li>
 * </ul>
 * </p>
 */
public class BasicAttachmentProvider
        implements WikiAttachmentProvider
{
    /** DOCUMENT ME! */
    public static final String PROPERTY_FILE = "attachment.properties";

    /** DOCUMENT ME! */
    public static final String DIR_EXTENSION = "-att";

    /** DOCUMENT ME! */
    public static final String ATTDIR_EXTENSION = "-dir";

    /** DOCUMENT ME! */
    static final Logger log = Logger.getLogger(BasicAttachmentProvider.class);

    /** DOCUMENT ME! */
    private String m_storageDir;

    /**
     * DOCUMENT ME!
     *
     * @param engine DOCUMENT ME!
     * @param conf DOCUMENT ME!
     *
     * @throws NoRequiredPropertyException DOCUMENT ME!
     * @throws IOException DOCUMENT ME!
     */
    public BasicAttachmentProvider(WikiEngine engine, Configuration conf)
            throws NoRequiredPropertyException, IOException
    {
        m_storageDir = engine.getStorageDir();

        if (m_storageDir == null)
        {
            throw new NoRequiredPropertyException("File based attachment providers need a "
                + "storage directory but none was found. Aborting!", WikiProperties.PROP_STORAGEDIR);
        }
    }

    /**
     * Finds storage dir, and if it exists, makes sure that it is valid.
     *
     * @param wikipage Page to which this attachment is attached.
     *
     * @return DOCUMENT ME!
     *
     * @throws ProviderException DOCUMENT ME!
     */
    private File findPageDir(String wikipage)
            throws ProviderException
    {
        wikipage = mangleName(wikipage);

        File f = new File(m_storageDir, wikipage + DIR_EXTENSION);

        if (f.exists() && !f.isDirectory())
        {
            throw new ProviderException("Storage dir '" + f.getAbsolutePath() + "' is not a directory!");
        }

        return f;
    }

    private static String mangleName(String wikiname)
    {
        String res = TextUtil.urlEncodeUTF8(wikiname);

        return res;
    }

    private static String unmangleName(String filename)
    {
        return TextUtil.urlDecodeUTF8(filename);
    }

    /**
     * Finds the dir in which the attachment lives.
     *
     * @param att DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws ProviderException DOCUMENT ME!
     */
    private File findAttachmentDir(Attachment att)
            throws ProviderException
    {
        File f = new File(findPageDir(att.getParentName()), mangleName(att.getFileName() + ATTDIR_EXTENSION));

        //
        //  Migration code for earlier versions of eyeWiki.
        //  Originally, we used plain filename.  Then we realized we need
        //  to urlencode it.  Then we realized that we have to use a
        //  postfix to make sure illegal file names are never formed.
        //
        if (!f.exists())
        {
            File oldf = new File(findPageDir(att.getParentName()), mangleName(att.getFileName()));

            if (oldf.exists())
            {
                f = oldf;
            }
            else
            {
                oldf = new File(findPageDir(att.getParentName()), att.getFileName());

                if (oldf.exists())
                {
                    f = oldf;
                }
            }
        }

        return f;
    }

    /**
     * Goes through the repository and decides which version is the newest one in that directory.
     *
     * @param att DOCUMENT ME!
     *
     * @return Latest version number in the repository, or 0, if there is no page in the repository.
     *
     * @throws ProviderException DOCUMENT ME!
     */
    private int findLatestVersion(Attachment att)
            throws ProviderException
    {
        // File pageDir = findPageDir( att.getName() );
        File attDir = findAttachmentDir(att);

        // log.debug("Finding pages in "+attDir.getAbsolutePath());
        String [] pages = attDir.list(new AttachmentVersionFilter());

        if (pages == null)
        {
            return 0; // No such thing found.
        }

        int version = 0;

        for (int i = 0; i < pages.length; i++)
        {
            // log.debug("Checking: "+pages[i]);
            int cutpoint = pages[i].indexOf('.');
            String pageNum = (cutpoint > 0) ? pages[i].substring(0, cutpoint) : pages[i];

            try
            {
                int res = Integer.parseInt(pageNum);

                if (res > version)
                {
                    version = res;
                }
            }
            catch (NumberFormatException e)
            {
                log.debug("Skipped NumberFormatException", e);
            }
        }

        return version;
    }

    /**
     * Returns the file extension.  For example "test.png" returns "png".
     *
     * <p>
     * If file has no extension, will return "bin"
     * </p>
     *
     * @param filename DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected static String getFileExtension(String filename)
    {
        String fileExt = "bin";

        int dot = filename.lastIndexOf('.');

        if ((dot >= 0) && (dot < (filename.length() - 1)))
        {
            fileExt = mangleName(filename.substring(dot + 1));
        }

        return fileExt;
    }

    /**
     * Writes the page properties back to the file system. Note that it WILL overwrite any previous properties.
     *
     * @param att DOCUMENT ME!
     * @param properties DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     * @throws ProviderException DOCUMENT ME!
     */
    private void putPageProperties(Attachment att, Properties properties)
            throws IOException, ProviderException
    {
        File attDir = findAttachmentDir(att);
        File propertyFile = new File(attDir, PROPERTY_FILE);

        OutputStream out = new FileOutputStream(propertyFile);

        properties.store(out, " eyeWiki page properties for " + att.getName() + ". DO NOT MODIFY!");

        out.close();
    }

    /**
     * Reads page properties from the file system.
     *
     * @param att DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     * @throws ProviderException DOCUMENT ME!
     */
    private Properties getPageProperties(Attachment att)
            throws IOException, ProviderException
    {
        Properties props = new Properties();

        File propertyFile = new File(findAttachmentDir(att), PROPERTY_FILE);

        if ((propertyFile != null) && propertyFile.exists())
        {
            InputStream in = new FileInputStream(propertyFile);

            props.load(in);

            in.close();
        }

        return props;
    }

    /**
     * DOCUMENT ME!
     *
     * @param att DOCUMENT ME!
     * @param data DOCUMENT ME!
     *
     * @throws ProviderException DOCUMENT ME!
     * @throws IOException DOCUMENT ME!
     */
    public void putAttachmentData(Attachment att, InputStream data)
            throws ProviderException, IOException
    {
        File attDir = findAttachmentDir(att);

        if (!attDir.exists())
        {
            attDir.mkdirs();
        }

        int latestVersion = findLatestVersion(att);

        try
        {
            int versionNumber = latestVersion + 1;

            File newfile = new File(attDir, versionNumber + "." + getFileExtension(att.getFileName()));

            if (log.isInfoEnabled())
            {
                log.info("Uploading attachment " + att.getFileName() + " to page " + att.getParentName());
                log.info("Saving attachment contents to " + newfile.getAbsolutePath());
            }

            OutputStream out = null;

            try
            {
                out = new FileOutputStream(newfile);
                FileUtil.copyContents(data, out);
            }
            finally
            {
                IOUtils.closeQuietly(out);
            }

            Properties props = getPageProperties(att);

            String author = att.getAuthor();

            if (author == null)
            {
                author = "unknown";
            }

            props.setProperty(versionNumber + ".author", author);
            putPageProperties(att, props);
        }
        catch (IOException e)
        {
            log.error("Could not save attachment data: ", e);
            throw (IOException) e.fillInStackTrace();
        }
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

    private File findFile(File dir, Attachment att)
            throws FileNotFoundException, ProviderException
    {
        int version = att.getVersion();

        if (version == WikiProvider.LATEST_VERSION)
        {
            version = findLatestVersion(att);
        }

        String ext = getFileExtension(att.getFileName());
        File f = new File(dir, version + "." + ext);

        if (!f.exists())
        {
            if ("bin".equals(ext))
            {
                File fOld = new File(dir, version + ".");

                if (fOld.exists())
                {
                    f = fOld;
                }
            }

            if (!f.exists())
            {
                throw new FileNotFoundException("No such file: " + f.getAbsolutePath() + " exists.");
            }
        }

        return f;
    }

    /**
     * DOCUMENT ME!
     *
     * @param att DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     * @throws ProviderException DOCUMENT ME!
     */
    public InputStream getAttachmentData(Attachment att)
            throws IOException, ProviderException
    {
        File attDir = findAttachmentDir(att);

        File f = findFile(attDir, att);

        return new FileInputStream(f);
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
    public Collection listAttachments(WikiPage page)
            throws ProviderException
    {
        Collection result = new ArrayList();

        File dir = findPageDir(page.getName());

        if (dir != null)
        {
            String [] attachments = dir.list();

            if (attachments != null)
            {
                //
                //  We now have a list of all potential attachments in
                //  the directory.
                //
                for (int i = 0; i < attachments.length; i++)
                {
                    File f = new File(dir, attachments[i]);

                    if (f.isDirectory())
                    {
                        String attachmentName = unmangleName(attachments[i]);

                        //
                        //  Is it a new-stylea attachment directory?  If yes,
                        //  we'll just deduce the name.  If not, however,
                        //  we'll check if there's a suitable property file
                        //  in the directory.
                        //
                        if (attachmentName.endsWith(ATTDIR_EXTENSION))
                        {
                            attachmentName = attachmentName.substring(0, attachmentName.length() - ATTDIR_EXTENSION.length());
                        }
                        else
                        {
                            File propFile = new File(f, PROPERTY_FILE);

                            if (!propFile.exists())
                            {
                                //
                                //  This is not obviously a eyeWiki attachment,
                                //  so let's just skip it.
                                //
                                continue;
                            }
                        }

                        Attachment att = getAttachmentInfo(page, attachmentName, WikiProvider.LATEST_VERSION);

                        //
                        //  Sanity check - shouldn't really be happening, unless
                        //  you mess with the repository directly.
                        //
                        if (att == null)
                        {
                            throw new ProviderException("Attachment disappeared while reading information:"
                                + " if you did not touch the repository, there is a serious bug somewhere. " + "Attachment = "
                                + attachments[i] + ", decoded = " + attachmentName);
                        }

                        result.add(att);
                    }
                }
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
    public Collection findAttachments(QueryItem [] query)
    {
        return null;
    }

    // FIXME: Very unoptimized.
    public List listAllChanged(Date timestamp)
            throws ProviderException
    {
        File attDir = new File(m_storageDir);

        if (!attDir.exists())
        {
            throw new ProviderException("Specified attachment directory " + m_storageDir + " does not exist!");
        }

        ArrayList list = new ArrayList();

        String [] pagesWithAttachments = attDir.list(new AttachmentFilter());

        for (int i = 0; i < pagesWithAttachments.length; i++)
        {
            String pageId = unmangleName(pagesWithAttachments[i]);
            pageId = pageId.substring(0, pageId.length() - DIR_EXTENSION.length());

            Collection c = listAttachments(new WikiPage(pageId));

            for (Iterator it = c.iterator(); it.hasNext();)
            {
                Attachment att = (Attachment) it.next();

                if (att.getLastModified().after(timestamp))
                {
                    list.add(att);
                }
            }
        }

        Collections.sort(list, new PageTimeComparator());

        return list;
    }

    /**
     * DOCUMENT ME!
     *
     * @param page DOCUMENT ME!
     * @param name DOCUMENT ME!
     * @param version DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws ProviderException DOCUMENT ME!
     */
    public Attachment getAttachmentInfo(WikiPage page, String name, int version)
            throws ProviderException
    {
        Attachment att = new Attachment(page.getName(), name);
        File dir = findAttachmentDir(att);

        if (!dir.exists())
        {
            // log.debug("Attachment dir not found - thus no attachment can exist.");
            return null;
        }

        if (version == WikiProvider.LATEST_VERSION)
        {
            version = findLatestVersion(att);
        }

        att.setVersion(version);

        try
        {
            Properties props = getPageProperties(att);

            att.setAuthor(props.getProperty(version + ".author"));

            File f = findFile(dir, att);

            att.setSize(f.length());
            att.setLastModified(new Date(f.lastModified()));
        }
        catch (IOException e)
        {
            log.error("Can't read page properties", e);
            throw new ProviderException("Cannot read page properties: " + e.getMessage());
        }

        // FIXME: Check for existence of this particular version.
        return att;
    }

    /**
     * DOCUMENT ME!
     *
     * @param att DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public List getVersionHistory(Attachment att)
    {
        ArrayList list = new ArrayList();

        try
        {
            int latest = findLatestVersion(att);

            for (int i = latest; i >= 1; i--)
            {
                Attachment a = getAttachmentInfo(new WikiPage(att.getParentName()), att.getFileName(), i);

                if (a != null)
                {
                    list.add(a);
                }
            }
        }
        catch (ProviderException e)
        {
            log.error("Getting version history failed for page: " + att, e);

            // FIXME: SHould this fail?
        }

        return list;
    }

    /**
     * DOCUMENT ME!
     *
     * @param att DOCUMENT ME!
     *
     * @throws ProviderException DOCUMENT ME!
     */
    public void deleteVersion(Attachment att)
            throws ProviderException
    {
        // FIXME: Does nothing yet.
    }

    /**
     * DOCUMENT ME!
     *
     * @param att DOCUMENT ME!
     *
     * @throws ProviderException DOCUMENT ME!
     */
    public void deleteAttachment(Attachment att)
            throws ProviderException
    {
        File dir = findAttachmentDir(att);
        String [] files = dir.list();

        for (int i = 0; i < files.length; i++)
        {
            File file = new File(dir.getAbsolutePath() + "/" + files[i]);
            file.delete();
        }

        dir.delete();
    }

    /**
     * Returns only those directories that contain attachments.
     */
    public static class AttachmentFilter
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
            return name.endsWith(DIR_EXTENSION);
        }
    }

    /**
     * Accepts only files that are actual versions, no control files.
     */
    public static class AttachmentVersionFilter
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
            return !name.equals(PROPERTY_FILE);
        }
    }
}
