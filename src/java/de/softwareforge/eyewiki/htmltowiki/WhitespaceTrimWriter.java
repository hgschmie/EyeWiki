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

import java.io.IOException;
import java.io.Writer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Part of the XHtmlToWikiTranslator
 *
 * @author <a href="mailto:sbaltes@gmx.com">Sebastian Baltes</a>
 */
public class WhitespaceTrimWriter
        extends Writer
{
    /**
     * DOCUMENT ME!
     */
    private StringBuffer result = new StringBuffer();

    /**
     * DOCUMENT ME!
     */
    private StringBuffer buffer = new StringBuffer();

    /**
     * DOCUMENT ME!
     */
    private boolean trimMode = true;

    /**
     * DOCUMENT ME!
     */
    private Pattern ps = Pattern.compile(".*?\\n\\s*?", Pattern.MULTILINE);

    /**
     * DOCUMENT ME!
     */
    private boolean currentlyOnLineBegin = true;

    /**
     * DOCUMENT ME!
     */
    public void flush()
    {
        if (buffer.length() > 0)
        {
            String s = buffer.toString();
            s = s.replaceAll("\r\n", "\n");

            if (trimMode)
            {
                s = s.replaceAll("(\\w+) \\[\\?\\|Edit\\.jsp\\?page=\\1\\]", "[$1]");
                s = s.replaceAll("\n{2,}", "\n\n");
                s = s.replaceAll("\\p{Blank}+", " ");
                s = s.replaceAll("[ ]*\n[ ]*", "\n");
                s = replacePluginNewlineBackslashes(s);
            }

            result.append(s);
            buffer = new StringBuffer();
        }
    }

    private String replacePluginNewlineBackslashes(String s)
    {
        Pattern p =
            Pattern.compile(
                "\\{\\{\\{(.*?)\\}\\}\\}|\\{\\{(.*?)\\}\\}|\\[\\{(.*?)\\}\\]",
                Pattern.DOTALL + Pattern.MULTILINE);
        Matcher m = p.matcher(s);
        StringBuffer sb = new StringBuffer();

        while (m.find())
        {
            String groupEscaped = m.group().replaceAll("\\\\|\\$", "\\\\$0");

            if (m.group(3) != null)
            {
                m.appendReplacement(sb, groupEscaped.replaceAll("\\\\\\\\\\\\\\\\", "\n"));
            }
            else
            {
                m.appendReplacement(sb, groupEscaped);
            }
        }

        m.appendTail(sb);
        s = sb.toString();

        return s;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isWhitespaceTrimMode()
    {
        return trimMode;
    }

    /**
     * DOCUMENT ME!
     *
     * @param trimMode DOCUMENT ME!
     */
    public void setWhitespaceTrimMode(boolean trimMode)
    {
        if (this.trimMode != trimMode)
        {
            flush();
            this.trimMode = trimMode;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param arg0 DOCUMENT ME!
     * @param arg1 DOCUMENT ME!
     * @param arg2 DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    public void write(char [] arg0, int arg1, int arg2)
            throws IOException
    {
        buffer.append(arg0, arg1, arg2);
        currentlyOnLineBegin = ps.matcher(buffer).matches();

        //    System.out.println("\""+PropertiesUtils.saveConvert(buffer.toString(),true)+"\">>
        // "+currentlyOnLineBegin);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    public void close()
            throws IOException
    {
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String toString()
    {
        flush();

        return result.toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isCurrentlyOnLineBegin()
    {
        return currentlyOnLineBegin;
    }
}
