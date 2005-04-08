/*
    JSPWiki - a JSP-based WikiWiki clone.

    Copyright (C) 2001 Janne Jalkanen (Janne.Jalkanen@iki.fi)

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
package com.ecyrd.jspwiki.plugin;

import com.ecyrd.jspwiki.WikiException;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class PluginException
        extends WikiException
{
    /** DOCUMENT ME! */
    private Throwable m_throwable;

    /**
     * Creates a new PluginException object.
     *
     * @param message DOCUMENT ME!
     */
    public PluginException(String message)
    {
        super(message);
    }

    /**
     * Creates a new PluginException object.
     *
     * @param message DOCUMENT ME!
     * @param original DOCUMENT ME!
     */
    public PluginException(String message, Throwable original)
    {
        super(message);
        m_throwable = original;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Throwable getRootThrowable()
    {
        return m_throwable;
    }
}
