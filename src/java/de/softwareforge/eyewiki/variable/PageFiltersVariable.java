package de.softwareforge.eyewiki.variable;

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

import java.util.Iterator;
import java.util.List;

import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.filters.FilterManager;
import de.softwareforge.eyewiki.manager.VariableManager;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Id$
 */
public class PageFiltersVariable
        extends AbstractVariable
        implements WikiVariable
{
    /** DOCUMENT ME! */
    private final FilterManager filterManager;

    /** DOCUMENT ME! */
    private final VariableManager variableManager;

    /**
     * Creates a new PageFiltersVariable object.
     *
     * @param variableManager DOCUMENT ME!
     * @param filterManager DOCUMENT ME!
     */
    public PageFiltersVariable(final VariableManager variableManager, final FilterManager filterManager)
    {
        this.filterManager = filterManager;
        this.variableManager = variableManager;
    }

    /**
     * DOCUMENT ME!
     */
    public synchronized void start()
    {
        variableManager.registerVariable("pagefilters", this);
    }

    /**
     * DOCUMENT ME!
     *
     * @param context DOCUMENT ME!
     * @param variableName DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public String getValue(WikiContext context, String variableName)
            throws Exception
    {
        List filters = filterManager.getVisibleFilterList();
        StringBuffer sb = new StringBuffer();

        for (Iterator i = filters.iterator(); i.hasNext();)
        {
            String f = i.next().getClass().getName();

            if (sb.length() > 0)
            {
                sb.append(", ");
            }

            sb.append(f);
        }

        return sb.toString();
    }
}
