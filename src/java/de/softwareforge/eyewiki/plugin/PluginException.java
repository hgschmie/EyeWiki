package de.softwareforge.eyewiki.plugin;

import de.softwareforge.eyewiki.WikiException;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class PluginException
        extends WikiException
{
    /** DOCUMENT ME! */
    private Throwable m_throwable;

    /**
     * Creates a new PluginException object.
     *
     * @param message DOCUMENT ME!
     */
    public PluginException(String message)
    {
        super(message);
    }

    /**
     * Creates a new PluginException object.
     *
     * @param message DOCUMENT ME!
     * @param original DOCUMENT ME!
     */
    public PluginException(String message, Throwable original)
    {
        super(message);
        m_throwable = original;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Throwable getRootThrowable()
    {
        return m_throwable;
    }
}
