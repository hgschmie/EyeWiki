package de.softwareforge.eyewiki.plugin;

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

import java.util.Map;

import de.softwareforge.eyewiki.WikiContext;

/**
 * Provides a page-specific counter.
 *
 * <P>
 * Parameters
 *
 * <UL>
 * <li>
 * name - Name of the counter.  Optional.
 * </li>
 * </ul>
 *
 * Stores a variable in the WikiContext called "counter", with the name of the optionally attached. For example:<BR> If name is
 * "thispage", then the variable name is called "counter-thispage".
 * </p>
 *
 * @author Janne Jalkanen
 *
 * @since 1.9.30
 */
public class Counter
        implements WikiPlugin
{
    /** DOCUMENT ME! */
    static final String VARIABLE_NAME = "counter";

    /**
     * DOCUMENT ME!
     *
     * @param context DOCUMENT ME!
     * @param params DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws PluginException DOCUMENT ME!
     */
    public String execute(WikiContext context, Map params)
            throws PluginException
    {
        //
        //  First, determine which kind of name we use to store in
        //  the WikiContext.
        //
        String countername = (String) params.get("name");

        if (countername == null)
        {
            countername = VARIABLE_NAME;
        }
        else
        {
            countername = VARIABLE_NAME + "-" + countername;
        }

        //
        //  Fetch, increment, and store back.
        //
        Integer val = (Integer) context.getVariable(countername);

        if (val == null)
        {
            val = new Integer(0);
        }

        val = new Integer(val.intValue() + 1);

        context.setVariable(countername, val);

        return val.toString();
    }
}
