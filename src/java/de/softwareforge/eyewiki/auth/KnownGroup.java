package de.softwareforge.eyewiki.auth;

import java.security.Principal;

import de.softwareforge.eyewiki.exception.InternalWikiException;


/**
 * A special kind of WikiGroup.  Anyone who has logged in and has been authenticated is a part of
 * this group.
 */
public class KnownGroup
        extends AllGroup
{
    /**
     * Creates a new KnownGroup object.
     */
    public KnownGroup()
    {
        setName(UserManager.GROUP_KNOWNPERSON);
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

            return p.isAuthenticated();
        }

        throw new InternalWikiException(
            "Someone offered us a Principal that is not an UserProfile!");
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
        return (o != null) && o instanceof KnownGroup;
    }

    public int hashCode()
    {
        return super.hashCode();
    }
}
