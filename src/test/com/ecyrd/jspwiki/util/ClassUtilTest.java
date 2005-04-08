package com.ecyrd.jspwiki.util;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


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
        Class foo = ClassUtil.findClass("com.ecyrd.jspwiki", "WikiPage");

        assertEquals(foo.getName(), "com.ecyrd.jspwiki.WikiPage");
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
            Class foo = ClassUtil.findClass("com.ecyrd.jspwiki", "MubbleBubble");
            fail("Found class");
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
