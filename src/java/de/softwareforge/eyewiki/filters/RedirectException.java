package de.softwareforge.eyewiki.filters;

/**
 * This exception may be thrown if a filter wants to reject something and redirect the user
 * elsewhere.
 *
 * @since 2.1.112
 */
public class RedirectException
        extends FilterException
{
    /** DOCUMENT ME! */
    private String m_where;

    /**
     * Creates a new RedirectException object.
     *
     * @param msg DOCUMENT ME!
     * @param redirect DOCUMENT ME!
     */
    public RedirectException(String msg, String redirect)
    {
        super(msg);

        m_where = redirect;
    }

    /**
     * Get the URI for redirection.
     *
     * @return DOCUMENT ME!
     */
    public String getRedirect()
    {
        return m_where;
    }
}
