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

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConversionException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;

import de.softwareforge.eyewiki.util.TextUtil;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class TextUtilTest
        extends TestCase
{
    /**
     * Creates a new TextUtilTest object.
     *
     * @param s DOCUMENT ME!
     */
    public TextUtilTest(String s)
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
    }

    /**
     * DOCUMENT ME!
     */
    public void tearDown()
    {
    }

    /**
     * DOCUMENT ME!
     */
    public void testEncodeName_1()
    {
        String name = "Hello/World";

        assertEquals("Hello/World", TextUtil.urlEncode(name, "ISO-8859-1"));
    }

    /**
     * DOCUMENT ME!
     */
    public void testEncodeName_2()
    {
        String name = "Hello~World";

        assertEquals("Hello%7EWorld", TextUtil.urlEncode(name, "ISO-8859-1"));
    }

    /**
     * DOCUMENT ME!
     */
    public void testEncodeName_3()
    {
        String name = "Hello/World ~";

        assertEquals("Hello/World+%7E", TextUtil.urlEncode(name, "ISO-8859-1"));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testDecodeName_1()
            throws Exception
    {
        String name = "Hello/World+%7E+%2F";

        assertEquals("Hello/World ~ /", TextUtil.urlDecode(name, "ISO-8859-1"));
    }

    /**
     * DOCUMENT ME!
     */
    public void testEncodeNameUTF8_1()
    {
        String name = "\u0041\u2262\u0391\u002E";

        assertEquals("A%E2%89%A2%CE%91.", TextUtil.urlEncodeUTF8(name));
    }

    /**
     * DOCUMENT ME!
     */
    public void testEncodeNameUTF8_2()
    {
        String name = "\uD55C\uAD6D\uC5B4";

        assertEquals("%ED%95%9C%EA%B5%AD%EC%96%B4", TextUtil.urlEncodeUTF8(name));
    }

    /**
     * DOCUMENT ME!
     */
    public void testEncodeNameUTF8_3()
    {
        String name = "\u65E5\u672C\u8A9E";

        assertEquals("%E6%97%A5%E6%9C%AC%E8%AA%9E", TextUtil.urlEncodeUTF8(name));
    }

    /**
     * DOCUMENT ME!
     */
    public void testEncodeNameUTF8_4()
    {
        String name = "Hello World";

        assertEquals("Hello+World", TextUtil.urlEncodeUTF8(name));
    }

    /**
     * DOCUMENT ME!
     */
    public void testDecodeNameUTF8_1()
    {
        String name = "A%E2%89%A2%CE%91.";

        assertEquals("\u0041\u2262\u0391\u002E", TextUtil.urlDecodeUTF8(name));
    }

    /**
     * DOCUMENT ME!
     */
    public void testDecodeNameUTF8_2()
    {
        String name = "%ED%95%9C%EA%B5%AD%EC%96%B4";

        assertEquals("\uD55C\uAD6D\uC5B4", TextUtil.urlDecodeUTF8(name));
    }

    /**
     * DOCUMENT ME!
     */
    public void testDecodeNameUTF8_3()
    {
        String name = "%E6%97%A5%E6%9C%AC%E8%AA%9E";

        assertEquals("\u65E5\u672C\u8A9E", TextUtil.urlDecodeUTF8(name));
    }

    /**
     * DOCUMENT ME!
     */
    public void testReplaceString1()
    {
        String text = "aabacaa";

        assertEquals("ddbacdd", StringUtils.replace(text, "aa", "dd"));
    }

    /**
     * DOCUMENT ME!
     */
    public void testReplaceString4()
    {
        String text = "aabacaafaa";

        assertEquals("ddbacddfdd", StringUtils.replace(text, "aa", "dd"));
    }

    /**
     * DOCUMENT ME!
     */
    public void testReplaceString5()
    {
        String text = "aaabacaaafaa";

        assertEquals("dbacdfaa", StringUtils.replace(text, "aaa", "d"));
    }

    /**
     * DOCUMENT ME!
     */
    public void testReplaceString2()
    {
        String text = "abcde";

        assertEquals("fbcde", StringUtils.replace(text, "a", "f"));
    }

    /**
     * DOCUMENT ME!
     */
    public void testReplaceString3()
    {
        String text = "ababab";

        assertEquals("afafaf", StringUtils.replace(text, "b", "f"));
    }

    // Pure UNIX.
    public void testNormalizePostdata1()
    {
        String text = "ab\ncd";

        assertEquals("ab\r\ncd\r\n", TextUtil.normalizePostData(text));
    }

    // Pure MSDOS.
    public void testNormalizePostdata2()
    {
        String text = "ab\r\ncd";

        assertEquals("ab\r\ncd\r\n", TextUtil.normalizePostData(text));
    }

    // Pure Mac
    public void testNormalizePostdata3()
    {
        String text = "ab\rcd";

        assertEquals("ab\r\ncd\r\n", TextUtil.normalizePostData(text));
    }

    // Mixed, ending correct.
    public void testNormalizePostdata4()
    {
        String text = "ab\ncd\r\n\r\n\r";

        assertEquals("ab\r\ncd\r\n\r\n\r\n", TextUtil.normalizePostData(text));
    }

    // Multiple newlines
    public void testNormalizePostdata5()
    {
        String text = "ab\ncd\n\n\n\n";

        assertEquals("ab\r\ncd\r\n\r\n\r\n\r\n", TextUtil.normalizePostData(text));
    }

    // Empty.
    public void testNormalizePostdata6()
    {
        String text = "";

        assertEquals("\r\n", TextUtil.normalizePostData(text));
    }

    // Just a newline.
    public void testNormalizePostdata7()
    {
        String text = "\n";

        assertEquals("\r\n", TextUtil.normalizePostData(text));
    }

    /**
     * DOCUMENT ME!
     */
    public void testGetBooleanProperty()
    {
        Configuration conf = new PropertiesConfiguration();

        conf.setProperty("foobar.0", "YES");
        conf.setProperty("foobar.1", "true");
        conf.setProperty("foobar.2", "false");
        conf.setProperty("foobar.3", "no");
        conf.setProperty("foobar.4", "on");
        conf.setProperty("foobar.5", "OFF");
        conf.setProperty("foobar.6", "gewkjoigew");

        assertTrue("foobar.0", conf.getBoolean("foobar.0", false));
        assertTrue("foobar.1", conf.getBoolean("foobar.1", false));

        assertFalse("foobar.2", conf.getBoolean("foobar.2", true));
        assertFalse("foobar.3", conf.getBoolean("foobar.3", true));
        assertTrue("foobar.4", conf.getBoolean("foobar.4", false));

        assertFalse("foobar.5", conf.getBoolean("foobar.5", true));

        try
        {
            assertFalse("foobar.6", conf.getBoolean("foobar.6", true));
            fail("No exception has been thrown!");
        }
        catch (Exception e)
        {
            assertEquals("Wrong exception thrown", ConversionException.class, e.getClass());
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testGetSection1()
            throws Exception
    {
        String src = "Single page.";

        assertEquals("section 1", src, TextUtil.getSection(src, 1));

        try
        {
            TextUtil.getSection(src, 5);
            fail("Did not get exception for 2");
        }
        catch (IllegalArgumentException e)
        {
        }

        try
        {
            TextUtil.getSection(src, -1);
            fail("Did not get exception for -1");
        }
        catch (IllegalArgumentException e)
        {
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testGetSection2()
            throws Exception
    {
        String src = "First section\n----\nSecond section\n\n----\n\nThird section";

        assertEquals("section 1", "First section\n", TextUtil.getSection(src, 1));
        assertEquals("section 2", "\nSecond section\n\n", TextUtil.getSection(src, 2));
        assertEquals("section 3", "\n\nThird section", TextUtil.getSection(src, 3));

        try
        {
            TextUtil.getSection(src, 4);
            fail("Did not get exception for section 4");
        }
        catch (IllegalArgumentException e)
        {
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testGetSection3()
            throws Exception
    {
        String src = "----\nSecond section\n----";

        assertEquals("section 1", "", TextUtil.getSection(src, 1));
        assertEquals("section 2", "\nSecond section\n", TextUtil.getSection(src, 2));
        assertEquals("section 3", "", TextUtil.getSection(src, 3));

        try
        {
            TextUtil.getSection(src, 4);
            fail("Did not get exception for section 4");
        }
        catch (IllegalArgumentException e)
        {
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Test suite()
    {
        return new TestSuite(TextUtilTest.class);
    }
}
