package com.ecyrd.jspwiki.util;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.ecyrd.jspwiki.WikiPage;
import com.ecyrd.jspwiki.WikiProperties;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class ClassUtilTest
        extends TestCase
{
    /**
     * Creates a new ClassUtilTest object.
     *
     * @param s DOCUMENT ME!
     */
    public ClassUtilTest(String s)
    {
        super(s);
    }

    /**
     * Tries to find an existing class.
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testFindClass()
            throws Exception
    {
        Class foo = ClassUtil.findClass(WikiProperties.DEFAULT_CLASS_PREFIX, WikiPage.class.getName());
        assertEquals("No WikiPage found!", foo, WikiPage.class);
    }

    /**
     * Non-existant classes should throw ClassNotFoundEx.
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testFindClassNoClass()
            throws Exception
    {
        try
        {
            Class foo = ClassUtil.findClass(WikiProperties.DEFAULT_CLASS_PREFIX, "MubbleBubble");
            fail("Found class 'MubbleBubble'?!?");
        }
        catch (ClassNotFoundException e)
        {
            // Expected
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Test suite()
    {
        return new TestSuite(ClassUtilTest.class);
    }
}
