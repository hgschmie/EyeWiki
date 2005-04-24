package com.ecyrd.jspwiki.filters;

import com.ecyrd.jspwiki.WikiContext;


/**
 * Provides a base implementation of a PageFilter.  None of the methods do anything, so it is a
 * good idea for you to extend from this class and implement only methods that you need.
 *
 * @author Janne Jalkanen
 */
public abstract class BasicPageFilter
        implements PageFilter
{

    public abstract boolean isVisible();

    public abstract int getPriority();

    /**
     * DOCUMENT ME!
     *
     * @param wikiContext DOCUMENT ME!
     * @param content DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws FilterException DOCUMENT ME!
     */
    public String preTranslate(WikiContext wikiContext, String content)
            throws FilterException
    {
        return content;
    }

    /**
     * DOCUMENT ME!
     *
     * @param wikiContext DOCUMENT ME!
     * @param htmlContent DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws FilterException DOCUMENT ME!
     */
    public String postTranslate(WikiContext wikiContext, String htmlContent)
            throws FilterException
    {
        return htmlContent;
    }

    /**
     * DOCUMENT ME!
     *
     * @param wikiContext DOCUMENT ME!
     * @param content DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws FilterException DOCUMENT ME!
     */
    public String preSave(WikiContext wikiContext, String content)
            throws FilterException
    {
        return content;
    }

    /**
     * DOCUMENT ME!
     *
     * @param wikiContext DOCUMENT ME!
     * @param content DOCUMENT ME!
     *
     * @throws FilterException DOCUMENT ME!
     */
    public void postSave(WikiContext wikiContext, String content)
            throws FilterException
    {
    }
}
