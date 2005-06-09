package de.softwareforge.eyewiki.providers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.WikiPage;


/**
 * Provides a simple directory based repository for Wiki pages.
 *
 * <P>
 * All files have ".txt" appended to make life easier for those who insist on using Windows or
 * other software which makes assumptions on the files contents based on its name.
 * </p>
 *
 * @author Janne Jalkanen
 */
public class FileSystemProvider
        extends AbstractFileProvider
{
    /** DOCUMENT ME! */
    private static final Logger log = Logger.getLogger(FileSystemProvider.class);

    /** All metadata is stored in a file with this extension. */
    public static final String EXTENSION_PROPS = ".properties";

    public FileSystemProvider(WikiEngine engine, Configuration conf)
            throws Exception
    {
        super(engine, conf);
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
        try
        {
            super.putPageText(page, text);
            putPageProperties(page);
        }
        catch (IOException e)
        {
            log.error("Saving failed", e);
        }
    }

    /**
     * Stores basic metadata to a file.
     *
     * @param page DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    private void putPageProperties(WikiPage page)
            throws IOException
    {
        Properties props = new Properties();
        OutputStream out = null;

        try
        {
            String author = page.getAuthor();

            if (author != null)
            {
                props.setProperty("author", author);

                File file =
                    new File(getPageDirectory(), mangleName(page.getName()) + EXTENSION_PROPS);

                out = new FileOutputStream(file);

                props.store(out, "eyeWiki page properties for page " + page.getName());
            }
        }
        finally
        {
            IOUtils.closeQuietly(out);
        }
    }

    /**
     * Gets basic metadata from file.
     *
     * @param page DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    private void getPageProperties(WikiPage page)
            throws IOException
    {
        Properties props = new Properties();
        InputStream in = null;

        try
        {
            File file = new File(getPageDirectory(), mangleName(page.getName()) + EXTENSION_PROPS);

            if ((file != null) && file.exists())
            {
                in = new FileInputStream(file);

                props.load(in);

                page.setAuthor(props.getProperty("author"));
            }
        }
        finally
        {
            IOUtils.closeQuietly(in);
        }
    }

    /**
     * DOCUMENT ME!
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
        WikiPage p = super.getPageInfo(page, version);

        if (p != null)
        {
            try
            {
                getPageProperties(p);
            }
            catch (IOException e)
            {
                log.error("Unable to read page properties", e);
                throw new ProviderException("Unable to read page properties, check logs.");
            }
        }

        return p;
    }
}
