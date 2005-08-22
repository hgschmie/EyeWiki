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
import java.security.acl.Group;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class WikiGroup
        extends WikiPrincipal
        implements Group
{
    /** DOCUMENT ME! */
    private Vector m_members = new Vector();

    /**
     * Creates a new WikiGroup object.
     */
    public WikiGroup()
    {
    }

    /**
     * Creates a new WikiGroup object.
     *
     * @param name DOCUMENT ME!
     */
    public WikiGroup(String name)
    {
        setName(name);
    }

    /**
     * DOCUMENT ME!
     *
     * @param user DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean addMember(Principal user)
    {
        if (isMember(user))
        {
            return false;
        }

        m_members.add(user);

        return true;
    }

    /**
     * DOCUMENT ME!
     *
     * @param user DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean removeMember(Principal user)
    {
        user = findMember(user.getName());

        if (user == null)
        {
            return false;
        }

        m_members.remove(user);

        return true;
    }

    /**
     * DOCUMENT ME!
     */
    public void clearMembers()
    {
        m_members.clear();
    }

    private Principal findMember(String name)
    {
        for (Iterator i = m_members.iterator(); i.hasNext();)
        {
            Principal member = (Principal) i.next();

            if (member.getName().equals(name))
            {
                return member;
            }
        }

        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param principal DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isMember(Principal principal)
    {
        return findMember(principal.getName()) != null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Enumeration members()
    {
        return m_members.elements();
    }

    /**
     * Each and every element is checked that another Group contains the same Principals.
     *
     * @param o DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean equals(Object o)
    {
        if ((o == null) || !(o instanceof WikiGroup))
        {
            return false;
        }

        WikiGroup g = (WikiGroup) o; // Just a shortcut.

        if (g.m_members.size() != m_members.size())
        {
            return false;
        }

        if ((getName() != null) && !getName().equals(g.getName()))
        {
            return false;
        }
        else if ((getName() == null) && (g.getName() != null))
        {
            return false;
        }

        for (Iterator i = m_members.iterator(); i.hasNext();)
        {
            if (!(g.isMember((Principal) i.next())))
            {
                return false;
            }
        }

        return true;
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

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();

        sb.append("[Group: " + getName() + ", members=");

        for (Iterator i = m_members.iterator(); i.hasNext();)
        {
            sb.append(i.next());
            sb.append(", ");
        }

        sb.append("]");

        return sb.toString();
    }
}
