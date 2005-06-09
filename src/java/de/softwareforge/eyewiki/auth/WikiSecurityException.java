package de.softwareforge.eyewiki.auth;

import de.softwareforge.eyewiki.WikiException;


/**
 * WikiSecurityException is used to provide authentication and authorization information.
 *
 * @author Erik Bunn
 *
 * @since 2.0
 */
public class WikiSecurityException
        extends WikiException
{
    /**
     * Constructs an exception.
     *
     * @param msg DOCUMENT ME!
     */
    public WikiSecurityException(String msg)
    {
        super(msg);
    }
}
