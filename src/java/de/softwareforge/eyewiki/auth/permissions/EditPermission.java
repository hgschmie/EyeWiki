package de.softwareforge.eyewiki.auth.permissions;

/**
 * Represents the permission to edit a page.  Also implies the permission to comment on a page
 * (CommentPermission) and uploading of files.
 */
public class EditPermission
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
        return (p != null) && (p instanceof EditPermission);
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
        return (p instanceof CommentPermission) || (p instanceof EditPermission)
        || (p instanceof CreatePermission) || (p instanceof UploadPermission);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String toString()
    {
        return "EditPermission";
    }
}
