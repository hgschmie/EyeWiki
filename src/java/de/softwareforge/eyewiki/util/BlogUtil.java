package de.softwareforge.eyewiki.util;

import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiEngine;


/**
 * Contains useful utilities for eyeWiki blogging functionality.
 *
 * @author Janne Jalkanen
 *
 * @since 2.2.
 */
public final class BlogUtil
{
    /** DOCUMENT ME! */
    public static final String VAR_BLOGNAME = "blogname";

    /**
     * Creates a new BlogUtil object.
     */
    private BlogUtil()
    {
    }

    /**
     * Figure out a site name for a feed.
     *
     * @param context DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static String getSiteName(WikiContext context)
    {
        WikiEngine engine = context.getEngine();

        String blogname = null;

        blogname = engine.getVariableManager().getValue(context, VAR_BLOGNAME, null);

        if (blogname == null)
        {
            blogname = engine.getApplicationName() + ": " + context.getPage().getName();
        }

        return blogname;
    }
}
