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

package com.ecyrd.jspwiki.variable;

import org.picocontainer.Startable;

import com.ecyrd.jspwiki.WikiContext;
import com.ecyrd.jspwiki.manager.VariableManager;

public class ConstantVariables
        implements Startable
{
    private final VariableManager variableManager;

    public ConstantVariables(final VariableManager variableManager)
    {
        this.variableManager = variableManager;
    }

    public synchronized void start()
    {
        variableManager.registerVariable(VariableManager.VAR_ERROR, new ConstantVariable(""));
        variableManager.registerVariable(VariableManager.VAR_MSG, new ConstantVariable(""));
    }

    public synchronized void stop()
    {
        // GNDN
    }

    private class ConstantVariable
            extends AbstractSimpleVariable
            implements WikiVariable
    {
        private final String value;

        private ConstantVariable(final String value)
        {
            this.value = value;
        }

        public String getValue(WikiContext context, String variableName)
        {
            return value;
        }
    }
}

