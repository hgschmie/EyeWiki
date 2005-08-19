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
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.log4j.Logger;

import de.softwareforge.eyewiki.WikiContext;

/**
 * Base class for eyeWiki tags.  You do not necessarily have to derive from this class, since this does some initialization.
 *
 * <P>
 * This tag is only useful if you're having an "empty" tag, with no body content.
 * </p>
 *
 * @author Janne Jalkanen
 *
 * @since 2.0
 */
public abstract class WikiTagBase
        extends TagSupport
{
    /** DOCUMENT ME! */
    public static final String ATTR_CONTEXT = "eyewiki.context";

    /** DOCUMENT ME! */
    protected Logger log = Logger.getLogger(this.getClass());

    /** DOCUMENT ME! */
    protected WikiContext m_wikiContext;

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws JspException DOCUMENT ME!
     */
    public int doStartTag()
            throws JspException
    {
        try
        {
            m_wikiContext = (WikiContext) pageContext.getAttribute(ATTR_CONTEXT, PageContext.REQUEST_SCOPE);

            if (m_wikiContext == null)
            {
                throw new JspException("WikiContext may not be NULL - serious internal problem!");
            }

            return doWikiStartTag();
        }
        catch (Exception e)
        {
            log.error("Tag failed", e);
            throw new JspException("Tag failed, check logs: " + e.getMessage());
        }
    }

    /**
     * This method is allowed to do pretty much whatever he wants. We then catch all mistakes.
     *
     * @return DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public abstract int doWikiStartTag()
            throws Exception;

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws JspException DOCUMENT ME!
     */
    public int doEndTag()
            throws JspException
    {
        return EVAL_PAGE;
    }
}
