package de.softwareforge.eyewiki.variable;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


import de.softwareforge.eyewiki.TestEngine;
import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiPage;
import de.softwareforge.eyewiki.exception.NoSuchVariableException;
import de.softwareforge.eyewiki.manager.VariableManager;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class ConfigurationVariablesTest
        extends TestCase
{
    /** The internally used engine */
    private TestEngine testEngine = null;

    VariableManager variableManager = null;

    /** DOCUMENT ME! */
    WikiContext context = null;
    
    /**
     * Creates a new ConfigurationVariablesTest object.
     *
     * @param s DOCUMENT ME!
     */
    public ConfigurationVariablesTest(String s)
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
        testEngine = new TestEngine();
        variableManager = testEngine.getVariableManager();
        context = new WikiContext(testEngine, new WikiPage("VariablePage"));
    }

    /**
     * DOCUMENT ME!
     */
    public void tearDown()
    {
        testEngine.cleanup();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Test suite()
    {
        return new TestSuite(ConfigurationVariablesTest.class);
    }


    public void testManager()
    {
        assertNotNull("No Variable Manager!", variableManager);
        assertNotNull("No Context!", context);
    }

    public void testConfigurationValue()
    {
        try
        {
            String res = variableManager.parseAndGetValue(context, "{$eyewiki.translatorReader.camelCaseLinks}");
            assertTrue("could not read camelCaseLinks", Boolean.valueOf(res).booleanValue());
        }
        catch (NoSuchVariableException e)
        {
            fail("Variable eyewiki.translatorReader.camelCaseLinks not found");
        }
    }

    public void testLowerCase()
    {
        try
        {
            String res = variableManager.parseAndGetValue(context, "{$eyewiki.translatorreader.camelcaselinks}");
            fail("Lower Case name found!");
        }
        catch (NoSuchVariableException e)
        {
        }
    }
}
