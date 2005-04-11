package com.ecyrd.jspwiki;

import java.util.Date;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class PageLock
{
    /** DOCUMENT ME! */
    private WikiPage m_page;

    /** DOCUMENT ME! */
    private String m_locker;

    /** DOCUMENT ME! */
    private Date m_lockAcquired;

    /** DOCUMENT ME! */
    private Date m_lockExpiry;

    /**
     * Creates a new PageLock object.
     *
     * @param page DOCUMENT ME!
     * @param locker DOCUMENT ME!
     * @param acquired DOCUMENT ME!
     * @param expiry DOCUMENT ME!
     */
    public PageLock(WikiPage page, String locker, Date acquired, Date expiry)
    {
        m_page = page;
        m_locker = locker;
        m_lockAcquired = new Date(acquired.getTime());
        m_lockExpiry = new Date(expiry.getTime());
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public WikiPage getPage()
    {
        return m_page;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getLocker()
    {
        return m_locker;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Date getAcquisitionTime()
    {
        return new Date((m_lockAcquired != null) ? m_lockAcquired.getTime() : 0);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Date getExpiryTime()
    {
        return new Date((m_lockExpiry != null) ? m_lockExpiry.getTime() : 0);
    }

    /**
     * Returns the amount of time left in minutes, rounded up to the nearest minute (so you get a
     * zero only at the last minute).
     *
     * @return DOCUMENT ME!
     */
    public long getTimeLeft()
    {
        long time = m_lockExpiry.getTime() - new Date().getTime();

        return (time / (1000L * 60)) + 1;
    }
}
