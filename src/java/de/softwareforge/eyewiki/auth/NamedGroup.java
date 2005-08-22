package de.softwareforge.eyewiki.auth;

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

import de.softwareforge.eyewiki.exception.InternalWikiException;

/**
 * A special kind of WikiGroup.  Anyone who has set their name in the cookie is a part of this group.
 */
public class NamedGroup
        extends AllGroup
{
    /**
     * Creates a new NamedGroup object.
     */
    public NamedGroup()
    {
        setName(UserManager.GROUP_NAMEDGUEST);
    }

    /**
     * DOCUMENT ME!
     *
     * @param user DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws InternalWikiException DOCUMENT ME!
     */
    public boolean isMember(Principal user)
    {
        if (user instanceof UserProfile)
        {
            UserProfile p = (UserProfile) user;

            return p.getLoginStatus() >= UserProfile.COOKIE;
        }
        else if (user instanceof WikiGroup)
        {
            WikiGroup wg = (WikiGroup) user;

            return equals(wg);
        }

        throw new InternalWikiException("Someone offered us a Principal that is not an UserProfile!");
    }

    /**
     * DOCUMENT ME!
     *
     * @param o DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean equals(Object o)
    {
        return (o != null) && o instanceof NamedGroup;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int hashCode()
    {
        return super.hashCode();
    }
}
