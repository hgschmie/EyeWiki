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

import java.util.Map;

import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.plugin.PluginException;
import de.softwareforge.eyewiki.plugin.WikiPlugin;

/**
 * Closes a WikiForm.
 *
 * @author ebu
 */
public class FormClose
        extends FormElement
        implements WikiPlugin
{
    /**
     * Builds a Form close tag. Removes any information on the form from the WikiContext.
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
        StringBuffer tags = new StringBuffer();
        tags.append("</form>\n");
        tags.append("</div>");

        // Don't render if no error and error-only-rendering is on.
        FormInfo info = getFormInfo(ctx);

        if (info != null)
        {
            if (info.hide())
            {
                return ("<p>(no need to show close now)</p>");
            }
        }

        // Get rid of remaining form data, so it doesn't mess up other forms.
        // After this, it is safe to add other Forms.
        storeFormInfo(ctx, null);

        return (tags.toString());
    }
}
