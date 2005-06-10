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


import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.manager.VariableManager;

public class InlinedImagesVariable
        extends AbstractVariable
        implements WikiVariable
{
    private final WikiEngine engine;
    private final VariableManager variableManager;

    public InlinedImagesVariable(final VariableManager variableManager, final WikiEngine engine)
    {
        this.engine = engine;
        this.variableManager = variableManager;
    }

    public synchronized void start()
    {
        variableManager.registerVariable("inlinedimages", this);
    }

    public String getValue(WikiContext context, String variableName)
            throws Exception
    {
            StringBuffer sb = new StringBuffer();
            for (
                Iterator i = engine.getAllInlinedImagePatterns().iterator();
                            i.hasNext();)
            {
                String ptrn = (String) i.next();
                sb.append(ptrn)
                        .append("<br />\n");
            }

            return sb.toString();
    }
}
