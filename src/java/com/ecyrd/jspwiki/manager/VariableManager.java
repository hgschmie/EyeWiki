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
package com.ecyrd.jspwiki.manager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ecyrd.jspwiki.WikiContext;
import com.ecyrd.jspwiki.WikiEngine;
import com.ecyrd.jspwiki.WikiProperties;
import com.ecyrd.jspwiki.exception.NoSuchVariableException;
import com.ecyrd.jspwiki.util.PriorityList;
import com.ecyrd.jspwiki.variable.WikiVariable;

import org.picocontainer.PicoContainer;
import org.picocontainer.Startable;
import org.picocontainer.defaults.ObjectReference;
import org.picocontainer.defaults.SimpleReference;


/**
 * Manages variables.  Variables are case-insensitive.  A list of all available variables is on a
 * Wiki page called "WikiVariables".
 *
 * @author Janne Jalkanen
 *
 * @since 1.9.20.
 */
public class VariableManager
        implements Startable
{
    /** DOCUMENT ME! */
    private static final Logger log = Logger.getLogger(VariableManager.class);

    /** DOCUMENT ME! */
    public static final String VAR_ERROR = "error";

    /** DOCUMENT ME! */
    public static final String VAR_MSG = "msg";

    /**
     * DOCUMENT ME!
     */
    protected final WikiEngine engine;

    /**
     * DOCUMENT ME!
     */
    protected final Configuration conf;

    /** The Container to manage the Variable Plugins */
    protected final PicoContainer variableContainer;

    /** Priorized list of Variable evaluators (catchall) */
    private final PriorityList evaluators = new PriorityList();

    /** Hashmap of directly registered Variables */
    private final Map variableMap = new HashMap();

    /**
     * Creates a new VariableManager object.
     *
     * @param engine DOCUMENT ME!
     * @param conf DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public VariableManager(final WikiEngine engine, final Configuration conf)
            throws Exception
    {
        this.engine = engine;
        this.conf = conf;

        ObjectReference parentRef = new SimpleReference();
        parentRef.set(engine.getComponentContainer());

        ObjectReference variableContainerRef = new SimpleReference();

        String wikiVariableFile =
            conf.getString(
                WikiProperties.PROP_VARIABLE_FILE, WikiProperties.PROP_VARIABLE_FILE_DEFAULT);
        engine.setupContainer(variableContainerRef, parentRef, wikiVariableFile);
        variableContainer = (PicoContainer) variableContainerRef.get();
    }

    /**
     * DOCUMENT ME!
     */
    public synchronized void start()
    {
        variableContainer.start();
    }

    /**
     * DOCUMENT ME!
     */
    public synchronized void stop()
    {
        variableContainer.stop();
    }

    /**
     * DOCUMENT ME!
     *
     * @param variableName DOCUMENT ME!
     * @param wikiVariable DOCUMENT ME!
     */
    public void registerVariable(final String variableName, final WikiVariable wikiVariable)
    {
        String varName = variableName.toLowerCase();

        if (variableMap.get(varName) != null)
        {
            throw new IllegalArgumentException("Already a variable registered for " + variableName);
        }

        variableMap.put(varName, wikiVariable);
    }

    /**
     * DOCUMENT ME!
     *
     * @param wikiEvaluator DOCUMENT ME!
     * @param priority DOCUMENT ME!
     */
    public void registerEvaluator(final WikiVariable wikiEvaluator, int priority)
    {
        if (evaluators.contains(wikiEvaluator))
        {
            throw new IllegalArgumentException(
                wikiEvaluator.getClass().getName() + " already as evaluator registered");
        }

        evaluators.add(wikiEvaluator, wikiEvaluator.getPriority());
    }

    /**
     * Returns true if the link is really command to insert a variable.
     * 
     * <P>
     * Currently we just check if the link starts with "{$".
     * </p>
     *
     * @param link DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static boolean isVariableLink(String link)
    {
        return link.startsWith("{$");
    }

    /**
     * Parses the link and finds a value.
     * 
     * <P>
     * A variable is inserted using the notation [{$variablename}].
     * </p>
     *
     * @param context DOCUMENT ME!
     * @param link DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws NoSuchVariableException If a variable is not known.
     * @throws IllegalArgumentException If the format is not valid (does not start with {$, is zero
     *         length, etc.)
     */
    public String parseAndGetValue(WikiContext context, String link)
            throws NoSuchVariableException
    {
        if (!link.startsWith("{$"))
        {
            throw new IllegalArgumentException("Link does not start with {$");
        }

        if (!link.endsWith("}"))
        {
            throw new IllegalArgumentException("Link does not end with }");
        }

        String varName = link.substring(2, link.length() - 1);

        return getValue(context, varName.trim());
    }

    /**
     * This method does in-place expansion of any variables.  However, the expansion is not done
     * twice, that is, a variable containing text $variable will not be expanded.
     * 
     * <P>
     * The variables should be in the same format ({$variablename} as in the web pages.
     * </p>
     *
     * @param context The WikiContext of the current page.
     * @param source The source string.
     *
     * @return DOCUMENT ME!
     */
    public String expandVariables(WikiContext context, String source)
    {
        StringBuffer result = new StringBuffer();

        int length = source.length();

        for (int i = 0; i < length; i++)
        {
            if (i < (length - 2))
            {
                if (source.charAt(i) == '{')
                {
                    if (source.charAt(i + 1) == '$')
                    {
                        int end = source.indexOf('}', i);

                        if (end != -1)
                        {
                            String varname = source.substring(i + 2, end);
                            String value;

                            try
                            {
                                value = getValue(context, varname);
                            }
                            catch (NoSuchVariableException e)
                            {
                                value = e.getMessage();
                            }
                            catch (IllegalArgumentException e)
                            {
                                value = e.getMessage();
                            }

                            result.append(value);
                            i = end;

                            continue;
                        }
                    }
                    else
                    {
                        result.append('{');
                    }

                    continue;
                }
            }

            result.append(source.charAt(i));
        }

        return result.toString();
    }

    /**
     * Returns a value of the named variable.
     *
     * @param context DOCUMENT ME!
     * @param varName Name of the variable.
     *
     * @return DOCUMENT ME!
     *
     * @throws NoSuchVariableException If a variable is not known.
     * @throws IllegalArgumentException If the name is somehow broken.
     */
    public String getValue(final WikiContext context, final String varName)
            throws NoSuchVariableException
    {
        if (StringUtils.isEmpty(varName))
        {
            throw new IllegalArgumentException("Illegal variable name:" + varName);
        }

        if (context == null)
        {
            throw new IllegalArgumentException("getValue() called with null context");
        }

        String name = varName.toLowerCase();
        WikiVariable variable = (WikiVariable) variableMap.get(name);

        try
        {
            if (variable != null)
            {
                return variable.getValue(context, name);
            }

            for (Iterator it = evaluators.iterator(); it.hasNext();)
            {
                WikiVariable evaluator = (WikiVariable) it.next();

                try
                {
                    return evaluator.getValue(context, varName);
                }
                catch (NoSuchVariableException e)
                {
                    if (log.isDebugEnabled())
                    {
                        log.debug(
                            "No match for " + varName + " in Evaluator " + evaluator.getName());
                    }
                }
            }
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("While evaluating " + varName);
        }

        throw new NoSuchVariableException("No variable " + varName + " defined.");
    }

    /**
     * Returns a value of the named variable or a default value
     *
     * @param context DOCUMENT ME!
     * @param varName Name of the variable.
     * @param defaultValue DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getValue(
        final WikiContext context, final String varName, final String defaultValue)
    {
        try
        {
            return getValue(context, varName);
        }
        catch (NoSuchVariableException e)
        {
            return defaultValue;
        }
    }
}
