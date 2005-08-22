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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.log4j.PropertyConfigurator;

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
public class FileUtilTest
        extends TestCase
{
    /**
     * Creates a new FileUtilTest object.
     *
     * @param s DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public FileUtilTest(String s)
            throws Exception
    {
        super(s);

        Configuration conf = null;

        conf = TestEngine.getConfiguration();
        PropertyConfigurator.configure(ConfigurationConverter.getProperties(conf));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void setUp()
            throws Exception
    {
    }

    /**
     * DOCUMENT ME!
     */
    public void tearDown()
    {
    }

    /**
     * This test actually checks if your JDK is misbehaving.  On my own Debian machine, changing the system to use UTF-8 suddenly
     * broke Java, and I put in this test to check for its brokenness.  If your tests suddenly stop running, check if this one is
     * failing too.  If it is, your platform is broken.  If it's not, seek for the bug in your code.
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testJDKString()
            throws Exception
    {
        String src = "abc\u00e4\u00e5\u00a6";

        String res = new String(src.getBytes("ISO-8859-1"), "ISO-8859-1");

        assertEquals(src, res);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testReadContentsLatin1()
            throws Exception
    {
        String src = "abc\u00e4\u00e5\u00a6";

        String res = FileUtil.readContents(new ByteArrayInputStream(src.getBytes("ISO-8859-1")), "ISO-8859-1");

        assertEquals(src, res);
    }

    /**
     * Check that fallbacks to ISO-Latin1 still work.
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testReadContentsLatin1_2()
            throws Exception
    {
        String src = "abc\u00e4\u00e5\u00a6def";

        String res = FileUtil.readContents(new ByteArrayInputStream(src.getBytes("ISO-8859-1")), "UTF-8");

        assertEquals(src, res);
    }

    /**
     * ISO Latin 1 from a pipe. FIXME: Works only on UNIX systems now.
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testReadContentsFromPipe()
            throws Exception
    {
        String src = "abc\n123456\n\nfoobar.\n";

        // Make a very long string.
        for (int i = 0; i < 10; i++)
        {
            src += src;
        }

        src += "\u00e4\u00e5\u00a6";

        File f = FileUtil.newTmpFile(src, "ISO-8859-1");

        String [] envp = {  };

        Process process = Runtime.getRuntime().exec("cat " + f.getAbsolutePath(), envp, f.getParentFile());

        String result = FileUtil.readContents(process.getInputStream(), "UTF-8");

        f.delete();

        assertEquals(src, result);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    public void testReadContentsReader()
            throws IOException
    {
        String data = "ABCDEF";

        String result = FileUtil.readContents(new StringReader(data));

        assertEquals(data, result);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Test suite()
    {
        return new TestSuite(FileUtilTest.class);
    }
}
