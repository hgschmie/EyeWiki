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

import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.exception.NoSuchVariableException;
import de.softwareforge.eyewiki.manager.VariableManager;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Id$
 */
public class HttpParameterVariables
        extends AbstractVariable
        implements WikiVariable
{
    /** DOCUMENT ME! */
    private final VariableManager variableManager;

    /**
     * Creates a new HttpParameterVariables object.
     *
     * @param variableManager DOCUMENT ME!
     */
    public HttpParameterVariables(VariableManager variableManager)
    {
        this.variableManager = variableManager;
    }

    /**
     * DOCUMENT ME!
     */
    public synchronized void start()
    {
        // Hardcoded sequence of the evaluators
        variableManager.registerEvaluator(this, MIN_PRIORITY - 4);
    }

    /**
     * DOCUMENT ME!
     *
     * @param context DOCUMENT ME!
     * @param varName DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     * @throws NoSuchVariableException DOCUMENT ME!
     */
    public String getValue(WikiContext context, String varName)
            throws Exception
    {
        //  Well, I guess it wasn't a final straw.  We also allow
        //  variables from the request.
        //
        String res = context.getHttpParameter(varName);

        if (res != null)
        {
            return res;
        }

        throw new NoSuchVariableException("");
    }
}
