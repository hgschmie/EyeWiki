package com.ecyrd.jspwiki.providers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.StringReader;

import java.util.Date;
import java.util.List;

import org.apache.commons.configuration.PropertiesConfiguration;

import com.ecyrd.jspwiki.FileUtil;
import com.ecyrd.jspwiki.TestEngine;
import com.ecyrd.jspwiki.WikiProperties;
import com.ecyrd.jspwiki.attachment.Attachment;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class BasicAttachmentProviderTest
        extends TestCase
{
    /** DOCUMENT ME! */
    public static final String NAME1 = "TestPage";

    /** DOCUMENT ME! */
    public static final String NAME2 = "TestPageToo";

    /** This is the sound of my head hitting the keyboard. */
    private static final String c_fileContents = "gy th tgyhgthygyth tgyfgftrfgvtgfgtr";

    /** DOCUMENT ME! */
    PropertiesConfiguration conf = new PropertiesConfiguration();

    /** DOCUMENT ME! */
    TestEngine m_engine;

    /** DOCUMENT ME! */
    BasicAttachmentProvider m_provider;

    /**
     * Creates a new BasicAttachmentProviderTest object.
     *
     * @param s DOCUMENT ME!
     */
    public BasicAttachmentProviderTest(String s)
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
        conf.load(TestEngine.findTestProperties());

        m_engine = new TestEngine(conf);

        m_provider = new BasicAttachmentProvider();
        m_provider.initialize(m_engine, conf);

        m_engine.saveText(NAME1, "Foobar");
        m_engine.saveText(NAME2, "Foobar2");
    }

    private File makeAttachmentFile()
            throws Exception
    {
        File tmpFile = File.createTempFile("test", "txt");
        tmpFile.deleteOnExit();

        FileWriter out = new FileWriter(tmpFile);

        FileUtil.copyContents(new StringReader(c_fileContents), out);

        out.close();

        return tmpFile;
    }

    private File makeExtraFile(File directory, String name)
            throws Exception
    {
        File tmpFile = new File(directory, name);
        FileWriter out = new FileWriter(tmpFile);

        FileUtil.copyContents(new StringReader(c_fileContents), out);

        out.close();

        return tmpFile;
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void tearDown()
            throws Exception
    {
        TestEngine.deleteTestPage(NAME1);
        TestEngine.deleteTestPage(NAME2);

        String tmpfiles = conf.getString(WikiProperties.PROP_STORAGEDIR);

        File f = new File(tmpfiles, NAME1 + BasicAttachmentProvider.DIR_EXTENSION);

        TestEngine.deleteAll(f);

        f = new File(tmpfiles, NAME2 + BasicAttachmentProvider.DIR_EXTENSION);

        TestEngine.deleteAll(f);

        TestEngine.emptyWorkDir();
    }

    /**
     * DOCUMENT ME!
     */
    public void testExtension()
    {
        String s = "test.png";

        assertEquals(BasicAttachmentProvider.getFileExtension(s), "png");
    }

    /**
     * DOCUMENT ME!
     */
    public void testExtension2()
    {
        String s = ".foo";

        assertEquals("foo", BasicAttachmentProvider.getFileExtension(s));
    }

    /**
     * DOCUMENT ME!
     */
    public void testExtension3()
    {
        String s = "test.png.3";

        assertEquals("3", BasicAttachmentProvider.getFileExtension(s));
    }

    /**
     * DOCUMENT ME!
     */
    public void testExtension4()
    {
        String s = "testpng";

        assertEquals("bin", BasicAttachmentProvider.getFileExtension(s));
    }

    /**
     * DOCUMENT ME!
     */
    public void testExtension5()
    {
        String s = "test.";

        assertEquals("bin", BasicAttachmentProvider.getFileExtension(s));
    }

    /**
     * DOCUMENT ME!
     */
    public void testExtension6()
    {
        String s = "test.a";

        assertEquals("a", BasicAttachmentProvider.getFileExtension(s));
    }

    /**
     * Can we save attachments with names in UTF-8 range?
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testPutAttachmentUTF8()
            throws Exception
    {
        File in = makeAttachmentFile();

        Attachment att = new Attachment(NAME1, "\u3072\u3048\u308b���test.f��");

        m_provider.putAttachmentData(att, new FileInputStream(in));

        List res = m_provider.listAllChanged(new Date(0L));

        Attachment a0 = (Attachment) res.get(0);

        assertEquals("name", att.getName(), a0.getName());
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testListAll()
            throws Exception
    {
        File in = makeAttachmentFile();

        Attachment att = new Attachment(NAME1, "test1.txt");

        m_provider.putAttachmentData(att, new FileInputStream(in));

        Thread.sleep(2000L); // So that we get a bit of granularity.

        Attachment att2 = new Attachment(NAME2, "test2.txt");

        m_provider.putAttachmentData(att2, new FileInputStream(in));

        List res = m_provider.listAllChanged(new Date(0L));

        assertEquals("list size", 2, res.size());

        Attachment a2 = (Attachment) res.get(0); // Most recently changed
        Attachment a1 = (Attachment) res.get(1); // Least recently changed

        assertEquals("a1 name", att.getName(), a1.getName());
        assertEquals("a2 name", att2.getName(), a2.getName());
    }

    /**
     * Check that the system does not fail if there are extra files in the directory.
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testListAllExtrafile()
            throws Exception
    {
        File in = makeAttachmentFile();

        File sDir =
            new File(m_engine.getWikiConfiguration().getString(WikiProperties.PROP_STORAGEDIR));
        File extrafile = makeExtraFile(sDir, "foobar.blob");

        try
        {
            Attachment att = new Attachment(NAME1, "test1.txt");

            m_provider.putAttachmentData(att, new FileInputStream(in));

            Thread.sleep(2000L); // So that we get a bit of granularity.

            Attachment att2 = new Attachment(NAME2, "test2.txt");

            m_provider.putAttachmentData(att2, new FileInputStream(in));

            List res = m_provider.listAllChanged(new Date(0L));

            assertEquals("list size", 2, res.size());

            Attachment a2 = (Attachment) res.get(0); // Most recently changed
            Attachment a1 = (Attachment) res.get(1); // Least recently changed

            assertEquals("a1 name", att.getName(), a1.getName());
            assertEquals("a2 name", att2.getName(), a2.getName());
        }
        finally
        {
            extrafile.delete();
        }
    }

    /**
     * Check that the system does not fail if there are extra files in the attachment directory.
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testListAllExtrafileInAttachmentDir()
            throws Exception
    {
        File in = makeAttachmentFile();

        File sDir =
            new File(m_engine.getWikiConfiguration().getString(WikiProperties.PROP_STORAGEDIR));
        File attDir = new File(sDir, NAME1 + "-att");

        Attachment att = new Attachment(NAME1, "test1.txt");

        m_provider.putAttachmentData(att, new FileInputStream(in));

        File extrafile = makeExtraFile(attDir, "ping.pong");

        try
        {
            Thread.sleep(2000L); // So that we get a bit of granularity.

            Attachment att2 = new Attachment(NAME2, "test2.txt");

            m_provider.putAttachmentData(att2, new FileInputStream(in));

            List res = m_provider.listAllChanged(new Date(0L));

            assertEquals("list size", 2, res.size());

            Attachment a2 = (Attachment) res.get(0); // Most recently changed
            Attachment a1 = (Attachment) res.get(1); // Least recently changed

            assertEquals("a1 name", att.getName(), a1.getName());
            assertEquals("a2 name", att2.getName(), a2.getName());
        }
        finally
        {
            extrafile.delete();
        }
    }

    /**
     * Check that the system does not fail if there are extra dirs in the attachment directory.
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testListAllExtradirInAttachmentDir()
            throws Exception
    {
        File in = makeAttachmentFile();

        File sDir =
            new File(m_engine.getWikiConfiguration().getString(WikiProperties.PROP_STORAGEDIR));
        File attDir = new File(sDir, NAME1 + "-att");

        Attachment att = new Attachment(NAME1, "test1.txt");

        m_provider.putAttachmentData(att, new FileInputStream(in));

        // This is our extraneous directory. 
        File extrafile = new File(attDir, "ping.pong");
        extrafile.mkdir();

        try
        {
            Thread.sleep(2000L); // So that we get a bit of granularity.

            Attachment att2 = new Attachment(NAME2, "test2.txt");

            m_provider.putAttachmentData(att2, new FileInputStream(in));

            List res = m_provider.listAllChanged(new Date(0L));

            assertEquals("list size", 2, res.size());

            Attachment a2 = (Attachment) res.get(0); // Most recently changed
            Attachment a1 = (Attachment) res.get(1); // Least recently changed

            assertEquals("a1 name", att.getName(), a1.getName());
            assertEquals("a2 name", att2.getName(), a2.getName());
        }
        finally
        {
            extrafile.delete();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testListAllNoExtension()
            throws Exception
    {
        File in = makeAttachmentFile();

        Attachment att = new Attachment(NAME1, "test1.");

        m_provider.putAttachmentData(att, new FileInputStream(in));

        Thread.sleep(2000L); // So that we get a bit of granularity.

        Attachment att2 = new Attachment(NAME2, "test2.");

        m_provider.putAttachmentData(att2, new FileInputStream(in));

        List res = m_provider.listAllChanged(new Date(0L));

        assertEquals("list size", 2, res.size());

        Attachment a2 = (Attachment) res.get(0); // Most recently changed
        Attachment a1 = (Attachment) res.get(1); // Least recently changed

        assertEquals("a1 name", att.getName(), a1.getName());
        assertEquals("a2 name", att2.getName(), a2.getName());
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Test suite()
    {
        return new TestSuite(BasicAttachmentProviderTest.class);
    }
}
