package de.softwareforge.eyewiki.filters;

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

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;

import de.softwareforge.eyewiki.WikiContext;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class ProfanityFilter
        extends BasicPageFilter
        implements PageFilter
{
    /** DOCUMENT ME! */
    private String [] profanities = { "fuck", "shit" };

    /**
     * Creates a new ProfanityFilter object.
     *
     * @param conf DOCUMENT ME!
     */
    public ProfanityFilter(final Configuration conf)
    {
        super(conf);
    }

    /**
     * DOCUMENT ME!
     *
     * @param context DOCUMENT ME!
     * @param content DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String preTranslate(WikiContext context, String content)
    {
        for (int i = 0; i < profanities.length; i++)
        {
            String word = profanities[i];
            String replacement = word.charAt(0) + "*" + word.charAt(word.length() - 1);

            content = StringUtils.replace(content, word, replacement);
        }

        return content;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isVisible()
    {
        return true;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getPriority()
    {
        return PageFilter.NORMAL_PRIORITY;
    }
}
