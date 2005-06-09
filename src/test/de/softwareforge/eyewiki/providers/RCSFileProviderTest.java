package de.softwareforge.eyewiki.providers;

import java.io.File;
import java.io.FileWriter;
import java.io.StringReader;
import java.io.Writer;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.configuration.Configuration;


import de.softwareforge.eyewiki.TestEngine;
import de.softwareforge.eyewiki.WikiPage;
import de.softwareforge.eyewiki.WikiProperties;
import de.softwareforge.eyewiki.WikiProvider;
import de.softwareforge.eyewiki.manager.PageManager;
import de.softwareforge.eyewiki.providers.FileSystemProvider;
import de.softwareforge.eyewiki.providers.NoSuchVersionException;
import de.softwareforge.eyewiki.providers.WikiPageProvider;
import de.softwareforge.eyewiki.util.FileUtil;


/**
 * Tests the RCSFileProvider.  If you are getting strange errors, please check that you actually
 * <i>have</i> RCS installed and in your path...
 *
 * @author jalkanen
 *
 * @since forever
 */
public class RCSFileProviderTest
        extends TestCase
{
    /** DOCUMENT ME! */
    public static final String NAME1 = "Test1";

    /** DOCUMENT ME! */
    Configuration conf = null;

    /** DOCUMENT ME! */
    TestEngine engine;

    /**
     * Creates a new RCSFileProviderTest object.
     *
     * @param s DOCUMENT ME!
     */
    public RCSFileProviderTest(String s)
    {
        super(s);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void setUp()
            throws Exception
    {
        conf = TestEngine.getConfiguration("/eyewiki_rcs.properties");
        engine = new TestEngine(conf);
    }

    /**
     * Remove NAME1 + all RCS directories for it.
     *
     * @throws Exception DOCUMENT ME!
     */
    public void tearDown()
            throws Exception
    {
        engine.cleanup();
    }

    /**
     * Bug report by Anon: RCS under Windows 2k:
     * <PRE>
     * In getPageInfo of RCSFileProvider:
     *  Problem:
     *  With a longer rlog result, the break clause in the last "else if"
     * breaks out of the reading loop before all the lines in the full
     * rlog have been read in. This causes the process.wait() to hang.
     *  Suggested quick fix:
     *  Always read all the contents of the rlog, even if it is slower.
     * </PRE>
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testMillionChanges()
            throws Exception
    {
        String text = "";
        String name = NAME1;
        int maxver = 100; // Save 100 versions.

        for (int i = 0; i < maxver; i++)
        {
            text = text + ".";
            engine.saveText(name, text);
        }

        WikiPage pageinfo = engine.getPage(NAME1);

        assertEquals("wrong version", maxver, pageinfo.getVersion());

        // +2 comes from \r\n at the end of each file.
        assertEquals("wrong text", maxver + 2, engine.getText(NAME1).length());
    }

    /**
     * Checks if migration from FileSystemProvider to VersioningFileProvider works by creating a
     * dummy file without corresponding content in OLD/
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testMigration()
            throws Exception
    {
        String files = conf.getString(WikiProperties.PROP_PAGEDIR);

        File f = new File(files, NAME1 + FileSystemProvider.FILE_EXT);

        Writer out = new FileWriter(f);
        FileUtil.copyContents(new StringReader("foobar"), out);
        out.close();

        String res = engine.getText(NAME1);

        assertEquals("latest did not work", "foobar", res);

        res = engine.getText(NAME1, 1); // Should be the first version.

        assertEquals("fetch by direct version did not work", "foobar", res);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testGetByVersion()
            throws Exception
    {
        String text = "diddo\r\n";

        engine.saveText(NAME1, text);

        WikiPage page = engine.getPage(NAME1, 1);

        assertEquals("name", NAME1, page.getName());
        assertEquals("version", 1, page.getVersion());
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testGetByLatestVersion()
            throws Exception
    {
        String text = "diddo\r\n";

        engine.saveText(NAME1, text);

        WikiPage page = engine.getPage(NAME1, WikiProvider.LATEST_VERSION);

        assertEquals("name", NAME1, page.getName());
        assertEquals("version", 1, page.getVersion());
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testDelete()
            throws Exception
    {
        engine.saveText(NAME1, "v1");
        engine.saveText(NAME1, "v2");
        engine.saveText(NAME1, "v3");

        PageManager mgr = engine.getPageManager();
        WikiPageProvider provider = mgr.getProvider();

        provider.deletePage(NAME1);

        String files = conf.getString(WikiProperties.PROP_PAGEDIR);

        File f = new File(files, NAME1 + FileSystemProvider.FILE_EXT);

        assertFalse("file exists", f.exists());

        f = new File(files + File.separator + "RCS", NAME1 + FileSystemProvider.FILE_EXT + ",v");

        assertFalse("RCS file exists", f.exists());
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testDeleteVersion()
            throws Exception
    {
        engine.saveText(NAME1, "v1\r\n");
        engine.saveText(NAME1, "v2\r\n");
        engine.saveText(NAME1, "v3\r\n");

        PageManager mgr = engine.getPageManager();
        WikiPageProvider provider = mgr.getProvider();

        provider.deleteVersion(NAME1, 2);

        List l = provider.getVersionHistory(NAME1);

        assertEquals("wrong # of versions", 2, l.size());

        assertEquals("v1", "v1\r\n", provider.getPageText(NAME1, 1));
        assertEquals("v3", "v3\r\n", provider.getPageText(NAME1, 3));

        try
        {
            provider.getPageText(NAME1, 2);
            fail("v2");
        }
        catch (NoSuchVersionException e)
        {
            // This is expected
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Test suite()
    {
        return new TestSuite(RCSFileProviderTest.class);
    }
}
