/*
    WikiForms - a WikiPage FORM handler for JSPWiki.

    Copyright (C) 2003 BaseN.

    JSPWiki Copyright (C) 2002 Janne Jalkanen (Janne.Jalkanen@iki.fi)

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published
    by the Free Software Foundation; either version 2.1 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program; if not, write to the Free Software
*/
package de.softwareforge.eyewiki.forms;

import java.util.Map;

import org.apache.commons.lang.StringUtils;


import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.plugin.PluginException;
import de.softwareforge.eyewiki.plugin.WikiPlugin;


/**
 * FormSet is a companion WikiPlugin for Form.
 *
 * <p>
 * The mandatory 'form' parameter specifies which form the variable applies to.  Any other
 * parameters are put directly into a FormInfo object that will be available to a Form plugin
 * called 'form' (presumably invoked later on the same WikiPage).
 * </p>
 *
 * <p>
 * If the name of a FormSet parameter is the same as the name of a Form plugin input element later
 * on the same page, the Form will consider the given value the default for the form field.
 * (However, the handler for the Form is free to use the value as it wishes, and even override
 * it.)
 * </p>
 *
 * <p>
 * If the name of a parameter is not present in Form input fields, the parameter is presumably
 * meant for sending initial information to the Form handler. If this is the case, you may want to
 * specify the <i>populate=''</i> in the Form <i>open</i> element, otherwise the form won't be
 * displayed on the first invocation.
 * </p>
 *
 * <p>
 * This object looks for a FormInfo object named FORM_VALUES_CARRIER in the WikiContext. If found,
 * it checks that its name matches the 'form' parameter, and if it does, adds the plugin
 * parameters to the FormInfo. If the names don't match, the old FormInfo is discarded and a new
 * one is created. Only one FormInfo is supported at a time. A practical consequence of this is
 * that a FormSet invocation only applies to the Form plugins that follow it; any further Forms
 * need new FormSet calls.
 * </p>
 *
 * @author ebu
 *
 * @see FormInfo
 */
public class FormSet
        implements WikiPlugin
{
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
        String formName = (String) params.remove(FormElement.PARAM_FORM);

        if (StringUtils.isBlank(formName))
        {
            return "";
        }

        FormInfo info = (FormInfo) ctx.getVariable(FormElement.FORM_VALUES_CARRIER);

        if ((info == null) || !formName.equals(info.getName()))
        {
            info = new FormInfo();
            ctx.setVariable(FormElement.FORM_VALUES_CARRIER, info);
        }

        info.setName(formName);
        info.addSubmission(params);

        return ("");
    }
}
