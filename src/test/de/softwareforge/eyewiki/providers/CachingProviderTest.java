package de.softwareforge.eyewiki.providers;

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
import java.io.PrintWriter;
import java.io.StringReader;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.log4j.PropertyConfigurator;

import de.softwareforge.eyewiki.TestEngine;
import de.softwareforge.eyewiki.WikiPage;
import de.softwareforge.eyewiki.WikiProperties;
import de.softwareforge.eyewiki.providers.CachingProvider;
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
public class CachingProviderTest
        extends TestCase
{
    /**
     * Creates a new CachingProviderTest object.
     *
     * @param s DOCUMENT ME!
     */
    public CachingProviderTest(String s)
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
        Configuration conf = TestEngine.getConfiguration();
        PropertyConfigurator.configure(ConfigurationConverter.getProperties(conf));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void tearDown()
            throws Exception
    {
    }

    /**
     * Checks that at startup we call the provider once, and once only.
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testInitialization()
            throws Exception
    {
        Configuration conf2 = TestEngine.getConfiguration();
        conf2.setProperty(WikiProperties.PROP_COMPONENTS_FILE, "src/test/etc/counterComponents.xml");
        conf2.setProperty(WikiProperties.PROP_CACHECAPACITY, "100");

        TestEngine engine = new TestEngine(conf2);

        CounterProvider p = (CounterProvider) ((CachingProvider) engine.getPageManager().getProvider()).getRealProvider();

        assertEquals("init", 1, p.m_initCalls);
        assertEquals("getAllPages", 1, p.m_getAllPagesCalls);
        assertEquals("pageExists", 0, p.m_pageExistsCalls);
        assertEquals("getPage", 2, p.m_getPageCalls); // These two are for non-existant pages (with and without s)
        assertEquals("getPageText", 4, p.m_getPageTextCalls);

        WikiPage wp = engine.getPage("Foo");

        assertEquals("pageExists2", 0, p.m_pageExistsCalls);
        assertEquals("getPage2", 2, p.m_getPageCalls);

        engine.cleanup();
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testSneakyAdd()
            throws Exception
    {
        Configuration conf2 = TestEngine.getConfiguration();
        conf2.setProperty(WikiProperties.PROP_CACHECHECKINTERVAL, "2");

        TestEngine engine = new TestEngine(conf2);

        String dir = engine.getPageDir();

        File f = new File(dir, "Testi.txt");
        String content = "[fuufaa]";

        PrintWriter out = new PrintWriter(new FileWriter(f));
        FileUtil.copyContents(new StringReader(content), out);
        out.close();

        Thread.sleep(4000L); // Make sure we wait long enough

        WikiPage p = engine.getPage("Testi");
        assertNotNull("page did not exist?", p);

        String text = engine.getText("Testi");
        assertEquals("text", "[fuufaa]", text);

        // TODO: ReferenceManager check as well
        engine.cleanup();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Test suite()
    {
        return new TestSuite(CachingProviderTest.class);
    }
}
