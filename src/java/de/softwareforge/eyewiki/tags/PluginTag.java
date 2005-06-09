/*
    JSPWiki - a JSP-based WikiWiki clone.

    Copyright (C) 2001-2004 Janne Jalkanen (Janne.Jalkanen@iki.fi)

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
package de.softwareforge.eyewiki.tags;

import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.log4j.Logger;


import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.plugin.PluginManager;


/**
 * Inserts any Wiki plugin.  The body of the tag becomes then the body for the plugin.
 *
 * <P>
 * <B>Attributes</B>
 * </p>
 *
 * <UL>
 * <li>
 * plugin - name of the plugin you want to insert.
 * </li>
 * <li>
 * args   - An argument string for the tag.
 * </li>
 * </ul>
 *
 *
 * @author Janne Jalkanen
 *
 * @since 2.0
 */
public class PluginTag
        extends BodyTagSupport
{
    /** DOCUMENT ME! */
    private static final Logger log = Logger.getLogger(PluginTag.class);

    /** DOCUMENT ME! */
    private String m_plugin;

    /** DOCUMENT ME! */
    private String m_args;

    /** DOCUMENT ME! */
    protected WikiContext m_wikiContext;

    /**
     * DOCUMENT ME!
     *
     * @param p DOCUMENT ME!
     */
    public void setPlugin(String p)
    {
        m_plugin = p;
    }

    /**
     * DOCUMENT ME!
     *
     * @param a DOCUMENT ME!
     */
    public void setArgs(String a)
    {
        m_args = a;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws JspException DOCUMENT ME!
     */
    public int doAfterBody()
            throws JspException
    {
        try
        {
            m_wikiContext =
                (WikiContext) pageContext.getAttribute(
                    WikiTagBase.ATTR_CONTEXT, PageContext.REQUEST_SCOPE);

            WikiEngine engine = m_wikiContext.getEngine();

            PluginManager pm = engine.getPluginManager();

            Map argmap = pm.parseArgs(m_args);

            BodyContent bc = getBodyContent();

            if (bc != null)
            {
                argmap.put("_body", bc.getString());
            }

            String result = pm.execute(m_wikiContext, m_plugin, argmap);

            pageContext.getOut().write(result);

            return SKIP_BODY;
        }
        catch (Exception e)
        {
            log.error("Failed to insert plugin", e);
            throw new JspException("Tag failed, check logs: " + e.getMessage());
        }
    }
}
