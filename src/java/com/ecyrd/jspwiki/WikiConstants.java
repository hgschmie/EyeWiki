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
package com.ecyrd.jspwiki;

/**
 * Here we collect all the constants used all over the Wiki code
 */
public interface WikiConstants
{
    /** The default Character Encoding */
    String DEFAULT_ENCODING = "UTF-8";

    /*
     * ========================================================================
     * 
     * Used Styles
     *
     * ========================================================================
     */

    /** Class for the internal wiki content rendering */
    String CSS_WIKICONTENT = "wikicontent";
    
    /** Class for an internal wiki page. Used with A HREF tags. */
    String CSS_LINK_WIKIPAGE = "wikipage";

    /** Class for an internal link target. Used with A tags */
    String CSS_ANCHOR = "wikianchor";

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

    /** Image class for an inline image. Used with IMG tags. */
    String CSS_IMG_INLINE = "inline";

    /** Image class for an external image. Used with IMG tags. */
    String CSS_IMG_OUTLINK = "outlink";

    /** Generic Error CSS. Used with SPAN, P tags. */
    String CSS_CLASS_ERROR = "error";

    /** Class for a Wiki internal form */
    String CSS_FORM_WIKIFORM = "wikiform";

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

    /** Class for building the index page. Used with DIV, H1, A, H2 tags */
    String CSS_INDEX = "index";

    /** Class to wrap around the index. Used with DIV tags */
    String CSS_INDEX_BODY = "indexbody";

    /** Class to wrap around a calendar. Used with TABLE */
    String CSS_CALENDAR = "calendar";

    /** Class to wrap around a calendar day element. Used with TD */
    String CSS_CALENDAR_DAY = "calendarday";

    /** Class to wrap around a calendar week day element. Used with TD */
    String CSS_CALENDAR_WEEKDAY = "calendarweekday";

    /** Class to wrap around a calendar month element. Used with TD */
    String CSS_CALENDAR_MONTH = "calendarmonth";

    /** Class to wrap around an alternative  calendar month element. Used with TD */
    String CSS_CALENDAR_OTHERMONTH = "calendarothermonth";

    /** Class for the RSS related Styles. Used with A and IMG tags */
    String CSS_RSS = "rss";

    /** Class for the Debugging Styles. Used with DIV, PRE, B, LI */
    String CSS_DEBUG = "debug";

    /** Class for Search elements. Used with TABLE, TH, TD, A Tags */
    String CSS_SEARCH = "pagefind";

    /** Class for Search Score elements. Used with TD, TH Tags */
    String CSS_SEARCHSCORE = "pagefindscore";

    /** Class for Editor. Used with TEXTAREA tags */
    String CSS_EDITOR = "editor";
}
