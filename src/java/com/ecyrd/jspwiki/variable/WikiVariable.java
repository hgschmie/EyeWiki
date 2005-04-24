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

public interface WikiVariable
{
    /** This evaluator should run as early as possible */
    int MAX_PRIORITY = 1000;

    /** This evaluator should run on normal priority */
    int NORMAL_PRIORITY = 0;

    /** This evaluator should run as late as possible */
    int MIN_PRIORITY = -1000;

    /**
     * Returns the value for this variable evaluation.
     *
     * Non-evaluating variables don't need to check the variable
     * name, they are only called with their registered name.
     */
    String getValue(WikiContext context, String variableName)
            throws Exception;

    /**
     * Evaluators can return a priority where they want to be evaluated.
     */
    int getPriority();

    /**
     * Should this variable / evaluator be visible in a possible list-out?
     */
    boolean isVisible();

    /**
     * The human readable name of this variable
     */
    String getName();
}
