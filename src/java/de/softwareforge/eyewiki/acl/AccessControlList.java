package de.softwareforge.eyewiki.acl;

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

import java.security.Principal;
import java.security.acl.Acl;
import java.security.acl.AclEntry;
import java.security.acl.Permission;

/**
 * Defines the eyeWiki-specific helper methods for accessing the Acl.
 *
 * @author Janne Jalkanen
 */
public interface AccessControlList
        extends Acl
{
    /** DOCUMENT ME! */
    int ALLOW = 1;

    /** DOCUMENT ME! */
    int DENY = -1;

    /** DOCUMENT ME! */
    int NONE = 0;

    /**
     * DOCUMENT ME!
     *
     * @param principal DOCUMENT ME!
     * @param permission DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    int findPermission(Principal principal, Permission permission);

    /**
     * DOCUMENT ME!
     *
     * @param principal DOCUMENT ME!
     * @param isNegative DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    AclEntry getEntry(Principal principal, boolean isNegative);
}
