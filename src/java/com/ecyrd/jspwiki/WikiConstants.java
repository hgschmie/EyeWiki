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

    /** Class for an internal wiki page. Used with A HREF tags. */
    String LINK_WIKIPAGE = "wikipage";

    /** Class for referencing a footnote. Used with A HREF tags. */
    String LINK_FOOTNOTE_REF = "footnoteref";

    /** Class for the footnote anchor. Used with A tags. */
    String LINK_FOOTNOTE_ANCHOR = "footnote";

    /** Class for an external link. Used with A HREF tags. */
    String LINK_EXTERNAL = "external"; 

    /** Class for an interwiki link. Used with A HREF tags. */
    String LINK_INTERWIKI = "interwiki";

    /** Class for an attachment. Used with A HREF tags. */
    String LINK_ATTACHMENT = "attachment";

    /** Class for a page info link. Used with A HREF tags */
    String LINK_PAGEINFO = "pageinfo";

    /** Class for a diff internal link (next/prev). Used with A HREF tags */
    String LINK_DIFF = "diff";

    /** Image class for an inline image. Used with IMG tags. */
    String IMG_INLINE = "inline";

    /** Image class for an external image. Used with IMG tags. */
    String IMG_OUTLINK = "outlink";

    /** Generic Error CSS. Used with SPAN, P tags. */
    String CSS_CLASS_ERROR = "error";

    /** Class for a Wiki internal table. Used with TABLE tags. */
    String TABLE_WIKITABLE = "wikitable";

    /** Class for a Wiki internal form */
    String FORM_WIKIFORM = "wikiform";

    /** Class for an added Element in a diff. Used with TD, SPAN tags */
    String DIFF_ADD = "diffadd";

    /** Class for a removed Element in a diff. Used with TD, SPAN tags */
    String DIFF_REM = "diffrem";

    /** Class for commenting changes in a diff. Used with TD tags */
    String DIFF = "diff";

    /** Class for encapsulating diff changes. Used with DIV, TABLE tags */
    String DIFF_BLOCK = "diffblock";

    /** Class for encapsulating a web log. Used with DIV, LI tags */
    String WEBLOG_BODY = "weblog";

    /** Class for encapsulating a single web log entry. Used with DIV tags */
    String WEBLOG_ENTRY = "weblogentry";

    /** Class for encapsulating the web log entry header. Used with DIV tags */
    String WEBLOG_ENTRY_HEADER = "weblogentryheader";

    /** Class for encapsulating the web log entry body. Used with DIV tags */
    String WEBLOG_ENTRY_BODY = "weblogentrybody";

    /** Class for encapsulating the web log entry footer. Used with DIV tags */
    String WEBLOG_ENTRY_FOOTER = "weblogentryfooter";

    /** Class for the Table of Contents. Used with DIV, H1 tags */
    String TOC = "toc";

    /** Class for building the index page. Used with DIV, H1, A, H2 tags */
    String INDEX = "index";

    /** Class to wrap around the index. Used with DIV tags */
    String INDEX_BODY = "indexbody";

    /** Class to wrap around a calendar. Used with TABLE */
    String CALENDAR = "calendar";

    /** Class to wrap around a calendar day element. Used with TD */
    String CALENDAR_DAY = "calendarday";

    /** Class to wrap around a calendar week day element. Used with TD */
    String CALENDAR_WEEKDAY = "calendarweekday";

    /** Class to wrap around a calendar month element. Used with TD */
    String CALENDAR_MONTH = "calendarmonth";

    /** Class to wrap around an alternative  calendar month element. Used with TD */
    String CALENDAR_OTHERMONTH = "calendarothermonth";

    /** Class for the RSS related Styles. Used with A and IMG tags */
    String RSS = "rss";

    /** Class for the Debugging Styles. Used with DIV, PRE, B, LI */
    String DEBUG = "debug";

    /** Class for Search elements. Used with TABLE, TH, TD, A Tags */
    String SEARCH = "pagefind";

    /** Class for Search Score elements. Used with TD, TH Tags */
    String SEARCHSCORE = "pagefindscore";

    /** Class for Editor. Used with TEXTAREA tags */
    String EDITOR = "editor";
}
