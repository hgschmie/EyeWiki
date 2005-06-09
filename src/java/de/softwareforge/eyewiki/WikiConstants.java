/*
    JSPWiki - a JSP-based WikiWiki clone.

    Copyright (C) 2001-2004 Janne Jalkanen (Janne.Jalkanen@iki.fi)

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation; either version 2.1 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package de.softwareforge.eyewiki;

/**
 * Here we collect all the constants used all over the Wiki code
 */
public interface WikiConstants
{
    /** The default Character Encoding */
    String DEFAULT_ENCODING = "UTF-8";

    /*
     *
     * ========================================================================
     *
     * The PicoContainer keys
     *
     * ========================================================================
     *
     */

    /** The key used to determine the URL Constructor */
    String URL_CONSTRUCTOR = "URLConstructor";

    /**
     * DOCUMENT ME!
     */
    String PAGE_MANAGER = "PageManager";

    /**
     * DOCUMENT ME!
     */
    String PLUGIN_MANAGER = "PluginManager";

    /**
     * DOCUMENT ME!
     */
    String DIFFERENCE_MANAGER = "DifferenceManager";

    /**
     * DOCUMENT ME!
     */
    String ATTACHMENT_MANAGER = "AttachmentManager";

    /**
     * DOCUMENT ME!
     */
    String VARIABLE_MANAGER = "VariableManager";

    /**
     * DOCUMENT ME!
     */
    String FILTER_MANAGER = "FilterManager";

    /**
     * DOCUMENT ME!
     */
    String REFERENCE_MANAGER = "ReferenceManager";

    /**
     * DOCUMENT ME!
     */
    String USER_MANAGER = "UserManager";

    /**
     * DOCUMENT ME!
     */
    String AUTHORIZATION_MANAGER = "AuthorizationManager";

    /**
     * DOCUMENT ME!
     */
    String TEMPLATE_MANAGER = "TemplateManager";

    /**
     * DOCUMENT ME!
     */
    String RSS_GENERATOR = "RSSGenerator";

    /** The Page Provider */
    String PAGE_PROVIDER = "PageProvider";

    /** The real Page Provider for the Caching Provider */
    String REAL_PAGE_PROVIDER = "RealPageProvider";

    /** The Attachment Provider */
    String ATTACHMENT_PROVIDER = "AttachmentProvider";

    /** The real Attachment Provider for the Caching Provider */
    String REAL_ATTACHMENT_PROVIDER = "RealAttachmentProvider";

    /** The Provider used for diff generation */
    String DIFF_PROVIDER = "DiffProvider";

    /** The Provider used for diff generation in RSS feeds */
    String RSS_DIFF_PROVIDER = "RssDiffProvider";

    /** The Authorizer to use */
    String AUTHORIZER = "Authorizer";

    /** The Authenticator to use */
    String AUTHENTICATOR = "Authenticator";

    /** The User database to use */
    String USER_DATABASE = "UserDatabase";

    /*
     * ========================================================================
     *
     * Used Styles
     *
     * ========================================================================
     */

    /** Class for an internal link target. Used with A tags */
    String CSS_ANCHOR = "wikianchor";

    /** Content Tag */
    String CSS_WIKICONTENT = "wikicontent";

    /** Class for referencing a footnote. Used with A HREF tags. */
    String CSS_LINK_FOOTNOTE_REF = "footnoteref";

    /** Class for the footnote anchor. Used with A tags. */
    String CSS_LINK_FOOTNOTE_ANCHOR = "footnote";

    /** Class for an external link. Used with A HREF tags. */
    String CSS_LINK_EXTERNAL = "external";

    /** Class for an interwiki link. Used with A HREF tags. */
    String CSS_LINK_INTERWIKI = "interwiki";

    /** Class for an attachment. Used with A HREF tags. */
    String CSS_LINK_ATTACHMENT = "attachment";

    /** Class for a page info link. Used with A HREF tags */
    String CSS_LINK_PAGEINFO = "pageinfo";

    /** Class for a diff internal link (next/prev). Used with A HREF tags */
    String CSS_LINK_DIFF = "diff";

    /** Class for a breadcrumbs link. Used with HREF tags */
    String CSS_LINK_BREADCRUMBS = "breadcrumbs";

    /** Class for building the index page. Used with A  tags */
    String CSS_LINK_INDEX = "index";

    /** Generic Error CSS. Used with SPAN, P tags. */
    String CSS_CLASS_ERROR = "error";

    /** String for a diff anchor target. Used with A tags */
    String CSS_DIFF_ANCHOR = "diffanchor";

    /** Class for an added Element in a diff. Used with TD, SPAN tags */
    String CSS_DIFF_ADD = "diffadd";

    /** Class for a removed Element in a diff. Used with TD, SPAN tags */
    String CSS_DIFF_REM = "diffrem";

    /** Class for commenting changes in a diff. Used with TD tags */
    String CSS_DIFF = "diff";

    /** Class for encapsulating diff changes. Used with DIV, TABLE tags */
    String CSS_DIFF_BLOCK = "diffblock";

    /** Class for encapsulating a web log. Used with DIV, LI tags */
    String CSS_WEBLOG_BODY = "weblog";

    /** Class for encapsulating a single web log entry. Used with DIV tags */
    String CSS_WEBLOG_ENTRY = "weblogentry";

    /** Class for encapsulating the web log entry header. Used with DIV tags */
    String CSS_WEBLOG_ENTRY_HEADER = "weblogentryheader";

    /** Class for encapsulating the web log entry body. Used with DIV tags */
    String CSS_WEBLOG_ENTRY_BODY = "weblogentrybody";

    /** Class for encapsulating the web log entry footer. Used with DIV tags */
    String CSS_WEBLOG_ENTRY_FOOTER = "weblogentryfooter";

    /** Class for the Table of Contents. Used with DIV, H1 tags */
    String CSS_TOC = "toc";

    /** Class to wrap around a calendar. Used with TABLE */
    String CSS_CALENDAR = "calendar";

    /** Class for the RSS related Styles. Used with A and IMG tags */
    String CSS_RSS = "rss";

    /** Class for Search elements. Used with TABLE, TH, TD, A Tags */
    String CSS_SEARCH = "pagefind";

    /** Class for Search Score elements. Used with TD, TH Tags */
    String CSS_SEARCHSCORE = "pagefindscore";

    /** Class for Preview Text Used with H3 tags */
    String CSS_PREVIEW = "preview";
}
