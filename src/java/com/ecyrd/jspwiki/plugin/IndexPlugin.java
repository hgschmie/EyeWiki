/*
  JSPWiki - a JSP-based WikiWiki clone.

  Copyright (C) 2002 Janne Jalkanen (Janne.Jalkanen@iki.fi)

  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation; either version 2.1 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
package com.ecyrd.jspwiki.plugin;

import java.io.StringWriter;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.apache.oro.text.GlobCompiler;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Matcher;

import com.ecyrd.jspwiki.TranslatorReader;
import com.ecyrd.jspwiki.WikiConstants;
import com.ecyrd.jspwiki.WikiContext;
import com.ecyrd.jspwiki.WikiEngine;
import com.ecyrd.jspwiki.WikiPage;
import com.ecyrd.jspwiki.manager.PageManager;
import com.ecyrd.jspwiki.providers.ProviderException;
import com.ecyrd.jspwiki.util.TextUtil;


/**
 * Builds an index of all pages.
 *
 * <P>
 * Parameters
 * </p>
 *
 * <UL>
 * <li>
 * itemsPerLine: How many items should be allowed per line before break. If set to zero (the
 * default), will not write breaks.
 * </li>
 * <li>
 * include: Include only these pages.
 * </li>
 * <li>
 * exclude: Exclude with this pattern.
 * </li>
 * </ul>
 *
 *
 * @author Alain Ravet
 * @author Janne Jalkanen
 *
 * @since 1.9.9
 */
