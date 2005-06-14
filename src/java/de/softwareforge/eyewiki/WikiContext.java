package de.softwareforge.eyewiki;


/*
 * ========================================================================
 *
 * eyeWiki - a WikiWiki clone written in Java
 *
 * ========================================================================
 *
 * Copyright (C) 2005 Henning Schmiedehausen <henning@software-forge.com>
 *
 * based on
 *
 * JSPWiki - a JSP-based WikiWiki clone.
 * Copyright (C) 2002-2005 Janne Jalkanen (Janne.Jalkanen@iki.fi)
 *
 * ========================================================================
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * ========================================================================
 */
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import de.softwareforge.eyewiki.auth.UserProfile;

/**
 * Provides state information throughout the processing of a page.  A WikiContext is born when the JSP pages that are the main
 * entry points, are invoked.  The eyeWiki engine creates the new WikiContext, which basically holds information about the page,
 * the handling engine, and in which context (view, edit, etc) the call was done.
 * 
 * <P>
 * A WikiContext also provides request-specific variables, which can be used to communicate between plugins on the same page, or
 * between different instances of the same plugin.  A WikiContext variable is valid until the processing of the page has ended.
 * For an example, please see the Counter plugin.
 * </p>
 *
 * @author Janne Jalkanen
 *
 * @see de.softwareforge.eyewiki.plugin.Counter
 */
