package de.softwareforge.eyewiki.tags;

import java.io.IOException;


import de.softwareforge.eyewiki.WikiPage;
import de.softwareforge.eyewiki.attachment.Attachment;


/**
 * Includes the body, if the current page is of proper type. <B>Attributes</B>
 *
 * <UL>
 * <li>
 * type - either "page", "attachment" or "weblogentry"
 * </li>
 * </ul>
 *
 *
 * @author Janne Jalkanen
 *
 * @since 2.0
 */
public class PageTypeTag
        extends WikiTagBase
{
    /** DOCUMENT ME! */
    private String m_type;

    /**
     * DOCUMENT ME!
     *
     * @param arg DOCUMENT ME!
     */
    public void setType(String arg)
    {
        m_type = arg.toLowerCase();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    public final int doWikiStartTag()
            throws IOException
    {
        WikiPage page = m_wikiContext.getPage();

        if (page != null)
        {
            if (m_type.equals("attachment") && page instanceof Attachment)
            {
                return EVAL_BODY_INCLUDE;
            }

            if (m_type.equals("page") && !(page instanceof Attachment))
            {
                return EVAL_BODY_INCLUDE;
            }

            if (
                m_type.equals("weblogentry") && !(page instanceof Attachment)
                            && (page.getName().indexOf("_blogentry_") != -1))
            {
                return EVAL_BODY_INCLUDE;
            }
        }

        return SKIP_BODY;
    }
}
