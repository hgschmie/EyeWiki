/*
    JSPWiki - a JSP-based WikiWiki clone.

    Copyright (C) 2001-2002 Janne Jalkanen (Janne.Jalkanen@iki.fi)

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
package com.ecyrd.jspwiki;

import java.io.IOException;

import org.apache.commons.configuration.Configuration;

import com.ecyrd.jspwiki.exception.NoRequiredPropertyException;


/**
 * A generic Wiki provider for all sorts of things that the Wiki can store.
 *
 * @author Janne Jalkanen
 *
 * @since 2.0
 */
public interface WikiProvider
{
    /** Passing this to any method should get the latest version */
    int LATEST_VERSION = -1;

    /**
     * Initializes the page provider.
     *
     * @param engine DOCUMENT ME!
     * @param conf DOCUMENT ME!
     *
     * @throws NoRequiredPropertyException DOCUMENT ME!
     * @throws IOException DOCUMENT ME!
     */
    void initialize(WikiEngine engine, Configuration conf)
            throws NoRequiredPropertyException, IOException;

    /**
     * Return a valid HTML string for information.  May be anything.
     *
     * @return DOCUMENT ME!
     *
     * @since 1.6.4
     */
    String getProviderInfo();
}
