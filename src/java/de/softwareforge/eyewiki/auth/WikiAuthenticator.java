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
package de.softwareforge.eyewiki.auth;



/**
 * Defines the interface for connecting to different authentication services.
 *
 * @author Erik Bunn
 *
 * @since 2.1.11.
 */
public interface WikiAuthenticator
{
    /**
     * Authenticates a user, using the name and password present in the parameter.
     *
     * @param wup DOCUMENT ME!
     *
     * @return true, if this is a valid UserProfile, false otherwise.
     *
     * @throws WikiSecurityException If the password has expired, but is valid otherwise.
     */
    boolean authenticate(UserProfile wup)
            throws WikiSecurityException;
}
