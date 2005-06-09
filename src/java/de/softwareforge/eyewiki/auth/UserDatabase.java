package de.softwareforge.eyewiki.auth;

import java.security.Principal;
import java.util.List;


/**
 * Defines an interface for grouping users to groups, etc.
 *
 * @author Janne Jalkanen
 *
 * @since 2.2.
 */
public interface UserDatabase
{
    /**
     * Returns a list of WikiGroup objects for the given Principal.
     *
     * @param p DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws NoSuchPrincipalException DOCUMENT ME!
     */
    List getGroupsForPrincipal(Principal p)
            throws NoSuchPrincipalException;

    /**
     * Creates a principal.  This method should return either a WikiGroup or a UserProfile (or a
     * subclass, if you need them for your own usage.
     *
     * <p>
     * It is the responsibility of the UserDatabase to implement appropriate caching of
     * UserProfiles and other principals.
     * </p>
     *
     * <p>
     * Yes, all this means that user names and user groups do actually live in the same namespace.
     * </p>
     *
     * <p>
     * FIXME: UserDatabase currently requires that getPrincipal() never return null.
     * </p>
     *
     * @param name DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    WikiPrincipal getPrincipal(String name);
}
