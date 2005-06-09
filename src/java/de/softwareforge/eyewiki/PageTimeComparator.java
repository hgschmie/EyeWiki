package de.softwareforge.eyewiki;

import java.util.Comparator;

import org.apache.log4j.Logger;


// FIXME: Does not implement equals().
public class PageTimeComparator
        implements Comparator
{
    /** DOCUMENT ME! */
    private static final Logger log =
            Logger.getLogger(PageTimeComparator.class);

    /**
     * DOCUMENT ME!
     *
     * @param o1 DOCUMENT ME!
     * @param o2 DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int compare(Object o1, Object o2)
    {
        WikiPage w1 = (WikiPage) o1;
        WikiPage w2 = (WikiPage) o2;

        if ((w1 == null) || (w2 == null))
        {
            log.error("W1 or W2 is NULL in PageTimeComparator!");

            return 0; // FIXME: Is this correct?
        }

        if (w1.getLastModified() == null)
        {
            log.error("NULL MODIFY DATE WITH " + w1.getName());

            return 0;
        }
        else if (w2.getLastModified() == null)
        {
            log.error("NULL MODIFY DATE WITH " + w2.getName());

            return 0;
        }

        // This gets most recent on top
        int timecomparison = w2.getLastModified().compareTo(w1.getLastModified());

        if (timecomparison == 0)
        {
            return w1.getName().compareTo(w2.getName());
        }

        return timecomparison;
    }
}
