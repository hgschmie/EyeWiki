package de.softwareforge.eyewiki.variable;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


import de.softwareforge.eyewiki.Release;
import de.softwareforge.eyewiki.TestEngine;
import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiPage;
import de.softwareforge.eyewiki.manager.VariableManager;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class VersionVariableTest
        extends TestCase
{
    /** The internally used engine */
    private TestEngine testEngine = null;

    VariableManager variableManager = null;

    /** DOCUMENT ME! */
    WikiContext context = null;
    
    /**
     * Creates a new VersionVariableTest object.
     *
     * @param s DOCUMENT ME!
     */
    public VersionVariableTest(String s)
    	throws Exception
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
        return new TestSuite(VersionVariableTest.class);
    }


    public void testManager()
    {
        assertNotNull("No Variable Manager!", variableManager);
        assertNotNull("No Context!", context);
    }

    public void testVersion()
    	throws Exception
    {
        String res = variableManager.parseAndGetValue(context, "{$eyewikiversion}");

        assertEquals("Could not resolve {$eyewikiversion}", Release.getVersionString(), res);
    }
}
