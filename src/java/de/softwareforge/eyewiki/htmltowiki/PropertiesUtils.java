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

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

/**
 * some usefull methods for properties
 *
 * @author <a href="mailto:sbaltes@gmx.com">Sebastian Baltes</a>
 * @version 1.0
 */
public class PropertiesUtils
{
    /** DOCUMENT ME! */
    private static final char [] hexDigit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    /**
     * <p>
     * like Properties.store, but stores the properties in sorted order
     * </p>
     *
     * @param properties
     *
     * @return String
     */
    public static String toSortedString(Properties properties)
    {
        TreeMap treemap = new TreeMap(properties);
        StringBuffer buf = new StringBuffer();

        for (Iterator it = treemap.entrySet().iterator(); it.hasNext();)
        {
            Map.Entry entry = (Map.Entry) it.next();

            String string_0_ = (String) entry.getKey();
            String string_1_ = (entry.getValue() == null) ? null : entry.getValue().toString();

            buf.append(toLine(string_0_, string_1_)).append("\n");
        }

        return buf.toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @param key
     * @param value
     *
     * @return
     */
    public static String toLine(String key, String value)
    {
        return saveConvert(key, true) + "=" + saveConvert(value, false);
    }

    /**
     * DOCUMENT ME!
     *
     * @param string
     * @param encodeWhiteSpace
     *
     * @return
     */
    public static String saveConvert(String string, boolean encodeWhiteSpace)
    {
        int i = string.length();
        StringBuffer stringbuffer = new StringBuffer(i * 2);

        for (int i_3_ = 0; i_3_ < i; i_3_++)
        {
            char c = string.charAt(i_3_);

            switch (c)
            {
            case ' ':

                if ((i_3_ == 0) || encodeWhiteSpace)
                {
                    stringbuffer.append('\\');
                }

                stringbuffer.append(' ');

                break;

            case '\\':
                stringbuffer.append('\\');
                stringbuffer.append('\\');

                break;

            case '\t':
                stringbuffer.append('\\');
                stringbuffer.append('t');

                break;

            case '\n':
                stringbuffer.append('\\');
                stringbuffer.append('n');

                break;

            case '\r':
                stringbuffer.append('\\');
                stringbuffer.append('r');

                break;

            case '\014':
                stringbuffer.append('\\');
                stringbuffer.append('f');

                break;

            default:

                if ((c < 32) || (c > 126))
                {
                    stringbuffer.append('\\');
                    stringbuffer.append('u');
                    stringbuffer.append(toHex((c >> 12) & 0xf));
                    stringbuffer.append(toHex((c >> 8) & 0xf));
                    stringbuffer.append(toHex((c >> 4) & 0xf));
                    stringbuffer.append(toHex(c & 0xf));
                }
                else
                {
                    if ("\t\r\n\014".indexOf(c) != -1)
                    {
                        stringbuffer.append('\\');
                    }

                    stringbuffer.append(c);
                }
            }
        }

        return stringbuffer.toString();
    }

    private static char toHex(int i)
    {
        return hexDigit[i & 0xf];
    }
}
