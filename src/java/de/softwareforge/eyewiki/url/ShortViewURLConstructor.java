package de.softwareforge.eyewiki.url;

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

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;

import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiEngine;

/**
 *  A specific URL constructor that returns easy-to-grok URLs for
 *  VIEW and ATTACH contexts, but goes through JSP pages otherwise.
 *
 *  @author jalkanen
 *
 *  @since
 */
public class ShortViewURLConstructor
        extends ShortURLConstructor
{
    public ShortViewURLConstructor(final WikiEngine engine, final Configuration conf)
    {
        super(engine, conf);
    }

    private String makeURL(String context, String name, boolean absolute)
    {
        String viewurl = m_urlPrefix+"%n";

        if (absolute)
        {
            viewurl = "%uwiki/%n";
        }

        if (context.equals(WikiContext.VIEW))
        {
            if (name == null)
            {
                return makeURL("%u","",absolute); // FIXME
            }
            return doReplacement(viewurl, name, absolute);
        }

        return doReplacement(
                DefaultURLConstructor.getURLPattern(context,name),
                name,
                true);
    }

    public String makeURL(String context, String name, boolean absolute, String parameters)
    {
        if (StringUtils.isNotEmpty(parameters))
        {
            if (context.equals(WikiContext.ATTACH) || context.equals(WikiContext.VIEW) || name == null)
            {
                parameters = "?" + parameters;
            }
            else
            {
                parameters = "&amp;"+parameters;
            }
        }
        else
        {
            parameters = "";
        }
        return makeURL(context, name, absolute) + parameters;
    }

    /**
     *   Since we're only called from WikiServlet, where we get the VIEW requests,
     *   we can safely return this.
     */
    public String getForwardPage(HttpServletRequest req)
    {
        return "Wiki.jsp";
    }
}
