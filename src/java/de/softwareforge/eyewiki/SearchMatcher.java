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
import java.io.StringReader;

/**
 * SearchMatcher performs the task of matching a search query to a page's contents. This utility class is isolated to simplify
 * WikiPageProvider implementations and to offer an easy target for upgrades. The upcoming(?) TranslatorReader rewrite will
 * presumably invalidate this, among other things.
 *
 * @author ebu at ecyrd dot com
 *
 * @since 2.1.5
 */
public class SearchMatcher
{
    /** DOCUMENT ME! */
    private QueryItem [] m_queries;

    /**
     * Creates a new SearchMatcher object.
     *
     * @param queries DOCUMENT ME!
     */
    public SearchMatcher(QueryItem [] queries)
    {
        m_queries = queries;
    }

    /**
     * Compares the page content, available through the given stream, to the query items of this matcher. Returns a search result
     * object describing the quality of the match.
     *
     * <p>
     * This method would benefit of regexps (1.4) and streaming. FIXME!
     * </p>
     *
     * @param wikiname DOCUMENT ME!
     * @param pageText DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    public SearchResult matchPageContent(String wikiname, String pageText)
            throws IOException
    {
        if (m_queries == null)
        {
            return (null);
        }

        int [] scores = new int[m_queries.length];
        BufferedReader in = new BufferedReader(new StringReader(pageText));
        String line = null;

        while ((line = in.readLine()) != null)
        {
            line = line.toLowerCase();

            for (int j = 0; j < m_queries.length; j++)
            {
                int index = -1;

                while ((index = line.indexOf(m_queries[j].getWord(), index + 1)) != -1)
                {
                    if (m_queries[j].getType() != QueryItem.FORBIDDEN)
                    {
                        scores[j]++; // Mark, found this word n times
                    }
                    else
                    {
                        // Found something that was forbidden.
                        return (null);
                    }
                }
            }
        }

        //
        //  Check that we have all required words.
        //
        int totalscore = 0;

        for (int j = 0; j < scores.length; j++)
        {
            // Give five points for each occurrence
            // of the word in the wiki name.
            if ((wikiname.toLowerCase().indexOf(m_queries[j].getWord()) != -1) && (m_queries[j].getType() != QueryItem.FORBIDDEN))
            {
                scores[j] += 5;
            }

            //  Filter out pages if the search word is marked 'required'
            //  but they have no score.
            if ((m_queries[j].getType() == QueryItem.REQUIRED) && (scores[j] == 0))
            {
                return (null);
            }

            //
            //  Count the total score for this page.
            //
            totalscore += scores[j];
        }

        if (totalscore > 0)
        {
            return (new SearchResultImpl(wikiname, totalscore));
        }

        return (null);
    }

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Revision$
     */
    public static class SearchResultImpl
            implements SearchResult
    {
        /** DOCUMENT ME! */
        private int m_score;

        /** DOCUMENT ME! */
        private WikiPage m_page;

        /**
         * Creates a new SearchResultImpl object.
         *
         * @param name DOCUMENT ME!
         * @param score DOCUMENT ME!
         */
        public SearchResultImpl(String name, int score)
        {
            m_page = new WikiPage(name);
            m_score = score;
        }

        /**
         * DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        public WikiPage getPage()
        {
            return m_page;
        }

        /**
         * DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        public int getScore()
        {
            return m_score;
        }
    }
}
