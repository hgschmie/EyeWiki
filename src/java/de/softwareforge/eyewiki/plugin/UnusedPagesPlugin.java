package de.softwareforge.eyewiki.plugin;

import java.util.Collection;
import java.util.Map;
import java.util.TreeSet;


import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.manager.ReferenceManager;


/**
 * Plugin for displaying pages that are not linked to in other pages. Uses the ReferenceManager.
 *
 * <p>
 * Parameters: none. <BR> From AbstractReferralPlugin:<BR> separator: How to separate generated
 * links; default is a wikitext line break, producing a vertical list.<BR> maxwidth: maximum
 * width, in chars, of generated links.
 * </p>
 *
 * @author Janne Jalkanen
 */
public class UnusedPagesPlugin
        extends AbstractReferralPlugin
        implements WikiPlugin
{

    protected final ReferenceManager referenceManager;

    public UnusedPagesPlugin(final WikiEngine engine, final ReferenceManager referenceManager)
    {
        super(engine);
        this.referenceManager = referenceManager;
    }

    /**
     * DOCUMENT ME!
     *
     * @param context DOCUMENT ME!
     * @param params DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws PluginException DOCUMENT ME!
     */
    public String execute(WikiContext context, Map params)
            throws PluginException
    {
        Collection links = referenceManager.findUnreferenced();

        super.initialize(context, params);

        TreeSet sortedSet = new TreeSet();

        sortedSet.addAll(links);

        String wikitext = wikitizeCollection(sortedSet, m_separator, ALL_ITEMS);

        return makeHTML(context, wikitext);
    }
}
