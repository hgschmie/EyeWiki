package de.softwareforge.eyewiki.filters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;

import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiPage;


/**
 *  A regular expression-based spamfilter that can also do choke modifications.
 *
 * @author Janne Jalkanen
 *
 *  Parameters:
 *  <ul>
 *    <li>wordlist - Page name where the regexps are found.  Use [{SET spamwords='regexp list separated with spaces'}] on
 *     that page.  Default is "SpamFilterWordList".
 *    <li>errorpage - The page to which the user is redirected.  Has a special variable $msg which states the reason. Default is "RejectedMessage".
 *    <li>pagechangesinminute - How many page changes are allowed/minute.  Default is 5.
 *    <li>bantime - How long an IP address stays on the temporary ban list (default is 60 for 60 minutes).
 *  </ul>
 * @since 2.1.112
 */
public class SpamFilter
        extends BasicPageFilter
        implements PageFilter
{
    /** DOCUMENT ME! */
    private static final String LISTVAR = "spamwords";

    /** DOCUMENT ME! */
    private static final Logger log = Logger.getLogger(SpamFilter.class);

    /** DOCUMENT ME! */
    public static final String PROP_WORDLIST = "wordlist";

    /** DOCUMENT ME! */
    public static final String PROP_ERRORPAGE = "errorpage";

    public static final String PROP_PAGECHANGES = "pagechangesinminute";

    public static final String PROP_BANTIME   = "bantime";
    
    /** DOCUMENT ME! */
    private String m_forbiddenWordsPage = "SpamFilterWordList";

    /** DOCUMENT ME! */
    private String m_errorPage = "RejectedMessage";

    /** DOCUMENT ME! */
    private PatternMatcher m_matcher = new Perl5Matcher();

    /** DOCUMENT ME! */
    private PatternCompiler m_compiler = new Perl5Compiler();

    /** DOCUMENT ME! */
    private Collection m_spamPatterns = null;

    /** DOCUMENT ME! */
    private Date m_lastRebuild = new Date(0L);

    private List m_temporaryBanList = new ArrayList();
    
    private int m_banTime = 60; // minutes
    
    private List m_lastModifications = new ArrayList();
    
    /**
     *  How many times a single IP address can change a page per minute?
     */
    private int m_limitSinglePageChanges = 5;
    
    /**
     * DOCUMENT ME!
     *
     * @param properties DOCUMENT ME!
     */
    public SpamFilter(Configuration conf)
    {
        super(conf);
        m_forbiddenWordsPage = conf.getString(PROP_WORDLIST, m_forbiddenWordsPage);
        m_errorPage = conf.getString(PROP_ERRORPAGE, m_errorPage);
        m_limitSinglePageChanges = conf.getInt(PROP_PAGECHANGES, m_limitSinglePageChanges);
        m_banTime = conf.getInt(PROP_BANTIME,m_banTime);

        if (log.isInfoEnabled())
        {
            log.info("Spam filter initialized.  Temporary ban time "
                    + m_banTime + " mins, max page changes/minute: "
                    + m_limitSinglePageChanges);
        }
    }

    private Collection parseWordList(WikiPage source, String list)
    {
        ArrayList compiledpatterns = new ArrayList();

        if (list != null)
        {
            StringTokenizer tok = new StringTokenizer(list, " \t\n");

            while (tok.hasMoreTokens())
            {
                String pattern = tok.nextToken();

                try
                {
                    compiledpatterns.add(m_compiler.compile(pattern));
                }
                catch (MalformedPatternException e)
                {
                    if (log.isDebugEnabled())
                    {
                        log.debug("Malformed spam filter pattern " + pattern);
                    }

                    source.setAttribute("error", "Malformed spam filter pattern " + pattern);
                }
            }
        }

        return compiledpatterns;
    }

    private synchronized void checkSinglePageChange(WikiContext context)
        throws RedirectException
    {
        HttpServletRequest req = context.getHttpRequest();
        
        if (req != null)
        {
            String addr = req.getRemoteAddr();
            int counter = 0;
                
            long time = System.currentTimeMillis()-60*1000L; // 1 minute
            
            for(Iterator i = m_lastModifications.iterator(); i.hasNext();)
            {
                Host host = (Host) i.next();
                
                //
                //  Check if this item is invalid
                //
                if (host.getAddedTime() < time)
                {
                    if (log.isDebugEnabled())
                    {
                        log.debug("Removed host " + host.getAddress()
                                + " from modification queue (expired)");
                    }

                    i.remove();
                    continue;
                }
                
                if (host.getAddress().equals(addr))
                {
                    counter++;
                }
            }
            
            if (counter >= m_limitSinglePageChanges)
            {
                Host host = new Host(addr);
                
                m_temporaryBanList.add(host);
                
                if (log.isInfoEnabled())
                {
                    log.info("Added host "+addr+" to temporary ban list for doing too many modifications/minute");
                }

                throw new RedirectException("Too many modifications/minute",
                                             context.getViewURL(m_errorPage));
            }
            
            m_lastModifications.add(new Host(addr));
        }
    }

    private synchronized void cleanBanList()
    {
        long now = System.currentTimeMillis();
        
        for(Iterator i = m_temporaryBanList.iterator(); i.hasNext();)
        {
            Host host = (Host)i.next();
            
            if (host.getReleaseTime() < now)
            {
                if (log.isDebugEnabled())
                {
                    log.debug("Removed host " + host.getAddress() + " from temporary ban list (expired)");
                }

                i.remove();
            }
        }
    }
    
    
    private void checkBanList(WikiContext context)
        throws RedirectException
    {
        HttpServletRequest req = context.getHttpRequest();
        
        if (req != null)
        {
            String remote = req.getRemoteAddr();
            
            long now = System.currentTimeMillis();
            
            for(Iterator i = m_temporaryBanList.iterator(); i.hasNext();)
            {
                Host host = (Host)i.next();
                
                if (host.getAddress().equals(remote))
                {
                    long timeleft = (host.getReleaseTime() - now) / 1000L;
                    throw new RedirectException("You have been temporarily banned from modifying this wiki. ("+timeleft+" seconds of ban left)",
                                                 context.getViewURL(m_errorPage));
                }
            }
        }
        
    }

    /**
     * DOCUMENT ME!
     *
     * @param context DOCUMENT ME!
     * @param content DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws RedirectException DOCUMENT ME!
     */
    public String preSave(WikiContext context, String content)
            throws RedirectException
    {
        cleanBanList();
        checkBanList(context);
        checkSinglePageChange(context);
        
        WikiPage source = context.getEngine().getPage(m_forbiddenWordsPage);

        if (source != null)
        {
            if (
                    (m_spamPatterns == null) || m_spamPatterns.isEmpty()
                    || source.getLastModified().after(m_lastRebuild))
            {
                m_lastRebuild = source.getLastModified();

                m_spamPatterns = parseWordList(source, (String) source.getAttribute(LISTVAR));

                if (log.isInfoEnabled())
                {
                    log.info(
                            "Spam filter reloaded - recognizing " + m_spamPatterns.size()
                            + " patterns from page " + m_forbiddenWordsPage);
                }
            }
        }

        //
        //  If we have no spam patterns defined, or we're trying to save
        //  the page containing the patterns, just return.
        //
        if ((m_spamPatterns == null) || context.getPage().getName().equals(m_forbiddenWordsPage))
        {
            return content;
        }

        for (Iterator i = m_spamPatterns.iterator(); i.hasNext();)
        {
            Pattern p = (Pattern) i.next();

            if (log.isDebugEnabled())
            {
                log.debug("Attempting to match page contents with " + p.getPattern());
            }

            if (m_matcher.contains(content, p))
            {
                //
                //  Spam filter has a match.
                //
                throw new RedirectException(
                        "Content matches the spam filter '" + p.getPattern() + "'",
                        context.getURL(WikiContext.VIEW, m_errorPage));
            }
        }

        return content;
    }

    public boolean isVisible()
    {
        return true;
    }

    public int getPriority()
    {
        return PageFilter.NORMAL_PRIORITY;
    }

    
    /**
     *  A local class for storing host information.
     * 
     *  @author jalkanen
     *
     *  @since
     */
    public class Host
    {
        private  long m_addedTime = System.currentTimeMillis();
        private  long m_releaseTime;
        private  String m_address;
        
        public Host(String ipaddress)
        {
            m_address = ipaddress;
            
            m_releaseTime = System.currentTimeMillis() + m_banTime * 60 * 1000L;
        }

        public String getAddress()
        {
            return m_address;
        }
        
        public long getReleaseTime()
        {
            return m_releaseTime;
        }
        
        public long getAddedTime()
        {
            return m_addedTime;
        }
    }
}
