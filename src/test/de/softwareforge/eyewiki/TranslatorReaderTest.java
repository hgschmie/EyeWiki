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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;

import de.softwareforge.eyewiki.LinkCollector;
import de.softwareforge.eyewiki.TranslatorReader;
import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.WikiException;
import de.softwareforge.eyewiki.WikiPage;
import de.softwareforge.eyewiki.WikiProperties;
import de.softwareforge.eyewiki.attachment.Attachment;
import de.softwareforge.eyewiki.exception.NoRequiredPropertyException;
import de.softwareforge.eyewiki.util.FileUtil;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class TranslatorReaderTest
        extends TestCase
{
    /** DOCUMENT ME! */
    static final String PAGE_NAME = "testpage";

    // This is a random find: the following page text caused an eternal loop in V2.0.x.

    /** DOCUMENT ME! */
    private static final String brokenPageText =
        "Please ''check [RecentChanges].\n" + "\n" + "Testing. fewfwefe\n" + "\n" + "CHeck [testpage]\n" + "\n" + "More testing.\n"
        + "dsadsadsa''\n" + "Is this {{truetype}} or not?\n" + "What about {{{This}}}?\n" + "How about {{this?\n" + "\n" + "{{{\n"
        + "{{text}}\n" + "}}}\n" + "goo\n" + "\n" + "<b>Not bold</b>\n" + "\n" + "motto\n" + "\n" + "* This is a list which we\n"
        + "shall continue on a other line.\n" + "* There is a list item here.\n" + "*  Another item.\n"
        + "* More stuff, which continues\n" + "on a second line.  And on\n" + "a third line as well.\n" + "And a fourth line.\n"
        + "* Third item.\n" + "\n" + "Foobar.\n" + "\n" + "----\n" + "\n" + "!!!Really big heading\n" + "Text.\n"
        + "!! Just a normal heading [with a hyperlink|Main]\n" + "More text.\n" + "!Just a small heading.\n" + "\n"
        + "This should be __bold__ text.\n" + "\n" + "__more bold text continuing\n" + "on the next line.__\n" + "\n"
        + "__more bold text continuing\n" + "\n" + "on the next paragraph.__\n" + "\n" + "\n" + "This should be normal.\n" + "\n"
        + "Now, let's try ''italic text''.\n" + "\n" + "Bulleted lists:\n" + "* One\n" + "Or more.\n" + "* Two\n" + "\n"
        + "** Two.One\n" + "\n" + "*** Two.One.One\n" + "\n" + "* Three\n" + "\n" + "Numbered lists.\n" + "# One\n" + "# Two\n"
        + "# Three\n" + "## Three.One\n" + "## Three.Two\n" + "## Three.Three\n" + "### Three.Three.One\n" + "# Four\n" + "\n"
        + "End?\n" + "\n" + "No, let's {{break}} things.\\ {{{ {{{ {{text}} }}} }}}\n" + "\n" + "More breaking.\n" + "\n" + "{{{\n"
        + "code.}}\n" + "----\n" + "author: [Asser], [Ebu], [JanneJalkanen], [Jarmo|mailto:jarmo@regex.com.au]\n";

    /** DOCUMENT ME! */
    Configuration conf = null;

    /** DOCUMENT ME! */
    TestEngine testEngine;

    /**
     * Creates a new TranslatorReaderTest object.
     *
     * @param s DOCUMENT ME!
     */
    public TranslatorReaderTest(String s)
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
        conf = TestEngine.getConfiguration();

        conf.setProperty(WikiProperties.PROP_MATCHPLURALS, "true");
        testEngine = new TestEngine(conf);
    }

    /**
     * DOCUMENT ME!
     */
    public void tearDown()
    {
        testEngine.cleanup();
    }

    private void newPage(String name)
            throws WikiException
    {
        testEngine.saveText(name, "<test>");
    }

    private String translate(String src)
            throws IOException, NoRequiredPropertyException, ServletException
    {
        return translate(new WikiPage(PAGE_NAME), src);
    }

    private String translate(WikiEngine e, String src)
            throws IOException, NoRequiredPropertyException, ServletException
    {
        return translate(e, new WikiPage(PAGE_NAME), src);
    }

    private String translate(WikiPage p, String src)
            throws IOException, NoRequiredPropertyException, ServletException
    {
        return translate(testEngine, p, src);
    }

    private String translate(WikiEngine e, WikiPage p, String src)
            throws IOException, NoRequiredPropertyException, ServletException
    {
        WikiContext context = new WikiContext(e, p);
        Reader r = new TranslatorReader(context, new BufferedReader(new StringReader(src)));
        StringWriter out = new StringWriter();
        int c;

        while ((c = r.read()) != -1)
        {
            out.write(c);
        }

        return out.toString();
    }

    private String translate_nofollow(String src)
            throws Exception
    {
        conf = TestEngine.getConfiguration();

        conf.setProperty(WikiProperties.PROP_USERRELNOFOLLOW, "true");

        TestEngine testEngine2 = new TestEngine(conf);

        WikiContext context = new WikiContext(testEngine2, new WikiPage(PAGE_NAME));
        Reader r = new TranslatorReader(context, new BufferedReader(new StringReader(src)));
        StringWriter out = new StringWriter();
        int c;

        while ((c = r.read()) != -1)
        {
            out.write(c);
        }

        return out.toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testHyperlinks2()
            throws Exception
    {
        newPage("Hyperlink");

        String src = "This should be a [hyperlink]";

        assertEquals("This should be a <a class=\"wikicontent\" href=\"Wiki.jsp?page=Hyperlink\">hyperlink</a>", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testHyperlinks3()
            throws Exception
    {
        newPage("HyperlinkToo");

        String src = "This should be a [hyperlink too]";

        assertEquals("This should be a <a class=\"wikicontent\" href=\"Wiki.jsp?page=HyperlinkToo\">hyperlink too</a>",
            translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testHyperlinks4()
            throws Exception
    {
        newPage("HyperLink");

        String src = "This should be a [HyperLink]";

        assertEquals("This should be a <a class=\"wikicontent\" href=\"Wiki.jsp?page=HyperLink\">HyperLink</a>", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testHyperlinks5()
            throws Exception
    {
        newPage("HyperLink");

        String src = "This should be a [here|HyperLink]";

        assertEquals("This should be a <a class=\"wikicontent\" href=\"Wiki.jsp?page=HyperLink\">here</a>", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testHyperlinksNamed1()
            throws Exception
    {
        newPage("HyperLink");

        String src = "This should be a [here|HyperLink#heading]";

        assertEquals("This should be a <a class=\"wikicontent\" href=\"Wiki.jsp?page=HyperLink#section-HyperLink-heading\">here</a>",
            translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testHyperlinksNamed2()
            throws Exception
    {
        newPage("HyperLink");

        String src = "This should be a [HyperLink#heading]";

        assertEquals("This should be a <a class=\"wikicontent\" href=\"Wiki.jsp?page=HyperLink#section-HyperLink-heading\">HyperLink#heading</a>",
            translate(src));
    }

    //
    //  Testing CamelCase hyperlinks
    //
    public void testHyperLinks6()
            throws Exception
    {
        newPage("DiscussionAboutWiki");
        newPage("WikiMarkupDevelopment");

        String src = "[DiscussionAboutWiki] [WikiMarkupDevelopment].";

        assertEquals("<a class=\"wikicontent\" href=\"Wiki.jsp?page=DiscussionAboutWiki\">DiscussionAboutWiki</a> <a class=\"wikicontent\" href=\"Wiki.jsp?page=WikiMarkupDevelopment\">WikiMarkupDevelopment</a>.",
            translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testHyperlinksCC()
            throws Exception
    {
        newPage("HyperLink");

        String src = "This should be a HyperLink.";

        assertEquals("This should be a <a class=\"wikicontent\" href=\"Wiki.jsp?page=HyperLink\">HyperLink</a>.", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testHyperlinksCCNonExistant()
            throws Exception
    {
        String src = "This should be a HyperLink.";

        assertEquals("This should be a <a class=\"wikicontent\" title=\"Create 'HyperLink'\" href=\"Edit.jsp?page=HyperLink\">HyperLink</a>.",
            translate(src));
    }

    /**
     * Check if the CC hyperlink translator gets confused with unorthodox bracketed links.
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testHyperlinksCC2()
            throws Exception
    {
        newPage("HyperLink");

        String src = "This should be a [  HyperLink  ].";

        assertEquals("This should be a <a class=\"wikicontent\" href=\"Wiki.jsp?page=HyperLink\">  HyperLink  </a>.", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testHyperlinksCC3()
            throws Exception
    {
        String src = "This should be a nonHyperLink.";

        assertEquals("This should be a nonHyperLink.", translate(src));
    }

    /**
     * Two links on same line.
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testHyperlinksCC4()
            throws Exception
    {
        newPage("HyperLink");
        newPage("ThisToo");

        String src = "This should be a HyperLink, and ThisToo.";

        assertEquals("This should be a <a class=\"wikicontent\" href=\"Wiki.jsp?page=HyperLink\">HyperLink</a>, and <a class=\"wikicontent\" href=\"Wiki.jsp?page=ThisToo\">ThisToo</a>.",
            translate(src));
    }

    /**
     * Two mixed links on same line.
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testHyperlinksCC5()
            throws Exception
    {
        newPage("HyperLink");
        newPage("ThisToo");

        String src = "This should be a [HyperLink], and ThisToo.";

        assertEquals("This should be a <a class=\"wikicontent\" href=\"Wiki.jsp?page=HyperLink\">HyperLink</a>, and <a class=\"wikicontent\" href=\"Wiki.jsp?page=ThisToo\">ThisToo</a>.",
            translate(src));
    }

    /**
     * Closing tags only.
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testHyperlinksCC6()
            throws Exception
    {
        newPage("HyperLink");
        newPage("ThisToo");

        String src = "] This ] should be a HyperLink], and ThisToo.";

        assertEquals("] This ] should be a <a class=\"wikicontent\" href=\"Wiki.jsp?page=HyperLink\">HyperLink</a>], and <a class=\"wikicontent\" href=\"Wiki.jsp?page=ThisToo\">ThisToo</a>.",
            translate(src));
    }

    /**
     * First and last words on line.
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testHyperlinksCCFirstAndLast()
            throws Exception
    {
        newPage("HyperLink");
        newPage("ThisToo");

        String src = "HyperLink, and ThisToo";

        assertEquals("<a class=\"wikicontent\" href=\"Wiki.jsp?page=HyperLink\">HyperLink</a>, and <a class=\"wikicontent\" href=\"Wiki.jsp?page=ThisToo\">ThisToo</a>",
            translate(src));
    }

    /**
     * Hyperlinks inside URIs.
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testHyperlinksCCURLs()
            throws Exception
    {
        String src = "http://www.foo.bar/ANewHope/";

        // System.out.println("EX:"+translate(src));
        assertEquals("<a class=\"external\" href=\"http://www.foo.bar/ANewHope/\">http://www.foo.bar/ANewHope/</a>", translate(src));
    }

    /**
     * Hyperlinks inside URIs.
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testHyperlinksCCURLs2()
            throws Exception
    {
        String src = "mailto:foo@bar.com";

        // System.out.println("EX:"+translate(src));
        assertEquals("<a class=\"external\" href=\"mailto:foo@bar.com\">mailto:foo@bar.com</a>", translate(src));
    }

    /**
     * Hyperlinks inside URIs.
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testHyperlinksCCURLs3()
            throws Exception
    {
        String src = "This should be a link: http://www.foo.bar/ANewHope/.  Is it?";

        // System.out.println("EX:"+translate(src));
        assertEquals("This should be a link: <a class=\"external\" href=\"http://www.foo.bar/ANewHope/\">http://www.foo.bar/ANewHope/</a>.  Is it?",
            translate(src));
    }

    /**
     * Hyperlinks in brackets.
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testHyperlinksCCURLs4()
            throws Exception
    {
        String src = "This should be a link: (http://www.foo.bar/ANewHope/)  Is it?";

        // System.out.println("EX:"+translate(src));
        assertEquals("This should be a link: (<a class=\"external\" href=\"http://www.foo.bar/ANewHope/\">http://www.foo.bar/ANewHope/</a>)  Is it?",
            translate(src));
    }

    /**
     * Hyperlinks end line.
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testHyperlinksCCURLs5()
            throws Exception
    {
        String src = "This should be a link: http://www.foo.bar/ANewHope/\nIs it?";

        // System.out.println("EX:"+translate(src));
        assertEquals("This should be a link: <a class=\"external\" href=\"http://www.foo.bar/ANewHope/\">http://www.foo.bar/ANewHope/</a>\nIs it?",
            translate(src));
    }

    /**
     * Hyperlinks with odd chars.
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testHyperlinksCCURLs6()
            throws Exception
    {
        String src = "This should not be a link: http://''some.server''/wiki/Wiki.jsp\nIs it?";

        // System.out.println("EX:"+translate(src));
        assertEquals("This should not be a link: http://<i>some.server</i>/wiki/Wiki.jsp\nIs it?", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testHyperlinksCCNegated()
            throws Exception
    {
        String src = "This should not be a ~HyperLink.";

        assertEquals("This should not be a HyperLink.", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testHyperlinksCCNegated2()
            throws Exception
    {
        String src = "~HyperLinks should not be matched.";

        assertEquals("HyperLinks should not be matched.", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testCCLinkInList()
            throws Exception
    {
        newPage("HyperLink");

        String src = "*HyperLink";

        assertEquals("<ul>\n<li><a class=\"wikicontent\" href=\"Wiki.jsp?page=HyperLink\">HyperLink</a></li>\n</ul>\n",
            translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testCCLinkBold()
            throws Exception
    {
        newPage("BoldHyperLink");

        String src = "__BoldHyperLink__";

        assertEquals("<b><a class=\"wikicontent\" href=\"Wiki.jsp?page=BoldHyperLink\">BoldHyperLink</a></b>", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testCCLinkBold2()
            throws Exception
    {
        newPage("HyperLink");

        String src = "Let's see, if a bold __HyperLink__ is correct?";

        assertEquals("Let's see, if a bold <b><a class=\"wikicontent\" href=\"Wiki.jsp?page=HyperLink\">HyperLink</a></b> is correct?",
            translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testCCLinkItalic()
            throws Exception
    {
        newPage("ItalicHyperLink");

        String src = "''ItalicHyperLink''";

        assertEquals("<i><a class=\"wikicontent\" href=\"Wiki.jsp?page=ItalicHyperLink\">ItalicHyperLink</a></i>", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testCCLinkWithPunctuation()
            throws Exception
    {
        newPage("HyperLink");

        String src = "Test. Punctuation. HyperLink.";

        assertEquals("Test. Punctuation. <a class=\"wikicontent\" href=\"Wiki.jsp?page=HyperLink\">HyperLink</a>.", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testCCLinkWithPunctuation2()
            throws Exception
    {
        newPage("HyperLink");
        newPage("ThisToo");

        String src = "Punctuations: HyperLink,ThisToo.";

        assertEquals("Punctuations: <a class=\"wikicontent\" href=\"Wiki.jsp?page=HyperLink\">HyperLink</a>,<a class=\"wikicontent\" href=\"Wiki.jsp?page=ThisToo\">ThisToo</a>.",
            translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testCCLinkWithScandics()
            throws Exception
    {
        newPage("\u00c4itiSy\u00f6\u00d6ljy\u00e4");

        String src = "Onko t\u00e4m\u00e4 hyperlinkki: \u00c4itiSy\u00f6\u00d6ljy\u00e4?";

        assertEquals("Onko t\u00e4m\u00e4 hyperlinkki: <a class=\"wikicontent\" href=\"Wiki.jsp?page=%C4itiSy%F6%D6ljy%E4\">\u00c4itiSy\u00f6\u00d6ljy\u00e4</a>?",
            translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testHyperlinksExt()
            throws Exception
    {
        String src = "This should be a [http://www.regex.fi/]";

        assertEquals("This should be a <a class=\"external\" href=\"http://www.regex.fi/\">http://www.regex.fi/</a>", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testHyperlinksExt2()
            throws Exception
    {
        String src = "This should be a [link|http://www.regex.fi/]";

        assertEquals("This should be a <a class=\"external\" href=\"http://www.regex.fi/\">link</a>", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testHyperlinksExtNofollow()
            throws Exception
    {
        String src = "This should be a [link|http://www.regex.fi/]";

        assertEquals("This should be a <a class=\"external\" rel=\"nofollow\" href=\"http://www.regex.fi/\">link</a>",
            translate_nofollow(src));
    }

    //
    //  Testing various odds and ends about hyperlink matching.
    //
    public void testHyperlinksPluralMatch()
            throws Exception
    {
        String src = "This should be a [HyperLinks]";

        newPage("HyperLink");

        assertEquals("This should be a <a class=\"wikicontent\" href=\"Wiki.jsp?page=HyperLink\">HyperLinks</a>", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testHyperlinksPluralMatch2()
            throws Exception
    {
        String src = "This should be a [HyperLinks]";

        assertEquals("This should be a <a class=\"wikicontent\" title=\"Create 'HyperLinks'\" href=\"Edit.jsp?page=HyperLinks\">HyperLinks</a>",
            translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testHyperlinksPluralMatch3()
            throws Exception
    {
        String src = "This should be a [HyperLink]";

        newPage("HyperLinks");

        assertEquals("This should be a <a class=\"wikicontent\" href=\"Wiki.jsp?page=HyperLinks\">HyperLink</a>", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testHyperlinkJS1()
            throws Exception
    {
        String src = "This should be a [link|http://www.haxored.com/\" onMouseOver=\"alert('Hahhaa');\"]";

        assertEquals("This should be a <a class=\"external\" href=\"http://www.haxored.com/&quot; onMouseOver=&quot;alert('Hahhaa');&quot;\">link</a>",
            translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testHyperlinksInterWiki1()
            throws Exception
    {
        String src = "This should be a [link|eyeWiki:HyperLink]";

        assertEquals("This should be a <a class=\"interwiki\" href=\"http://jspwiki.org/wiki/HyperLink\">link</a>", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testAttachmentLink()
            throws Exception
    {
        newPage("Test");

        Attachment att = new Attachment("Test", "TestAtt.txt");
        att.setAuthor("FirstPost");
        testEngine.getAttachmentManager().storeAttachment(att, testEngine.makeAttachmentFile());

        String src = "This should be an [attachment link|Test/TestAtt.txt]";

        assertEquals("This should be an <a class=\"attachment\" href=\"attach/Test/TestAtt.txt\">attachment link</a>"
            + "<a class=\"wikicontent\" href=\"PageInfo.jsp?page=Test/TestAtt.txt\"><img src=\"images/attachment_small.png\" border=\"0\" alt=\"(info)\"/></a>",
            translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testAttachmentLink2()
            throws Exception
    {
        Configuration conf = TestEngine.getConfiguration();
        conf.setProperty("eyewiki.encoding", "ISO-8859-1");

        //TODO
        TestEngine testEngine2 = new TestEngine(conf);

        testEngine2.saveText("Test", "foo ");

        Attachment att = new Attachment("Test", "TestAtt.txt");
        att.setAuthor("FirstPost");

        testEngine2.getAttachmentManager().storeAttachment(att, testEngine.makeAttachmentFile());

        String src = "This should be an [attachment link|Test/TestAtt.txt]";

        assertEquals("This should be an <a class=\"attachment\" href=\"attach/Test/TestAtt.txt\">attachment link</a>"
            + "<a class=\"wikicontent\" href=\"PageInfo.jsp?page=Test/TestAtt.txt\"><img src=\"images/attachment_small.png\" border=\"0\" alt=\"(info)\"/></a>",
            translate(testEngine2, src));
    }

    /**
     * Are attachments parsed correctly also when using gappy text?
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testAttachmentLink3()
            throws Exception
    {
        Configuration conf = TestEngine.getConfiguration();
        TestEngine testEngine2 = new TestEngine(conf);

        testEngine2.saveText("TestPage", "foo ");

        Attachment att = new Attachment("TestPage", "TestAtt.txt");
        att.setAuthor("FirstPost");

        testEngine2.getAttachmentManager().storeAttachment(att, testEngine.makeAttachmentFile());

        String src = "[Test page/TestAtt.txt]";

        assertEquals("<a class=\"attachment\" href=\"attach/TestPage/TestAtt.txt\">Test page/TestAtt.txt</a>"
            + "<a class=\"wikicontent\" href=\"PageInfo.jsp?page=TestPage/TestAtt.txt\"><img src=\"images/attachment_small.png\" border=\"0\" alt=\"(info)\"/></a>",
            translate(testEngine2, src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testAttachmentLink4()
            throws Exception
    {
        Configuration conf = TestEngine.getConfiguration();
        TestEngine testEngine2 = new TestEngine(conf);

        testEngine2.saveText("TestPage", "foo ");

        Attachment att = new Attachment("TestPage", "TestAtt.txt");
        att.setAuthor("FirstPost");

        testEngine2.getAttachmentManager().storeAttachment(att, testEngine.makeAttachmentFile());

        String src = "[" + testEngine2.beautifyTitle("TestPage/TestAtt.txt") + "]";

        assertEquals("<a class=\"attachment\" href=\"attach/TestPage/TestAtt.txt\">TestPage/TestAtt.txt</a>"
            + "<a class=\"wikicontent\" href=\"PageInfo.jsp?page=TestPage/TestAtt.txt\"><img src=\"images/attachment_small.png\" border=\"0\" alt=\"(info)\"/></a>",
            translate(testEngine2, src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testNoHyperlink()
            throws Exception
    {
        newPage("HyperLink");

        String src = "This should not be a [[HyperLink]";

        assertEquals("This should not be a [HyperLink]", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testNoHyperlink2()
            throws Exception
    {
        String src = "This should not be a [[[[HyperLink]";

        assertEquals("This should not be a [[[HyperLink]", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testNoHyperlink3()
            throws Exception
    {
        String src = "[[HyperLink], and this [[Neither].";

        assertEquals("[HyperLink], and this [Neither].", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testNoPlugin()
            throws Exception
    {
        String src = "There is [[{NoPlugin}] here.";

        assertEquals("There is [{NoPlugin}] here.", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testErroneousHyperlink()
            throws Exception
    {
        String src = "What if this is the last char [";

        assertEquals("What if this is the last char ", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testErroneousHyperlink2()
            throws Exception
    {
        String src = "What if this is the last char [[";

        assertEquals("What if this is the last char [", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testExtraPagename1()
            throws Exception
    {
        String src = "Link [test_page]";

        newPage("Test_page");

        assertEquals("Link <a class=\"wikicontent\" href=\"Wiki.jsp?page=Test_page\">test_page</a>", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testExtraPagename2()
            throws Exception
    {
        String src = "Link [test.page]";

        newPage("Test.page");

        assertEquals("Link <a class=\"wikicontent\" href=\"Wiki.jsp?page=Test.page\">test.page</a>", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testExtraPagename3()
            throws Exception
    {
        String src = "Link [.testpage_]";

        newPage(".testpage_");

        assertEquals("Link <a class=\"wikicontent\" href=\"Wiki.jsp?page=.testpage_\">.testpage_</a>", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testInlineImages()
            throws Exception
    {
        String src = "Link [test|http://www.ecyrd.com/test.png]";

        assertEquals("Link <img src=\"http://www.ecyrd.com/test.png\" alt=\"test\" />", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testInlineImages2()
            throws Exception
    {
        String src = "Link [test|http://www.ecyrd.com/test.ppm]";

        assertEquals("Link <a class=\"external\" href=\"http://www.ecyrd.com/test.ppm\">test</a>", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testInlineImages3()
            throws Exception
    {
        String src = "Link [test|http://images.com/testi]";

        assertEquals("Link <img src=\"http://images.com/testi\" alt=\"test\" />", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testInlineImages4()
            throws Exception
    {
        String src = "Link [test|http://foobar.jpg]";

        assertEquals("Link <img src=\"http://foobar.jpg\" alt=\"test\" />", translate(src));
    }

    // No link text should be just embedded link.
    public void testInlineImagesLink2()
            throws Exception
    {
        String src = "Link [http://foobar.jpg]";

        assertEquals("Link <img src=\"http://foobar.jpg\" alt=\"http://foobar.jpg\" />", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testInlineImagesLink()
            throws Exception
    {
        String src = "Link [http://link.to/|http://foobar.jpg]";

        assertEquals("Link <a class=\"wikicontent\" href=\"http://link.to/\"><img src=\"http://foobar.jpg\" alt=\"http://link.to/\"/></a>",
            translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testInlineImagesLink3()
            throws Exception
    {
        String src = "Link [SandBox|http://foobar.jpg]";

        newPage("SandBox");

        assertEquals("Link <a class=\"wikicontent\" href=\"Wiki.jsp?page=SandBox\"><img src=\"http://foobar.jpg\" alt=\"SandBox\" /></a>",
            translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testScandicPagename1()
            throws Exception
    {
        String src = "Link [\u00C5\u00E4Test]";

        newPage("\u00C5\u00E4Test"); // FIXME: Should be capital

        assertEquals("Link <a class=\"wikicontent\" href=\"Wiki.jsp?page=%C5%E4Test\">\u00c5\u00e4Test</a>", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testParagraph()
            throws Exception
    {
        String src = "1\n\n2\n\n3";

        assertEquals("1\n<p>2\n</p>\n<p>3</p>\n", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testParagraph2()
            throws Exception
    {
        String src = "[WikiEtiquette]\r\n\r\n[Find page]";

        newPage("WikiEtiquette");
        newPage("FindPage");

        assertEquals("<a class=\"wikicontent\" href=\"Wiki.jsp?page=WikiEtiquette\">WikiEtiquette</a>\n"
            + "<p><a class=\"wikicontent\" href=\"Wiki.jsp?page=FindPage\">Find page</a></p>\n", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testParagraph3()
            throws Exception
    {
        String src = "\r\n\r\n!Testi\r\n\r\nFoo.";

        assertEquals("<p></p>\n<h4><a class=\"wikianchor\" name=\"section-testpage-Testi\" />Testi</h4>\n<p>Foo.</p>\n",
            translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testParagraph4()
            throws Exception
    {
        String src = "\r\n[Recent Changes]\\\\\r\n[WikiEtiquette]\r\n\r\n[Find pages|FindPage]\\\\\r\n[Unused pages|UnusedPages]";

        newPage("WikiEtiquette");
        newPage("RecentChanges");
        newPage("FindPage");
        newPage("UnusedPages");

        assertEquals("<p><a class=\"wikicontent\" href=\"Wiki.jsp?page=RecentChanges\">Recent Changes</a><br />\n"
            + "<a class=\"wikicontent\" href=\"Wiki.jsp?page=WikiEtiquette\">WikiEtiquette</a>\n</p>\n"
            + "<p><a class=\"wikicontent\" href=\"Wiki.jsp?page=FindPage\">Find pages</a><br />\n"
            + "<a class=\"wikicontent\" href=\"Wiki.jsp?page=UnusedPages\">Unused pages</a></p>\n", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testLinebreak()
            throws Exception
    {
        String src = "1\\\\2";

        assertEquals("1<br />2", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testLinebreakEscape()
            throws Exception
    {
        String src = "1~\\\\2";

        assertEquals("1\\\\2", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testLinebreakClear()
            throws Exception
    {
        String src = "1\\\\\\2";

        assertEquals("1<br clear=\"all\" />2", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testTT()
            throws Exception
    {
        String src = "1{{2345}}6";

        assertEquals("1<tt>2345</tt>6", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testTTAcrossLines()
            throws Exception
    {
        String src = "1{{\n2345\n}}6";

        assertEquals("1<tt>\n2345\n</tt>6", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testTTLinks()
            throws Exception
    {
        String src = "1{{\n2345\n[a link]\n}}6";

        newPage("ALink");

        assertEquals("1<tt>\n2345\n<a class=\"wikicontent\" href=\"Wiki.jsp?page=ALink\">a link</a>\n</tt>6", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testPre()
            throws Exception
    {
        String src = "1{{{2345}}}6";

        assertEquals("1<span style=\"font-family:monospace; whitespace:pre;\">2345</span>6", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testPreEscape()
            throws Exception
    {
        String src = "1~{{{2345}}}6";

        assertEquals("1{{{2345}}}6", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testPre2()
            throws Exception
    {
        String src = "1 {{{ {{{ 2345 }}} }}} 6";

        assertEquals("1 <span style=\"font-family:monospace; whitespace:pre;\"> {{{ 2345 </span> }}} 6", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testHTMLInPre()
            throws Exception
    {
        String src = "1\n{{{ <b> }}}";

        assertEquals("1\n<pre> &lt;b&gt; </pre>\n", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testCamelCaseInPre()
            throws Exception
    {
        String src = "1\n{{{ CamelCase }}}";

        assertEquals("1\n<pre> CamelCase </pre>\n", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testList1()
            throws Exception
    {
        String src = "A list:\n* One\n* Two\n* Three\n";

        assertEquals("A list:\n<ul>\n<li> One\n</li>\n<li> Two\n</li>\n<li> Three\n</li>\n</ul>\n", translate(src));
    }

    /**
     * Plain multi line testing:
     * <pre>
     * One
     *  continuing
     * Two
     * Three
     *  </pre>
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testMultilineList1()
            throws Exception
    {
        String src = "A list:\n* One\n continuing.\n* Two\n* Three\n";

        assertEquals("A list:\n<ul>\n<li> One\n continuing.\n</li>\n<li> Two\n</li>\n<li> Three\n</li>\n</ul>\n", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testMultilineList2()
            throws Exception
    {
        String src = "A list:\n* One\n continuing.\n* Two\n* Three\nShould be normal.";

        assertEquals("A list:\n<ul>\n<li> One\n continuing.\n</li>\n<li> Two\n</li>\n<li> Three\n</li>\n</ul>\nShould be normal.",
            translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testHTML()
            throws Exception
    {
        String src = "<b>Test</b>";

        assertEquals("&lt;b&gt;Test&lt;/b&gt;", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testHTML2()
            throws Exception
    {
        String src = "<p>";

        assertEquals("&lt;p&gt;", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testHTMLWhenAllowed()
            throws Exception
    {
        String src = "<p>";

        conf.setProperty(WikiProperties.PROP_ALLOWHTML, "true");
        testEngine = new TestEngine(conf);

        WikiContext context = new WikiContext(testEngine, new WikiPage(PAGE_NAME));

        Reader r = new TranslatorReader(context, new BufferedReader(new StringReader(src)));
        StringWriter out = new StringWriter();
        int c;

        while ((c = r.read()) != -1)
        {
            out.write(c);
        }

        assertEquals("<p>", out.toString());
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testQuotes()
            throws Exception
    {
        String src = "\"Test\"\"";

        assertEquals("&quot;Test&quot;&quot;", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testItalicAcrossLinebreak()
            throws Exception
    {
        String src = "''This is a\ntest.''";

        assertEquals("<i>This is a\ntest.</i>", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testBoldAcrossLinebreak()
            throws Exception
    {
        String src = "__This is a\ntest.__";

        assertEquals("<b>This is a\ntest.</b>", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testBoldItalic()
            throws Exception
    {
        String src = "__This ''is'' a test.__";

        assertEquals("<b>This <i>is</i> a test.</b>", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testFootnote1()
            throws Exception
    {
        String src = "Footnote[1]";

        assertEquals("Footnote<a class=\"footnoteref\" href=\"#ref-testpage-1\">[1]</a>", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testFootnote2()
            throws Exception
    {
        String src = "[#2356] Footnote.";

        assertEquals("<a class=\"footnote\" name=\"ref-testpage-2356\">[#2356]</a> Footnote.", translate(src));
    }

    /**
     * Check an reported error condition where empty list items could cause crashes
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testEmptySecondLevelList()
            throws Exception
    {
        String src = "A\n\n**\n\nB";

        // System.out.println(translate(src));
        assertEquals("A\n<ul>\n<li><ul>\n<li>\n</li>\n</ul>\n</li>\n</ul>\n<p>B</p>\n", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testEmptySecondLevelList2()
            throws Exception
    {
        String src = "A\n\n##\n\nB";

        // System.out.println(translate(src));
        assertEquals("A\n<ol>\n<li><ol>\n<li>\n</li>\n</ol>\n</li>\n</ol>\n<p>B</p>\n", translate(src));
    }

    /**
     * <pre>
     * Item A
     *   ##Numbered 1
     *   ##Numbered 2
     * Item B
     * </pre>
     * would come out as: &lt;ul&gt; &lt;li&gt;Item A &lt;/ul&gt; &lt;ol&gt; &lt;li&gt;Numbered 1 &lt;li&gt;Numbered 2 &lt;/ol&gt;
     * &lt;ul&gt; Item B &lt;/ul&gt; (by Mahlen Morris).
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testMixedList()
            throws Exception
    {
        String src = "*Item A\n##Numbered 1\n##Numbered 2\n*Item B\n";

        String result = translate(src);

        // Remove newlines for easier parsing.
        result = StringUtils.replace(result, "\n", "");

        assertEquals("<ul><li>Item A" + "<ol><li>Numbered 1</li>" + "<li>Numbered 2</li>" + "</ol></li>" + "<li>Item B</li>"
            + "</ul>", result);
    }

    /**
     * Like testMixedList() but the list types have been reversed.
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testMixedList2()
            throws Exception
    {
        String src = "#Item A\n**Numbered 1\n**Numbered 2\n#Item B\n";

        String result = translate(src);

        // Remove newlines for easier parsing.
        result = StringUtils.replace(result, "\n", "");

        assertEquals("<ol><li>Item A" + "<ul><li>Numbered 1</li>" + "<li>Numbered 2</li>" + "</ul></li>" + "<li>Item B</li>"
            + "</ol>", result);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testNestedList()
            throws Exception
    {
        String src = "*Item A\n**Numbered 1\n**Numbered 2\n*Item B\n";

        String result = translate(src);

        // Remove newlines for easier parsing.
        result = StringUtils.replace(result, "\n", "");

        assertEquals("<ul><li>Item A" + "<ul><li>Numbered 1</li>" + "<li>Numbered 2</li>" + "</ul></li>" + "<li>Item B</li>"
            + "</ul>", result);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testNestedList2()
            throws Exception
    {
        String src = "*Item A\n**Numbered 1\n**Numbered 2\n***Numbered3\n*Item B\n";

        String result = translate(src);

        // Remove newlines for easier parsing.
        result = StringUtils.replace(result, "\n", "");

        assertEquals("<ul><li>Item A" + "<ul><li>Numbered 1</li>" + "<li>Numbered 2" + "<ul><li>Numbered3</li>" + "</ul></li>"
            + "</ul></li>" + "<li>Item B</li>" + "</ul>", result);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testPluginInsert()
            throws Exception
    {
        String src = "[{INSERT SamplePlugin WHERE text=test}]";

        assertEquals("test", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testPluginNoInsert()
            throws Exception
    {
        String src = "[{SamplePlugin text=test}]";

        assertEquals("test", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testPluginInsertJS()
            throws Exception
    {
        String src = "Today: [{INSERT JavaScriptPlugin}] ''day''.";

        assertEquals("Today: <script language=\"JavaScript\"><!--\nfoo='';\n--></script>\n <i>day</i>.", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testShortPluginInsert()
            throws Exception
    {
        String src = "[{INSERT SamplePlugin WHERE text=test}]";

        assertEquals("test", translate(src));
    }

    /**
     * Test two plugins on same row.
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testShortPluginInsert2()
            throws Exception
    {
        String src = "[{INSERT SamplePlugin WHERE text=test}] [{INSERT SamplePlugin WHERE text=test2}]";

        assertEquals("test test2", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testPluginQuotedArgs()
            throws Exception
    {
        String src = "[{INSERT SamplePlugin WHERE text='test me now'}]";

        assertEquals("test me now", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testPluginDoublyQuotedArgs()
            throws Exception
    {
        String src = "[{INSERT SamplePlugin WHERE text='test \\'me too\\' now'}]";

        assertEquals("test 'me too' now", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testPluginQuotedArgs2()
            throws Exception
    {
        String src = "[{INSERT SamplePlugin WHERE text=foo}] [{INSERT SamplePlugin WHERE text='test \\'me too\\' now'}]";

        assertEquals("foo test 'me too' now", translate(src));
    }

    /**
     * Plugin output must not be parsed as Wiki text.
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testPluginWikiText()
            throws Exception
    {
        String src = "[{INSERT SamplePlugin WHERE text=PageContent}]";

        assertEquals("PageContent", translate(src));
    }

    /**
     * Nor should plugin input be interpreted as wiki text.
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testPluginWikiText2()
            throws Exception
    {
        String src = "[{INSERT SamplePlugin WHERE text='----'}]";

        assertEquals("----", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testMultilinePlugin1()
            throws Exception
    {
        String src = "Test [{INSERT SamplePlugin WHERE\n text=PageContent}]";

        assertEquals("Test PageContent", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testMultilinePluginBodyContent()
            throws Exception
    {
        String src = "Test [{INSERT SamplePlugin\ntext=PageContent\n\n123\n456\n}]";

        assertEquals("Test PageContent (123+456+)", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testMultilinePluginBodyContent2()
            throws Exception
    {
        String src = "Test [{INSERT SamplePlugin\ntext=PageContent\n\n\n123\n456\n}]";

        assertEquals("Test PageContent (+123+456+)", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testMultilinePluginBodyContent3()
            throws Exception
    {
        String src = "Test [{INSERT SamplePlugin\n\n123\n456\n}]";

        assertEquals("Test  (123+456+)", translate(src));
    }

    /**
     * Has an extra space after plugin name.
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testMultilinePluginBodyContent4()
            throws Exception
    {
        String src = "Test [{INSERT SamplePlugin \n\n123\n456\n}]";

        assertEquals("Test  (123+456+)", translate(src));
    }

    /**
     * Check that plugin end is correctly recognized.
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testPluginEnd()
            throws Exception
    {
        String src = "Test [{INSERT SamplePlugin text=']'}]";

        assertEquals("Test ]", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testPluginEnd2()
            throws Exception
    {
        String src = "Test [{INSERT SamplePlugin text='a[]+b'}]";

        assertEquals("Test a[]+b", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testPluginEnd3()
            throws Exception
    {
        String src = "Test [{INSERT SamplePlugin\n\na[]+b\n}]";

        assertEquals("Test  (a[]+b+)", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testPluginEnd4()
            throws Exception
    {
        String src = "Test [{INSERT SamplePlugin text='}'}]";

        assertEquals("Test }", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testPluginEnd5()
            throws Exception
    {
        String src = "Test [{INSERT SamplePlugin\n\na[]+b{}\nGlob.\n}]";

        assertEquals("Test  (a[]+b{}+Glob.+)", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testPluginEnd6()
            throws Exception
    {
        String src = "Test [{INSERT SamplePlugin\n\na[]+b{}\nGlob.\n}}]";

        assertEquals("Test  (a[]+b{}+Glob.+})", translate(src));
    }

    //  FIXME: I am not entirely certain if this is the right result
    //  Perhaps some sort of an error should be checked?
    public void testPluginNoEnd()
            throws Exception
    {
        String src = "Test [{INSERT SamplePlugin\n\na+b{}\nGlob.\n}";

        assertEquals("Test {INSERT SamplePlugin\n\na+b{}\nGlob.\n}", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testVariableInsert()
            throws Exception
    {
        String src = "[{$pagename}]";

        assertEquals(PAGE_NAME + "", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testTable1()
            throws Exception
    {
        String src = "|| heading || heading2 \n| Cell 1 | Cell 2 \n| Cell 3 | Cell 4\n\n";

        assertEquals("<table>\n" + "<tr><th> heading </th><th> heading2 </th></tr>\n"
            + "<tr><td> Cell 1 </td><td> Cell 2 </td></tr>\n" + "<tr><td> Cell 3 </td><td> Cell 4</td></tr>\n"
            + "</table>\n<p></p>\n", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testTable2()
            throws Exception
    {
        String src = "||heading||heading2\n|Cell 1| Cell 2\n| Cell 3 |Cell 4\n\n";

        assertEquals("<table>\n" + "<tr><th>heading</th><th>heading2</th></tr>\n" + "<tr><td>Cell 1</td><td> Cell 2</td></tr>\n"
            + "<tr><td> Cell 3 </td><td>Cell 4</td></tr>\n" + "</table>\n<p></p>\n", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testTable3()
            throws Exception
    {
        String src = "|Cell 1| Cell 2\n| Cell 3 |Cell 4\n\n";

        assertEquals("<table>\n" + "<tr><td>Cell 1</td><td> Cell 2</td></tr>\n" + "<tr><td> Cell 3 </td><td>Cell 4</td></tr>\n"
            + "</table>\n<p></p>\n", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testTableLink()
            throws Exception
    {
        String src = "|Cell 1| Cell 2\n|[Cell 3|ReallyALink]|Cell 4\n\n";

        newPage("ReallyALink");

        assertEquals("<table>\n" + "<tr><td>Cell 1</td><td> Cell 2</td></tr>\n"
            + "<tr><td><a class=\"wikicontent\" href=\"Wiki.jsp?page=ReallyALink\">Cell 3</a></td><td>Cell 4</td></tr>\n"
            + "</table>\n<p></p>\n", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testTableLinkEscapedBar()
            throws Exception
    {
        String src = "|Cell 1| Cell~| 2\n|[Cell 3|ReallyALink]|Cell 4\n\n";

        newPage("ReallyALink");

        assertEquals("<table>\n" + "<tr><td>Cell 1</td><td> Cell| 2</td></tr>\n"
            + "<tr><td><a class=\"wikicontent\" href=\"Wiki.jsp?page=ReallyALink\">Cell 3</a></td><td>Cell 4</td></tr>\n"
            + "</table>\n<p></p>\n", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testDescription()
            throws Exception
    {
        String src = ";:Foo";

        assertEquals("<dl>\n<dt></dt><dd>Foo</dd>\n</dl>", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testDescription2()
            throws Exception
    {
        String src = ";Bar:Foo";

        assertEquals("<dl>\n<dt>Bar</dt><dd>Foo</dd>\n</dl>", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testDescription3()
            throws Exception
    {
        String src = ";:";

        assertEquals("<dl>\n<dt></dt><dd></dd>\n</dl>", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testDescription4()
            throws Exception
    {
        String src = ";Bar:Foo :-)";

        assertEquals("<dl>\n<dt>Bar</dt><dd>Foo :-)</dd>\n</dl>", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testRuler()
            throws Exception
    {
        String src = "----";

        assertEquals("<hr />", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testRulerCombo()
            throws Exception
    {
        String src = "----Foo";

        assertEquals("<hr />Foo", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testShortRuler1()
            throws Exception
    {
        String src = "-";

        assertEquals("-", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testShortRuler2()
            throws Exception
    {
        String src = "--";

        assertEquals("--", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testShortRuler3()
            throws Exception
    {
        String src = "---";

        assertEquals("---", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testLongRuler()
            throws Exception
    {
        String src = "------";

        assertEquals("<hr />", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testHeading1()
            throws Exception
    {
        String src = "!Hello\nThis is a test";

        assertEquals("<h4><a class=\"wikianchor\" name=\"section-testpage-Hello\" />Hello</h4>\nThis is a test", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testHeading2()
            throws Exception
    {
        String src = "!!Hello, testing 1, 2, 3";

        assertEquals("<h3><a class=\"wikianchor\" name=\"section-testpage-HelloTesting123\" />Hello, testing 1, 2, 3</h3>",
            translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testHeading3()
            throws Exception
    {
        String src = "!!!Hello there, how are you doing?";

        assertEquals("<h2><a class=\"wikianchor\" name=\"section-testpage-HelloThereHowAreYouDoing\" />Hello there, how are you doing?</h2>",
            translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testHeadingHyperlinks()
            throws Exception
    {
        String src = "!!![Hello]";

        assertEquals("<h2><a class=\"wikianchor\" name=\"section-testpage-Hello\" /><a class=\"wikicontent\" title=\"Create 'Hello'\" href=\"Edit.jsp?page=Hello\">Hello</a></h2>",
            translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testHeadingHyperlinks2()
            throws Exception
    {
        String src = "!!![Hello|http://www.google.com/]";

        assertEquals("<h2><a class=\"wikianchor\" name=\"section-testpage-Hello\" /><a class=\"external\" href=\"http://www.google.com/\">Hello</a></h2>",
            translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testHeadingHyperlinks3()
            throws Exception
    {
        String src = "![Hello|http://www.google.com/?p=a&c=d]";

        assertEquals("<h4><a class=\"wikianchor\" name=\"section-testpage-Hello\" /><a class=\"external\" href=\"http://www.google.com/?p=a&amp;c=d\">Hello</a></h4>",
            translate(src));
    }

    /**
     * in 2.0.0, this one throws OutofMemoryError.
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testBrokenPageText()
            throws Exception
    {
        String translation = translate(brokenPageText);

        assertNotNull(translation);
    }

    /**
     * Shortened version of the previous one.
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testBrokenPageTextShort()
            throws Exception
    {
        String src = "{{{\ncode.}}\n";

        assertEquals("<pre>\ncode.}}\n</pre>\n", translate(src));
    }

    /**
     * Shortened version of the previous one.
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testBrokenPageTextShort2()
            throws Exception
    {
        String src = "{{{\ncode.}\n";

        assertEquals("<pre>\ncode.}\n</pre>\n", translate(src));
    }

    /*
     *    public void testSimpleACL1()
     *        throws Exception
     *    {
     *        String src = "Foobar.[{ALLOW view JanneJalkanen}]";
     *
     *        WikiPage p = new WikiPage(PAGE_NAME);
     *
     *        String res = translate(p, src);
     *
     *        assertEquals("Page text", "Foobar.", res);
     *
     *        AccessControlList acl = p.getAcl();
     *
     *        UserProfile prof = new UserProfile();
     *        prof.setName("JanneJalkanen");
     *
     *        assertTrue( "no read", acl.checkPermission(prof, new ViewPermission()));
     *        assertFalse("has edit", acl.checkPermission(prof, new EditPermission()));
     *    }
     *
     *    public void testSimpleACL2()
     *        throws Exception
     *    {
     *        String src = "Foobar.[{ALLOW view JanneJalkanen}]\n"+
     *                     "[{DENY view ErikBunn, SuloVilen}]\n"+
     *                     "[{ALLOW edit JanneJalkanen, SuloVilen}]";
     *
     *        WikiPage p = new WikiPage(PAGE_NAME);
     *
     *        String res = translate(p, src);
     *
     *        assertEquals("Page text", "Foobar.\n\n", res);
     *
     *        AccessControlList acl = p.getAcl();
     *
     *        UserProfile prof = new UserProfile();
     *        prof.setName("JanneJalkanen");
     *
     *        assertTrue("no read for JJ", acl.checkPermission(prof, new ViewPermission()));
     *        assertTrue("no edit for JJ", acl.checkPermission(prof, new EditPermission()));
     *
     *        prof.setName("ErikBunn");
     *
     *        assertFalse( "read for EB", acl.checkPermission(prof, new ViewPermission()));
     *        assertFalse("has edit for EB", acl.checkPermission(prof, new EditPermission()));
     *
     *        prof.setName("SuloVilen");
     *
     *        assertFalse("read for SV", acl.checkPermission(prof, new ViewPermission()));
     *        assertTrue("no edit for SV", acl.checkPermission(prof, new EditPermission()));
     *    }
     */

    /**
     * ACL tests.
     *
     * @param l DOCUMENT ME!
     * @param name DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private boolean containsGroup(List l, String name)
    {
        for (Iterator i = l.iterator(); i.hasNext();)
        {
            String group = (String) i.next();

            if (group.equals(name))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Metadata tests
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testSet1()
            throws Exception
    {
        String src = "Foobar.[{SET name=foo}]";

        WikiPage p = new WikiPage(PAGE_NAME);

        String res = translate(p, src);

        assertEquals("Page text", "Foobar.", res);

        assertEquals("foo", p.getAttribute("name"));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testSet2()
            throws Exception
    {
        String src = "Foobar.[{SET name = foo}]";

        WikiPage p = new WikiPage(PAGE_NAME);

        String res = translate(p, src);

        assertEquals("Page text", "Foobar.", res);

        assertEquals("foo", p.getAttribute("name"));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testSet3()
            throws Exception
    {
        String src = "Foobar.[{SET name= Janne Jalkanen}]";

        WikiPage p = new WikiPage(PAGE_NAME);

        String res = translate(p, src);

        assertEquals("Page text", "Foobar.", res);

        assertEquals("Janne Jalkanen", p.getAttribute("name"));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testSet4()
            throws Exception
    {
        String src = "Foobar.[{SET name='Janne Jalkanen'}][{SET too='{$name}'}]";

        WikiPage p = new WikiPage(PAGE_NAME);

        String res = translate(p, src);

        assertEquals("Page text", "Foobar.", res);

        assertEquals("Janne Jalkanen", p.getAttribute("name"));
        assertEquals("Janne Jalkanen", p.getAttribute("too"));
    }

    /**
     * Test collection of links.
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testCollectingLinks()
            throws Exception
    {
        LinkCollector coll = new LinkCollector();
        String src = "[Test]";
        WikiContext context = new WikiContext(testEngine, new WikiPage(PAGE_NAME));

        TranslatorReader r = new TranslatorReader(context, new BufferedReader(new StringReader(src)));
        r.addLocalLinkHook(coll);
        r.addExternalLinkHook(coll);
        r.addAttachmentLinkHook(coll);

        StringWriter out = new StringWriter();

        FileUtil.copyContents(r, out);

        Collection links = coll.getLinks();

        assertEquals("no links found", 1, links.size());
        assertEquals("wrong link", "Test", links.iterator().next());
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testCollectingLinks2()
            throws Exception
    {
        LinkCollector coll = new LinkCollector();
        String src = "[" + PAGE_NAME + "/Test.txt]";
        WikiContext context = new WikiContext(testEngine, new WikiPage(PAGE_NAME));

        TranslatorReader r = new TranslatorReader(context, new BufferedReader(new StringReader(src)));
        r.addLocalLinkHook(coll);
        r.addExternalLinkHook(coll);
        r.addAttachmentLinkHook(coll);

        StringWriter out = new StringWriter();

        FileUtil.copyContents(r, out);

        Collection links = coll.getLinks();

        assertEquals("no links found", 1, links.size());
        assertEquals("wrong link", PAGE_NAME + "/Test.txt", links.iterator().next());
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testCollectingLinksAttachment()
            throws Exception
    {
        // First, make an attachment.
        Attachment att = new Attachment(PAGE_NAME, "TestAtt.txt");
        att.setAuthor("FirstPost");
        testEngine.getAttachmentManager().storeAttachment(att, testEngine.makeAttachmentFile());

        LinkCollector coll = new LinkCollector();
        LinkCollector coll_others = new LinkCollector();

        String src = "[TestAtt.txt]";
        WikiContext context = new WikiContext(testEngine, new WikiPage(PAGE_NAME));

        TranslatorReader r = new TranslatorReader(context, new BufferedReader(new StringReader(src)));
        r.addLocalLinkHook(coll_others);
        r.addExternalLinkHook(coll_others);
        r.addAttachmentLinkHook(coll);

        StringWriter out = new StringWriter();

        FileUtil.copyContents(r, out);

        Collection links = coll.getLinks();

        assertEquals("no links found", 1, links.size());
        assertEquals("wrong link", PAGE_NAME + "/TestAtt.txt", links.iterator().next());

        assertEquals("wrong links found", 0, coll_others.getLinks().size());
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testDivStyle1()
            throws Exception
    {
        String src = "%%foo\ntest\n%%\n";

        assertEquals("<div class=\"foo\">\ntest\n</div>\n", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testDivStyle2()
            throws Exception
    {
        String src = "%%(foo:bar;)\ntest\n%%\n";

        assertEquals("<div style=\"foo:bar;\">\ntest\n</div>\n", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testSpanStyle1()
            throws Exception
    {
        String src = "%%foo test%%\n";

        assertEquals("<span class=\"foo\">test</span>\n", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testSpanStyle2()
            throws Exception
    {
        String src = "%%(foo:bar;)test%%\n";

        assertEquals("<span style=\"foo:bar;\">test</span>\n", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testSpanStyle3()
            throws Exception
    {
        String src = "Johan %%(foo:bar;)test%%\n";

        assertEquals("Johan <span style=\"foo:bar;\">test</span>\n", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testSpanNested()
            throws Exception
    {
        String src = "Johan %%(color: rgb(1,2,3);)test%%\n";

        assertEquals("Johan <span style=\"color: rgb(1,2,3);\">test</span>\n", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testSpanStyleTable()
            throws Exception
    {
        String src = "|%%(foo:bar;)test%%|no test\n";

        assertEquals("<table>\n<tr><td><span style=\"foo:bar;\">test</span></td><td>no test</td></tr>\n</table>\n", translate(src));
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Test suite()
    {
        return new TestSuite(TranslatorReaderTest.class);
    }
}
