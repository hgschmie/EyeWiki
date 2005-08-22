package de.softwareforge.eyewiki.auth.permissions;

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

import java.security.acl.Permission;

/**
 * Superclass for all eyeWiki permissions.
 *
 * @author Janne Jalkanen
 */
public abstract class WikiPermission
        implements Permission
{
    /** DOCUMENT ME! */
    private static WikiPermission c_viewPermission = new ViewPermission();

    /** DOCUMENT ME! */
    private static WikiPermission c_editPermission = new EditPermission();

    /** DOCUMENT ME! */
    private static WikiPermission c_createPermission = new CreatePermission();

    /** DOCUMENT ME! */
    private static WikiPermission c_commentPermission = new CommentPermission();

    /** DOCUMENT ME! */
    private static WikiPermission c_deletePermission = new DeletePermission();

    /** DOCUMENT ME! */
    private static WikiPermission c_uploadPermission = new UploadPermission();

    /**
     * This method should return true, if the this permission implies also the given permission. For example "Edit" should always
     * imply "Comment" as well, but not vice versa.  "Edit" should also imply itself, since this method is used to test for
     * permissions.
     *
     * @param permission DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract boolean implies(WikiPermission permission);

    /**
     * DOCUMENT ME!
     *
     * @param representation DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws IllegalArgumentException DOCUMENT ME!
     */
    public static WikiPermission newInstance(String representation)
    {
        if (representation.equalsIgnoreCase("view"))
        {
            return c_viewPermission;
        }
        else if (representation.equalsIgnoreCase("edit"))
        {
            return c_editPermission;
        }
        else if (representation.equalsIgnoreCase("create"))
        {
            return c_createPermission;
        }
        else if (representation.equalsIgnoreCase("comment"))
        {
            return c_commentPermission;
        }
        else if (representation.equalsIgnoreCase("delete"))
        {
            return c_deletePermission;
        }
        else if (representation.equalsIgnoreCase("upload"))
        {
            return c_uploadPermission;
        }

        throw new IllegalArgumentException("No such permission: " + representation);
    }
}
