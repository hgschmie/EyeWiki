package de.softwareforge.eyewiki.diff;

import org.apache.commons.configuration.Configuration;

import de.softwareforge.eyewiki.WikiEngine;


/**
 * This is a diff provider for the RSS Feed using the Traditional Provider as its base.
 *
 * @author Janne Jalkanen
 * @author Erik Bunn
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 */
public class RssTraditionalDiffProvider
        extends TraditionalDiffProvider
        implements DiffProvider
{
    /**
     * Creates a new RssTraditionalDiffProvider object.
     */
    public RssTraditionalDiffProvider(WikiEngine engine, Configuration conf)
    {
        super(engine, conf);
        diffAdd = "<font color=\"#99FF99\">";
        diffRem = "<font color=\"#FF9933\">";
        diffComment = "<font color=\"white\">";
        diffClose = "</font>\n";

        diffPrefix = "";
        diffPostfix = "\n";
    }
}
