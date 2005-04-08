package com.ecyrd.jspwiki.filters;

import java.util.Properties;

import com.ecyrd.jspwiki.WikiContext;


/**
 * Provides a definition for a page filter.  A page filter is a class that can be used to transform
 * the WikiPage content being saved or being loaded at any given time.
 *
 * <p>
 * Note that the WikiContext.getPage() method always returns the context in which text is rendered,
 * i.e. the original request.  Thus the content may actually be different content than what what
 * the wikiContext.getPage() implies!  This happens often if you are for example including
 * multiple pages on the same page.
 * </p>
 *
 * <p>
 * PageFilters must be thread-safe!  There is only one instance of each PageFilter per each
 * WikiEngine invocation.  If you need to store data persistently, use VariableManager, or
 * WikiContext.
 * </p>
 *
 * @author Janne Jalkanen
 */
public interface PageFilter
{
    /**
     * Is called whenever the a new PageFilter is instantiated and reset.
     *
     * @param props DOCUMENT ME!
     *
     * @throws FilterException DOCUMENT ME!
     */
    public void initialize(Properties props)
            throws FilterException;

    /**
     * This method is called whenever a page has been loaded from the provider, but not yet been
     * sent through the TranslatorReader.  Note that you cannot do HTML translation here, because
     * TranslatorReader is likely to escape it.
     *
     * @param wikiContext The current wikicontext.
     * @param content WikiMarkup.
     *
     * @return DOCUMENT ME!
     *
     * @throws FilterException DOCUMENT ME!
     */
    public String preTranslate(WikiContext wikiContext, String content)
            throws FilterException;

    /**
     * This method is called after a page has been fed through the TranslatorReader, so anything
     * you are seeing here is translated content.  If you want to do any of your own
     * WikiMarkup2HTML translation, do it here.
     *
     * @param wikiContext DOCUMENT ME!
     * @param htmlContent DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws FilterException DOCUMENT ME!
     */
    public String postTranslate(WikiContext wikiContext, String htmlContent)
            throws FilterException;

    /**
     * This method is called before the page has been saved to the PageProvider.
     *
     * @param wikiContext DOCUMENT ME!
     * @param content DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws FilterException DOCUMENT ME!
     */
    public String preSave(WikiContext wikiContext, String content)
            throws FilterException;

    /**
     * This method is called after the page has been successfully saved. If the saving fails for
     * any reason, then this method will not be called.
     *
     * <p>
     * Since the result is discarded from this method, this is only useful for things like
     * counters, etc.
     * </p>
     *
     * @param wikiContext DOCUMENT ME!
     * @param content DOCUMENT ME!
     *
     * @throws FilterException DOCUMENT ME!
     */
    public void postSave(WikiContext wikiContext, String content)
            throws FilterException;
}
