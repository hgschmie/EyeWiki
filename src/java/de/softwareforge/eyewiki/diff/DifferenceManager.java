package de.softwareforge.eyewiki.diff;


/*
 * ========================================================================
 *
 * eyeWiki - a WikiWiki clone written in Java
 *
 * ========================================================================
 *
 * Copyright (C) 2005 Henning Schmiedehausen <henning@software-forge.com>
 *
 * based on
 *
 * JSPWiki - a JSP-based WikiWiki clone.
 * Copyright (C) 2002-2005 Janne Jalkanen (Janne.Jalkanen@iki.fi)
 *
 * ========================================================================
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * ========================================================================
 */
import org.apache.log4j.Logger;

import de.softwareforge.eyewiki.WikiConstants;
import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.WikiProperties;

import org.picocontainer.PicoContainer;

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
            isRss ? m_provider.makeDiff(firstWikiText, secondWikiText) : m_rssProvider.makeDiff(firstWikiText, secondWikiText);

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
