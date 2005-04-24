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

import com.ecyrd.jspwiki.WikiContext;
import com.ecyrd.jspwiki.auth.UserProfile;
import com.ecyrd.jspwiki.exception.NoSuchVariableException;
import com.ecyrd.jspwiki.manager.VariableManager;

public class ContextVariables
        extends AbstractVariable
        implements WikiVariable
{
    private final VariableManager variableManager;

    public ContextVariables(VariableManager variableManager)
    {
        this.variableManager = variableManager;
    }

    public synchronized void start()
    {
        // Hardcoded sequence of the evaluators
        variableManager.registerEvaluator(this, MIN_PRIORITY - 0);

        variableManager.registerVariable("loginstatus", new LoginStatus());
        variableManager.registerVariable("username", new UserName());
        variableManager.registerVariable("requestcontext", new RequestContext());
    }

    public String getValue(WikiContext context, String varName)
            throws Exception
    {
        //
        // Check if such a context variable exists,
        // returning its string representation.
        //
        if ((context.getVariable(varName)) != null)
        {
            return context.getVariable(varName).toString();
        }
        throw new NoSuchVariableException("");
    }

    private static class LoginStatus
            extends AbstractSimpleVariable
            implements WikiVariable
    {
        public String getValue(WikiContext context, String varName)
                throws Exception
        {
            UserProfile wup = context.getCurrentUser();

            int status = (wup != null)
                    ? wup.getLoginStatus()
                    : UserProfile.NONE;

            switch (status)
            {
            case UserProfile.NONE:
                return "unknown (not logged in)";
                
            case UserProfile.COOKIE:
                return "named (cookie)";

            case UserProfile.CONTAINER:
                return "validated (container)";
                
            case UserProfile.PASSWORD:
                return "validated (password)";
                
            default:
                return "ILLEGAL STATUS!";
            }
        }
    }

    private static class UserName
            extends AbstractSimpleVariable
            implements WikiVariable
    {
        public String getValue(WikiContext context, String varName)
                throws Exception
        {
            UserProfile wup = context.getCurrentUser();

            return (wup != null)
                    ? wup.getName()
                    : "not logged in";
        }
    }

    private static class RequestContext
            extends AbstractSimpleVariable
            implements WikiVariable
    {
        public String getValue(WikiContext context, String varName)
                throws Exception
        {
            return context.getRequestContext();
        }
    }
}
