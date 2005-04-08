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
package com.ecyrd.jspwiki.auth;

import java.security.Principal;
import java.util.List;

import org.apache.commons.configuration.Configuration;

import com.ecyrd.jspwiki.WikiEngine;
import com.ecyrd.jspwiki.exception.NoRequiredPropertyException;


/**
 * Defines an interface for grouping users to groups, etc.
 *
 * @author Janne Jalkanen
 *
 * @since 2.2.
 */
public interface UserDatabase
{
    /**
     * Initializes the WikiPrincipalist based on values from a Properties object.
     *
     * @param engine DOCUMENT ME!
     * @param conf DOCUMENT ME!
     *
     * @throws NoRequiredPropertyException DOCUMENT ME!
     */
    public void initialize(WikiEngine engine, Configuration conf)
            throws NoRequiredPropertyException;

    /**
     * Returns a list of WikiGroup objects for the given Principal.
     *
     * @param p DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws NoSuchPrincipalException DOCUMENT ME!
     */
    public List getGroupsForPrincipal(Principal p)
            throws NoSuchPrincipalException;

    /**
     * Creates a principal.  This method should return either a WikiGroup or a UserProfile (or a
     * subclass, if you need them for your own usage.
     *
     * <p>
     * It is the responsibility of the UserDatabase to implement appropriate caching of
     * UserProfiles and other principals.
     * </p>
     *
     * <p>
     * Yes, all this means that user names and user groups do actually live in the same namespace.
     * </p>
     *
     * <p>
     * FIXME: UserDatabase currently requires that getPrincipal() never return null.
     * </p>
     *
     * @param name DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public WikiPrincipal getPrincipal(String name);
}
