package de.softwareforge.eyewiki.filters;


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
import org.apache.commons.configuration.Configuration;

import de.softwareforge.eyewiki.WikiContext;

/**
 * Provides a base implementation of a PageFilter.  None of the methods do anything, so it is a good idea for you to extend from
 * this class and implement only methods that you need.
 *
 * @author Janne Jalkanen
 */
public abstract class BasicPageFilter
        implements PageFilter
{
    /**
     * Force subclasses to implement a c'tor with a properties object
     *
     * @param conf DOCUMENT ME!
     */
    public BasicPageFilter(final Configuration conf)
    {
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract boolean isVisible();

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract int getPriority();

    /**
     * DOCUMENT ME!
     *
     * @param wikiContext DOCUMENT ME!
     * @param content DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws FilterException DOCUMENT ME!
     */
    public String preTranslate(WikiContext wikiContext, String content)
            throws FilterException
    {
        return content;
    }

    /**
     * DOCUMENT ME!
     *
     * @param wikiContext DOCUMENT ME!
     * @param htmlContent DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws FilterException DOCUMENT ME!
     */
    public String postTranslate(WikiContext wikiContext, String htmlContent)
            throws FilterException
    {
        return htmlContent;
    }

    /**
     * DOCUMENT ME!
     *
     * @param wikiContext DOCUMENT ME!
     * @param content DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws FilterException DOCUMENT ME!
     */
    public String preSave(WikiContext wikiContext, String content)
            throws FilterException
    {
        return content;
    }

    /**
     * DOCUMENT ME!
     *
     * @param wikiContext DOCUMENT ME!
     * @param content DOCUMENT ME!
     *
     * @throws FilterException DOCUMENT ME!
     */
    public void postSave(WikiContext wikiContext, String content)
            throws FilterException
    {
    }
}
