package de.softwareforge.eyewiki.dav;

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

import java.util.Collection;
import java.util.Iterator;

import javax.servlet.http.HttpServletResponse;

import org.apache.ecs.XhtmlDocument;
import org.apache.ecs.xhtml.li;
import org.apache.ecs.xhtml.ul;

import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiPage;

/**
 * DOCUMENT ME!
 *
 * @author jalkanen
 *
 * @since
 */
public class DavUtil
{
    /**
     * DOCUMENT ME!
     *
     * @param context DOCUMENT ME!
     * @param coll DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static String getCollectionInHTML(WikiContext context, Collection coll)
    {
        XhtmlDocument doc = new XhtmlDocument("UTF-8");

        ul content = new ul();

        for (Iterator i = coll.iterator(); i.hasNext();)
        {
            Object o = i.next();

            if (o instanceof WikiPage)
            {
                WikiPage p = (WikiPage) o;
                content.addElement(new li().addElement(p.getName()));
            }
            else if (o instanceof String)
            {
                content.addElement(new li().addElement(o.toString()));
            }
        }

        doc.appendBody(content);

        return doc.toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @param res DOCUMENT ME!
     * @param txt DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    public static void sendHTMLResponse(HttpServletResponse res, String txt)
            throws IOException
    {
        res.setContentType("text/html; charset=UTF-8");
        res.setContentLength(txt.length());

        res.getWriter().print(txt);
    }
}
