package de.softwareforge.eyewiki.auth;

import java.security.Principal;


/**
 * A special kind of WikiGroup.  Everyone is a member of this group.
 */
public class AllGroup
        extends WikiGroup
{
    /**
     * Creates a new AllGroup object.
     */
    public AllGroup()
    {
        setName(UserManager.GROUP_GUEST);
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
        return true;
    }

    /**
     * DOCUMENT ME!
     *
     * @param user DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isMember(Principal user)
    {
        return true;
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
        return (o != null) && o instanceof AllGroup;
    }

    public int hashCode()
    {
        return super.hashCode();
    }
}
