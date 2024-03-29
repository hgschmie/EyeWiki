package de.softwareforge.eyewiki.plugin;

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

import de.softwareforge.eyewiki.WikiConstants;
import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.attachment.Attachment;
import de.softwareforge.eyewiki.attachment.AttachmentManager;
import de.softwareforge.eyewiki.providers.ProviderException;
import de.softwareforge.eyewiki.util.TextUtil;

/**
 * Provides an image plugin for better control than is possible with a simple image inclusion.
 *
 * @author Janne Jalkanen
 *
 * @since 2.1.4.
 */

// FIXME: It is not yet possible to do wiki internal links.  In order to
//        do this cleanly, a TranslatorReader revamp is needed.
public class Image
        implements WikiPlugin
{
    /** DOCUMENT ME! */
    public static final String PARAM_SRC = "src";

    /** DOCUMENT ME! */
    public static final String PARAM_ALIGN = "align";

    /** DOCUMENT ME! */
    public static final String PARAM_HEIGHT = "height";

    /** DOCUMENT ME! */
    public static final String PARAM_WIDTH = "width";

    /** DOCUMENT ME! */
    public static final String PARAM_ALT = "alt";

    /** DOCUMENT ME! */
    public static final String PARAM_CAPTION = "caption";

    /** DOCUMENT ME! */
    public static final String PARAM_LINK = "link";

    /** DOCUMENT ME! */
    public static final String PARAM_STYLE = "style";

    /** DOCUMENT ME! */
    public static final String PARAM_CLASS = "class";

    //    public static final String PARAM_MAP      = "map";

    /** DOCUMENT ME! */
    public static final String PARAM_BORDER = "border";

    /** DOCUMENT ME! */
    private final WikiEngine engine;

    /**
     * Creates a new Image object.
     *
     * @param engine DOCUMENT ME!
     */
    public Image(final WikiEngine engine)
    {
        this.engine = engine;
    }

    /**
     * This method is used to clean away things like quotation marks which a malicious user could use to stop processing and insert
     * javascript.
     *
     * @param params DOCUMENT ME!
     * @param paramId DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private static String getCleanParameter(final Map params, final String paramId)
    {
        return TextUtil.replaceEntities((String) params.get(paramId));
    }

    /**
     * DOCUMENT ME!
     *
     * @param context DOCUMENT ME!
     * @param params DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws PluginException DOCUMENT ME!
     */
    public String execute(WikiContext context, Map params)
            throws PluginException
    {
        String src = getCleanParameter(params, PARAM_SRC);
        String align = getCleanParameter(params, PARAM_ALIGN);
        String ht = getCleanParameter(params, PARAM_HEIGHT);
        String wt = getCleanParameter(params, PARAM_WIDTH);
        String alt = getCleanParameter(params, PARAM_ALT);
        String caption = getCleanParameter(params, PARAM_CAPTION);
        String link = getCleanParameter(params, PARAM_LINK);
        String style = getCleanParameter(params, PARAM_STYLE);
        String cssclass = getCleanParameter(params, PARAM_CLASS);

        // String map     = getCleanParameter( params, PARAM_MAP );
        String border = getCleanParameter(params, PARAM_BORDER);

        if (src == null)
        {
            throw new PluginException("Parameter 'src' is required for Image plugin");
        }

        if (cssclass == null)
        {
            cssclass = "imageplugin";
        }

        try
        {
            AttachmentManager mgr = engine.getAttachmentManager();
            Attachment att = mgr.getAttachmentInfo(context, src);

            if (att != null)
            {
                src = context.getURL(WikiContext.ATTACH, att.getName());
            }
        }
        catch (ProviderException e)
        {
            throw new PluginException("Attachment info failed: " + e.getMessage());
        }

        StringBuffer result = new StringBuffer()
                .append("<span class=\"")
                .append(cssclass)
                .append("\"");

        if (align != null)
        {
            result.append(" align=\"").append(align).append("\"");
        }

        if (style != null)
        {
            result.append(" style=\"").append(style).append("\"");
        }

        result.append(">");

        if (caption != null)
        {
            result.append("<caption align=bottom>").append(TextUtil.replaceEntities(caption)).append("</caption>\n");
        }

        if (link != null)
        {
            result.append("<a class=\"").append(WikiConstants.CSS_WIKICONTENT).append("\" href=\"").append(link).append("\">");
        }

        result.append("<img src=\"").append(src).append("\"");

        if (ht != null)
        {
            result.append(" height=\"").append(ht).append("\"");
        }

        if (wt != null)
        {
            result.append(" width=\"").append(wt).append("\"");
        }

        if (alt != null)
        {
            result.append(" alt=\"").append(alt).append("\"");
        }

        if (border != null)
        {
            result.append(" border=\"").append(border).append("\"");
        }

        // if( map != null )    result.append(" map=\""+map+"\"");
        result.append(" />");

        if (link != null)
        {
            result.append("</a>");
        }

        result.append("</span>\n");

        return result.toString();
    }
}
