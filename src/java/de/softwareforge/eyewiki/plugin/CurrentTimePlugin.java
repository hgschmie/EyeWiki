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

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;

import de.softwareforge.eyewiki.WikiContext;

/**
 * Just displays the current date and time. The time format is exactly like in the java.util.SimpleDateFormat class.
 *
 * @author Janne Jalkanen
 *
 * @see java.util.SimpleDateFormat
 * @since 1.7.8
 */
public class CurrentTimePlugin
        implements WikiPlugin
{
    /** DOCUMENT ME! */
    private static Logger log = Logger.getLogger(CurrentTimePlugin.class);

    /** DOCUMENT ME! */
    public static final String DEFAULT_FORMAT = "HH:mm:ss dd-MMM-yyyy zzzz";

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
        String formatString = (String) params.get("format");

        if (formatString == null)
        {
            formatString = DEFAULT_FORMAT;
        }

        if (log.isDebugEnabled())
        {
            log.debug("Date format string is: " + formatString);
        }

        try
        {
            SimpleDateFormat fmt = new SimpleDateFormat(formatString);

            Date d = new Date(); // Now.

            return fmt.format(d);
        }
        catch (IllegalArgumentException e)
        {
            throw new PluginException("You specified bad format: " + e.getMessage());
        }
    }
}
