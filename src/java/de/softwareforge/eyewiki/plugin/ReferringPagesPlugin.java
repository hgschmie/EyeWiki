package de.softwareforge.eyewiki.plugin;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.WikiPage;
import de.softwareforge.eyewiki.manager.ReferenceManager;
import de.softwareforge.eyewiki.util.TextUtil;


/**
 * Displays the pages referring to the current page. Parameters: <BR> max: How many items to
 * show.<BR> extras: How to announce extras.<BR> From AbstractReferralPlugin:<BR> separator: How
 * to separate generated links; default is a wikitext line break, producing a vertical list.<BR>
 * maxwidth: maximum width, in chars, of generated links.
 *
 * @author Janne Jalkanen
 */
public class ReferringPagesPlugin
        extends AbstractReferralPlugin
        implements WikiPlugin
{
    /** DOCUMENT ME! */
    private static Logger log = Logger.getLogger(ReferringPagesPlugin.class);

    /** DOCUMENT ME! */
    public static final String PARAM_MAX = "max";

    /** DOCUMENT ME! */
    public static final String PARAM_EXTRAS = "extras";

    protected final ReferenceManager referenceManager;

    public ReferringPagesPlugin(final WikiEngine engine, final ReferenceManager referenceManager)
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
        WikiPage page = context.getPage();

        if (page != null)
        {
            Collection links = referenceManager.findReferrers(page.getName());
            String wikitext;

            super.initialize(context, params);

            int items = TextUtil.parseIntParameter((String) params.get(PARAM_MAX), ALL_ITEMS);
            String extras = (String) params.get(PARAM_EXTRAS);

            if (extras == null)
            {
                extras = "...and %d more\\\\";
            }

            if (log.isDebugEnabled())
            {
                log.debug(
                    "Fetching referring pages for " + context.getPage().getName()
                    + " with a max of " + items);
            }

            if ((links != null) && (links.size() > 0))
            {
                wikitext = wikitizeCollection(links, m_separator, items);

                if ((items < links.size()) && (items > 0))
                {
                    extras = StringUtils.replace(extras, "%d", "" + (links.size() - items));
                    wikitext += extras;
                }
            }
            else
            {
                wikitext = "...nobody";
            }

            return makeHTML(context, wikitext);
        }

        return "";
    }
}
