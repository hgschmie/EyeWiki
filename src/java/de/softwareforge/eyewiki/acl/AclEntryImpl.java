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
import java.security.acl.AclEntry;
import java.security.acl.Permission;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import de.softwareforge.eyewiki.auth.permissions.WikiPermission;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class AclEntryImpl
        implements AclEntry
{
    /** DOCUMENT ME! */
    private Principal m_principal;

    /** DOCUMENT ME! */
    private boolean m_negative = false;

    /** DOCUMENT ME! */
    private Vector m_permissions = new Vector();

    /**
     * DOCUMENT ME!
     *
     * @param user DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean setPrincipal(Principal user)
    {
        if ((m_principal != null) || (user == null))
        {
            return false;
        }

        m_principal = user;

        return true;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Principal getPrincipal()
    {
        return m_principal;
    }

    /**
     * DOCUMENT ME!
     */
    public void setNegativePermissions()
    {
        m_negative = true;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isNegative()
    {
        return m_negative;
    }

    /**
     * Looks through the permission list and finds a permission that matches the permission.
     *
     * @param p DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private Permission findPermission(WikiPermission p)
    {
        for (Iterator i = m_permissions.iterator(); i.hasNext();)
        {
            WikiPermission pp = (WikiPermission) i.next();

            if (pp.implies(p))
            {
                return pp;
            }
        }

        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param permission DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean addPermission(Permission permission)
    {
        if (findPermission((WikiPermission) permission) != null)
        {
            return true;
        }

        m_permissions.add(permission);

        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param permission DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean removePermission(Permission permission)
    {
        Permission p = findPermission((WikiPermission) permission);

        if (p != null)
        {
            m_permissions.remove(p);

            return true;
        }

        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param permission DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean checkPermission(Permission permission)
    {
        return findPermission((WikiPermission) permission) != null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Enumeration permissions()
    {
        return m_permissions.elements();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws RuntimeException DOCUMENT ME!
     */
    public Object clone()
    {
        AclEntryImpl aei = null;

        try
        {
            aei = (AclEntryImpl) super.clone();
        }
        catch (CloneNotSupportedException cne)
        {
            throw new RuntimeException("Could not clone AclEntryImpl", cne);
        }

        aei.setPrincipal(m_principal);

        aei.m_permissions = (Vector) m_permissions.clone();
        aei.m_negative = m_negative;

        return aei;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();

        Principal p = getPrincipal();

        sb.append("AclEntry: [User=" + ((p != null) ? p.getName() : "null"));
        sb.append(m_negative ? " DENY " : " ALLOW ");

        for (Iterator i = m_permissions.iterator(); i.hasNext();)
        {
            Permission pp = (Permission) i.next();

            sb.append(pp.toString());
            sb.append(",");
        }

        sb.append("]");

        return sb.toString();
    }
}
