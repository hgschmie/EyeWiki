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

import java.util.HashMap;
import java.util.Map;

import org.apache.ecs.ConcreteElement;
import org.apache.ecs.html.TextArea;

import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.plugin.PluginException;
import de.softwareforge.eyewiki.plugin.WikiPlugin;

/**
 * DOCUMENT ME!
 *
 * @author ebu
 */
public class FormTextarea
        extends FormElement
        implements WikiPlugin
{
    /** DOCUMENT ME! */
    public static final String PARAM_ROWS = "rows";

    /** DOCUMENT ME! */
    public static final String PARAM_COLS = "cols";

    /**
     * DOCUMENT ME!
     *
     * @param ctx DOCUMENT ME!
     * @param params DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws PluginException DOCUMENT ME!
     */
    public String execute(WikiContext ctx, Map params)
            throws PluginException
    {
        // Don't render if no error and error-only-rendering is on.
        FormInfo info = getFormInfo(ctx);

        if (info == null)
        {
            return "";
        }

        if (info.hide())
        {
            return "<p>(no need to show textarea field now)</p>";
        }

        Map previousValues = info.getSubmission();

        if (previousValues == null)
        {
            previousValues = new HashMap();
        }

        ConcreteElement field = null;

        field = buildTextArea(params, previousValues);

        // We should look for extra params, e.g. width, ..., here.
        return (field == null) ? "" : field.toString();
    }

    private TextArea buildTextArea(Map params, Map previousValues)
            throws PluginException
    {
        String inputName = (String) params.get(PARAM_INPUTNAME);
        String rows = (String) params.get(PARAM_ROWS);
        String cols = (String) params.get(PARAM_COLS);

        if (inputName == null)
        {
            throw new PluginException("Textarea element is missing " + "parameter 'name'.");
        }

        // In order to isolate posted form elements into their own
        // map, prefix the variable name here. It will be stripped
        // when the handler plugin is executed.
        TextArea field = new TextArea(HANDLERPARAM_PREFIX + inputName, rows, cols);

        if (previousValues != null)
        {
            String oldValue = (String) previousValues.get(inputName);

            if (oldValue != null)
            {
                field.addElement(oldValue);
            }
            else
            {
                oldValue = (String) params.get(PARAM_VALUE);

                if (oldValue != null)
                {
                    field.addElement(oldValue);
                }
            }
        }

        return field;
    }
}
