package com.ecyrd.jspwiki;

import java.io.File;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.configuration.PropertiesConfiguration;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * DOCUMENT ME!
 *
 * @author Torsten Hildebrandt.
 */
public class ReferenceManagerTest
        extends TestCase
{
    /** DOCUMENT ME! */
    PropertiesConfiguration conf = new PropertiesConfiguration();

    /** DOCUMENT ME! */
    TestEngine engine;

    /** DOCUMENT ME! */
    ReferenceManager mgr;

    /**
     * Creates a new ReferenceManagerTest object.
     *
     * @param s DOCUMENT ME!
     */
    public ReferenceManagerTest(String s)
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
        conf.setProperty(WikiProperties.PROP_MATCHPLURALS, "true");

        //
        //  We must make sure that the reference manager cache is cleaned before.
        //
        String workDir = conf.getString(WikiProperties.PROP_WORKDIR);

        if (workDir != null)
        {
            File refmgrfile = new File(workDir, "refmgr.ser");

            if (refmgrfile.exists())
            {
                refmgrfile.delete();
            }
        }

        engine = new TestEngine(conf);

        engine.saveText("TestPage", "Reference to [Foobar].");
        engine.saveText("Foobar", "Reference to [Foobar2], [Foobars], [Foobar]");

        mgr = engine.getReferenceManager();
    }

    /**
     * DOCUMENT ME!
     */
    public void tearDown()
    {
        TestEngine.deleteTestPage("TestPage");
        TestEngine.deleteTestPage("Foobar");
        TestEngine.deleteTestPage("Foobars");
        TestEngine.deleteTestPage("Foobar2");
        TestEngine.deleteTestPage("Foobar2s");
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testUnreferenced()
            throws Exception
    {
        Collection c = mgr.findUnreferenced();
        assertTrue(
            "Unreferenced page not found by ReferenceManager",
            Util.collectionContains(c, "TestPage"));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testBecomesUnreferenced()
            throws Exception
    {
        engine.saveText("Foobar2", "[TestPage]");

        Collection c = mgr.findUnreferenced();
        assertEquals("Wrong # of orphan pages, stage 1", 0, c.size());

        engine.saveText("Foobar2", "norefs");
        c = mgr.findUnreferenced();
        assertEquals("Wrong # of orphan pages", 1, c.size());

        Iterator i = c.iterator();
        String first = (String) i.next();
        assertEquals("Not correct referrers", "TestPage", first);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testUncreated()
            throws Exception
    {
        Collection c = mgr.findUncreated();

        assertTrue((c.size() == 1) && ((String) c.iterator().next()).equals("Foobar2"));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testReferrers()
            throws Exception
    {
        Collection c = mgr.findReferrers("TestPage");
        assertNull("TestPage referrers", c);

        c = mgr.findReferrers("Foobar");
        assertTrue(
            "Foobar referrers", (c.size() == 1)
            && ((String) c.iterator().next()).equals("TestPage"));

        c = mgr.findReferrers("Foobar2");
        assertTrue(
            "Foobar2 referrers", (c.size() == 1) && ((String) c.iterator().next()).equals("Foobar"));

        c = mgr.findReferrers("Foobars");
        assertTrue(
            "Foobars referrers", (c.size() == 1) && ((String) c.iterator().next()).equals("Foobar"));
    }

    /**
     * Is a page recognized as referenced if only plural form links exist.
     *
     * @throws Exception DOCUMENT ME!
     */

    // NB: Unfortunately, cleaning out self-references in the case there's
    //     a plural and a singular form of the page becomes nigh impossible, so we
    //     just don't do it.
    public void testUpdatePluralOnlyRef()
            throws Exception
    {
        engine.saveText("TestPage", "Reference to [Foobars].");

        Collection c = mgr.findUnreferenced();
        assertTrue(
            "Foobar unreferenced",
            (c.size() == 1) && ((String) c.iterator().next()).equals("TestPage"));

        c = mgr.findReferrers("Foobar");

        Iterator it = c.iterator();
        String s1 = (String) it.next();
        String s2 = (String) it.next();
        assertTrue(
            "Foobar referrers",
            (c.size() == 2)
            && ((s1.equals("TestPage") && s2.equals("Foobar"))
            || (s1.equals("Foobar") && s2.equals("TestPage"))));
    }

    /**
     * Opposite to testUpdatePluralOnlyRef(). Is a page with plural form recognized as the page
     * referenced by a singular link.
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testUpdateFoobar2s()
            throws Exception
    {
        engine.saveText("Foobar2s", "qwertz");
        assertTrue("no uncreated", mgr.findUncreated().size() == 0);

        Collection c = mgr.findReferrers("Foobar2s");
        assertTrue(
            "referrers",
            (c != null) && (c.size() == 1) && ((String) c.iterator().next()).equals("Foobar"));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testUpdateBothExist()
            throws Exception
    {
        engine.saveText("Foobars", "qwertz");

        Collection c = mgr.findReferrers("Foobars");
        assertTrue(
            "Foobars referrers", (c.size() == 1) && ((String) c.iterator().next()).equals("Foobar"));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testUpdateBothExist2()
            throws Exception
    {
        engine.saveText("Foobars", "qwertz");
        engine.saveText("TestPage", "Reference to [Foobar], [Foobars].");

        Collection c = mgr.findReferrers("Foobars");
        assertEquals("Foobars referrers count", c.size(), 2);

        Iterator i = c.iterator();
        String first = (String) i.next();
        String second = (String) i.next();
        assertTrue(
            "Foobars referrers",
            (first.equals("Foobar") && second.equals("TestPage"))
            || (first.equals("TestPage") && second.equals("Foobar")));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testCircularRefs()
            throws Exception
    {
        engine.saveText("Foobar2", "ref to [TestPage]");

        assertTrue("no uncreated", mgr.findUncreated().size() == 0);
        assertTrue("no unreferenced", mgr.findUnreferenced().size() == 0);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Test suite()
    {
        return new TestSuite(ReferenceManagerTest.class);
    }

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String [] args)
    {
        junit.textui.TestRunner.main(new String []
            {
                ReferenceManagerTest.class.getName()
            });
    }

    /**
     * Test method: dumps the contents of  ReferenceManager link lists to stdout. This method is
     * NOT synchronized, and should be used in testing with one user, one WikiEngine only.
     *
     * @param rm DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static String dumpReferenceManager(ReferenceManager rm)
    {
        StringBuffer buf = new StringBuffer();

        try
        {
            buf.append("================================================================\n");
            buf.append("Referred By list:\n");

            Set keys = rm.getReferredBy().keySet();
            Iterator it = keys.iterator();

            while (it.hasNext())
            {
                String key = (String) it.next();
                buf.append(key + " referred by: ");

                Set refs = (Set) rm.getReferredBy().get(key);
                Iterator rit = refs.iterator();

                while (rit.hasNext())
                {
                    String aRef = (String) rit.next();
                    buf.append(aRef + " ");
                }

                buf.append("\n");
            }

            buf.append("----------------------------------------------------------------\n");
            buf.append("Refers To list:\n");
            keys = rm.getRefersTo().keySet();
            it = keys.iterator();

            while (it.hasNext())
            {
                String key = (String) it.next();
                buf.append(key + " refers to: ");

                Collection refs = (Collection) rm.getRefersTo().get(key);

                if (refs != null)
                {
                    Iterator rit = refs.iterator();

                    while (rit.hasNext())
                    {
                        String aRef = (String) rit.next();
                        buf.append(aRef + " ");
                    }

                    buf.append("\n");
                }
                else
                {
                    buf.append("(no references)\n");
                }
            }

            buf.append("================================================================\n");
        }
        catch (Exception e)
        {
            buf.append("Problem in dump(): " + e + "\n");
        }

        return (buf.toString());
    }
}
