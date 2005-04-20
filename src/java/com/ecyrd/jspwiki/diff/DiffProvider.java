package com.ecyrd.jspwiki.diff;

import com.ecyrd.jspwiki.WikiProvider;


/**
 * TODO
 */
public interface DiffProvider
        extends WikiProvider
{
    /**
     * The return string is to be XHTML compliant ready to display html.  No further processing of
     * this text will be done by the wiki engine.
     *
     * @param oldWikiText DOCUMENT ME!
     * @param newWikiText DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    String makeDiff(String oldWikiText, String newWikiText);
}
