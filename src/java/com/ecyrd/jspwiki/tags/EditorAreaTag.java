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
package com.ecyrd.jspwiki.tags;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.ecs.ConcreteElement;
import org.apache.ecs.xhtml.br;
import org.apache.ecs.xhtml.div;
import org.apache.ecs.xhtml.h3;
import org.apache.ecs.xhtml.noscript;
import org.apache.ecs.xhtml.script;
import org.apache.ecs.xhtml.textarea;

import com.ecyrd.jspwiki.TranslatorReader;
import com.ecyrd.jspwiki.WikiConstants;
import com.ecyrd.jspwiki.WikiContext;
import com.ecyrd.jspwiki.WikiEngine;
import com.ecyrd.jspwiki.WikiProperties;
import com.ecyrd.jspwiki.exception.NoSuchVariableException;


/**
 * DOCUMENT ME!
 *
 * @author jalkanen
 *
 * @since
 */
public class EditorAreaTag
        extends WikiTagBase
{
    /**
     * DOCUMENT ME!
     */
    public static final String PROP_EDITORTYPE = "jspwiki.editor";

    /**
     * DOCUMENT ME!
     */
    public static final String EDITOR_PLAIN = "Plain";

    /**
     * DOCUMENT ME!
     */
    public static final String EDITOR_FCK = "FCK";

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public int doWikiStartTag()
            throws Exception
    {
        pageContext.getOut().print(getEditorArea(m_wikiContext).toString());

        return SKIP_BODY;
    }

    private static ConcreteElement getFCKEditorArea(WikiContext context)
    {
        WikiEngine engine = context.getEngine();

        // FIXME: Should this change the properties?
        context.setVariable(WikiProperties.PROP_RUNFILTERS, "false");
        context.setVariable(TranslatorReader.PROP_RUNPLUGINS, "false");

        String pageAsHtml =
            StringEscapeUtils.escapeJavaScript(engine.textToHTML(context, getText(context)));

        div container = new div();
        script area = new script();

        area.setType("text/javascript");

        area.addElement("var oFCKeditor = new FCKeditor( 'htmlPageText' );");
        area.addElement("oFCKeditor.BasePath = 'scripts/fckeditor/';");
        area.addElement("oFCKeditor.Value = '" + pageAsHtml + "' ;");
        area.addElement("oFCKeditor.Width  = '100%';");
        area.addElement("oFCKeditor.Height = '500';");
        area.addElement("oFCKeditor.ToolbarSet = 'JSPWiki';");
        area.addElement(
            "oFCKeditor.Config['CustomConfigurationsPath'] = '"
            + context.getEngine().getURL(WikiContext.NONE, "scripts/fckconfig.js", null, true)
            + "';");
        area.addElement("oFCKeditor.Create() ;");

        noscript noscriptarea = new noscript();

        noscriptarea.addElement(new br());
        noscriptarea.addElement(
            new h3().addElement(
                "You need to enable Javascript in your browser to use the WYSIWYG editor").setStyle(
                WikiConstants.CSS_PREVIEW));
        noscriptarea.addElement(new br());

        container.addElement(area);
        container.addElement(noscriptarea);

        area.setPrettyPrint(true);
        container.setPrettyPrint(true);

        return container;
    }

    private static String getText(WikiContext context)
    {
        String usertext = null;

        if (context.getRequestContext().equals(WikiContext.EDIT))
        {
            usertext = context.getHttpParameter("text");

            if (usertext == null)
            {
                usertext = context.getEngine().getText(context, context.getPage());
            }
        }
        else if (context.getRequestContext().equals(WikiContext.COMMENT))
        {
            usertext = context.getHttpParameter("text");
        }

        return usertext;
    }

    /**
     * Returns an element for constructing an editor.
     *
     * @param context Current WikiContext
     *
     * @return
     */
    public static ConcreteElement getEditorArea(WikiContext context)
    {
        try
        {
            String editor =
                context.getEngine().getVariableManager().getValue(context, PROP_EDITORTYPE);

            if (EDITOR_FCK.equals(editor))
            {
                return getFCKEditorArea(context);
            }
        }
        catch (NoSuchVariableException e)
        {
        } // This is fine

        return getPlainEditorArea(context);
    }

    /**
     * Returns an element for constructing an editor.
     *
     * @param context Current WikiContext
     *
     * @return
     */
    public static ConcreteElement getPlainEditorArea(WikiContext context)
    {
        textarea area = new textarea();

        area.setWrap("virtual");
        area.setName("text");
        area.setRows(25);
        area.setCols(80);
        area.setStyle("width:100%;");

        String text = getText(context);

        if (text != null)
        {
            area.addElement(text);
        }

        return area;
    }
}
