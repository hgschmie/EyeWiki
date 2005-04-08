package com.ecyrd.jspwiki.auth.permissions;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class ViewPermission
    extends WikiPermission
{
    /**
     * DOCUMENT ME!
     *
     * @param p DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean equals(Object p)
    {
        return (p != null) && (p instanceof ViewPermission);
    }

    /**
     * DOCUMENT ME!
     *
     * @param p DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean implies(WikiPermission p)
    {
        return (p instanceof ViewPermission);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String toString()
    {
        return "ViewPermission";
    }
}
