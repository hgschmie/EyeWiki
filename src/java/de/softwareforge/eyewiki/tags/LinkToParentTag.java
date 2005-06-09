package de.softwareforge.eyewiki.tags;

import java.io.IOException;


import de.softwareforge.eyewiki.WikiPage;
import de.softwareforge.eyewiki.attachment.Attachment;


/**
 * Writes a link to a parent of a Wiki page.
 *
 * <P>
 * <B>Attributes</B>
 * </p>
 *
 * <UL>
 * <li>
 * page - Page name to refer to.  Default is the current page.
 * </li>
 * <li>
 * format - either "anchor" or "url" to output either an &lt;A&gt;... or just the HREF part of one.
 * </li>
 * </ul>
 *
 *
 * @author Janne Jalkanen
 *
 * @since 2.0
 */
public class LinkToParentTag
        extends LinkToTag
{
    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    public int doWikiStartTag()
            throws IOException
    {
        WikiPage p = m_wikiContext.getPage();

        //
        //  We just simply set the page to be our parent page
        //  and call the superclass.
        //
        if (p instanceof Attachment)
        {
            setPage(((Attachment) p).getParentName());
        }
        else
        {
            String name = p.getName();

            int entrystart = name.indexOf("_blogentry_");

            if (entrystart != -1)
            {
                setPage(name.substring(0, entrystart));
            }

            int commentstart = name.indexOf("_comments_");

            if (commentstart != -1)
            {
                setPage(name.substring(0, commentstart));
            }
        }

        return super.doWikiStartTag();
    }
}
