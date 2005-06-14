package de.softwareforge.eyewiki.htmltowiki;


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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;

import de.softwareforge.eyewiki.util.TextUtil;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Text;
import org.jdom.xpath.XPath;

/**
 * Converting XHtml to Wiki Markup
 *
 * @author <a href="mailto:sbaltes@gmx.com">Sebastian Baltes</a>
 */
public class XHtmlElementToWikiTranslator
{
    /** DOCUMENT ME! */
    private XHtmlToWikiConfig config;

    /** DOCUMENT ME! */
    private WhitespaceTrimWriter outTimmer;

    /** DOCUMENT ME! */
    private PrintWriter out;

    /** DOCUMENT ME! */
    private LiStack li = new LiStack();

    /** DOCUMENT ME! */
    private PreStack pre = new PreStack();

    /**
     * Creates a new XHtmlElementToWikiTranslator object.
     *
     * @param base DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     * @throws JDOMException DOCUMENT ME!
     */
    public XHtmlElementToWikiTranslator(Element base)
            throws IOException, JDOMException
    {
        this(base, new XHtmlToWikiConfig());
    }

    /**
     * Creates a new XHtmlElementToWikiTranslator object.
     *
     * @param base DOCUMENT ME!
     * @param config DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     * @throws JDOMException DOCUMENT ME!
     */
    public XHtmlElementToWikiTranslator(Element base, XHtmlToWikiConfig config)
            throws IOException, JDOMException
    {
        this.config = config;
        outTimmer = new WhitespaceTrimWriter();
        out = new PrintWriter(outTimmer);
        print(base);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getWikiString()
    {
        return outTimmer.toString();
    }

    private void print(String s)
    {
        s = StringEscapeUtils.unescapeHtml(s);
        out.print(s);
    }

    private void print(Object element)
            throws IOException, JDOMException
    {
        if (element instanceof Text)
        {
            Text t = (Text) element;
            String s = t.getText();

            if (pre.isPreMode())
            {
                out.print(s);
            }
            else
            {
                s = s.replaceAll("\\s+", " ");

                if (!s.equals(" "))
                {
                    out.print(s);
                }
            }
        }
        else if (element instanceof Element)
        {
            Element base = (Element) element;

            if ("imageplugin".equals(base.getAttributeValue("class")))
            {
                printImage(base);
            }
            else
            {
                boolean bold = false;
                boolean italic = false;
                boolean monospace = false;
                String cssSpecial = null;
                Map styleProps = getStylePropertiesLowerCase(base);

                if (styleProps != null)
                {
                    String weight = (String) styleProps.remove("font-weight");
                    String style = (String) styleProps.remove("font-style");
                    String font = (String) styleProps.remove("font-family");
                    String center = (String) styleProps.get("text-align");

                    if ("center".equals(center))
                    {
                        styleProps.put("display", "block");
                    }

                    italic = "oblique".equals(style) || "italic".equals(style);
                    bold = "bold".equals(weight) || "bolder".equals(weight);
                    monospace = (font != null) && ((font.indexOf("mono") >= 0) || (font.indexOf("courier") >= 0));

                    if (!styleProps.isEmpty())
                    {
                        cssSpecial = propsToStyleString(styleProps);
                    }
                }

                if (bold)
                {
                    out.print("__");
                }

                if (italic)
                {
                    out.print("''");
                }

                if (monospace)
                {
                    out.print("{{{");
                    pre.push();
                }

                if (cssSpecial != null)
                {
                    out.print("%%(" + cssSpecial + " )");
                }

                printChildren(base);

                if (cssSpecial != null)
                {
                    out.print("%%");
                }

                if (monospace)
                {
                    pre.pop();
                    out.print("}}}");
                }

                if (italic)
                {
                    out.print("''");
                }

                if (bold)
                {
                    out.print("__");
                }
            }
        }
    }

    private void printChildren(Element base)
            throws IOException, JDOMException
    {
        for (Iterator i = base.getContent().iterator(); i.hasNext();)
        {
            Object c = i.next();

            if (c instanceof Element)
            {
                Element e = (Element) c;
                String n = e.getName().toLowerCase();

                if (n.equals("h1"))
                {
                    out.print("!!!");
                    print(e);
                    out.println();
                }
                else if (n.equals("h2"))
                {
                    out.print("!!");
                    print(e);
                    out.println();
                }
                else if (n.equals("h3"))
                {
                    out.print("!");
                    print(e);
                    out.println();
                }
                else if (n.equals("h4"))
                {
                    out.print("!");
                    print(e);
                    out.println();
                }
                else if (n.equals("p"))
                {
                    out.println();
                    out.println();
                    print(e);
                    out.println();
                    out.println();
                }
                else if (n.equals("br"))
                {
                    if (pre.isPreMode())
                    {
                        out.println();
                    }
                    else
                    {
                        out.print(" \\\\");
                    }

                    print(e);
                }
                else if (n.equals("hr"))
                {
                    out.println();
                    print("----");
                    print(e);
                    out.println();
                }
                else if (n.equals("table"))
                {
                    if (!outTimmer.isCurrentlyOnLineBegin())
                    {
                        out.println();
                    }

                    print(e);
                }
                else if (n.equals("tr"))
                {
                    print(e);
                    out.println();
                }
                else if (n.equals("td"))
                {
                    out.print("| ");
                    print(e);

                    if (!pre.isPreMode())
                    {
                        print(" ");
                    }
                }
                else if (n.equals("th"))
                {
                    out.print("|| ");
                    print(e);

                    if (!pre.isPreMode())
                    {
                        print(" ");
                    }
                }
                else if (n.equals("a"))
                {
                    if (!isIgnorableWikiMarkupLink(e))
                    {
                        if (e.getChild("IMG") != null)
                        {
                            printImage(e);
                        }
                        else
                        {
                            String ref = e.getAttributeValue("href");

                            if (ref == null)
                            {
                                print(e);
                            }
                            else
                            {
                                ref = trimLink(ref);

                                if (ref != null)
                                {
                                    if (ref.startsWith("#"))
                                    {
                                        print(e);
                                    }
                                    else
                                    {
                                        out.print(" [");
                                        print(e);

                                        if (!e.getTextTrim().replaceAll("\\s", "").equals(ref))
                                        {
                                            out.print("|");
                                            print(ref);
                                        }

                                        out.print("]");
                                    }
                                }
                            }

                            out.print(" ");
                        }
                    }
                }
                else if (n.equals("b"))
                {
                    out.print("__");
                    print(e);
                    out.print("__");
                }
                else if (n.equals("i"))
                {
                    out.print("''");
                    print(e);
                    out.print("''");
                }
                else if (n.equals("ul"))
                {
                    out.println();
                    li.push("*");
                    print(e);
                    li.pop();
                }
                else if (n.equals("ol"))
                {
                    out.println();
                    li.push("#");
                    print(e);
                    li.pop();
                }
                else if (n.equals("li"))
                {
                    out.print(li + " ");
                    print(e);
                    out.println();
                }
                else if (n.equals("pre"))
                {
                    out.print("{{{");
                    pre.push();
                    print(e);
                    pre.pop();
                    out.println("}}}");
                }
                else if (n.equals("code") || n.equals("tt"))
                {
                    out.print("{{");
                    pre.push();
                    print(e);
                    pre.pop();
                    out.println("}}");
                }
                else if (n.equals("img"))
                {
                    if (!isIgnorableWikiMarkupLink(e))
                    {
                        out.print("[");
                        print(trimLink(e.getAttributeValue("src")));
                        out.print("]");
                        out.print(" ");
                    }
                }
                else
                {
                    print(e);
                }
            }
            else
            {
                print(c);
            }
        }
    }

    private void printImage(Element base)
            throws JDOMException
    {
        Element child = (Element) XPath.selectSingleNode(base, "TBODY/TR/TD/*");

        if (child == null)
        {
            child = base;
        }

        Element img;
        String href;
        Map map = new ForgetNullValuesLinkedHashMap();

        if (child.getName().equals("A"))
        {
            img = child.getChild("IMG");
            href = child.getAttributeValue("href");
        }
        else
        {
            img = child;
            href = null;
        }

        if (img == null)
        {
            return;
        }

        String src = trimLink(img.getAttributeValue("src"));

        if (src == null)
        {
            return;
        }

        map.put("align", base.getAttributeValue("align"));
        map.put("height", img.getAttributeValue("height"));
        map.put("width", img.getAttributeValue("width"));
        map.put("alt", img.getAttributeValue("alt"));
        map.put("caption", emptyToNull(XPath.newInstance("CAPTION").valueOf(base)));
        map.put("link", href);
        map.put("border", img.getAttributeValue("border"));
        map.put("style", base.getAttributeValue("style"));

        if (map.size() > 0)
        {
            out.print("[{Image src='" + src + "'");

            for (Iterator i = map.entrySet().iterator(); i.hasNext();)
            {
                Map.Entry entry = (Map.Entry) i.next();
                out.print(" " + entry.getKey() + "='" + entry.getValue() + "'");
            }

            out.print("}]");
        }
        else
        {
            out.print("[" + src + "]");
        }
    }

    private String emptyToNull(String s)
    {
        return (s == null) ? null : ((s.replaceAll("\\s", "").length() == 0) ? null : s);
    }

    private String propsToStyleString(Map styleProps)
    {
        StringBuffer style = new StringBuffer();

        for (Iterator i = styleProps.entrySet().iterator(); i.hasNext();)
        {
            Map.Entry entry = (Map.Entry) i.next();
            style.append(" ").append(entry.getKey()).append(": ").append(entry.getValue()).append(";");
        }

        return style.toString();
    }

    private boolean isIgnorableWikiMarkupLink(Element a)
    {
        String ref = a.getAttributeValue("href");
        String class_ = a.getAttributeValue("class");

        return ((ref != null) && ref.startsWith(config.getPageInfoJsp()))
        || ((class_ != null) && class_.trim().equalsIgnoreCase(config.getOutlink()));
    }

    private Map getStylePropertiesLowerCase(Element base)
            throws IOException
    {
        //"font-weight: bold; font-style: italic;"
        String style = base.getAttributeValue("style");

        if (style == null)
        {
            return null;
        }
        else
        {
            style = style.replace(';', '\n').toLowerCase();

            LinkedHashMap m = new LinkedHashMap();
            new PersistentMapDecorator(m).load(new ByteArrayInputStream(style.getBytes()));

            return m;
        }
    }

    private String trimLink(String ref)
    {
        if (ref == null)
        {
            return null;
        }

        ref = TextUtil.urlDecodeUTF8(ref);
        ref = ref.trim();

        if (ref.startsWith(config.getAttachPage()))
        {
            ref = ref.substring(config.getAttachPage().length());
        }

        if (ref.startsWith(config.getWikiJspPage()))
        {
            ref = ref.substring(config.getWikiJspPage().length());
        }

        if (config.getPageName() != null)
        {
            if (ref.startsWith(config.getPageName()))
            {
                ref = ref.substring(config.getPageName().length());
            }
        }

        return ref;
    }

    // FIXME: These should probably be better used with java.util.Stack
    public static class LiStack
    {
        /** DOCUMENT ME! */
        private String li = "";

        /**
         * DOCUMENT ME!
         *
         * @param c DOCUMENT ME!
         */
        public void push(String c)
        {
            li += c;
        }

        /**
         * DOCUMENT ME!
         */
        public void pop()
        {
            li = li.substring(0, li.length() - 1);
        }

        /**
         * DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        public String toString()
        {
            return li;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Revision$
     */
    class PreStack
    {
        /** DOCUMENT ME! */
        private int pre = 0;

        /**
         * DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        public boolean isPreMode()
        {
            return pre > 0;
        }

        /**
         * DOCUMENT ME!
         */
        public void push()
        {
            pre++;
            outTimmer.setWhitespaceTrimMode(!isPreMode());
        }

        /**
         * DOCUMENT ME!
         */
        public void pop()
        {
            pre--;
            outTimmer.setWhitespaceTrimMode(!isPreMode());
        }
    }
}
