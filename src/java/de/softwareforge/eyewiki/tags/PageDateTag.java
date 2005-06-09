package de.softwareforge.eyewiki.tags;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.softwareforge.eyewiki.WikiPage;


/**
 * Writes the modification date of the page, formatted as specified in the attribute "format".
 *
 * <UL>
 * <li>
 * format = A string describing which format you want to use. This is exactly like in
 * "java.text.SimpleDateFormat".
 * </li>
 * </ul>
 *
 *
 * @author Janne Jalkanen
 *
 * @since 2.0
 */

// FIXME: Should also take the current user TimeZone into account.
public class PageDateTag
        extends WikiTagBase
{
    /** DOCUMENT ME! */
    public static final String DEFAULT_FORMAT = "dd-MMM-yyyy HH:mm:ss zzz";

    /** DOCUMENT ME! */
    private String m_format = null;

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getFormat()
    {
        if (m_format == null)
        {
            return DEFAULT_FORMAT;
        }

        return m_format;
    }

    /**
     * DOCUMENT ME!
     *
     * @param arg DOCUMENT ME!
     */
    public void setFormat(String arg)
    {
        m_format = arg;
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
            Date d = page.getLastModified();

            //
            //  Date may be null if the page does not exist.
            //
            if (d != null)
            {
                SimpleDateFormat fmt = new SimpleDateFormat(getFormat());

                pageContext.getOut().write(fmt.format(d));
            }
            else
            {
                pageContext.getOut().write("&lt;never&gt;");
            }
        }

        return SKIP_BODY;
    }
}
