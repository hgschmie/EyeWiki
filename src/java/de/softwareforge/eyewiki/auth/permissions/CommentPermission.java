package de.softwareforge.eyewiki.auth.permissions;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class CommentPermission
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
        return (p != null) && (p instanceof CommentPermission);
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
        return (p instanceof CommentPermission);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String toString()
    {
        return "CommentPermission";
    }
}