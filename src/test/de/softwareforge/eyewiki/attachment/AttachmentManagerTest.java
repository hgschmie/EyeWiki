package de.softwareforge.eyewiki.attachment;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.configuration.Configuration;


import de.softwareforge.eyewiki.TestEngine;
import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiPage;
import de.softwareforge.eyewiki.attachment.Attachment;
import de.softwareforge.eyewiki.attachment.AttachmentManager;
import de.softwareforge.eyewiki.util.FileUtil;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class AttachmentManagerTest
        extends TestCase
{
    /** DOCUMENT ME! */
    public static final String NAME1 = "TestPage";

    /** DOCUMENT ME! */
    public static final String NAMEU = "TestPage\u00e6";

    /** DOCUMENT ME! */
    static String c_fileContents = "ABCDEFGHIJKLMNOPQRSTUVWxyz";

    /** DOCUMENT ME! */
    Configuration conf = null;

    /** DOCUMENT ME! */
    TestEngine m_engine;

    /** DOCUMENT ME! */
    AttachmentManager m_manager;

    /**
     * Creates a new AttachmentManagerTest object.
     *
     * @param s DOCUMENT ME!
     */
    public AttachmentManagerTest(String s)
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

        m_engine = new TestEngine(conf);
        m_manager = m_engine.getAttachmentManager();

        m_engine.saveText(NAME1, "Foobar");
        m_engine.saveText(NAMEU, "Foobar");
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

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void tearDown()
            throws Exception
    {
        m_engine.cleanup();
    }

    /**
     * DOCUMENT ME!
     */
    public void testEnabled()
    {
        assertTrue("not enabled", m_manager.attachmentsEnabled());
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testSimpleStore()
            throws Exception
    {
        Attachment att = new Attachment(NAME1, "test1.txt");

        att.setAuthor("FirstPost");

        m_manager.storeAttachment(att, makeAttachmentFile());

        Attachment att2 =
            m_manager.getAttachmentInfo(
                new WikiContext(m_engine, new WikiPage(NAME1)), "test1.txt");

        assertNotNull("attachment disappeared", att2);
        assertEquals("name", att.getName(), att2.getName());
        assertEquals("author", att.getAuthor(), att2.getAuthor());
        assertEquals("size", c_fileContents.length(), att2.getSize());

        InputStream in = m_manager.getAttachmentStream(att2);

        assertNotNull("stream", in);

        StringWriter sout = new StringWriter();
        FileUtil.copyContents(new InputStreamReader(in), sout);

        in.close();
        sout.close();

        assertEquals("contents", c_fileContents, sout.toString());
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testSimpleStoreByVersion()
            throws Exception
    {
        Attachment att = new Attachment(NAME1, "test1.txt");

        att.setAuthor("FirstPost");

        m_manager.storeAttachment(att, makeAttachmentFile());

        Attachment att2 =
            m_manager.getAttachmentInfo(
                new WikiContext(m_engine, new WikiPage(NAME1)), "test1.txt", 1);

        assertNotNull("attachment disappeared", att2);
        assertEquals("version", 1, att2.getVersion());
        assertEquals("name", att.getName(), att2.getName());
        assertEquals("author", att.getAuthor(), att2.getAuthor());
        assertEquals("size", c_fileContents.length(), att2.getSize());

        InputStream in = m_manager.getAttachmentStream(att2);

        assertNotNull("stream", in);

        StringWriter sout = new StringWriter();
        FileUtil.copyContents(new InputStreamReader(in), sout);

        in.close();
        sout.close();

        assertEquals("contents", c_fileContents, sout.toString());
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testMultipleStore()
            throws Exception
    {
        Attachment att = new Attachment(NAME1, "test1.txt");

        att.setAuthor("FirstPost");

        m_manager.storeAttachment(att, makeAttachmentFile());

        att.setAuthor("FooBar");
        m_manager.storeAttachment(att, makeAttachmentFile());

        Attachment att2 =
            m_manager.getAttachmentInfo(
                new WikiContext(m_engine, new WikiPage(NAME1)), "test1.txt");

        assertNotNull("attachment disappeared", att2);
        assertEquals("name", att.getName(), att2.getName());
        assertEquals("author", att.getAuthor(), att2.getAuthor());
        assertEquals("version", 2, att2.getVersion());

        InputStream in = m_manager.getAttachmentStream(att2);

        assertNotNull("stream", in);

        StringWriter sout = new StringWriter();
        FileUtil.copyContents(new InputStreamReader(in), sout);

        in.close();
        sout.close();

        assertEquals("contents", c_fileContents, sout.toString());

        //
        // Check that first author did not disappear
        //
        Attachment att3 =
            m_manager.getAttachmentInfo(
                new WikiContext(m_engine, new WikiPage(NAME1)), "test1.txt", 1);
        assertEquals("version of v1", 1, att3.getVersion());
        assertEquals("name of v1", "FirstPost", att3.getAuthor());
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testListAttachments()
            throws Exception
    {
        Attachment att = new Attachment(NAME1, "test1.txt");

        att.setAuthor("FirstPost");

        m_manager.storeAttachment(att, makeAttachmentFile());

        Collection c = m_manager.listAttachments(new WikiPage(NAME1));

        assertEquals("Length", 1, c.size());

        Attachment att2 = (Attachment) c.toArray()[0];

        assertEquals("name", att.getName(), att2.getName());
        assertEquals("author", att.getAuthor(), att2.getAuthor());
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testSimpleStoreWithoutExt()
            throws Exception
    {
        Attachment att = new Attachment(NAME1, "test1");

        att.setAuthor("FirstPost");

        m_manager.storeAttachment(att, makeAttachmentFile());

        Attachment att2 =
            m_manager.getAttachmentInfo(new WikiContext(m_engine, new WikiPage(NAME1)), "test1");

        assertNotNull("attachment disappeared", att2);
        assertEquals("name", att.getName(), att2.getName());
        assertEquals("author", "FirstPost", att2.getAuthor());
        assertEquals("size", c_fileContents.length(), att2.getSize());
        assertEquals("version", 1, att2.getVersion());

        InputStream in = m_manager.getAttachmentStream(att2);

        assertNotNull("stream", in);

        StringWriter sout = new StringWriter();
        FileUtil.copyContents(new InputStreamReader(in), sout);

        in.close();
        sout.close();

        assertEquals("contents", c_fileContents, sout.toString());
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testExists()
            throws Exception
    {
        Attachment att = new Attachment(NAME1, "test1");

        att.setAuthor("FirstPost");

        m_manager.storeAttachment(att, makeAttachmentFile());

        assertTrue("attachment disappeared", m_engine.pageExists(NAME1 + "/test1"));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testExists2()
            throws Exception
    {
        Attachment att = new Attachment(NAME1, "test1.bin");

        att.setAuthor("FirstPost");

        m_manager.storeAttachment(att, makeAttachmentFile());

        assertTrue("attachment disappeared", m_engine.pageExists(att.getName()));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testExistsUTF1()
            throws Exception
    {
        Attachment att = new Attachment(NAME1, "test\u00e4.bin");

        att.setAuthor("FirstPost");

        m_manager.storeAttachment(att, makeAttachmentFile());

        assertTrue("attachment disappeared", m_engine.pageExists(att.getName()));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testExistsUTF2()
            throws Exception
    {
        Attachment att = new Attachment(NAMEU, "test\u00e4.bin");

        att.setAuthor("FirstPost");

        m_manager.storeAttachment(att, makeAttachmentFile());

        assertTrue("attachment disappeared", m_engine.pageExists(att.getName()));
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Test suite()
    {
        return new TestSuite(AttachmentManagerTest.class);
    }
}
