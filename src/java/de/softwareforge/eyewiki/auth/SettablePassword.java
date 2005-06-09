package de.softwareforge.eyewiki.auth;

import de.softwareforge.eyewiki.providers.ProviderException;


/**
 * Defines an interface for WikiAuthenticators that are able to set passwords.
 *
 * @author Janne Jalkanen
 *
 * @since 2.1.123
 */
public interface SettablePassword
{
    /**
     * Sets the user password.  This is an optional operation.
     *
     * @param wup DOCUMENT ME!
     * @param password DOCUMENT ME!
     *
     * @throws ProviderException If the password cannot be set.
     */
    void setPassword(UserProfile wup, String password)
            throws ProviderException;
}
