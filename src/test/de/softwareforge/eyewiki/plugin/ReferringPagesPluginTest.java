package de.softwareforge.eyewiki.plugin;

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

import org.apache.commons.configuration.Configuration;

import de.softwareforge.eyewiki.TestEngine;
import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiPage;
import de.softwareforge.eyewiki.WikiProperties;
import de.softwareforge.eyewiki.plugin.PluginManager;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class ReferringPagesPluginTest
        extends TestCase
{
    /** DOCUMENT ME! */
    Configuration conf = null;

    /** DOCUMENT ME! */
    TestEngine engine;

    /** DOCUMENT ME! */
    WikiContext context;

    /** DOCUMENT ME! */
    PluginManager manager;

    /**
     * Creates a new ReferringPagesPluginTest object.
     *
     * @param s DOCUMENT ME!
     */
    public ReferringPagesPluginTest(String s)
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

        conf.setProperty(WikiProperties.PROP_BEAUTIFYTITLE, "false");
        engine = new TestEngine(conf);

        engine.saveText("TestPage", "Reference to [Foobar].");
        engine.saveText("Foobar", "Reference to [TestPage].");
        engine.saveText("Foobar2", "Reference to [TestPage].");
        engine.saveText("Foobar3", "Reference to [TestPage].");
        engine.saveText("Foobar4", "Reference to [TestPage].");
        engine.saveText("Foobar5", "Reference to [TestPage].");
        engine.saveText("Foobar6", "Reference to [TestPage].");
        engine.saveText("Foobar7", "Reference to [TestPage].");

        context = new WikiContext(engine, new WikiPage("TestPage"));
        manager = new PluginManager(engine, conf);
    }

    /**
     * DOCUMENT ME!
     */
    public void tearDown()
    {
        engine.cleanup();
    }

    private String mkLink(String page)
    {
        return mkFullLink(page, page);
    }

    private String mkFullLink(String page, String link)
    {
        return "<a class=\"wikicontent\" href=\"Wiki.jsp?page=" + link + "\">" + page + "</a>";
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testSingleReferral()
            throws Exception
    {
        WikiContext context2 = new WikiContext(engine, new WikiPage("Foobar"));

        String res = manager.execute(context2, "{INSERT ReferringPagesPlugin WHERE max=5}");

        assertEquals(mkLink("TestPage") + "<br />", res);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testMaxReferences()
            throws Exception
    {
        String res = manager.execute(context, "{INSERT ReferringPagesPlugin WHERE max=5}");

        int count = 0;
        int index = -1;

        // Count the number of hyperlinks.  We could check their
        // correctness as well, though.
        while ((index = res.indexOf("<a", index + 1)) != -1)
        {
            count++;
        }

        assertEquals(5, count);

        String expected = "...and 2 more<br />";

        assertEquals("End", expected, res.substring(res.length() - expected.length()));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testReferenceWidth()
            throws Exception
    {
        WikiContext context2 = new WikiContext(engine, new WikiPage("Foobar"));

        String res = manager.execute(context2, "{INSERT ReferringPagesPlugin WHERE maxwidth=5}");

        assertEquals(mkFullLink("TestP...", "TestPage") + "<br />", res);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Test suite()
    {
        return new TestSuite(ReferringPagesPluginTest.class);
    }
}
