package de.softwareforge.eyewiki.auth;

import java.security.Principal;

import de.softwareforge.eyewiki.exception.InternalWikiException;


/**
 * A special kind of WikiGroup.  Anyone who has set their name in the cookie is a part of this
 * group.
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
        return (o != null) && o instanceof NamedGroup;
    }

    public int hashCode()
    {
        return super.hashCode();
    }
}