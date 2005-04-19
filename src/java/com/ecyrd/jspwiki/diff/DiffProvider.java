package com.ecyrd.jspwiki.diff;

import java.io.IOException;

import org.apache.commons.configuration.Configuration;

import com.ecyrd.jspwiki.WikiEngine;
import com.ecyrd.jspwiki.WikiProvider;
import com.ecyrd.jspwiki.exception.NoRequiredPropertyException;


/**
 * TODO
 */
public interface DiffProvider
        extends WikiProvider
{
    /**
     * Initializes the page provider.
     *
     * @param engine DOCUMENT ME!
     * @param conf DOCUMENT ME!
     *
     * @throws NoRequiredPropertyException DOCUMENT ME!
     * @throws IOException DOCUMENT ME!
     */
    void initialize(WikiEngine engine, Configuration conf)
            throws NoRequiredPropertyException, IOException;

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

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Revision$
     */
    public static class NullDiffProvider
            implements DiffProvider
    {
        /**
         * DOCUMENT ME!
         *
         * @param oldWikiText DOCUMENT ME!
         * @param newWikiText DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        public String makeDiff(String oldWikiText, String newWikiText)
        {
            return "You are using the NullDiffProvider, check your properties file.";
        }

        /**
         * DOCUMENT ME!
         *
         * @param engine DOCUMENT ME!
         * @param conf DOCUMENT ME!
         *
         * @throws NoRequiredPropertyException DOCUMENT ME!
         * @throws IOException DOCUMENT ME!
         */
        public void initialize(WikiEngine engine, Configuration conf)
                throws NoRequiredPropertyException, IOException
        {
        }

        /**
         * DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        public String getProviderInfo()
        {
            return "NullDiffProvider";
        }
    }
}
