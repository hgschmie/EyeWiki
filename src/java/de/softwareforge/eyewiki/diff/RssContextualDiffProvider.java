package de.softwareforge.eyewiki.diff;

import org.apache.commons.configuration.Configuration;

import de.softwareforge.eyewiki.WikiEngine;


/**
 * This is a diff provider for the RSS Feed using the Contextual Provider as its base.
 *
 * @author Janne Jalkanen
 * @author Erik Bunn
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 */
public class RssContextualDiffProvider
        extends ContextualDiffProvider
        implements DiffProvider
{
    /**
     * Creates a new RssContextualDiffProvider object.
     */
    public RssContextualDiffProvider(WikiEngine engine, Configuration conf)
    {
        super(engine, conf);
        m_emitChangeNextPreviousHyperlinks = false;

        m_changeStartHtml = ""; //This could be a image '>' for a start marker
        m_changeEndHtml = ""; //and an image for an end '<' marker

        m_diffStart = "";
        m_diffEnd = "";

        m_insertionStartHtml = "<font color=\"blue\">";
        m_insertionEndHtml = "</font>";

        m_deletionStartHtml = "<font color=\"red\">";
        m_deletionEndHtml = "</font>";
    }
}
