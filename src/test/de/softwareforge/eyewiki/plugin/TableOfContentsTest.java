package de.softwareforge.eyewiki.plugin;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.configuration.Configuration;

import de.softwareforge.eyewiki.TestEngine;

/**
 *  @author jalkanen
 *
 *  @since 
 */
public class TableOfContentsTest
        extends TestCase
{
    /** DOCUMENT ME! */
    Configuration conf = null;

    TestEngine testEngine;

    public TableOfContentsTest(String s)
    {
        super(s);
    }

    
    /*
     * @see TestCase#setUp()
     */
    protected void setUp()
            throws Exception
    {
        conf = TestEngine.getConfiguration();
        testEngine = new TestEngine(conf);
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        testEngine.cleanup();
    }

    public void testHeadingVariables()
            throws Exception
    {
        String src="[{SET foo=bar}]\n\n[{TableOfContents}]\n\n!!!Heading [{$foo}]";
        
        testEngine.saveText( "Test", src );
        
        String res = testEngine.getHTML( "Test" );
        
        // FIXME: There's an extra space before the <a>...  Where does it come from?
        // FIXME: The <p> should not be here.
        assertEquals( "\n<p><div class=\"toc\">\n"+
                "<h1 class=\"toc\">Table of Contents</h1>\n"+
                "<ul>\n"+
                "<li> <a class=\"wikicontent\" href=\"Wiki.jsp?page=Test#section-Test-HeadingBar\">Heading bar</a>\n</li>\n"+
                "</ul>\n</div>\n\n</p>"+
                "\n<h2><a class=\"wikianchor\" name=\"section-Test-HeadingBar\" />Heading bar</h2>\n",
                res );
    }

    public static Test suite()
    {
        return new TestSuite( TableOfContentsTest.class );
    }
    
}
