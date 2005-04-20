package com.ecyrd.jspwiki.auth;

import com.ecyrd.jspwiki.WikiPage;
import com.ecyrd.jspwiki.acl.AccessControlList;


/**
 * Provides a mean to gather permissions for different pages.  For example, a WikiPageAuthorizer
 * could fetch the authorization data from the page in question.
 *
 * @author Janne Jalkanen
 */
public interface WikiAuthorizer
{
    /**
     * Returns the permissions for this page.
     *
     * @param page DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    AccessControlList getPermissions(WikiPage page);

    /**
     * Returns the default permissions.  For example, fetch always from a page called
     * "DefaultPermissions".
     *
     * @return DOCUMENT ME!
     */
    AccessControlList getDefaultPermissions();
}
