package de.softwareforge.eyewiki.tags;

import java.io.IOException;


import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.WikiPage;
import de.softwareforge.eyewiki.providers.ProviderException;


/**
 * Does a version check on the page.  Mode is as follows:
 *
 * <UL>
 * <li>
 * latest = Include page content, if the page is the latest version.
 * </li>
 * <li>
 * notlatest = Include page content, if the page is NOT the latest version.
 * </li>
 * </ul>
 *
 * If the page does not exist, body content is never included.
 *
 * @author Janne Jalkanen
 *
 * @since 2.0
 */
public class CheckVersionTag
        extends WikiTagBase
{
    /** DOCUMENT ME! */
    public static final int LATEST = 0;

    /** DOCUMENT ME! */
    public static final int NOTLATEST = 1;

    /** DOCUMENT ME! */
    public static final int FIRST = 2;

    /** DOCUMENT ME! */
    public static final int NOTFIRST = 3;

    /** DOCUMENT ME! */
    private int m_mode;

    /**
     * DOCUMENT ME!
     *
     * @param arg DOCUMENT ME!
     */
    public void setMode(String arg)
    {
        if ("latest".equals(arg))
        {
            m_mode = LATEST;
        }
        else if ("notfirst".equals(arg))
        {
            m_mode = NOTFIRST;
        }
        else if ("first".equals(arg))
        {
            m_mode = FIRST;
        }
        else
        {
            m_mode = NOTLATEST;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     * @throws ProviderException DOCUMENT ME!
     */
    public final int doWikiStartTag()
            throws IOException, ProviderException
    {
        WikiEngine engine = m_wikiContext.getEngine();
        WikiPage page = m_wikiContext.getPage();

        if ((page != null) && engine.pageExists(page))
        {
            int version = page.getVersion();
            boolean include = false;

            WikiPage latest = engine.getPage(page.getName());

            //log.debug("Doing version check: this="+page.getVersion()+
            //          ", latest="+latest.getVersion());
            switch (m_mode)
            {
            case LATEST:
                include = (version < 0) || (latest.getVersion() == version);

                break;

            case NOTLATEST:
                include = (version > 0) && (latest.getVersion() != version);

                break;

            case FIRST:
                include = (version == 1) || ((version < 0) && (latest.getVersion() == 1));

                break;

            case NOTFIRST:
                include = (version > 1);

                break;

            default:
                break;
            }

            if (include)
            {
                // log.debug("INCLD");
                return EVAL_BODY_INCLUDE;
            }
        }

        return SKIP_BODY;
    }
}
