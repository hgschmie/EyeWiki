package de.softwareforge.eyewiki.auth;

/**
 * Thrown in some error situations where a WikiPrincipal object does not exist.
 */
public class NoSuchPrincipalException
        extends WikiSecurityException
{
    /**
     * Creates a new NoSuchPrincipalException object.
     *
     * @param msg DOCUMENT ME!
     */
    public NoSuchPrincipalException(String msg)
    {
        super(msg);
    }
}
