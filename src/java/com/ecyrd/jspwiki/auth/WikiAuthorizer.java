package com.ecyrd.jspwiki.auth;

import org.apache.commons.configuration.Configuration;

import com.ecyrd.jspwiki.WikiEngine;
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
     * Initializes a WikiAuthorizer.
     *
     * @param engine The WikiEngine that owns this authorizer.
     * @param conf A bunch of properties.
     */
    public void initialize(WikiEngine engine, Configuration conf);

    /**
     * Returns the permissions for this page.
     *
     * @param page DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public AccessControlList getPermissions(WikiPage page);

    /**
     * Returns the default permissions.  For example, fetch always from a page called
     * "DefaultPermissions".
     *
     * @return DOCUMENT ME!
     */
    public AccessControlList getDefaultPermissions();
}
