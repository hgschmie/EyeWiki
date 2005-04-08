package com.ecyrd.jspwiki;

import java.io.IOException;

import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.PropertyConfigurator;

import com.ecyrd.jspwiki.exception.NoSuchVariableException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class VariableManagerTest
        extends TestCase
{
    /** DOCUMENT ME! */
    static final String PAGE_NAME = "TestPage";

    /** DOCUMENT ME! */
    VariableManager m_variableManager;

    /** DOCUMENT ME! */
    WikiContext m_context;

    /**
     * Creates a new VariableManagerTest object.
     *
     * @param s DOCUMENT ME!
     */
    public VariableManagerTest(String s)
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
        PropertiesConfiguration conf = new PropertiesConfiguration();

        try
        {
            conf.load(TestEngine.findTestProperties());
            PropertyConfigurator.configure(ConfigurationConverter.getProperties(conf));

            m_variableManager = new VariableManager(conf);

            TestEngine testEngine = new TestEngine(conf);
            m_context = new WikiContext(testEngine, new WikiPage(PAGE_NAME));
        }
        catch (IOException e)
        {
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void tearDown()
    {
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testIllegalInsert1()
            throws Exception
    {
        try
        {
            m_variableManager.parseAndGetValue(m_context, "");
            fail("Did not fail");
        }
        catch (IllegalArgumentException e)
        {
            // OK.
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testIllegalInsert2()
            throws Exception
    {
        try
        {
            m_variableManager.parseAndGetValue(m_context, "{$");
            fail("Did not fail");
        }
        catch (IllegalArgumentException e)
        {
            // OK.
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testIllegalInsert3()
            throws Exception
    {
        try
        {
            m_variableManager.parseAndGetValue(m_context, "{$pagename");
            fail("Did not fail");
        }
        catch (IllegalArgumentException e)
        {
            // OK.
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testIllegalInsert4()
            throws Exception
    {
        try
        {
            m_variableManager.parseAndGetValue(m_context, "{$}");
            fail("Did not fail");
        }
        catch (IllegalArgumentException e)
        {
            // OK.
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void testNonExistantVariable()
    {
        try
        {
            m_variableManager.parseAndGetValue(m_context, "{$no_such_variable}");
            fail("Did not fail");
        }
        catch (NoSuchVariableException e)
        {
            // OK.
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testPageName()
            throws Exception
    {
        String res = m_variableManager.getValue(m_context, "pagename");

        assertEquals(PAGE_NAME, res);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testPageName2()
            throws Exception
    {
        String res = m_variableManager.parseAndGetValue(m_context, "{$  pagename  }");

        assertEquals(PAGE_NAME, res);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testMixedCase()
            throws Exception
    {
        String res = m_variableManager.parseAndGetValue(m_context, "{$PAGeNamE}");

        assertEquals(PAGE_NAME, res);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testExpand1()
            throws Exception
    {
        String res = m_variableManager.expandVariables(m_context, "Testing {$pagename}...");

        assertEquals("Testing " + PAGE_NAME + "...", res);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testExpand2()
            throws Exception
    {
        String res = m_variableManager.expandVariables(m_context, "{$pagename} tested...");

        assertEquals(PAGE_NAME + " tested...", res);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testExpand3()
            throws Exception
    {
        String res =
            m_variableManager.expandVariables(m_context, "Testing {$pagename}, {$applicationname}");

        assertEquals("Testing " + PAGE_NAME + ", JSPWiki", res);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testExpand4()
            throws Exception
    {
        String res = m_variableManager.expandVariables(m_context, "Testing {}, {{{}");

        assertEquals("Testing {}, {{{}", res);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Test suite()
    {
        return new TestSuite(VariableManagerTest.class);
    }
}
