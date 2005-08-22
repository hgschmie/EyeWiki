package de.softwareforge.eyewiki.tags;

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

import java.io.IOException;
import java.io.Serializable;

import java.util.LinkedList;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;

import de.softwareforge.eyewiki.WikiConstants;
import de.softwareforge.eyewiki.WikiContext;

/**
 * Implement a "breadcrumb" (most recently visited) trail.  This tag can be added to any view jsp page. Separate breadcrumb trails
 * are not tracked across multiple browser windows.<br>
 * The optional attributes are:
 *
 * <p>
 * <b>maxpages</b>, the number of pages to store, 10 by default<br>
 * <b>separator</b>, the separator string to use between pages, " | " by default<br>
 * </p>
 *
 * <p>
 * This class is implemented by storing a breadcrumb trail, which is a fixed size queue, into a session variable "breadCrumbTrail".
 * This queue is displayed as a series of links separated by a separator character.
 * </p>
 *
 * @author <a href="mailto:ken@kenliu.net">Ken Liu</a>
 */
public class BreadcrumbsTag
        extends WikiTagBase
{
    /** DOCUMENT ME! */
    private static final String BREADCRUMBTRAIL_KEY = "breadCrumbTrail";

    /** DOCUMENT ME! */
    private int m_maxQueueSize = 11;

    /** DOCUMENT ME! */
    private String m_separator = " > ";

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getMaxpages()
    {
        return m_maxQueueSize;
    }

    /**
     * DOCUMENT ME!
     *
     * @param maxpages DOCUMENT ME!
     */
    public void setMaxpages(int maxpages)
    {
        m_maxQueueSize = maxpages + 1;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getSeparator()
    {
        return m_separator;
    }

    /**
     * DOCUMENT ME!
     *
     * @param separator DOCUMENT ME!
     */
    public void setSeparator(String separator)
    {
        m_separator = separator;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    public final int doWikiStartTag()
            throws IOException
    {
        HttpSession session = pageContext.getSession();
        FixedQueue trail = (FixedQueue) session.getAttribute(BREADCRUMBTRAIL_KEY);

        String page = m_wikiContext.getPage().getName();

        if (trail == null)
        {
            trail = new FixedQueue(m_maxQueueSize);
        }

        if (m_wikiContext.getRequestContext().equals(WikiContext.VIEW))
        {
            if (trail.isEmpty())
            {
                trail.push(page);
            }
            else
            {
                //
                // Don't add the page to the queue if the page was just refreshed
                //
                if (!((String) trail.getLast()).equals(page))
                {
                    trail.push(page);

                    if (log.isDebugEnabled())
                    {
                        log.debug("added page: " + page);
                    }
                }

                log.debug("didn't add page because of refresh");
            }
        }

        session.setAttribute(BREADCRUMBTRAIL_KEY, trail);

        //
        //  Print out the breadcrumb trail
        //
        // FIXME: this code would be much simpler if we could just output the [pagename] and then use the
        // wiki engine to output the appropriate wikilink
        JspWriter out = pageContext.getOut();
        int queueSize = trail.size();
        String linkclass = WikiConstants.CSS_LINK_BREADCRUMBS;
        String curPage = null;

        for (int i = 0; i < (queueSize - 1); i++)
        {
            curPage = (String) trail.get(i);

            //FIXME: I can't figure out how to detect the appropriate jsp page to put here, so I hard coded Wiki.jsp
            //This breaks when you view an attachment metadata page
            out.print("<a class=\"" + linkclass + "\" href=\"" + m_wikiContext.getViewURL(curPage) + "\">" + curPage + "</a>");

            if (i < (queueSize - 2))
            {
                out.print(m_separator);
            }
        }

        return SKIP_BODY;
    }

    /**
     * Extends the LinkedList class to provide a fixed-size queue implementation
     */
    private static class FixedQueue
            extends LinkedList
            implements Serializable
    {
        /** DOCUMENT ME! */
        private int m_size;

        /**
         * Creates a new FixedQueue object.
         *
         * @param size DOCUMENT ME!
         */
        FixedQueue(int size)
        {
            m_size = size;
        }

        /**
         * DOCUMENT ME!
         *
         * @param o DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        Object push(Object o)
        {
            add(o);

            if (size() > m_size)
            {
                return removeFirst();
            }
            else
            {
                return null;
            }
        }
    }
}
