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
