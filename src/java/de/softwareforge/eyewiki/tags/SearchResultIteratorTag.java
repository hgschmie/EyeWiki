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

import java.util.Collection;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import de.softwareforge.eyewiki.SearchResult;
import de.softwareforge.eyewiki.WikiContext;

/**
 * Iterates through Search result results.
 *
 * <P>
 * <B>Attributes</B>
 * </p>
 *
 * <UL>
 * <li>
 * max = how many search results should be shown.
 * </li>
 * </ul>
 *
 *
 * @author Janne Jalkanen
 *
 * @since 2.0
 */

// FIXME: Shares MUCH too much in common with IteratorTag.  Must refactor.
public class SearchResultIteratorTag
        extends IteratorTag
{
    /** DOCUMENT ME! */
    private int m_maxItems;

    /** DOCUMENT ME! */
    private int m_count = 0;

    /**
     * DOCUMENT ME!
     *
     * @param arg DOCUMENT ME!
     */
    public void setMaxItems(String arg)
    {
        m_maxItems = Integer.parseInt(arg);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public final int doStartTag()
    {
        //
        //  Do lazy eval if the search results have not been set.
        //
        if (m_iterator == null)
        {
            Collection searchresults = (Collection) pageContext.getAttribute("searchresults", PageContext.REQUEST_SCOPE);
            setList(searchresults);
        }

        m_count = 0;
        m_wikiContext = (WikiContext) pageContext.getAttribute(WikiTagBase.ATTR_CONTEXT, PageContext.REQUEST_SCOPE);

        return nextResult();
    }

    private int nextResult()
    {
        if ((m_iterator != null) && m_iterator.hasNext() && (m_count++ < m_maxItems))
        {
            SearchResult r = (SearchResult) m_iterator.next();

            WikiContext context = (WikiContext) m_wikiContext.clone();
            context.setPage(r.getPage());
            pageContext.setAttribute(WikiTagBase.ATTR_CONTEXT, context, PageContext.REQUEST_SCOPE);
            pageContext.setAttribute(getId(), r);

            return EVAL_BODY_AGAIN;
        }

        return SKIP_BODY;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int doAfterBody()
    {
        if (bodyContent != null)
        {
            try
            {
                JspWriter out = getPreviousOut();
                out.print(bodyContent.getString());
                bodyContent.clearBody();
            }
            catch (IOException e)
            {
                log.error("Unable to get inner tag text", e);

                // FIXME: throw something?
            }
        }

        return nextResult();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int doEndTag()
    {
        m_iterator = null;

        return super.doEndTag();
    }
}
