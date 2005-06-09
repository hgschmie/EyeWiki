package de.softwareforge.eyewiki.auth;

/**
 * The authenticator may fail with this.
 *
 * @author Janne Jalkanen
 *
 * @since 2.2
 */
public class PasswordExpiredException
        extends WikiSecurityException
{
    /**
     * Constructs an exception.
     *
     * @param msg DOCUMENT ME!
     */
    public PasswordExpiredException(String msg)
    {
        super(msg);
    }
}
