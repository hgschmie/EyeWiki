package de.softwareforge.eyewiki.providers;

/**
 * If the provider detects that someone has modified the repository externally, it should throw
 * this exception.
 *
 * <p>
 * Any provider throwing this exception should first clean up any references to the modified page
 * it has, so that when we call this the next time, the page is handled as completely, and we
 * don't get the same exception again.
 * </p>
 *
 * @author Janne Jalkanen
 *
 * @since 2.1.25
 */
public class RepositoryModifiedException
        extends ProviderException
{
    /** DOCUMENT ME! */
    protected String m_page;

    /**
     * Constructs the exception.
     *
     * @param msg
     * @param pageName The name of the page which was modified
     */
    public RepositoryModifiedException(String msg, String pageName)
    {
        super(msg);

        m_page = pageName;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getPageName()
    {
        return m_page;
    }
}