public class WikiContext
        implements Cloneable
{
    /** The VIEW context - the user just wants to view the page contents. */
    public static final String VIEW = "view";

    /** The EDIT context - the user is editing the page. */
    public static final String EDIT = "edit";

    /** User is preparing for a login/authentication. */
    public static final String LOGIN = "login";

    /** User is viewing a diff between the two versions of the page. */
    public static final String DIFF = "diff";

    /** User is viewing page history. */
    public static final String INFO = "info";

    /** User is previewing the changes he just made. */
    public static final String PREVIEW = "preview";

    /** User has an internal conflict, and does quite not know what to do. Please provide some counseling. */
    public static final String CONFLICT = "conflict";

    /** An error has been encountered and the user needs to be informed. */
    public static final String ERROR = "error";

    /** DOCUMENT ME! */
    public static final String UPLOAD = "upload";

    /** DOCUMENT ME! */
    public static final String COMMENT = "comment";

    /** DOCUMENT ME! */
    public static final String FIND = "find";

    /** DOCUMENT ME! */
    public static final String PREFS = "prefs";

    /** DOCUMENT ME! */
    public static final String DELETE = "del";

    /** DOCUMENT ME! */
    public static final String ATTACH = "att";

    /** DOCUMENT ME! */
    public static final String RSS = "rss";

    /** DOCUMENT ME! */
    public static final String NONE = ""; // This is not a eyeWiki context, use it to access static files

    /** DOCUMENT ME! */
    protected WikiPage m_page;

    /** DOCUMENT ME! */
    protected WikiEngine m_engine;

    /** DOCUMENT ME! */
    protected String m_requestContext = VIEW;

    /** DOCUMENT ME! */
    protected String m_template = "default";

    /** DOCUMENT ME! */
    protected Map m_variableMap = new HashMap();

    /** DOCUMENT ME! */
    protected HttpServletRequest m_request = null;

    /** DOCUMENT ME! */
    protected UserProfile m_currentUser;

    /**
     * Create a new WikiContext for the given WikiPage.
     *
     * @param engine The WikiEngine that is handling the request.
     * @param page The WikiPage.  If you want to create a WikiContext for an older version of a page, you must use this
     *        constructor.
     */
    public WikiContext(WikiEngine engine, WikiPage page)
    {
        m_page = page;
        m_engine = engine;
    }

    /**
     * Needed for clone
     */
    private WikiContext()
    {
    }

    /**
     * Needed for clone
     *
     * @param engine DOCUMENT ME!
     */
    private void setEngine(final WikiEngine engine)
    {
        this.m_engine = engine;
    }

    /**
     * Returns the handling engine.
     *
     * @return DOCUMENT ME!
     */
    public WikiEngine getEngine()
    {
        return m_engine;
    }

    /**
     * Returns the page that is being handled.
     *
     * @return DOCUMENT ME!
     */
    public WikiPage getPage()
    {
        return m_page;
    }

    /**
     * Sets the page that is being handled.
     *
     * @param page DOCUMENT ME!
     *
     * @since 2.1.37.
     */
    public void setPage(WikiPage page)
    {
        m_page = page;
    }

    /**
     * Returns the request context.
     *
     * @return DOCUMENT ME!
     */
    public String getRequestContext()
    {
        return m_requestContext;
    }

    /**
     * Sets the request context.  See above for the different request contexts (VIEW, EDIT, etc.)
     *
     * @param arg The request context (one of the predefined contexts.)
     */
    public void setRequestContext(String arg)
    {
        m_requestContext = arg;
    }

    /**
     * Gets a previously set variable.
     *
     * @param key The variable name.
     *
     * @return The variable contents.
     */
    public Object getVariable(String key)
    {
        return m_variableMap.get(key);
    }

    /**
     * Sets a variable.  The variable is valid while the WikiContext is valid, i.e. while page processing continues.  The variable
     * data is discarded once the page processing is finished.
     *
     * @param key The variable name.
     * @param data The variable value.
     */
    public void setVariable(String key, Object data)
    {
        m_variableMap.put(key, data);
    }

    /**
     * This method will safely return any HTTP parameters that might have been defined.  You should use this method instead of
     * peeking directly into the result of getHttpRequest(), since this method is smart enough to do all of the right things,
     * figure out UTF-8 encoded parameters, etc.
     *
     * @param paramName Parameter name to look for.
     *
     * @return HTTP parameter, or null, if no such parameter existed.
     *
     * @since 2.0.13.
     */
    public String getHttpParameter(String paramName)
    {
        String result = null;

        if (m_request != null)
        {
            result = m_engine.safeGetParameter(m_request, paramName);
        }

        return result;
    }

    /**
     * If the request originated from a HTTP server, the HTTP request is stored here.
     *
     * @param req The HTTP servlet request.
     *
     * @since 2.0.13.
     */
    public void setHttpRequest(HttpServletRequest req)
    {
        m_request = req;
    }

    /**
     * If the request did originate from a HTTP request, then the HTTP request can be fetched here. However, it the request did NOT
     * originate from a HTTP request, then this method will return null, and YOU SHOULD CHECK FOR IT!
     *
     * @return Null, if no HTTP request was done.
     *
     * @since 2.0.13.
     */
    public HttpServletRequest getHttpRequest()
    {
        return m_request;
    }

    /**
     * Sets the template to be used for this request.
     *
     * @param dir DOCUMENT ME!
     *
     * @since 2.1.15.
     */
    public void setTemplate(String dir)
    {
        m_template = dir;
    }

    /**
     * Gets the template that is to be used throughout this request.
     *
     * @return DOCUMENT ME!
     *
     * @since 2.1.15.
     */
    public String getTemplate()
    {
        return m_template;
    }

    /**
     * Sets the current user.
     *
     * @param wup DOCUMENT ME!
     */
    public void setCurrentUser(UserProfile wup)
    {
        m_currentUser = wup;
    }

    /**
     * Gets the current user.  May return null, in case the current user has not yet been determined; or this is an internal
     * system.
     *
     * @return DOCUMENT ME!
     */
    public UserProfile getCurrentUser()
    {
        return m_currentUser;
    }

    /**
     * DOCUMENT ME!
     *
     * @param page DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getViewURL(String page)
    {
        return getURL(VIEW, page, null);
    }

    /**
     * DOCUMENT ME!
     *
     * @param context DOCUMENT ME!
     * @param page DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getURL(String context, String page)
    {
        return getURL(context, page, null);
    }

    /**
     * Returns an URL from a page
     *
     * @param context DOCUMENT ME!
     * @param page DOCUMENT ME!
     * @param params DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getURL(String context, String page, String params)
    {
        // FIXME: is rather slow
        return m_engine.getURL(context, page, params, "absolute".equals(m_engine.getVariable(this, WikiEngine.PROP_REFSTYLE)));
    }

    /**
     * Returns a shallow clone of the WikiContext.
     *
     * @return DOCUMENT ME!
     *
     * @throws RuntimeException DOCUMENT ME!
     *
     * @since 2.1.37.
     */
    public Object clone()
    {
        WikiContext copy = null;

        try
        {
            copy = (WikiContext) super.clone();
        }
        catch (CloneNotSupportedException cne)
        {
            throw new RuntimeException("Could not clone WikiContext", cne);
        }

        copy.setEngine(m_engine);
        copy.setPage(m_page);

        copy.setRequestContext(m_requestContext);
        copy.setTemplate(m_template);
        copy.setHttpRequest(m_request);
        copy.setCurrentUser(m_currentUser);
        copy.m_variableMap = m_variableMap;

        return copy;
    }
}
