package de.softwareforge.eyewiki.exception;

/**
 * Denotes something really serious going on inside Wiki. It is a runtime exception so that the API
 * does not need to be changed, but it's helluva lot better than NullPointerException =).
 *
 * @author Janne Jalkanen
 *
 * @since 1.6.9
 */
public class InternalWikiException
        extends RuntimeException
{
    /**
     * Creates a new InternalWikiException object.
     *
     * @param msg DOCUMENT ME!
     */
    public InternalWikiException(String msg)
    {
        super(msg);
    }
}
