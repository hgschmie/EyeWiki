package de.softwareforge.eyewiki.tags;

import java.io.IOException;


import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.WikiPage;
import de.softwareforge.eyewiki.providers.ProviderException;


/**
 * Includes the body in case there is no such page available.
 *
 * @author Janne Jalkanen
 *
 * @since 2.0
 */
public class NoSuchPageTag
        extends WikiTagBase
{
    /** DOCUMENT ME! */
    private String m_pageName;

    /**
     * DOCUMENT ME!
     *
     * @param name DOCUMENT ME!
     */
    public void setPage(String name)
    {
        m_pageName = name;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getPage()
    {
        return m_pageName;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     * @throws ProviderException DOCUMENT ME!
     */
    public int doWikiStartTag()
            throws IOException, ProviderException
    {
        WikiEngine engine = m_wikiContext.getEngine();
        WikiPage page;

        if (m_pageName == null)
        {
            page = m_wikiContext.getPage();
        }
        else
        {
            page = engine.getPage(m_pageName);
        }

        if ((page != null) && engine.pageExists(page.getName(), page.getVersion()))
        {
            return SKIP_BODY;
        }

        return EVAL_BODY_INCLUDE;
    }
}
