package com.ecyrd.jspwiki.acl;

import java.security.Principal;
import java.security.acl.AclEntry;
import java.security.acl.Permission;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import com.ecyrd.jspwiki.auth.permissions.WikiPermission;


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

        sb.append("AclEntry: [User=" + ((p != null)
            ? p.getName()
            : "null"));
        sb.append(m_negative
            ? " DENY "
            : " ALLOW ");

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
