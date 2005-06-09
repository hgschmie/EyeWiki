package de.softwareforge.eyewiki.tags;

import java.io.IOException;


import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.WikiPage;
import de.softwareforge.eyewiki.auth.AuthorizationManager;
import de.softwareforge.eyewiki.auth.UserProfile;


/**
 * Tells if a page may be edited.  This tag takes care of all possibilities, user permissions, page
 * version, etc.
 *
 * @author Janne Jalkanen
 *
 * @since 2.0
 */
public class PermissionTag
        extends WikiTagBase
{
    /** DOCUMENT ME! */
    private String m_permission;

    /**
     * DOCUMENT ME!
     *
     * @param permission DOCUMENT ME!
     */
    public void setPermission(String permission)
    {
        m_permission = permission;
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
        WikiEngine engine = m_wikiContext.getEngine();
        WikiPage page = m_wikiContext.getPage();
        AuthorizationManager mgr = engine.getAuthorizationManager();
        boolean gotPermission = false;
        UserProfile userprofile = m_wikiContext.getCurrentUser();

        if (page != null)
        {
            gotPermission = mgr.checkPermission(page, userprofile, m_permission);
        }

        return gotPermission
                ? EVAL_BODY_INCLUDE
                : SKIP_BODY;
    }
}
