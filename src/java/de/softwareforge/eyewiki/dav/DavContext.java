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
import javax.servlet.http.HttpServletRequest;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class DavContext
{
    /** DOCUMENT ME! */
    public String m_davcontext = "";

    /** DOCUMENT ME! */
    public String m_page = null;

    /** DOCUMENT ME! */
    public int m_depth = -1;

    /**
     * Creates a new DavContext object.
     *
     * @param req DOCUMENT ME!
     */
    public DavContext(HttpServletRequest req)
    {
        String path = req.getPathInfo();

        if (path != null)
        {
            if (path.startsWith("/"))
            {
                path = path.substring(1);
            }

            int idx = path.indexOf('/');

            if (idx != -1)
            {
                m_davcontext = path.substring(0, idx);
                m_page = path.substring(idx + 1);
            }
            else
            {
                m_davcontext = path;
            }
        }

        String depth = req.getHeader("Depth");
        m_depth = (depth == null) ? (-1) : Integer.parseInt(depth);
    }
}
