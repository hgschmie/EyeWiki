/*
   JSPWiki - a JSP-based WikiWiki clone.

   Copyright (C) 2001-2002 Janne Jalkanen (Janne.Jalkanen@iki.fi)

   This program is free software; you can redistribute it and/or modify
   it under the terms of the GNU Lesser General Public License as published by
   the Free Software Foundation; either version 2.1 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General Public License
   along with this program; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
package de.softwareforge.eyewiki.diff;

import org.apache.commons.configuration.Configuration;

import de.softwareforge.eyewiki.WikiEngine;


/**
 * This is a diff provider for the RSS Feed using the Traditional Provider as its base.
 *
 * @author Janne Jalkanen
 * @author Erik Bunn
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 */
public class RssTraditionalDiffProvider
        extends TraditionalDiffProvider
        implements DiffProvider
{
    /**
     * Creates a new RssTraditionalDiffProvider object.
     */
    public RssTraditionalDiffProvider(WikiEngine engine, Configuration conf)
    {
        super(engine, conf);
        diffAdd = "<font color=\"#99FF99\">";
        diffRem = "<font color=\"#FF9933\">";
        diffComment = "<font color=\"white\">";
        diffClose = "</font>\n";

        diffPrefix = "";
        diffPostfix = "\n";
    }
}
