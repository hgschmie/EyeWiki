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

import de.softwareforge.eyewiki.providers.ProviderException;

/**
 * Includes body, if the request context matches.
 *
 * @author Janne Jalkanen
 *
 * @since 2.0
 */
public class CheckRequestContextTag
        extends WikiTagBase
{
    /** DOCUMENT ME! */
    private String m_context;

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getContext()
    {
        return m_context;
    }

    /**
     * DOCUMENT ME!
     *
     * @param arg DOCUMENT ME!
     */
    public void setContext(String arg)
    {
        m_context = arg;
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
        if (m_wikiContext.getRequestContext().equalsIgnoreCase(getContext()))
        {
            return EVAL_BODY_INCLUDE;
        }

        return SKIP_BODY;
    }
}
