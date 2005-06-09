package de.softwareforge.eyewiki.tags;

import java.io.IOException;


import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.WikiPage;
import de.softwareforge.eyewiki.providers.ProviderException;


/**
 * Returns the currently requested page or attachment size.
 *
 * @author Janne Jalkanen
 *
 * @since 2.0
 */
public class PageSizeTag
        extends WikiTagBase
{
    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    public final int doWikiStartTag()
            throws IOException
    {
        WikiEngine engine = m_wikiContext.getEngine();
        WikiPage page = m_wikiContext.getPage();

        try
        {
            if (page != null)
            {
                long size = page.getSize();

                if ((size == -1) && engine.pageExists(page)) // should never happen with attachments
                {
                    size = engine.getPureText(page.getName(), page.getVersion()).length();
                    page.setSize(size);
                }

                pageContext.getOut().write(Long.toString(size));
            }
        }
        catch (ProviderException e)
        {
            log.warn("Providers did not work: ", e);
            pageContext.getOut().write("Error determining page size: " + e.getMessage());
        }

        return SKIP_BODY;
    }
}
