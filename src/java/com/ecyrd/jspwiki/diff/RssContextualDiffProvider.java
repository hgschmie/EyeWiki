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

import com.ecyrd.jspwiki.NoRequiredPropertyException;
import com.ecyrd.jspwiki.WikiEngine;


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
    public RssContextualDiffProvider()
    {
        super();
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

    /**
     * @see com.ecyrd.jspwiki.WikiProvider#getProviderInfo()
     */
    public String getProviderInfo()
    {
        return "RssContextualDiffProvider";
    }

    /**
     * @see com.ecyrd.jspwiki.WikiProvider#initialize(com.ecyrd.jspwiki.WikiEngine,
     *      java.util.Properties)
     */
    public void initialize(WikiEngine engine, Configuration conf)
            throws NoRequiredPropertyException, IOException
    {
        super.initialize(engine, conf);
    }
}
