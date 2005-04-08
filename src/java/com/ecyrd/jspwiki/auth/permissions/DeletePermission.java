package com.ecyrd.jspwiki.auth.permissions;

/**
 * Represents a permission to delete a page or versions of it.  Also implies a permission to edit
 * or comment a page.
 */
public class DeletePermission
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
        return (p != null) && (p instanceof DeletePermission);
    }

    public int hashCode()
    {
        return super.hashCode();
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
        return (p instanceof EditPermission || p instanceof DeletePermission
        || p instanceof CommentPermission);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String toString()
    {
        return "DeletePermission";
    }
}
