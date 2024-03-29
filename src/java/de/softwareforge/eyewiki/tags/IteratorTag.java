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
import java.util.Iterator;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.log4j.Logger;

import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiPage;

/**
 * Iterates through tags.
 *
 * <P>
 * <B>Attributes</B>
 * </p>
 *
 * <UL>
 * <li>
 * list - a collection.
 * </li>
 * </ul>
 *
 *
 * @author Janne Jalkanen
 *
 * @since 2.0
 */
public abstract class IteratorTag
        extends BodyTagSupport
{
    /** DOCUMENT ME! */
    protected Logger log = Logger.getLogger(this.getClass());

    /** DOCUMENT ME! */
    protected String m_pageName;

    /** DOCUMENT ME! */
    protected Iterator m_iterator;

    /** DOCUMENT ME! */
    protected WikiContext m_wikiContext;

    /**
     * Sets the collection that is used to form the iteration.
     *
     * @param arg DOCUMENT ME!
     */
    public void setList(Collection arg)
    {
        m_iterator = arg.iterator();
    }

    /**
     * Sets the iterator directly that is used to form the iteration.
     */

    /*
     *    public void setList( Iterator arg )
     *    {
     *        m_iterator = arg;
     *    }
     */
    public void clearList()
    {
        m_iterator = null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int doStartTag()
    {
        m_wikiContext = (WikiContext) pageContext.getAttribute(WikiTagBase.ATTR_CONTEXT, PageContext.REQUEST_SCOPE);

        if (m_iterator == null)
        {
            return SKIP_BODY;
        }

        if (m_iterator.hasNext())
        {
            buildContext();
        }

        return EVAL_BODY_AGAIN;
    }

    /**
     * Arg, I hate globals.
     */
    private void buildContext()
    {
        //
        //  Build a clone of the current context
        //
        WikiContext context = (WikiContext) m_wikiContext.clone();
        context.setPage((WikiPage) m_iterator.next());

        //
        //  Push it to the iterator stack, and set the id.
        //
        pageContext.setAttribute(WikiTagBase.ATTR_CONTEXT, context, PageContext.REQUEST_SCOPE);
        pageContext.setAttribute(getId(), context.getPage());
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int doEndTag()
    {
        // Return back to the original.
        pageContext.setAttribute(WikiTagBase.ATTR_CONTEXT, m_wikiContext, PageContext.REQUEST_SCOPE);

        return EVAL_PAGE;
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

        if ((m_iterator != null) && m_iterator.hasNext())
        {
            buildContext();

            return EVAL_BODY_AGAIN;
        }
        else
        {
            return SKIP_BODY;
        }
    }
}
