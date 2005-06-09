package de.softwareforge.eyewiki.acl;

import java.security.Principal;
import java.security.acl.Acl;
import java.security.acl.AclEntry;
import java.security.acl.Permission;


/**
 * Defines the eyeWiki-specific helper methods for accessing the Acl.
 *
 * @author Janne Jalkanen
 */
public interface AccessControlList
        extends Acl
{
    /** DOCUMENT ME! */
    int ALLOW = 1;

    /** DOCUMENT ME! */
    int DENY = -1;

    /** DOCUMENT ME! */
    int NONE = 0;

    /**
     * DOCUMENT ME!
     *
     * @param principal DOCUMENT ME!
     * @param permission DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    int findPermission(Principal principal, Permission permission);

    /**
     * DOCUMENT ME!
     *
     * @param principal DOCUMENT ME!
     * @param isNegative DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    AclEntry getEntry(Principal principal, boolean isNegative);
}
