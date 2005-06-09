package de.softwareforge.eyewiki.auth;

import java.security.Principal;


/**
 * This is the master class for all wiki UserProfiles and WikiGroups.
 *
 * @author Janne Jalkanen
 *
 * @since 2.2
 */
public abstract class WikiPrincipal
        implements Principal
{
    /** DOCUMENT ME! */
    private String m_name;

    /**
     * Creates a new WikiPrincipal object.
     */
    public WikiPrincipal()
    {
    }

    /**
     * Creates a new WikiPrincipal object.
     *
     * @param name DOCUMENT ME!
     */
    public WikiPrincipal(String name)
    {
        m_name = name;
    }

    /**
     * Returns the WikiName of the Principal.
     *
     * @return DOCUMENT ME!
     */
    public String getName()
    {
        return m_name;
    }

    /**
     * Is used to set the WikiName of the principal.
     *
     * @param arg DOCUMENT ME!
     */
    public void setName(String arg)
    {
        m_name = arg;
    }
}
