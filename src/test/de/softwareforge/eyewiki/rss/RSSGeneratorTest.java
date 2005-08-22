package de.softwareforge.eyewiki.rss;

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

import java.util.Date;
import java.util.List;

import org.apache.commons.configuration.Configuration;

import de.softwareforge.eyewiki.TestEngine;
import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.plugin.WeblogEntryPlugin;
import de.softwareforge.eyewiki.plugin.WeblogPlugin;
import de.softwareforge.eyewiki.rss.RSSGenerator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * DOCUMENT ME!
 *
 * @author jalkanen
 *
 * @since
 */
public class RSSGeneratorTest
        extends TestCase
{
    /** DOCUMENT ME! */
    Configuration conf = null;

    /** DOCUMENT ME! */
    TestEngine m_testEngine;

    /**
     * Creates a new RSSGeneratorTest object.
     *
     * @param arg0 DOCUMENT ME!
     */
    public RSSGeneratorTest(String arg0)
    {
        super(arg0);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    protected void setUp()
            throws Exception
    {
        conf = TestEngine.getConfiguration("/eyewiki_rss.properties");

        conf.setProperty(WikiEngine.PROP_BASEURL, "http://localhost/");
        m_testEngine = new TestEngine(conf);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    protected void tearDown()
            throws Exception
    {
        m_testEngine.cleanup();
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testBlogRSS()
            throws Exception
    {
        WeblogEntryPlugin plugin = (WeblogEntryPlugin) m_testEngine.getPluginManager().findPlugin("WeblogEntryPlugin");
        m_testEngine.saveText("TestBlog", "Foo1");

        assertNotNull("No Weblog Entry Plugin found", plugin);

        String newPage = plugin.getNewEntryPage("TestBlog");
        m_testEngine.saveText(newPage, "!Title1\r\nFoo");

        newPage = plugin.getNewEntryPage("TestBlog");
        m_testEngine.saveText(newPage, "!Title2\r\n__Bar__");

        RSSGenerator gen = m_testEngine.getRSSGenerator();

        assertNotNull("No RSS Generator found", gen);

        WikiContext context = new WikiContext(m_testEngine, m_testEngine.getPage("TestBlog"));

        WeblogPlugin blogplugin = (WeblogPlugin) m_testEngine.getPluginManager().findPlugin("WeblogPlugin");

        assertNotNull("No Weblog Plugin found", blogplugin);

        List entries = blogplugin.findBlogEntries("TestBlog", new Date(0), new Date(Long.MAX_VALUE));

        String blog = gen.generateBlogRSS(context, entries);

        assertTrue("has Foo", blog.indexOf("<description>Foo</description>") != -1);
        assertTrue("has proper Bar", blog.indexOf("&lt;b&gt;Bar&lt;/b&gt;") != -1);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Test suite()
    {
        return new TestSuite(RSSGeneratorTest.class);
    }
}
