package de.softwareforge.eyewiki.providers;

import de.softwareforge.eyewiki.WikiException;


/**
 * This exception represents the superclass of all exceptions that providers may throw.  It is okay
 * to throw it in case you cannot use any of the specific subclasses, in which case the page
 * loading is considered to be broken, and the user is notified.
 */
public class ProviderException
        extends WikiException
{
    /**
     * Creates a new ProviderException object.
     *
     * @param msg DOCUMENT ME!
     */
    public ProviderException(String msg)
    {
        super(msg);
    }
}
