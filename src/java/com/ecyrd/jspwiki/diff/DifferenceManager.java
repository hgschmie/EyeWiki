/*
   JSPWiki - a JSP-based WikiWiki clone.

   Copyright (C) 2001-2002 Janne Jalkanen (Janne.Jalkanen@iki.fi)

   This program is free software; you can redistribute it and/or modify
   it under the terms of the GNU Lesser General Public License as published by
   the Free Software Foundation; either version 2.1 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General Public License
   along with this program; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
package com.ecyrd.jspwiki.diff;

import java.io.IOException;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;

import com.ecyrd.jspwiki.WikiEngine;
import com.ecyrd.jspwiki.WikiProperties;
import com.ecyrd.jspwiki.exception.NoRequiredPropertyException;
import com.ecyrd.jspwiki.util.ClassUtil;


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
    public DifferenceManager(WikiEngine engine, Configuration conf)
    {
        loadProvider(conf);

        initializeProvider(engine, conf);

        if (log.isInfoEnabled())
        {
            log.info("Using difference provider: " + m_provider.getProviderInfo());
        }
    }

    private void loadProvider(Configuration conf)
    {
        String providerClassName =
            conf.getString(PROP_CLASS_DIFF_PROVIDER, PROP_CLASS_DIFF_PROVIDER_DEFAULT);

        m_provider = getProvider(providerClassName, new DiffProvider.NullDiffProvider());

        providerClassName =
            conf.getString(PROP_CLASS_DIFF_RSS_PROVIDER, PROP_CLASS_DIFF_RSS_PROVIDER_DEFAULT);

        m_rssProvider = getProvider(providerClassName, m_provider);
    }

    private DiffProvider getProvider(String className, DiffProvider defaultProvider)
    {
        try
        {
            Class providerClass = ClassUtil.findClass(DEFAULT_DIFF_CLASS_PREFIX, className);

            return (DiffProvider) providerClass.newInstance();
        }
        catch (ClassNotFoundException e)
        {
            log.warn(
                "Failed loading " + className + ", will use Default: "
                + defaultProvider.getClass().getName(), e);
        }
        catch (InstantiationException e)
        {
            log.warn(
                "Failed loading " + className + ", will use Default: "
                + defaultProvider.getClass().getName(), e);
        }
        catch (IllegalAccessException e)
        {
            log.warn(
                "Failed loading " + className + ", will use Default: "
                + defaultProvider.getClass().getName(), e);
        }

        return defaultProvider;
    }

    private void initializeProvider(WikiEngine engine, Configuration conf)
    {
        try
        {
            m_provider.initialize(engine, conf);
        }
        catch (NoRequiredPropertyException e1)
        {
            log.warn("Failed initializing DiffProvider, will use NullDiffProvider.", e1);
            m_provider = new DiffProvider.NullDiffProvider(); //doesn't need init'd
        }
        catch (IOException e1)
        {
            log.warn("Failed initializing DiffProvider, will use NullDiffProvider.", e1);
            m_provider = new DiffProvider.NullDiffProvider(); //doesn't need init'd
        }

        try
        {
            m_rssProvider.initialize(engine, conf);
        }
        catch (NoRequiredPropertyException e1)
        {
            log.warn("Failed initializing RssDiffProvider, will use NullDiffProvider.", e1);
            m_rssProvider = new DiffProvider.NullDiffProvider(); //doesn't need init'd
        }
        catch (IOException e1)
        {
            log.warn("Failed initializing RssDiffProvider, will use NullDiffProvider.", e1);
            m_rssProvider = new DiffProvider.NullDiffProvider(); //doesn't need init'd
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

        return (diff != null)
        ? diff
        : "";
    }
}
