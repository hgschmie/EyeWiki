package com.ecyrd.jspwiki.providers;

/**
 * Indicates that an non-existing version was specified.
 */
public class NoSuchVersionException
        extends ProviderException
{
    /**
     * Creates a new NoSuchVersionException object.
     *
     * @param msg DOCUMENT ME!
     */
    public NoSuchVersionException(String msg)
    {
        super(msg);
    }
}
