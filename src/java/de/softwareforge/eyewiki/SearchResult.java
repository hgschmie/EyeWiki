package de.softwareforge.eyewiki;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public interface SearchResult
{
    /**
     * Return the page.
     *
     * @return DOCUMENT ME!
     */
    WikiPage getPage();

    /**
     * Returns the score.
     *
     * @return DOCUMENT ME!
     */
    int getScore();
}
