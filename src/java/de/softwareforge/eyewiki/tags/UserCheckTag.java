package de.softwareforge.eyewiki.tags;

import java.io.IOException;

import de.softwareforge.eyewiki.auth.UserProfile;


/**
 * Includes the content if an user check validates.  This has been considerably enhanced for 2.2.
 * The possibilities for the "status"-argument are:
 *
 * <ul>
 * <li>
 * "unknown"     - the body of the tag is included if the user is completely unknown (no cookie, no
 * password)
 * </li>
 * <li>
 * "known"       - the body of the tag is included if the user is not unknown (i.e has a cookie, or
 * has been authenticated.
 * </li>
 * <li>
 * "named"       - the body of the tag is included if the user has either been named by a cookie,
 * but not been authenticated.
 * </li>
 * <li>
 * "validated"   - the body of the tag is included if the user is validated either through the
 * container, or by our own authentication.
 * </li>
 * <li>
 * "unvalidated" - the body of the tag is included if the user is not validated (i.e. he could have
 * a cookie, but has not been authenticated.)
 * </li>
 * </ul>
 *
 * If the old "exists" -argument is used, it corresponds as follows:
 *
 * <p>
 * <tt>exists="true" ==> status="known"</tt><tt>exists="false" ==> status="unknown"</tt> It is NOT
 * a good idea to use BOTH of the arguments.
 * </p>
 *
 * @author Janne Jalkanen
 * @author Erik Bunn
 *
 * @since 2.0
 */
public class UserCheckTag
        extends WikiTagBase
{
    /** DOCUMENT ME! */
    private String m_status;

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getStatus()
    {
        return (m_status);
    }

    /**
     * DOCUMENT ME!
     *
     * @param arg DOCUMENT ME!
     */
    public void setStatus(String arg)
    {
        m_status = arg;
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
        UserProfile wup = m_wikiContext.getCurrentUser();

        if (m_status != null)
        {
            if (wup == null)
            {
                // This may happen when strict login policy is used.
                return (SKIP_BODY);
            }

            if ("unknown".equals(m_status) && (wup.getLoginStatus() == UserProfile.NONE))
            {
                return EVAL_BODY_INCLUDE;
            }
            else if ("known".equals(m_status) && (wup.getLoginStatus() > UserProfile.NONE))
            {
                return EVAL_BODY_INCLUDE;
            }
            else if ("named".equals(m_status) && (wup.getLoginStatus() == UserProfile.COOKIE))
            {
                return EVAL_BODY_INCLUDE;
            }
            else if ("validated".equals(m_status) && (wup.getLoginStatus() > UserProfile.CONTAINER))
            {
                return EVAL_BODY_INCLUDE;
            }
            else if (
                "unvalidated".equals(m_status) && (wup.getLoginStatus() < UserProfile.CONTAINER))
            {
                return EVAL_BODY_INCLUDE;
            }
        }

        return SKIP_BODY;
    }
}
