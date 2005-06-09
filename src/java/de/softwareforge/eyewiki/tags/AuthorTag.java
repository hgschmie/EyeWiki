package de.softwareforge.eyewiki.tags;

import java.io.IOException;
import java.io.StringReader;

import de.softwareforge.eyewiki.TranslatorReader;
import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.WikiPage;


/**
 * Writes the author name of the current page.
 *
 * @author Janne Jalkanen
 *
 * @since 2.0
 */
public class AuthorTag
        extends WikiTagBase
{
    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    public final int doWikiStartTag()
            throws IOException
    {
        WikiEngine engine = m_wikiContext.getEngine();
        WikiPage page = m_wikiContext.getPage();

        String author = page.getAuthor();

        if (author != null)
        {
            if (engine.pageExists(author))
            {
                // FIXME: It's very boring to have to do this.
                //        Slow, too.
                TranslatorReader tr = new TranslatorReader(m_wikiContext, new StringReader(""));
                author = tr.makeLink(TranslatorReader.READ, author, author);
            }

            pageContext.getOut().print(author);
        }
        else
        {
            pageContext.getOut().print("unknown");
        }

        return SKIP_BODY;
    }
}
