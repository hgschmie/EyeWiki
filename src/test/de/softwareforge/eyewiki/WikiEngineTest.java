package de.softwareforge.eyewiki;

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
import java.io.FileWriter;
import java.io.StringReader;

import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.configuration.Configuration;

import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.WikiException;
import de.softwareforge.eyewiki.WikiPage;
import de.softwareforge.eyewiki.WikiProperties;
import de.softwareforge.eyewiki.WikiProvider;
import de.softwareforge.eyewiki.attachment.Attachment;
import de.softwareforge.eyewiki.attachment.AttachmentManager;
import de.softwareforge.eyewiki.manager.ReferenceManager;
import de.softwareforge.eyewiki.providers.CachingProvider;
import de.softwareforge.eyewiki.providers.FileSystemProvider;
import de.softwareforge.eyewiki.providers.VerySimpleProvider;
import de.softwareforge.eyewiki.util.FileUtil;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class WikiEngineTest
        extends TestCase
{
    /** DOCUMENT ME! */
    public static final String NAME1 = "Test1";

    /** DOCUMENT ME! */
    public static final long PAGEPROVIDER_RESCAN_PERIOD = 2;

    /** DOCUMENT ME! */
    Configuration conf = null;

    /** DOCUMENT ME! */
    TestEngine m_engine;

    /**
     * Creates a new WikiEngineTest object.
     *
     * @param s DOCUMENT ME!
     */
    public WikiEngineTest(String s)
    {
        super(s);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Test suite()
    {
        return new TestSuite(WikiEngineTest.class);
    }

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String [] args)
    {
        junit.textui.TestRunner.main(new String [] { WikiEngineTest.class.getName() });
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
        conf.setProperty(WikiEngine.PROP_MATCHPLURALS, "true");

        // We'll need a shorter-than-default consistency check for
        // the page-changed checks. This will cause additional load
        // to the file system, though.
        conf.setProperty(WikiProperties.PROP_CACHECHECKINTERVAL, Long.toString(PAGEPROVIDER_RESCAN_PERIOD));

        m_engine = new TestEngine(conf);
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
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testNonExistantDirectory()
            throws Exception
    {
        String tmpdir = "target/tests/workdir";
        String dirname = "non-existant-directory";

        String newdir = new File(tmpdir, dirname).getAbsolutePath();

        conf.setProperty(WikiProperties.PROP_PAGEDIR, newdir);

        WikiEngine test = new TestEngine(conf);

        File f = new File(newdir);

        assertTrue("didn't create it", f.exists());
        assertTrue("isn't a dir", f.isDirectory());

        f.delete();
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testNonExistantDirProperty()
            throws Exception
    {
        String files = conf.getString(WikiProperties.PROP_PAGEDIR);

        conf.clearProperty(WikiProperties.PROP_PAGEDIR);

        try
        {
            WikiEngine test = new TestEngine(conf);

            fail("Wiki did not warn about missing property.");
        }
        catch (WikiException e)
        {
            // This is okay.
        }

        conf.setProperty(WikiProperties.PROP_PAGEDIR, files);
    }

    /**
     * Check that calling pageExists( String ) works.
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testNonExistantPage()
            throws Exception
    {
        String pagename = "Test1";

        assertEquals("Page already exists", false, m_engine.pageExists(pagename));
    }

    /**
     * Check that calling pageExists( WikiPage ) works.
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testNonExistantPage2()
            throws Exception
    {
        WikiPage page = new WikiPage("Test1");

        assertEquals("Page already exists", false, m_engine.pageExists(page));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testFinalPageName()
            throws Exception
    {
        m_engine.saveText("Foobar", "1");
        m_engine.saveText("Foobars", "2");

        assertEquals("plural mistake", "Foobars", m_engine.getFinalPageName("Foobars"));

        assertEquals("singular mistake", "Foobar", m_engine.getFinalPageName("Foobar"));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testFinalPageNameSingular()
            throws Exception
    {
        m_engine.saveText("Foobar", "1");

        assertEquals("plural mistake", "Foobar", m_engine.getFinalPageName("Foobars"));
        assertEquals("singular mistake", "Foobar", m_engine.getFinalPageName("Foobar"));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testFinalPageNamePlural()
            throws Exception
    {
        m_engine.saveText("Foobars", "1");

        assertEquals("plural mistake", "Foobars", m_engine.getFinalPageName("Foobars"));
        assertEquals("singular mistake", "Foobars", m_engine.getFinalPageName("Foobar"));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testPutPage()
            throws Exception
    {
        String text = "Foobar.\r\n";
        String name = NAME1;

        m_engine.saveText(name, text);

        assertEquals("page does not exist", true, m_engine.pageExists(name));

        assertEquals("wrong content", text, m_engine.getText(name));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testPutPageEntities()
            throws Exception
    {
        String text = "Foobar. &quot;\r\n";
        String name = NAME1;

        m_engine.saveText(name, text);

        assertEquals("page does not exist", true, m_engine.pageExists(name));

        assertEquals("wrong content", "Foobar. &amp;quot;\r\n", m_engine.getText(name));
    }

    /**
     * Cgeck that basic " is changed.
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testPutPageEntities2()
            throws Exception
    {
        String text = "Foobar. \"\r\n";
        String name = NAME1;

        m_engine.saveText(name, text);

        assertEquals("page does not exist", true, m_engine.pageExists(name));

        assertEquals("wrong content", "Foobar. &quot;\r\n", m_engine.getText(name));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testGetHTML()
            throws Exception
    {
        String text = "''Foobar.''";
        String name = NAME1;

        m_engine.saveText(name, text);

        String data = m_engine.getHTML(name);

        assertEquals("<i>Foobar.</i>\n", data);
    }

    /**
     * DOCUMENT ME!
     */
    public void testEncodeNameLatin1()
    {
        String name = "abc\u00e5\u00e4\u00f6";

        assertEquals("abc%E5%E4%F6", m_engine.encodeName(name));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testEncodeNameUTF8()
            throws Exception
    {
        String name = "\u0041\u2262\u0391\u002E";

        conf.setProperty(WikiEngine.PROP_ENCODING, "UTF-8");

        WikiEngine engine = new TestEngine(conf);

        assertEquals("A%E2%89%A2%CE%91.", engine.encodeName(name));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testReadLinks()
            throws Exception
    {
        String src = "Foobar. [Foobar].  Frobozz.  [This is a link].";

        Object [] result = m_engine.scanWikiLinks(new WikiPage("Test"), src).toArray();

        assertEquals("item 0", result[0], "Foobar");
        assertEquals("item 1", result[1], "ThisIsALink");
    }

    /**
     * DOCUMENT ME!
     */
    public void testBeautifyTitle()
    {
        String src = "WikiNameThingy";

        assertEquals("Wiki Name Thingy", m_engine.beautifyTitle(src));
    }

    /**
     * Acronyms should be treated wisely.
     */
    public void testBeautifyTitleAcronym()
    {
        String src = "JSPWikiPage";

        assertEquals("JSP Wiki Page", m_engine.beautifyTitle(src));
    }

    /**
     * Acronyms should be treated wisely.
     */
    public void testBeautifyTitleAcronym2()
    {
        String src = "DELETEME";

        assertEquals("DELETEME", m_engine.beautifyTitle(src));
    }

    /**
     * DOCUMENT ME!
     */
    public void testBeautifyTitleAcronym3()
    {
        String src = "JSPWikiFAQ";

        assertEquals("JSP Wiki FAQ", m_engine.beautifyTitle(src));
    }

    /**
     * DOCUMENT ME!
     */
    public void testBeautifyTitleNumbers()
    {
        String src = "TestPage12";

        assertEquals("Test Page 12", m_engine.beautifyTitle(src));
    }

    /**
     * English articles too.
     */
    public void testBeautifyTitleArticle()
    {
        String src = "ThisIsAPage";

        assertEquals("This Is A Page", m_engine.beautifyTitle(src));
    }

    /**
     * English articles too, pathological case...
     *
     * @throws Exception DOCUMENT ME!
     */

    /*
     *    public void testBeautifyTitleArticle2()
     *    {
     *        String src = "ThisIsAJSPWikiPage";
     *
     *        assertEquals("This Is A JSP Wiki Page", m_engine.beautifyTitle( src ) );
     *    }
     */
    public void testLatestGet()
            throws Exception
    {
        conf.setProperty(WikiProperties.PROP_COMPONENTS_FILE, "src/test/etc/vspComponents.xml");

        WikiEngine engine = new TestEngine(conf);

        WikiPage p = engine.getPage("test", -1);

        VerySimpleProvider vsp = (VerySimpleProvider) engine.getPageManager().getProvider();

        assertEquals("wrong page", "test", vsp.m_latestReq);
        assertEquals("wrong version", -1, vsp.m_latestVers);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testLatestGet2()
            throws Exception
    {
        conf.setProperty(WikiProperties.PROP_COMPONENTS_FILE, "src/test/etc/vspComponents.xml");

        WikiEngine engine = new TestEngine(conf);

        String p = engine.getText("test", -1);

        VerySimpleProvider vsp = (VerySimpleProvider) engine.getPageManager().getProvider();

        assertEquals("wrong page", "test", vsp.m_latestReq);
        assertEquals("wrong version", -1, vsp.m_latestVers);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testLatestGet3()
            throws Exception
    {
        conf.setProperty(WikiProperties.PROP_COMPONENTS_FILE, "src/test/etc/vspComponents.xml");

        WikiEngine engine = new TestEngine(conf);
        VerySimpleProvider vsp = (VerySimpleProvider) engine.getPageManager().getProvider();

        WikiPage p = engine.getPage("test");

        assertEquals("wrong page", "test", vsp.m_latestReq);
        assertEquals("wrong version", -1, vsp.m_latestVers);

        String res = engine.getHTML("test");
        assertEquals("wrong page", "test", vsp.m_latestReq);

        // getHTML now pulls the PureText by Version
        assertEquals("wrong version", 5, vsp.m_latestVers);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testLatestGet4()
            throws Exception
    {
        conf.setProperty(WikiProperties.PROP_COMPONENTS_FILE, "src/test/etc/vspCacheComponents.xml");

        WikiEngine engine = new TestEngine(conf);

        String p = engine.getHTML(VerySimpleProvider.PAGENAME);

        CachingProvider cp = (CachingProvider) engine.getPageManager().getProvider();
        VerySimpleProvider vsp = (VerySimpleProvider) cp.getRealProvider();

        assertEquals("wrong page", VerySimpleProvider.PAGENAME, vsp.m_latestReq);
        assertEquals("wrong version", -1, vsp.m_latestVers);
    }

    /**
     * Checks, if ReferenceManager is informed of new attachments.
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testAttachmentRefs()
            throws Exception
    {
        ReferenceManager refMgr = m_engine.getReferenceManager();
        AttachmentManager attMgr = m_engine.getAttachmentManager();

        m_engine.saveText(NAME1, "fooBar");

        Attachment att = new Attachment(NAME1, "TestAtt.txt");
        att.setAuthor("FirstPost");
        attMgr.storeAttachment(att, m_engine.makeAttachmentFile());

        try
        {
            // and check post-conditions
            Collection c = refMgr.findUncreated();
            assertTrue("attachment exists: " + c, (c == null) || (c.size() == 0));

            c = refMgr.findUnreferenced();
            assertEquals("unreferenced count", 2, c.size());

            Iterator i = c.iterator();
            String first = (String) i.next();
            String second = (String) i.next();
            assertTrue("unreferenced",
                (first.equals(NAME1) && second.equals(NAME1 + "/TestAtt.txt"))
                || (first.equals(NAME1 + "/TestAtt.txt") && second.equals(NAME1)));
        }
        finally
        {
            // do cleanup
            String files = conf.getString(WikiProperties.PROP_PAGEDIR);
            m_engine.deleteAttachments(files);
        }
    }

    /**
     * Is ReferenceManager updated properly if a page references its own attachments?
     *
     * @throws Exception DOCUMENT ME!
     */

    /*
     *      FIXME: This is a deep problem.  The real problem is that the reference
     *      manager cannot know when it encounters a link like "testatt.txt" that it
     *      is really a link to an attachment IF the link is created before
     *      the attachment.  This means that when the attachment is created,
     *      the link will stay in the "uncreated" list.
     *
     *      There are two issues here: first of all, TranslatorReader should
     *      able to return the proper attachment references (which I think
     *      it does), and second, the ReferenceManager should be able to
     *      remove any links that are not referred to, nor they are created.
     *
     *      However, doing this in a relatively sane timeframe can be a problem.
     */
    public void testAttachmentRefs2()
            throws Exception
    {
        ReferenceManager refMgr = m_engine.getReferenceManager();
        AttachmentManager attMgr = m_engine.getAttachmentManager();

        m_engine.saveText(NAME1, "[TestAtt.txt]");

        // check a few pre-conditions
        Collection c = refMgr.findReferrers("TestAtt.txt");
        assertTrue("normal, unexisting page", (c != null) && ((String) c.iterator().next()).equals(NAME1));

        c = refMgr.findReferrers(NAME1 + "/TestAtt.txt");
        assertTrue("no attachment", (c == null) || (c.size() == 0));

        c = refMgr.findUncreated();
        assertTrue("unknown attachment", (c != null) && (c.size() == 1) && ((String) c.iterator().next()).equals("TestAtt.txt"));

        // now we create the attachment
        Attachment att = new Attachment(NAME1, "TestAtt.txt");
        att.setAuthor("FirstPost");
        attMgr.storeAttachment(att, m_engine.makeAttachmentFile());

        try
        {
            // and check post-conditions
            c = refMgr.findUncreated();
            assertTrue("attachment exists: ", (c == null) || (c.size() == 0));

            c = refMgr.findReferrers("TestAtt.txt");
            assertTrue("no normal page", (c == null) || (c.size() == 0));

            c = refMgr.findReferrers(NAME1 + "/TestAtt.txt");
            assertTrue("attachment exists now", (c != null) && ((String) c.iterator().next()).equals(NAME1));

            c = refMgr.findUnreferenced();
            assertTrue("unreferenced", (c.size() == 1) && ((String) c.iterator().next()).equals(NAME1));
        }
        finally
        {
            // do cleanup
            String files = conf.getString(WikiProperties.PROP_PAGEDIR);
            m_engine.deleteAttachments(files);
        }
    }

    /**
     * Checks, if ReferenceManager is informed if a link to an attachment is added.
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testAttachmentRefs3()
            throws Exception
    {
        ReferenceManager refMgr = m_engine.getReferenceManager();
        AttachmentManager attMgr = m_engine.getAttachmentManager();

        m_engine.saveText(NAME1, "fooBar");

        Attachment att = new Attachment(NAME1, "TestAtt.txt");
        att.setAuthor("FirstPost");
        attMgr.storeAttachment(att, m_engine.makeAttachmentFile());

        m_engine.saveText(NAME1, " [" + NAME1 + "/TestAtt.txt] ");

        try
        {
            // and check post-conditions
            Collection c = refMgr.findUncreated();
            assertTrue("attachment exists", (c == null) || (c.size() == 0));

            c = refMgr.findUnreferenced();
            assertEquals("unreferenced count", c.size(), 1);
            assertTrue("unreferenced", ((String) c.iterator().next()).equals(NAME1));
        }
        finally
        {
            // do cleanup
            String files = conf.getString(WikiProperties.PROP_PAGEDIR);
            m_engine.deleteAttachments(files);
        }
    }

    /**
     * Checks, if ReferenceManager is informed if a third page references an attachment.
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testAttachmentRefs4()
            throws Exception
    {
        ReferenceManager refMgr = m_engine.getReferenceManager();
        AttachmentManager attMgr = m_engine.getAttachmentManager();

        m_engine.saveText(NAME1, "[TestPage2]");

        Attachment att = new Attachment(NAME1, "TestAtt.txt");
        att.setAuthor("FirstPost");
        attMgr.storeAttachment(att, m_engine.makeAttachmentFile());

        m_engine.saveText("TestPage2", "[" + NAME1 + "/TestAtt.txt]");

        try
        {
            // and check post-conditions
            Collection c = refMgr.findUncreated();
            assertTrue("attachment exists", (c == null) || (c.size() == 0));

            c = refMgr.findUnreferenced();
            assertEquals("unreferenced count", c.size(), 1);
            assertTrue("unreferenced", ((String) c.iterator().next()).equals(NAME1));
        }
        finally
        {
            // do cleanup
            String files = conf.getString(WikiProperties.PROP_PAGEDIR);
            m_engine.deleteAttachments(files);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testDeletePage()
            throws Exception
    {
        m_engine.saveText(NAME1, "Test");

        String files = conf.getString(WikiProperties.PROP_PAGEDIR);
        File saved = new File(files, NAME1 + FileSystemProvider.FILE_EXT);

        assertTrue("Didn't create it!", saved.exists());

        WikiPage page = m_engine.getPage(NAME1, WikiProvider.LATEST_VERSION);

        m_engine.deletePage(page.getName());

        assertFalse("Page has not been removed!", saved.exists());
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testDeleteVersion()
            throws Exception
    {
        conf.setProperty(WikiProperties.PROP_COMPONENTS_FILE, "src/test/etc/versComponents.xml");

        TestEngine engine = new TestEngine(conf);
        engine.saveText(NAME1, "Test1");
        engine.saveText(NAME1, "Test2");
        engine.saveText(NAME1, "Test3");

        WikiPage page = engine.getPage(NAME1, 3);

        engine.deleteVersion(page);

        assertNull("got page", engine.getPage(NAME1, 3));

        String content = engine.getText(NAME1, WikiProvider.LATEST_VERSION);

        assertEquals("content", "Test2", content.trim());
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testDeleteVersion2()
            throws Exception
    {
        conf.setProperty(WikiProperties.PROP_COMPONENTS_FILE, "src/test/etc/versComponents.xml");

        TestEngine engine = new TestEngine(conf);
        engine.saveText(NAME1, "Test1");
        engine.saveText(NAME1, "Test2");
        engine.saveText(NAME1, "Test3");

        WikiPage page = engine.getPage(NAME1, 1);

        engine.deleteVersion(page);

        assertNull("got page", engine.getPage(NAME1, 1));

        String content = engine.getText(NAME1, WikiProvider.LATEST_VERSION);

        assertEquals("content", "Test3", content.trim());

        assertEquals("content1", "", engine.getText(NAME1, 1).trim());
    }

    /**
     * Assumes that CachingProvider is in use.
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testExternalModificationRefs()
            throws Exception
    {
        ReferenceManager refMgr = m_engine.getReferenceManager();

        m_engine.saveText(NAME1, "[Foobar]");
        m_engine.getText(NAME1); // Ensure that page is cached.

        Collection c = refMgr.findUncreated();
        assertTrue("Non-existent reference not detected by ReferenceManager", Util.collectionContains(c, "Foobar"));

        Thread.sleep(2000L); // Wait two seconds for filesystem granularity

        String files = conf.getString(WikiProperties.PROP_PAGEDIR);

        File saved = new File(files, NAME1 + FileSystemProvider.FILE_EXT);

        assertTrue("No file!", saved.exists());

        FileWriter out = new FileWriter(saved);
        FileUtil.copyContents(new StringReader("[Puppaa]"), out);
        out.close();

        Thread.sleep(2000L * PAGEPROVIDER_RESCAN_PERIOD); // Wait five seconds for CachingProvider to wake up.

        String text = m_engine.getText(NAME1);

        assertEquals("wrong contents", "[Puppaa]", text);

        c = refMgr.findUncreated();

        assertTrue("Non-existent reference after external page change " + "not detected by ReferenceManager",
            Util.collectionContains(c, "Puppaa"));
    }

    /**
     * Assumes that CachingProvider is in use.
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testExternalModificationRefsDeleted()
            throws Exception
    {
        ReferenceManager refMgr = m_engine.getReferenceManager();

        m_engine.saveText(NAME1, "[Foobar]");
        m_engine.getText(NAME1); // Ensure that page is cached.

        Collection c = refMgr.findUncreated();
        assertEquals("uncreated count", 1, c.size());
        assertEquals("wrong referenced page", "Foobar", (String) c.iterator().next());

        Thread.sleep(2000L); // Wait two seconds for filesystem granularity

        String files = conf.getString(WikiProperties.PROP_PAGEDIR);

        File saved = new File(files, NAME1 + FileSystemProvider.FILE_EXT);

        assertTrue("No file!", saved.exists());

        saved.delete();

        assertFalse("File not deleted!", saved.exists());

        Thread.sleep(2000L * PAGEPROVIDER_RESCAN_PERIOD); // Wait five seconds for CachingProvider to catch up.

        WikiPage p = m_engine.getPage(NAME1);

        assertNull("Got page!", p);

        String text = m_engine.getText(NAME1);

        assertEquals("wrong contents", "", text);

        c = refMgr.findUncreated();
        assertEquals("NEW: uncreated count", 0, c.size());
    }

    /**
     * Assumes that CachingProvider is in use.
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testExternalModification()
            throws Exception
    {
        m_engine.saveText(NAME1, "Foobar");

        m_engine.getText(NAME1); // Ensure that page is cached.

        Thread.sleep(2000L); // Wait two seconds for filesystem granularity

        String files = conf.getString(WikiProperties.PROP_PAGEDIR);

        File saved = new File(files, NAME1 + FileSystemProvider.FILE_EXT);

        assertTrue("No file!", saved.exists());

        FileWriter out = new FileWriter(saved);
        FileUtil.copyContents(new StringReader("Puppaa"), out);
        out.close();

        // Wait for the caching provider to notice a refresh.
        Thread.sleep(2000L * PAGEPROVIDER_RESCAN_PERIOD);

        // Trim - engine.saveText() may append a newline.
        String text = m_engine.getText(NAME1).trim();
        assertEquals("wrong contents", "Puppaa", text);
    }
}
