package com.ecyrd.jspwiki.filters;

import org.apache.commons.lang.StringUtils;

import com.ecyrd.jspwiki.WikiContext;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class ProfanityFilter
        extends BasicPageFilter
{
    /** DOCUMENT ME! */
    private String [] profanities =
    {
        "fuck",
        "shit"
    };

    /**
     * DOCUMENT ME!
     *
     * @param context DOCUMENT ME!
     * @param content DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String preTranslate(WikiContext context, String content)
    {
        for (int i = 0; i < profanities.length; i++)
        {
            String word = profanities[i];
            String replacement = word.charAt(0) + "*" + word.charAt(word.length() - 1);

            content = StringUtils.replace(content, word, replacement);
        }

        return content;
    }
}
