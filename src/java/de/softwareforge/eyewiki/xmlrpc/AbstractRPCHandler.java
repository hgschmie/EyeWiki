package de.softwareforge.eyewiki.xmlrpc;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.WikiPage;


/**
 * Provides definitions for RPC handler routines.
 *
 * @author Janne Jalkanen
 *
 * @since 1.6.13
 */
public abstract class AbstractRPCHandler
        implements WikiRPCHandler
{
    /** Error code: no such page. */
    public static final int ERR_NOPAGE = 1;

    /** DOCUMENT ME! */
    public static final int ERR_NOPERMISSION = 2;

    /** Link to a local wiki page. */
    public static final String LINK_LOCAL = "local";

    /** Link to an external resource. */
    public static final String LINK_EXTERNAL = "external";

    /** This is an inlined image. */
    public static final String LINK_INLINE = "inline";

    /** This is the currently implemented eyeWiki XML-RPC code revision. */
    public static final int RPC_VERSION = 1;

    /** DOCUMENT ME! */
    protected WikiEngine m_engine;

    /**
     * DOCUMENT ME!
     *
     * @param engine DOCUMENT ME!
     */
    public void initialize(WikiEngine engine)
    {
        m_engine = engine;
    }

    /**
     * DOCUMENT ME!
     *
     * @param p DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected abstract Hashtable encodeWikiPage(WikiPage p);

    /**
     * DOCUMENT ME!
     *
     * @param since DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Vector getRecentChanges(Date since)
    {
        Collection pages = m_engine.getRecentChanges();
        Vector result = new Vector();

        // Transform UTC into local time.
        Calendar cal = Calendar.getInstance();
        cal.setTime(since);
        cal.add(
            Calendar.MILLISECOND,
            (cal.get(Calendar.ZONE_OFFSET)
            + (cal.getTimeZone().inDaylightTime(since)
            ? cal.get(Calendar.DST_OFFSET)
            : 0)));

        for (Iterator i = pages.iterator(); i.hasNext();)
        {
            WikiPage page = (WikiPage) i.next();

            if (page.getLastModified().after(cal.getTime()))
            {
                result.add(encodeWikiPage(page));
            }
        }

        return result;
    }

    /**
     * Returns the current supported eyeWiki XML-RPC API.
     *
     * @return DOCUMENT ME!
     */
    public int getRPCVersionSupported()
    {
        return RPC_VERSION;
    }
}
