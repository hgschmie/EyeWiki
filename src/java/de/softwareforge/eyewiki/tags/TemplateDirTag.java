package de.softwareforge.eyewiki.tags;

import java.io.IOException;


/**
 * Returns the currently used template.  For example "default"
 *
 * @author Janne Jalkanen
 *
 * @since 2.1.15.
 */
public class TemplateDirTag
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
        String template = m_wikiContext.getTemplate();

        pageContext.getOut().print(template);

        return SKIP_BODY;
    }
}
