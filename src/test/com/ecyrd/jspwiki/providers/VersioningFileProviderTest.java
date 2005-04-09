package com.ecyrd.jspwiki.providers;

import java.io.File;
import java.io.FileWriter;
import java.io.StringReader;
import java.io.Writer;
import java.util.Collection;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.configuration.Configuration;

import com.ecyrd.jspwiki.TestEngine;
import com.ecyrd.jspwiki.WikiPage;
import com.ecyrd.jspwiki.WikiProperties;
import com.ecyrd.jspwiki.manager.PageManager;
import com.ecyrd.jspwiki.util.FileUtil;


// FIXME: Should this thingy go directly to the VersioningFileProvider,
//        or should it rely on the WikiEngine API?
public class VersioningFileProviderTest
        extends TestCase
{
    /** DOCUMENT ME! */
    public static final String NAME1 = "Test1";

    /** DOCUMENT ME! */
    Configuration conf = null;

    /** DOCUMENT ME! */
    TestEngine engine;

    /**
     * Creates a new VersioningFileProviderTest object.
     *
     * @param s DOCUMENT ME!
     */
    public VersioningFileProviderTest(String s)
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
        conf = TestEngine.getConfiguration("/jspwiki_vers.properties");

        engine = new TestEngine(conf);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void tearDown()
            throws Exception
    {
        engine.cleanup();
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

        // +2 comes from \r\n.
        assertEquals("wrong text", maxver + 2, engine.getText(NAME1).length());
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testCheckin()
            throws Exception
    {
        String text = "diddo\r\n";

        engine.saveText(NAME1, text);

        String res = engine.getText(NAME1);

        assertEquals(text, res);
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
    public void testPageInfo()
            throws Exception
    {
        String text = "diddo\r\n";

        engine.saveText(NAME1, text);

        WikiPage res = engine.getPage(NAME1);

        assertEquals(1, res.getVersion());
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testGetOldVersion()
            throws Exception
    {
        String text = "diddo\r\n";
        String text2 = "barbar\r\n";
        String text3 = "Barney\r\n";

        engine.saveText(NAME1, text);
        engine.saveText(NAME1, text2);
        engine.saveText(NAME1, text3);

        WikiPage res = engine.getPage(NAME1);

        assertEquals("wrong version", 3, res.getVersion());

        assertEquals("ver1", text, engine.getText(NAME1, 1));
        assertEquals("ver2", text2, engine.getText(NAME1, 2));
        assertEquals("ver3", text3, engine.getText(NAME1, 3));
    }

    /**
     * 2.0.7 and before got this wrong.
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testGetOldVersionUTF8()
            throws Exception
    {
        String text = "���\r\n";
        String text2 = "barbar��\r\n";
        String text3 = "Barney��\r\n";

        engine.saveText(NAME1, text);
        engine.saveText(NAME1, text2);
        engine.saveText(NAME1, text3);

        WikiPage res = engine.getPage(NAME1);

        assertEquals("wrong version", 3, res.getVersion());

        assertEquals("ver1", text, engine.getText(NAME1, 1));
        assertEquals("ver2", text2, engine.getText(NAME1, 2));
        assertEquals("ver3", text3, engine.getText(NAME1, 3));
    }

    /**
     * DOCUMENT ME!
     */
    public void testNonexistantPage()
    {
        assertNull(engine.getPage("fjewifjeiw"));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testVersionHistory()
            throws Exception
    {
        String text = "diddo\r\n";
        String text2 = "barbar\r\n";
        String text3 = "Barney\r\n";

        engine.saveText(NAME1, text);
        engine.saveText(NAME1, text2);
        engine.saveText(NAME1, text3);

        Collection history = engine.getVersionHistory(NAME1);

        assertEquals("size", 3, history.size());
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

        List l = provider.getVersionHistory(NAME1);
        assertEquals("wrong # of versions", 3, l.size());

        provider.deleteVersion(NAME1, 2);

        l = provider.getVersionHistory(NAME1);

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
        return new TestSuite(VersioningFileProviderTest.class);
    }
}
