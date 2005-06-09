package de.softwareforge.eyewiki.util;

import java.util.Map;

import de.softwareforge.eyewiki.util.FormUtil;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class FormUtilTest
        extends TestCase
{
    /**
     * Creates a new FormUtilTest object.
     *
     * @param s DOCUMENT ME!
     */
    public FormUtilTest(String s)
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
        return new TestSuite(FormUtilTest.class);
    }

    public void testNullReq()
    {
        Map params = FormUtil.requestToMap(null, "foo");

        assertNotNull("No params Map returned!", params);
        assertEquals("Found a parameter in an empty request!", 0 , params.size());
    }
}

