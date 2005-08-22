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
public class PluginManagerTest
        extends TestCase
{
    /** DOCUMENT ME! */
    public static final String NAME1 = "Test1";

    /** DOCUMENT ME! */
    Configuration conf = null;

    /** DOCUMENT ME! */
    TestEngine engine;

    /** DOCUMENT ME! */
    WikiContext context;

    /** DOCUMENT ME! */
    PluginManager manager;

    /**
     * Creates a new PluginManagerTest object.
     *
     * @param s DOCUMENT ME!
     */
    public PluginManagerTest(String s)
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

        engine = new TestEngine(conf);
        context = new WikiContext(engine, new WikiPage("testpage"));
        manager = new PluginManager(engine, conf);
    }

    /**
     * DOCUMENT ME!
     */
    public void tearDown()
    {
        engine.cleanup();
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testSimpleInsert()
            throws Exception
    {
        String res = manager.execute(context, "{INSERT SamplePlugin WHERE text=foobar}");

        assertEquals("foobar", res);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testSimpleInsertNoPackage()
            throws Exception
    {
        String res = manager.execute(context, "{INSERT SamplePlugin WHERE text=foobar}");

        assertEquals("foobar", res);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testSimpleInsertNoPackage2()
            throws Exception
    {
        PluginManager m = new PluginManager(engine, conf);
        String res = m.execute(context, "{INSERT SamplePlugin2 WHERE text=foobar}");

        assertEquals("foobar", res);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testSimpleInsertNoPackage3()
            throws Exception
    {
        PluginManager m = new PluginManager(engine, conf);
        String res = m.execute(context, "{INSERT SamplePlugin3 WHERE text=foobar}");

        assertEquals("foobar", res);
    }

    /**
     * Check that in all cases de.softwareforge.eyewiki.plugin is searched.
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testSimpleInsertNoPackage4()
            throws Exception
    {
        PluginManager m = new PluginManager(engine, conf);
        String res = m.execute(context, "{INSERT SamplePlugin WHERE text=foobar}");

        assertEquals("foobar", res);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testSimpleInsert2()
            throws Exception
    {
        String res = manager.execute(context, "{INSERT SamplePlugin  WHERE   text = foobar2, moo=blat}");

        assertEquals("foobar2", res);
    }

    /**
     * Missing closing brace
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testSimpleInsert3()
            throws Exception
    {
        String res = manager.execute(context, "{INSERT SamplePlugin  WHERE   text = foobar2, moo=blat");

        assertEquals("foobar2", res);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testQuotedArgs()
            throws Exception
    {
        String res = manager.execute(context, "{INSERT SamplePlugin WHERE text='this is a space'}");

        assertEquals("this is a space", res);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testQuotedArgs2()
            throws Exception
    {
        String res = manager.execute(context, "{INSERT SamplePlugin WHERE text='this \\'is a\\' space'}");

        assertEquals("this 'is a' space", res);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testNumberArgs()
            throws Exception
    {
        String res = manager.execute(context, "{INSERT SamplePlugin WHERE text=15}");

        assertEquals("15", res);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testNoInsert()
            throws Exception
    {
        String res = manager.execute(context, "{SamplePlugin WHERE text=15}");

        assertEquals("15", res);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Test suite()
    {
        return new TestSuite(PluginManagerTest.class);
    }
}
