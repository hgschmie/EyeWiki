package com.ecyrd.jspwiki;

import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.PropertyConfigurator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class AllTests
        extends TestCase
{
    //
    //  Ensure everything runs properly and that we can locate all necessary
    //  thingies.
    //
    static
    {
        PropertiesConfiguration conf = new PropertiesConfiguration();

        try
        {
            conf.load(TestEngine.findTestProperties());
            PropertyConfigurator.configure(ConfigurationConverter.getProperties(conf));
        }
        catch (Exception e)
        {
        }
    }

    /**
     * Creates a new AllTests object.
     *
     * @param s DOCUMENT ME!
     */
    public AllTests(String s)
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
        TestSuite suite = new TestSuite("JSPWiki Unit Tests");

        suite.addTest(FileUtilTest.suite());
        suite.addTest(PageManagerTest.suite());
        suite.addTest(TextUtilTest.suite());
        suite.addTest(TranslatorReaderTest.suite());
        suite.addTest(VariableManagerTest.suite());
        suite.addTest(WikiEngineTest.suite());
        suite.addTest(ReferenceManagerTest.suite());
        suite.addTest(com.ecyrd.jspwiki.plugin.AllTests.suite());
        suite.addTest(com.ecyrd.jspwiki.xmlrpc.AllTests.suite());
        suite.addTest(com.ecyrd.jspwiki.providers.AllTests.suite());
        suite.addTest(com.ecyrd.jspwiki.attachment.AllTests.suite());
        suite.addTest(com.ecyrd.jspwiki.acl.AllTests.suite());

        // TODO: Fix these so that they can be added.
        // suite.addTest( com.ecyrd.jspwiki.auth.AllTests.suite() );
        suite.addTest(com.ecyrd.jspwiki.util.AllTests.suite());
        suite.addTest(com.ecyrd.jspwiki.filters.AllTests.suite());

        return suite;
    }
}
