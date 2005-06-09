package de.softwareforge.eyewiki.diff;

import org.apache.log4j.Logger;

import org.picocontainer.PicoContainer;

import de.softwareforge.eyewiki.WikiConstants;
import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.WikiProperties;


/**
 * Load, initialize and delegate to the DiffProvider that will actually do the work.
 *
 * @author John Volkar
 */
public class DifferenceManager
        implements WikiProperties
{
    /** DOCUMENT ME! */
    private static final Logger log = Logger.getLogger(DifferenceManager.class);

    /** DOCUMENT ME! */
    private DiffProvider m_provider;

    /** DOCUMENT ME! */
    private DiffProvider m_rssProvider;

    /**
     * Creates a new DifferenceManager object.
     *
     * @param engine DOCUMENT ME!
     * @param conf DOCUMENT ME!
     */
    public DifferenceManager(WikiEngine engine)
    {
        PicoContainer container = engine.getComponentContainer();

        m_provider = (DiffProvider) container.getComponentInstance(WikiConstants.DIFF_PROVIDER);

        if (m_provider == null)
        {
            m_provider = new NullDiffProvider();
        }

        m_rssProvider = (DiffProvider) container.getComponentInstance(WikiConstants.RSS_DIFF_PROVIDER);

        if (m_rssProvider == null)
        {
            m_rssProvider = new NullDiffProvider();
        }

        if (log.isInfoEnabled())
        {
            if (m_provider != null)
            {
                log.info("Using difference provider " + m_provider.getProviderInfo());
            }

            if (m_rssProvider != null)
            {
                log.info("Using difference provider " + m_rssProvider.getProviderInfo() + " for RSS Feeds");
            }
        }
    }

    /**
     * Returns valid XHTML string to be used in any way you please.
     *
     * @param firstWikiText DOCUMENT ME!
     * @param secondWikiText DOCUMENT ME!
     * @param isRss DOCUMENT ME!
     *
     * @return XHTML, or empty string, if no difference detected.
     */
    public String makeDiff(String firstWikiText, String secondWikiText, boolean isRss)
    {
        String diff =
            isRss
            ? m_provider.makeDiff(firstWikiText, secondWikiText)
            : m_rssProvider.makeDiff(firstWikiText, secondWikiText);

        return (diff != null) ? diff : "";
    }

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Revision$
     */
    private static class NullDiffProvider
            implements DiffProvider
    {
        /**
         * DOCUMENT ME!
         *
         * @param oldWikiText DOCUMENT ME!
         * @param newWikiText DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        public String makeDiff(String oldWikiText, String newWikiText)
        {
            return "You are using the NullDiffProvider, check your properties file.";
        }

        /**
         * DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        public String getProviderInfo()
        {
            return "NullDiffProvider";
        }
    }

}
