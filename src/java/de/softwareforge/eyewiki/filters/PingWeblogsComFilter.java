package de.softwareforge.eyewiki.filters;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;
import org.apache.xmlrpc.AsyncCallback;
import org.apache.xmlrpc.XmlRpcClient;

import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiEngine;


/**
 * A very dumb class that pings weblogs.com on each save.  INTERNAL USE ONLY SO FAR! Look, but
 * don't use as-is.
 */

// FIXME: Needs to figure out when only weblogs have been saved.
// FIXME: rpc endpoint must be configurable
// FIXME: Should really be settable per-page.
// FIXME: Weblog name has been set to stone
public class PingWeblogsComFilter
        extends BasicPageFilter
        implements PageFilter
{
    /** DOCUMENT ME! */
    private static final Logger log = Logger.getLogger(PingWeblogsComFilter.class);

    /** DOCUMENT ME! */
    public static final String PROP_PINGURL = "pingurl";

    public static final String PROP_PINGURL_DEFAULT = "http://rpc.weblogs.com/RPC2";

    /** DOCUMENT ME! */
    private String m_pingURL;

    /**
     * DOCUMENT ME!
     *
     * @param props DOCUMENT ME!
     */
    public PingWeblogsComFilter(Configuration conf)
    {
        super(conf);
        m_pingURL = conf.getString(PROP_PINGURL, PROP_PINGURL_DEFAULT);
    }

    /**
     * DOCUMENT ME!
     *
     * @param context DOCUMENT ME!
     * @param pagecontent DOCUMENT ME!
     */
    public void postSave(WikiContext context, String pagecontent)
    {
        String blogName = context.getPage().getName();
        WikiEngine engine = context.getEngine();

        int blogentryTxt = blogName.indexOf("_blogentry_");

        if (blogentryTxt == -1)
        {
            return; // This is not a weblog entry.
        }

        blogName = blogName.substring(0, blogentryTxt);

        if (blogName.equals(engine.getFrontPage()))
        {
            blogName = null;
        }

        try
        {
            XmlRpcClient xmlrpc = new XmlRpcClient(m_pingURL);
            Vector params = new Vector();
            params.addElement("The Butt Ugly Weblog"); // FIXME: Must be settable
            params.addElement(engine.getURL(WikiContext.VIEW, blogName, null, true));

            if (log.isDebugEnabled())
            {
                log.debug(
                    "Pinging weblogs.com with URL: "
                    + engine.getURL(WikiContext.VIEW, blogName, null, true));
            }

            xmlrpc.executeAsync(
                "weblogUpdates.ping", params,
                new AsyncCallback()
                {
                    public void handleError(Exception ex, URL url, String method)
                    {
                        log.error(
                            "Unable to execute weblogs.com ping to URL: " + url.toString(), ex);
                    }

                    public void handleResult(Object result, URL url, String method)
                    {
                        Hashtable res = (Hashtable) result;

                        Boolean flerror = (Boolean) res.get("flerror");
                        String msg = (String) res.get("message");

                        if (Boolean.TRUE.equals(flerror))
                        {
                            log.error("Failed to ping: " + msg);
                        }

                        log.info("Weblogs.com has been pinged.");
                    }
                });
        }
        catch (MalformedURLException e)
        {
            log.error("Malformed URL", e);
        }
    }

    public boolean isVisible()
    {
        return true;
    }

    public int getPriority()
    {
        return PageFilter.NORMAL_PRIORITY;
    }
}
