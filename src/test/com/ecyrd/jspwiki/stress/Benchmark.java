package com.ecyrd.jspwiki.stress;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class Benchmark
{
    /** DOCUMENT ME! */
    long m_start;

    /** DOCUMENT ME! */
    long m_stop;

    /**
     * Creates a new Benchmark object.
     */
    public Benchmark()
    {
    }

    /**
     * DOCUMENT ME!
     */
    public final void start()
    {
        m_start = System.currentTimeMillis();
    }

    /**
     * DOCUMENT ME!
     */
    public final void stop()
    {
        m_stop = System.currentTimeMillis();
    }

    /**
     * Returns duration in milliseconds.
     *
     * @return DOCUMENT ME!
     */
    public long getDurationMs()
    {
        return m_stop - m_start;
    }

    /**
     * Returns seconds.
     *
     * @return DOCUMENT ME!
     */
    public String toString()
    {
        return Double.toString(((double) m_stop - (double) m_start) / 1000.0);
    }

    /**
     * How many operations/second?
     *
     * @param operations DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String toString(int operations)
    {
        double totalTime = (double) m_stop - (double) m_start;

        return Double.toString((operations / totalTime) * 1000.0);
    }
}
