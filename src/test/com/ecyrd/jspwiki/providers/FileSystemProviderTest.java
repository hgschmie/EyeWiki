package com.ecyrd.jspwiki.providers;

import java.io.File;
import java.io.FileInputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.PropertyConfigurator;

import com.ecyrd.jspwiki.TestEngine;
import com.ecyrd.jspwiki.WikiEngine;
import com.ecyrd.jspwiki.WikiException;
import com.ecyrd.jspwiki.WikiPage;
import com.ecyrd.jspwiki.WikiProperties;
import com.ecyrd.jspwiki.manager.PageManager;
import com.ecyrd.jspwiki.util.FileUtil;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class FileSystemProviderTest
        extends TestCase
{
    /** DOCUMENT ME! */
    FileSystemProvider m_provider;

    /** DOCUMENT ME! */
    FileSystemProvider m_providerUTF8;

    /** DOCUMENT ME! */
    Configuration conf = null;

    /** DOCUMENT ME! */
    TestEngine m_engine;

    /**
     * Creates a new FileSystemProviderTest object.
     *
     * @param s DOCUMENT ME!
     */
    public FileSystemProviderTest(String s)
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
        conf = TestEngine.getConfiguration();
        PropertyConfigurator.configure(ConfigurationConverter.getProperties(conf));
        conf.setProperty(PageManager.PROP_CLASS_PAGEPROVIDER, "FileSystemProvider");

        m_engine = new TestEngine(conf);

        m_provider = new FileSystemProvider();

        m_provider.initialize(m_engine, conf);

        conf.setProperty(WikiEngine.PROP_ENCODING, "UTF-8");
        m_providerUTF8 = new FileSystemProvider();
        m_providerUTF8.initialize(m_engine, conf);
    }

    /**
     * DOCUMENT ME!
     */
    public void tearDown()
    {
        m_engine.cleanup();
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testScandinavianLetters()
            throws Exception
    {
        WikiPage page = new WikiPage("\u00c5\u00e4Test");

        m_provider.putPageText(page, "test");

        File resultfile = new File(m_engine.getPageDir(), "%C5%E4Test.txt");

        assertTrue("No such file", resultfile.exists());

        String contents = FileUtil.readContents(new FileInputStream(resultfile), "ISO-8859-1");

        assertEquals("Wrong contents", contents, "test");
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testScandinavianLettersUTF8()
            throws Exception
    {
        WikiPage page = new WikiPage("\u00c5\u00e4Test");

        m_providerUTF8.putPageText(page, "test\u00d6");

        File resultfile = new File(m_engine.getPageDir(), "%C3%85%C3%A4Test.txt");

        assertTrue("No such file", resultfile.exists());

        String contents = FileUtil.readContents(new FileInputStream(resultfile), "UTF-8");

        assertEquals("Wrong contents", contents, "test\u00d6");
    }

    /**
     * This should never happen, but let's check that we're protected anyway.
     *
     * @throws Exception
     */
    public void testSlashesInPageNamesUTF8()
            throws Exception
    {
        WikiPage page = new WikiPage("Test/Foobar");

        m_providerUTF8.putPageText(page, "test");

        File resultfile = new File(m_engine.getPageDir(), "Test%2FFoobar.txt");

        assertTrue("No such file", resultfile.exists());

        String contents = FileUtil.readContents(new FileInputStream(resultfile), "UTF-8");

        assertEquals("Wrong contents", contents, "test");
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testSlashesInPageNames()
            throws Exception
    {
        WikiPage page = new WikiPage("Test/Foobar");

        m_provider.putPageText(page, "test");

        File resultfile = new File(m_engine.getPageDir(), "Test%2FFoobar.txt");

        assertTrue("No such file", resultfile.exists());

        String contents = FileUtil.readContents(new FileInputStream(resultfile), "ISO-8859-1");

        assertEquals("Wrong contents", contents, "test");
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testAuthor()
            throws Exception
    {
        WikiPage page = new WikiPage("\u00c5\u00e4Test");
        page.setAuthor("Min\u00e4");

        m_provider.putPageText(page, "test");

        WikiPage page2 = m_provider.getPageInfo("\u00c5\u00e4Test", 1);

        assertEquals("Min\u00e4", page2.getAuthor());
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testNonExistantDirectory()
            throws Exception
    {
        String tmpdir = m_engine.getPageDir();
        String dirname = "non-existant-directory";

        File newDir = new File(tmpdir, dirname);
        newDir.delete();

        PropertiesConfiguration conf2 = new PropertiesConfiguration();

        conf2.setProperty(WikiProperties.PROP_PAGEDIR, newDir.getAbsolutePath());
        conf2.setThrowExceptionOnMissing(true);

        FileSystemProvider test = new FileSystemProvider();

        TestEngine m_engine2 = new TestEngine(conf2);

        test.initialize(m_engine2, conf2);

        assertTrue("didn't create it", newDir.exists());
        assertTrue("isn't a dir", newDir.isDirectory());
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testDirectoryIsFile()
            throws Exception
    {
        File tmpFile = null;

        tmpFile = FileUtil.newTmpFile("foobar"); // Content does not matter.

        PropertiesConfiguration conf = new PropertiesConfiguration();

        conf.setProperty(WikiProperties.PROP_PAGEDIR, tmpFile.getAbsolutePath());

        FileSystemProvider test = new FileSystemProvider();

        try
        {
            TestEngine m_engine2 = new TestEngine(conf);
            test.initialize(m_engine2, conf);
            
            fail("Wiki did not warn about wrong property.");
        }
        catch (Exception e)
        {
            assertEquals("Wrong Exception thrown", WikiException.class, e.getClass());
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testDelete()
            throws Exception
    {
        m_provider.putPageText(new WikiPage("Test"), "v1");

        m_provider.deletePage("Test");

        String files = conf.getString(WikiProperties.PROP_PAGEDIR);

        File f = new File(files, "Test" + FileSystemProvider.FILE_EXT);

        assertFalse("file exists", f.exists());
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Test suite()
    {
        return new TestSuite(FileSystemProviderTest.class);
    }
}
