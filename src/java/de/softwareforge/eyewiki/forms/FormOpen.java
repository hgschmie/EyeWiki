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

import org.apache.log4j.Logger;

import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.plugin.PluginException;
import de.softwareforge.eyewiki.plugin.WikiPlugin;

/**
 * Opens a WikiForm. Builds the HTML code for opening a FORM.
 *
 * <p>
 * Since we're only providing an opening FORM tag, we can't use the ECS utilities. A Form plugin line that produces one looks like
 * this:
 * </p>
 *
 * <p>
 * <pre>
 *   [{FormOpen name='formname' handler='pluginname'
 *          submit='submitservlet'
 *          show='always'
 *   }]
 * </pre>
 * </p>
 *
 * <p>
 * Mandatory parameters: <br>
 * The <i>name</i> field identifies this particular form to the  Form plugin across pages. <br>
 * The <i>handler</i> field is a WikiPlugin name; it will be  invoked with the form field values.
 * </p>
 *
 * <p>
 * Optional parameters:
 * </p>
 *
 * <p>
 * The submitservlet is the name of a JSP/servlet capable of  handling the input from this form. It is optional; the default value
 * is the current page (which can handle the input by using this Plugin.)
 * </p>
 *
 * <p>
 * The <i>hide</i> parameter affects the visibility of this form. If left out, the form is always shown. If set to 'onsuccess', the
 * form is not shown if it was submitted successfully. (Note that a reload of the page would cause the context to reset, and the
 * form would be shown again. This may be a useless option.)
 * </p>
 *
 * @author ebu
 */
public class FormOpen
        extends FormElement
        implements WikiPlugin
{
    /** DOCUMENT ME! */
    private static Logger log = Logger.getLogger(FormOpen.class);

    /** DOCUMENT ME! */
    public static final String PARAM_METHOD = "method";

    /**
                                                         */
    public String execute(WikiContext ctx, Map params)
            throws PluginException
    {
        String formName = (String) params.get(PARAM_FORM);

        if (formName == null)
        {
            throw new PluginException("The FormOpen element is missing the '" + PARAM_FORM + "' parameter.");
        }

        String hide = (String) params.get(PARAM_HIDEFORM);
        String sourcePage = ctx.getPage().getName();
        String submitServlet = (String) params.get(PARAM_SUBMITHANDLER);

        if (submitServlet == null)
        {
            submitServlet = ctx.getURL(WikiContext.VIEW, sourcePage);
        }

        String method = (String) params.get(PARAM_METHOD);

        if (method == null)
        {
            method = "post";
        }

        if (!(method.equalsIgnoreCase("get") || method.equalsIgnoreCase("post")))
        {
            throw new PluginException("Method must be either 'post' or 'get'");
        }

        FormInfo info = getFormInfo(ctx);

        if (info != null)
        {
            // Previous information may be the result of submitting
            // this form, or of a FormSet plugin, or both. If it
            // exists and is for this form, fine.
            if (formName.equals(info.getName()))
            {
                if (log.isDebugEnabled())
                {
                    log.debug("Previous FormInfo for this form was found in context.");
                }

                // If the FormInfo exists, and if we're supposed to display on
                // error only, we need to exit now.
                if ((hide != null) && HIDE_SUCCESS.equals(hide) && (info.getStatus() == FormInfo.EXECUTED))
                {
                    info.setHide(true);

                    return "<p>(no need to show form open now)";
                }
            }
            else
            {
                // This would mean that a new form was started without
                // closing an old one.  Get rid of the garbage.
                info = new FormInfo();
            }
        }
        else
        {
            // No previous FormInfo available; store now, so it'll be
            // available for upcoming Form input elements.
            info = new FormInfo();
            storeFormInfo(ctx, info);
        }

        info.setName(formName);
        info.setAction(submitServlet);

        StringBuffer tag =
            new StringBuffer().append("<div>\n").append("<form action=\"").append(submitServlet).append("\" name=\"")
                              .append(formName).append("\" method=\"").append(method)
                              .append("\" enctype=\"application/x-www-form-urlencoded\">\n")
                              .append("  <input type=\"hidden\" name=\"").append(PARAM_FORMNAMEHIDDEN).append("\" value=\"")
                              .append(formName).append("\"/>\n");

        return tag.toString();
    }
}