public class IndexPlugin
        implements WikiPlugin
{
    /** DOCUMENT ME! */
    private static final Logger log = Logger.getLogger(IndexPlugin.class);

    /** DOCUMENT ME! */
    public static final String INITIALS_COLOR = "red";

    /** DOCUMENT ME! */
    private static final int DEFAULT_ITEMS_PER_LINE = 0;

    /** DOCUMENT ME! */
    private static final String PARAM_ITEMS_PER_LINE = "itemsPerLine";

    /** DOCUMENT ME! */
    private static final String PARAM_INCLUDE = "include";

    /** DOCUMENT ME! */
    private static final String PARAM_EXCLUDE = "exclude";

    /** DOCUMENT ME! */
    private int m_currentNofPagesOnLine = 0;

    /** DOCUMENT ME! */
    private int m_itemsPerLine;

    /** DOCUMENT ME! */
    protected String m_previousPageFirstLetter = "";

    /** DOCUMENT ME! */
    protected StringWriter m_bodyPart = new StringWriter();

    /** DOCUMENT ME! */
    protected StringWriter m_headerPart = new StringWriter();

    /** DOCUMENT ME! */
    private Pattern m_includePattern;

    /** DOCUMENT ME! */
    private Pattern m_excludePattern;

    /**
     * DOCUMENT ME!
     *
     * @param i_context DOCUMENT ME!
     * @param i_params DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws PluginException DOCUMENT ME!
     */
    public String execute(WikiContext i_context, Map i_params)
            throws PluginException
    {
        //
        //  Parse arguments and create patterns.
        //
        PatternCompiler compiler = new GlobCompiler();
        m_itemsPerLine =
                TextUtil.parseIntParameter(
                        (String) i_params.get(PARAM_ITEMS_PER_LINE), DEFAULT_ITEMS_PER_LINE);

        try
        {
            String ptrn = (String) i_params.get(PARAM_INCLUDE);

            if (ptrn == null)
            {
                ptrn = "*";
            }

            m_includePattern = compiler.compile(ptrn);

            ptrn = (String) i_params.get(PARAM_EXCLUDE);

            if (ptrn == null)
            {
                ptrn = "";
            }

            m_excludePattern = compiler.compile(ptrn);
        }
        catch (MalformedPatternException e)
        {
            throw new PluginException("Illegal pattern detected."); // FIXME, make a proper error.
        }

        //
        //  Get pages, then sort.
        //
        final Collection allPages = getAllPagesSortedByName(i_context);
        final TranslatorReader linkProcessor =
                new TranslatorReader(i_context, new java.io.StringReader(""));

        //
        //  Build the page.
        //
        buildIndexPageHeaderAndBody(i_context, allPages, linkProcessor);

        StringBuffer res = new StringBuffer();

        return res.append("<h3 class=\"")
                .append(WikiConstants.CSS_WIKICONTENT)
                .append("\">\n")
                .append(m_headerPart.toString())
                .append("</h3>\n")
                .append(m_bodyPart.toString())
                .append("\n")
                .toString();
    }

    private void buildIndexPageHeaderAndBody(
            WikiContext context, final Collection i_allPages, final TranslatorReader i_linkProcessor)
    {
        PatternMatcher matcher = new Perl5Matcher();

        for (Iterator i = i_allPages.iterator(); i.hasNext();)
        {
            WikiPage curPage = (WikiPage) i.next();

            if (matcher.matches(curPage.getName(), m_includePattern))
            {
                if (!matcher.matches(curPage.getName(), m_excludePattern))
                {
                    ++m_currentNofPagesOnLine;

                    String pageNameFirstLetter = curPage.getName().substring(0, 1).toUpperCase();

                    if (!m_previousPageFirstLetter.equals(pageNameFirstLetter))
                    {
                        addLetterToIndexHeader(pageNameFirstLetter);
                        addLetterHeader(pageNameFirstLetter);

                        m_currentNofPagesOnLine = 1;
                        m_previousPageFirstLetter = pageNameFirstLetter;
                    }

                    addPageToIndex(context, curPage, i_linkProcessor);
                    breakLineIfTooLong();
                }
            }
        } // for
    }

    /**
     * Gets all pages, then sorts them.
     *
     * @param i_context DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    static Collection getAllPagesSortedByName(WikiContext i_context)
    {
        final WikiEngine engine = i_context.getEngine();

        final PageManager pageManager = engine.getPageManager();

        if (pageManager == null)
        {
            return null;
        }

        Collection result =
                new TreeSet(
                        new Comparator()
                        {
                            public int compare(Object o1, Object o2)
                            {
                                if ((o1 == null) || (o2 == null))
                                {
                                    return 0;
                                }

                                WikiPage page1 = (WikiPage) o1;
                                WikiPage page2 = (WikiPage) o2;

                                return page1.getName().compareTo(page2.getName());
                            }
                        });

        try
        {
            Collection allPages = pageManager.getAllPages();
            result.addAll(allPages);
        }
        catch (ProviderException e)
        {
            log.fatal("PageProvider is unable to list pages: ", e);
        }

        return result;
    }

    private void addLetterToIndexHeader(final String i_firstLetter)
    {
        final boolean noLetterYetInTheIndex = !"".equals(m_previousPageFirstLetter);

        if (noLetterYetInTheIndex)
        {
            m_headerPart.write(" - ");
        }

        StringBuffer sb = new StringBuffer("<a class=\"")
                .append(WikiConstants.CSS_LINK_INDEX)
                .append("\" href=\"#")
                .append(i_firstLetter)
                .append("\">")
                .append(i_firstLetter)
                .append("</a>");

        m_headerPart.write(sb.toString());
    }

    private void addLetterHeader(final String i_firstLetter)
    {
        StringBuffer sb = new StringBuffer("<h4 class=\"")
                .append(WikiConstants.CSS_WIKICONTENT)
                .append("\"><a class=\"")
                .append(WikiConstants.CSS_ANCHOR)
                .append("\" name=\"")
                .append(i_firstLetter)
                .append("\">")
                .append(i_firstLetter)
                .append("</a></h4>\n");

        m_bodyPart.write(sb.toString());
    }

    /**
     * DOCUMENT ME!
     *
     * @param context DOCUMENT ME!
     * @param i_curPage DOCUMENT ME!
     * @param i_linkProcessor DOCUMENT ME!
     */
    protected void addPageToIndex(
            WikiContext context, WikiPage i_curPage, final TranslatorReader i_linkProcessor)
    {
        final boolean notFirstPageOnLine = 2 <= m_currentNofPagesOnLine;

        if (notFirstPageOnLine)
        {
            m_bodyPart.write(",&nbsp; ");
        }

        m_bodyPart.write(
                i_linkProcessor.makeLink(
                        TranslatorReader.READ, i_curPage.getName(),
                        context.getEngine().beautifyTitleNoBreak(i_curPage.getName())));
    }

    /**
     * DOCUMENT ME!
     */
    protected void breakLineIfTooLong()
    {
        final boolean limitReached = (m_itemsPerLine == m_currentNofPagesOnLine);

        if (limitReached)
        {
            m_bodyPart.write("<br />\n");
            m_currentNofPagesOnLine = 0;
        }
    }
}
