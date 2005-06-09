package de.softwareforge.eyewiki.auth;

/**
 * If a proper group/user cannot be located, then we use this class.
 */
public class UndefinedPrincipal
        extends WikiPrincipal
{
    /**
     * Creates a new UndefinedPrincipal object.
     *
     * @param name DOCUMENT ME!
     */
    public UndefinedPrincipal(String name)
    {
        super(name);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String toString()
    {
        return "[Undefined: " + getName() + "]";
    }

    /**
     * DOCUMENT ME!
     *
     * @param o DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean equals(Object o)
    {
        return (o != null) && o instanceof WikiPrincipal
        && ((WikiPrincipal) o).getName().equals(getName());
    }

    public int hashCode()
    {
        return super.hashCode();
    }
}
