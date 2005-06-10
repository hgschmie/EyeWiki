package de.softwareforge.eyewiki.tags;

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

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;

import org.apache.commons.lang.StringUtils;
import org.apache.ecs.ConcreteElement;
import org.apache.ecs.GenericElement;
import org.apache.ecs.wml.Tr;
import org.apache.ecs.xhtml.form;
import org.apache.ecs.xhtml.h2;
import org.apache.ecs.xhtml.input;
import org.apache.ecs.xhtml.p;
import org.apache.ecs.xhtml.table;
import org.apache.ecs.xhtml.td;

import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.WikiPage;


/**
 * Creates an editor component with all the necessary parts to get it working.
 *
 * <p>
 * In the future, this component should be expanded to provide a customized version of the editor
 * according to user preferences.
 * </p>
 *
 * @author Janne Jalkanen
 *
 * @since 2.2
 */
public class EditorTag
        extends WikiBodyTag
{
    /** DOCUMENT ME! */
    private String m_submit = "Save";

    /** DOCUMENT ME! */
    private String m_preview = "Preview";

    /** DOCUMENT ME! */
    private String m_cancel = "Cancel";

    /** DOCUMENT ME! */
    private String m_formName = "editForm";

    /** DOCUMENT ME! */
    private String m_action = null;

    /**
     * DOCUMENT ME!
     *
     * @param s DOCUMENT ME!
     */
    public void setSubmit(String s)
    {
        m_submit = s;
    }

    /**
     * DOCUMENT ME!
     *
     * @param s DOCUMENT ME!
     */
    public void setPreview(String s)
    {
        m_preview = s;
    }

    /**
     * DOCUMENT ME!
     *
     * @param s DOCUMENT ME!
     */
    public void setCancel(String s)
    {
        m_cancel = s;
    }

    /**
     * DOCUMENT ME!
     *
     * @param s DOCUMENT ME!
     */
    public void setName(String s)
    {
        m_formName = s;
    }

    /**
     * DOCUMENT ME!
     *
     * @param s DOCUMENT ME!
     */
    public void setAction(String s)
    {
        m_action = s;
    }

    private GenericElement createInput(String name, String value)
    {
        input in = new input();
        in.setType("hidden");
        in.setName(name);
        in.setValue(value);

        return in;
    }

    private GenericElement createSubmit(String name, String value)
    {
        input in = new input();
        in.setType("submit");
        in.setName(name);
        in.setValue(value);

        return in;
    }

    private form createSimpleEditor()
    {
        WikiPage page = m_wikiContext.getPage();
        WikiEngine engine = m_wikiContext.getEngine();

        form f = new form();
        f.setName(m_formName);

        if (m_action != null)
        {
            f.setAction(m_action);
        }
        else if (
            m_wikiContext.getRequestContext().equals(WikiContext.COMMENT)
                        || "comment".equals(m_wikiContext.getHttpParameter("action")))
        {
            f.setAction(m_wikiContext.getURL(WikiContext.COMMENT, page.getName()));
        }
        else
        {
            f.setAction(m_wikiContext.getURL(WikiContext.EDIT, page.getName()));
        }

        f.setMethod("post");
        f.setAcceptCharset(engine.getContentEncoding());

        p para1 = new p();

        // Kludge to get preview working from Comment.jsp
        if (m_wikiContext.getHttpParameter("author") != null)
        {
            para1.addElement(createInput("author", m_wikiContext.getHttpParameter("author")));
        }

        f.addElement(para1);

        para1.addElement(createInput("page", page.getName()));
        para1.addElement(createInput("action", "save"));
        para1.addElement(
            createInput(
                "edittime",
                (String) pageContext.getAttribute("lastchange", PageContext.REQUEST_SCOPE)));

        if (m_wikiContext.getRequestContext().equals("comment"))
        {
            para1.addElement(createInput("comment", "true"));
        }

        return f;
    }

    private ConcreteElement getEditorArea()
    {
        return EditorAreaTag.getEditorArea(m_wikiContext);
    }

    /**
     * Returns an edit button block.
     *
     * @return
     */
    private ConcreteElement getButtons()
    {
        h2 h2 = new h2();

        table t = new table();

        h2.addElement(t);

        Tr tr = new Tr();
        t.addElement(tr);

        td td = null;

        td = new td();
        td.addElement(createSubmit("ok", m_submit));
        tr.addElement(td);

        td = new td();
        td.addElement(createSubmit("preview", m_preview));
        tr.addElement(td);

        td = new td();
        td.addElement(createSubmit("cancel", m_cancel));
        tr.addElement(td);

        return h2;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    public final int doWikiStartTag()
            throws IOException
    {
        return EVAL_BODY_AGAIN;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws JspException DOCUMENT ME!
     */
    public int doEndTag()
            throws JspException
    {
        BodyContent bc = getBodyContent();

        form editor = createSimpleEditor();

        //
        // If there is no body tag content, then we'll assume old
        // behaviour and append the stuff ourselves.
        //
        if ((bc == null) || StringUtils.isEmpty(bc.getString()))
        {
            editor.addElement(getEditorArea());
            editor.addElement(getButtons());
        }
        else
        {
            editor.addElement(bc.getString());
        }

        try
        {
            pageContext.getOut().print(editor.toString());
        }
        catch (IOException e)
        {
            throw new JspException("Could not print Editor tag: " + e.getMessage());
        }

        return EVAL_PAGE;
    }
}
