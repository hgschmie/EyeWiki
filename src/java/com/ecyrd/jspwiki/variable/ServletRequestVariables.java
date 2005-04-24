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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.ecyrd.jspwiki.WikiContext;
import com.ecyrd.jspwiki.exception.NoSuchVariableException;
import com.ecyrd.jspwiki.manager.VariableManager;

public class ServletRequestVariables
        extends AbstractVariable
        implements WikiVariable
{
    private final VariableManager variableManager;

    public ServletRequestVariables(VariableManager variableManager)
    {
        this.variableManager = variableManager;
    }

    public synchronized void start()
    {
        // Hardcoded sequence of the evaluators
        variableManager.registerEvaluator(this, MIN_PRIORITY - 3);
    }

    public String getValue(WikiContext context, String varName)
            throws Exception
    {
        //  Well, I guess it wasn't a final straw.  We also allow
        //  variables from the session.
        //

        HttpServletRequest req = context.getHttpRequest();

        if (req != null)
        {
            HttpSession session = req.getSession();

            Object attribute = session.getAttribute(varName);

            if (attribute != null)
            {
                return String.valueOf(attribute);
            }
        }

        throw new NoSuchVariableException("");
    }
}
