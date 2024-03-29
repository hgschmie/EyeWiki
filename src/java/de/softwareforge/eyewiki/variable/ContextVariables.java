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
import de.softwareforge.eyewiki.auth.UserProfile;
import de.softwareforge.eyewiki.exception.NoSuchVariableException;
import de.softwareforge.eyewiki.manager.VariableManager;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Id$
 */
public class ContextVariables
        extends AbstractVariable
        implements WikiVariable
{
    /** DOCUMENT ME! */
    private final VariableManager variableManager;

    /**
     * Creates a new ContextVariables object.
     *
     * @param variableManager DOCUMENT ME!
     */
    public ContextVariables(VariableManager variableManager)
    {
        this.variableManager = variableManager;
    }

    /**
     * DOCUMENT ME!
     */
    public synchronized void start()
    {
        // Hardcoded sequence of the evaluators
        variableManager.registerEvaluator(this, MIN_PRIORITY - 0);

        variableManager.registerVariable("loginstatus", new LoginStatus());
        variableManager.registerVariable("username", new UserName());
        variableManager.registerVariable("requestcontext", new RequestContext());
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

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Id$
     */
    private static class LoginStatus
            extends AbstractSimpleVariable
            implements WikiVariable
    {
        /**
         * DOCUMENT ME!
         *
         * @param context DOCUMENT ME!
         * @param varName DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         *
         * @throws Exception DOCUMENT ME!
         */
        public String getValue(WikiContext context, String varName)
                throws Exception
        {
            UserProfile wup = context.getCurrentUser();

            int status = (wup != null) ? wup.getLoginStatus() : UserProfile.NONE;

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

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Id$
     */
    private static class UserName
            extends AbstractSimpleVariable
            implements WikiVariable
    {
        /**
         * DOCUMENT ME!
         *
         * @param context DOCUMENT ME!
         * @param varName DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         *
         * @throws Exception DOCUMENT ME!
         */
        public String getValue(WikiContext context, String varName)
                throws Exception
        {
            UserProfile wup = context.getCurrentUser();

            return (wup != null) ? wup.getName() : "not logged in";
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Id$
     */
    private static class RequestContext
            extends AbstractSimpleVariable
            implements WikiVariable
    {
        /**
         * DOCUMENT ME!
         *
         * @param context DOCUMENT ME!
         * @param varName DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         *
         * @throws Exception DOCUMENT ME!
         */
        public String getValue(WikiContext context, String varName)
                throws Exception
        {
            return context.getRequestContext();
        }
    }
}
