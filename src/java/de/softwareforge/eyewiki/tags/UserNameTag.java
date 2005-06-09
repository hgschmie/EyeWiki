package de.softwareforge.eyewiki.tags;

import java.io.IOException;


import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.auth.UserManager;
import de.softwareforge.eyewiki.auth.UserProfile;


/**
 * Returns the current user name, or empty, if the user has not been validated.
 *
 * @author Janne Jalkanen
 *
 * @since 2.0
 */
public class UserNameTag
        extends WikiTagBase
{
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
        WikiEngine engine = m_wikiContext.getEngine();
        UserManager mgr = engine.getUserManager();

        UserProfile user =
            mgr.getUserProfile((javax.servlet.http.HttpServletRequest) pageContext.getRequest());

        if (user != null)
        {
            pageContext.getOut().print(user.getName());
        }

        return SKIP_BODY;
    }
}
