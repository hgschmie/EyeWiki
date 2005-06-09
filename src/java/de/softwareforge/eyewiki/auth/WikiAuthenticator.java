package de.softwareforge.eyewiki.auth;



/**
 * Defines the interface for connecting to different authentication services.
 *
 * @author Erik Bunn
 *
 * @since 2.1.11.
 */
public interface WikiAuthenticator
{
    /**
     * Authenticates a user, using the name and password present in the parameter.
     *
     * @param wup DOCUMENT ME!
     *
     * @return true, if this is a valid UserProfile, false otherwise.
     *
     * @throws WikiSecurityException If the password has expired, but is valid otherwise.
     */
    boolean authenticate(UserProfile wup)
            throws WikiSecurityException;
}
