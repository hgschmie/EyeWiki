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

import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;

import de.softwareforge.eyewiki.providers.ProviderException;

/**
 * Includes an another JSP page, making sure that we actually pass the WikiContext correctly.
 *
 * @author Janne Jalkanen
 *
 * @since 2.0
 */

// FIXME: Perhaps unnecessary?
public class IncludeTag
        extends WikiTagBase
{
    /** DOCUMENT ME! */
    protected String m_page;

    /**
     * DOCUMENT ME!
     *
     * @param page DOCUMENT ME!
     */
    public void setPage(String page)
    {
        m_page = page;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getPage()
    {
        return m_page;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     * @throws ProviderException DOCUMENT ME!
     */
    public final int doWikiStartTag()
            throws IOException, ProviderException
    {
        // WikiEngine engine = m_wikiContext.getEngine();
        return SKIP_BODY;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws JspException DOCUMENT ME!
     */
    public final int doEndTag()
            throws JspException
    {
        try
        {
            String page = m_wikiContext.getEngine().getTemplateManager().findJSP(pageContext, m_wikiContext.getTemplate(), m_page);
            pageContext.include(page);
        }
        catch (ServletException e)
        {
            log.warn("Including failed, got a servlet exception from sub-page. " + "Rethrowing the exception to the JSP engine.", e);
            throw new JspException(e.getMessage());
        }
        catch (IOException e)
        {
            log.warn("I/O exception - probably the connection was broken. " + "Rethrowing the exception to the JSP engine.", e);
            throw new JspException(e.getMessage());
        }

        return EVAL_PAGE;
    }
}
