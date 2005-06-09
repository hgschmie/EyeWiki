package de.softwareforge.eyewiki;

import java.util.Comparator;


/**
 * Simple class that decides which search results are more important than others.
 */
public class SearchResultComparator
        implements Comparator
{
    /**
     * Compares two SearchResult objects, returning the one that scored higher.
     *
     * @param o1 DOCUMENT ME!
     * @param o2 DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int compare(Object o1, Object o2)
    {
        SearchResult s1 = (SearchResult) o1;
        SearchResult s2 = (SearchResult) o2;

        // Bigger scores are first.
        int res = s2.getScore() - s1.getScore();

        if (res == 0)
        {
            res = s1.getPage().getName().compareTo(s2.getPage().getName());
        }

        return res;
    }
}
