package de.softwareforge.eyewiki.plugin;

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
