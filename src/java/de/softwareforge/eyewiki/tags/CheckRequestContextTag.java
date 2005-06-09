package de.softwareforge.eyewiki.tags;

import java.io.IOException;

import de.softwareforge.eyewiki.providers.ProviderException;


/**
 * Includes body, if the request context matches.
 *
 * @author Janne Jalkanen
 *
 * @since 2.0
 */
public class CheckRequestContextTag
        extends WikiTagBase
{
    /** DOCUMENT ME! */
    private String m_context;

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getContext()
    {
        return m_context;
    }

    /**
     * DOCUMENT ME!
     *
     * @param arg DOCUMENT ME!
     */
    public void setContext(String arg)
    {
        m_context = arg;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     * @throws ProviderException DOCUMENT ME!
     */
    public final int doWikiStartTag()
            throws IOException, ProviderException
    {
        if (m_wikiContext.getRequestContext().equalsIgnoreCase(getContext()))
        {
            return EVAL_BODY_INCLUDE;
        }

        return SKIP_BODY;
    }
}
