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
package com.ecyrd.jspwiki.acl;

import java.security.Principal;
import java.security.acl.AclEntry;
import java.security.acl.Group;
import java.security.acl.Permission;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;


/**
 * JSPWiki implementation of an Access Control List.
 *
 * <p>
 * This implementation does not care about owners, and thus all actions are allowed by default.
 * </p>
 */
public class AclImpl
        implements AccessControlList
{
    /** DOCUMENT ME! */
    private Vector m_entries = new Vector();

    /** DOCUMENT ME! */
    private String m_name = null;

    /**
     * DOCUMENT ME!
     *
     * @param caller DOCUMENT ME!
     * @param owner DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean addOwner(Principal caller, Principal owner)
    {
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param caller DOCUMENT ME!
     * @param owner DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean deleteOwner(Principal caller, Principal owner)
    {
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param owner DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isOwner(Principal owner)
    {
        return true;
    }

    /**
     * DOCUMENT ME!
     *
     * @param caller DOCUMENT ME!
     * @param name DOCUMENT ME!
     */
    public void setName(Principal caller, String name)
    {
        m_name = name;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getName()
    {
        return m_name;
    }

    private boolean hasEntry(AclEntry entry)
    {
        if (entry == null)
        {
            return false;
        }

        for (Iterator i = m_entries.iterator(); i.hasNext();)
        {
            AclEntry e = (AclEntry) i.next();

            Principal ep = e.getPrincipal();
            Principal entryp = entry.getPrincipal();

            if ((ep == null) || (entryp == null))
            {
                throw new IllegalArgumentException(
                    "Entry is null; check code, please (entry=" + entry + "; e=" + e + ")");
            }

            if (ep.getName().equals(entryp.getName()) && (e.isNegative() == entry.isNegative()))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param caller DOCUMENT ME!
     * @param entry DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws IllegalArgumentException DOCUMENT ME!
     */
    public boolean addEntry(Principal caller, AclEntry entry)
    {
        if (entry.getPrincipal() == null)
        {
            throw new IllegalArgumentException("Entry principal cannot be null");
        }

        if (hasEntry(entry))
        {
            return false;
        }

        m_entries.add(entry);

        return true;
    }

    /**
     * DOCUMENT ME!
     *
     * @param caller DOCUMENT ME!
     * @param entry DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean removeEntry(Principal caller, AclEntry entry)
    {
        return m_entries.remove(entry);
    }

    // FIXME: Does not understand anything about groups yet.
    public Enumeration getPermissions(Principal user)
    {
        Vector perms = new Vector();

        for (Iterator i = m_entries.iterator(); i.hasNext();)
        {
            AclEntry ae = (AclEntry) i.next();

            if (ae.getPrincipal().getName().equals(user.getName()))
            {
                //
                //  Principal direct match.
                //
                for (Enumeration myEnum = ae.permissions(); myEnum.hasMoreElements();)
                {
                    perms.add(myEnum.nextElement());
                }
            }
        }

        return perms.elements();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Enumeration entries()
    {
        return m_entries.elements();
    }

    /**
     * DOCUMENT ME!
     *
     * @param principal DOCUMENT ME!
     * @param permission DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean checkPermission(Principal principal, Permission permission)
    {
        int res = findPermission(principal, permission);

        return (res == ALLOW);
    }

    /**
     * DOCUMENT ME!
     *
     * @param principal DOCUMENT ME!
     * @param isNegative DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public AclEntry getEntry(Principal principal, boolean isNegative)
    {
        for (Enumeration e = m_entries.elements(); e.hasMoreElements();)
        {
            AclEntry entry = (AclEntry) e.nextElement();

            if (
                entry.getPrincipal().getName().equals(principal.getName())
                            && (entry.isNegative() == isNegative))
            {
                return entry;
            }
        }

        return null;
    }

    /**
     * A new kind of an interface, where the possible results are either ALLOW, DENY, or NONE.
     *
     * @param principal DOCUMENT ME!
     * @param permission DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int findPermission(Principal principal, Permission permission)
    {
        boolean posEntry = false;

        for (Enumeration e = m_entries.elements(); e.hasMoreElements();)
        {
            AclEntry entry = (AclEntry) e.nextElement();

            if (entry.getPrincipal().getName().equals(principal.getName()))
            {
                if (entry.checkPermission(permission))
                {
                    if (entry.isNegative())
                    {
                        return DENY;
                    }
                    else
                    {
                        return ALLOW;

                        // posEntry = true;
                    }
                }
            }
        }

        //
        //  In case both positive and negative permissions have been set,
        //  we'll err for the negative by quitting immediately if we see
        //  a match.  For positive, we have to wait until here.
        //
        if (posEntry)
        {
            return ALLOW;
        }

        //
        //  Now, if the individual permissions did not match, we'll go through
        //  it again but this time looking at groups.
        //
        for (Enumeration e = m_entries.elements(); e.hasMoreElements();)
        {
            AclEntry entry = (AclEntry) e.nextElement();

            if (entry.getPrincipal() instanceof Group)
            {
                Group entryGroup = (Group) entry.getPrincipal();

                if (entryGroup.isMember(principal) && entry.checkPermission(permission))
                {
                    return entry.isNegative() ? DENY : ALLOW;
                }
            }
        }

        if (posEntry)
        {
            return ALLOW;
        }

        return NONE;
    }

    /**
     * Returns a string representation of the contents of this Acl.
     *
     * @return DOCUMENT ME!
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();

        for (Enumeration myEnum = entries(); myEnum.hasMoreElements();)
        {
            AclEntry entry = (AclEntry) myEnum.nextElement();

            Principal pal = entry.getPrincipal();

            if (pal != null)
            {
                sb.append("  user = " + pal.getName() + ": ");
            }
            else
            {
                sb.append("  user = null: ");
            }

            if (entry.isNegative())
            {
                sb.append("NEG");
            }

            sb.append("(");

            for (Enumeration perms = entry.permissions(); perms.hasMoreElements();)
            {
                Permission perm = (Permission) perms.nextElement();
                sb.append(perm.toString());
            }

            sb.append(")\n");
        }

        return sb.toString();
    }
}
