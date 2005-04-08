package com.ecyrd.jspwiki.auth.permissions;

/**
 * Represents the permission to edit a page.  Also implies the permission to comment on a page
 * (CommentPermission) and uploading of files.
 */
public class CreatePermission
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
        return (p != null) && (p instanceof CreatePermission);
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
        return (p instanceof CreatePermission);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String toString()
    {
        return "CreatePermission";
    }
}
