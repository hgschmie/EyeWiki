package de.softwareforge.eyewiki;



/**
 * A generic Wiki provider for all sorts of things that the Wiki can store.
 *
 * @author Janne Jalkanen
 *
 * @since 2.0
 */
public interface WikiProvider
{
    /** Passing this to any method should get the latest version */
    int LATEST_VERSION = -1;

    /**
     * Return a valid HTML string for information.  May be anything.
     *
     * @return DOCUMENT ME!
     *
     * @since 1.6.4
     */
    String getProviderInfo();
}
