package de.softwareforge.eyewiki.forms;

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

import java.io.Serializable;

import java.util.HashMap;
import java.util.Map;

/**
 * Container for carrying HTTP FORM information between WikiPlugin invocations in the Session.
 *
 * @author ebu
 */
public class FormInfo
        implements Serializable
{
    /** DOCUMENT ME! */
    public static final int EXECUTED = 1;

    /** DOCUMENT ME! */
    public static final int OK = 0;

    /** DOCUMENT ME! */
    public static final int ERROR = -1;

    /** DOCUMENT ME! */
    private int status;

    /** DOCUMENT ME! */
    private boolean hide;

    /** DOCUMENT ME! */
    private String action;

    /** DOCUMENT ME! */
    private String name;

    /** DOCUMENT ME! */
    private String handler;

    /** DOCUMENT ME! */
    private String result;

    /** DOCUMENT ME! */
    private String error;

    //private PluginParameters submission;

    /** DOCUMENT ME! */
    private Map submission;

    /**
     * Creates a new FormInfo object.
     */
    public FormInfo()
    {
        status = OK;
    }

    /**
     * DOCUMENT ME!
     *
     * @param val DOCUMENT ME!
     */
    public void setStatus(int val)
    {
        status = val;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getStatus()
    {
        return (status);
    }

    /**
     * DOCUMENT ME!
     *
     * @param val DOCUMENT ME!
     */
    public void setHide(boolean val)
    {
        hide = val;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean hide()
    {
        return (hide);
    }

    /**
     * DOCUMENT ME!
     *
     * @param val DOCUMENT ME!
     */
    public void setAction(String val)
    {
        action = val;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getAction()
    {
        return (action);
    }

    /**
     * DOCUMENT ME!
     *
     * @param val DOCUMENT ME!
     */
    public void setName(String val)
    {
        name = val;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getName()
    {
        return (name);
    }

    /**
     * DOCUMENT ME!
     *
     * @param val DOCUMENT ME!
     */
    public void setHandler(String val)
    {
        handler = val;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getHandler()
    {
        return (handler);
    }

    /**
     * DOCUMENT ME!
     *
     * @param val DOCUMENT ME!
     */
    public void setResult(String val)
    {
        result = val;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getResult()
    {
        return (result);
    }

    /**
     * DOCUMENT ME!
     *
     * @param val DOCUMENT ME!
     */
    public void setError(String val)
    {
        error = val;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getError()
    {
        return (error);
    }

    /**
     * Copies the given values into the handler parameter map using Map.putAll().
     *
     * @param val parameter name-value pairs for a Form handler WikiPlugin
     */
    public void setSubmission(Map val)
    {
        submission = new HashMap();
        submission.putAll(val);
    }

    /**
     * Adds the given values into the handler parameter map.
     *
     * @param val parameter name-value pairs for a Form handler WikiPlugin
     */
    public void addSubmission(Map val)
    {
        if (submission == null)
        {
            submission = new HashMap();
        }

        submission.putAll(val);
    }

    /**
     * Returns parameter name-value pairs for a Form handler WikiPlugin. The names are those of Form input fields, and the values
     * whatever the user selected in the form. The FormSet plugin can also be used to provide initial values.
     *
     * @return Handler parameter name-value pairs.
     */
    public Map getSubmission()
    {
        return (submission);
    }
}
