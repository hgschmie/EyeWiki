/*
    JSPWiki - a JSP-based WikiWiki clone.

    Copyright (C) 2001 Janne Jalkanen (Janne.Jalkanen@iki.fi)

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

package de.softwareforge.eyewiki.variable;

import java.util.NoSuchElementException;

import org.apache.commons.configuration.Configuration;


import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiProperties;
import de.softwareforge.eyewiki.exception.NoSuchVariableException;
import de.softwareforge.eyewiki.manager.VariableManager;

public class ConfigurationVariables
        extends AbstractVariable
        implements WikiVariable
{
    private final Configuration conf;
    private final VariableManager variableManager;

    public ConfigurationVariables(VariableManager variableManager, Configuration conf)
    {
        this.conf = conf;
        this.variableManager = variableManager;
    }

    public synchronized void start()
    {
        // Hardcoded sequence of the evaluators
        variableManager.registerEvaluator(this, MIN_PRIORITY - 1);
    }

    public String getValue(WikiContext context, String varName)
            throws Exception
    {
        // Next-to-final straw: attempt to fetch using property name
        // We don't allow fetching any other properties than those starting
        // with "jspwiki.".  I know my own code, but I can't vouch for bugs
        // in other people's code... :-)
        if (varName.startsWith(WikiProperties.PROP_PREFIX))
        {
            try
            {
                return conf.getString(varName);
            }
            catch (NoSuchElementException nsee)
            {
                // Does nothing, just continue searching...
            }
        }

        throw new NoSuchVariableException("");
    }
}
