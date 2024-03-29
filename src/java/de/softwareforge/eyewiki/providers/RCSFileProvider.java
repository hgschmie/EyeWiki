package de.softwareforge.eyewiki.providers;

/*
 * ========================================================================
 *
 * eyeWiki - a WikiWiki clone written in Java
 *
 * ========================================================================
 *
 * Copyright (C) 2005 Henning Schmiedehausen <henning@software-forge.com>
 *
 * based on
 *
 * JSPWiki - a JSP-based WikiWiki clone.
 * Copyright (C) 2002-2005 Janne Jalkanen (Janne.Jalkanen@iki.fi)
 *
 * ========================================================================
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * ========================================================================
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;

import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.WikiPage;
import de.softwareforge.eyewiki.WikiProperties;
import de.softwareforge.eyewiki.WikiProvider;
import de.softwareforge.eyewiki.exception.InternalWikiException;
import de.softwareforge.eyewiki.exception.NoRequiredPropertyException;
import de.softwareforge.eyewiki.util.FileUtil;
import de.softwareforge.eyewiki.util.TextUtil;

/**
 * This class implements a simple RCS file provider.  NOTE: You MUST have the RCS package installed for this to work.  They must
 * also be in your path...
 *
 * <P>
 * The RCS file provider extends from the FileSystemProvider, which means that it provides the pages in the same way.  The only
 * difference is that it implements the version history commands, and also in each checkin it writes the page to the RCS
 * repository as well.
 * </p>
 *
 * <p>
 * If you decide to dabble with the default commands, please make sure that you do not check the default archive suffix ",v".  File
 * deletion depends on it.
 * </p>
 *
 * @author Janne Jalkanen
 */

// FIXME: Not all commands read their format from the property file yet.
public class RCSFileProvider
        extends AbstractFileProvider
        implements WikiProperties
{
    /** DOCUMENT ME! */
    private static final Logger log = Logger.getLogger(RCSFileProvider.class);

    /** DOCUMENT ME! */
    private static final String PATTERN_DATE = "^date:\\s*(.*\\d);";

    /** DOCUMENT ME! */
    private static final String PATTERN_AUTHOR = "^\"?author=([\\w\\.\\s\\+\\.\\%]*)\"?";

    /** DOCUMENT ME! */
    private static final String PATTERN_REVISION = "^revision \\d+\\.(\\d+)";

    /** DOCUMENT ME! */
    private static final String RCSFMT_DATE = "yyyy-MM-dd HH:mm:ss";

    /** DOCUMENT ME! */
    private static final String RCSFMT_DATE_UTC = "yyyy/MM/dd HH:mm:ss";

    /** DOCUMENT ME! */
    private String m_checkinCommand = PROP_RCS_CHECKIN_DEFAULT;

    /** DOCUMENT ME! */
    private String m_checkoutCommand = PROP_RCS_CHECKOUT_DEFAULT;

    /** DOCUMENT ME! */
    private String m_logCommand = PROP_RCS_LOG_DEFAULT;

    /** DOCUMENT ME! */
    private String m_fullLogCommand = PROP_RCS_FULLLOG_DEFAULT;

    /** DOCUMENT ME! */
    private String m_checkoutVersionCommand = PROP_RCS_CHECKOUTVERSION_DEFAULT;

    /** DOCUMENT ME! */
    private String m_deleteVersionCommand = PROP_RCS_DELETEVERSION_DEFAULT;

    // Date format parsers, placed here to save on object creation

    /** DOCUMENT ME! */
    private SimpleDateFormat m_rcsdatefmt = new SimpleDateFormat(RCSFMT_DATE);

    /** DOCUMENT ME! */
    private SimpleDateFormat m_rcsdatefmtUTC = new SimpleDateFormat(RCSFMT_DATE_UTC);

    /**
     * DOCUMENT ME!
     *
     * @param engine DOCUMENT ME!
     * @param conf DOCUMENT ME!
     *
     * @throws NoRequiredPropertyException DOCUMENT ME!
     * @throws IOException DOCUMENT ME!
     */
    public RCSFileProvider(WikiEngine engine, Configuration conf)
            throws NoRequiredPropertyException, IOException
    {
        super(engine, conf);
        log.debug("Initing RCS");

        m_checkinCommand = conf.getString(PROP_RCS_CHECKIN, PROP_RCS_CHECKIN_DEFAULT);
        m_checkoutCommand = conf.getString(PROP_RCS_CHECKOUT, PROP_RCS_CHECKOUT_DEFAULT);
        m_logCommand = conf.getString(PROP_RCS_LOG, PROP_RCS_LOG_DEFAULT);
        m_fullLogCommand = conf.getString(PROP_RCS_FULLLOG, PROP_RCS_FULLLOG_DEFAULT);
        m_checkoutVersionCommand = conf.getString(PROP_RCS_CHECKOUTVERSION, PROP_RCS_CHECKOUTVERSION_DEFAULT);
        m_deleteVersionCommand = conf.getString(PROP_RCS_DELETEVERSION, PROP_RCS_DELETEVERSION_DEFAULT);

        File rcsdir = new File(getPageDirectory(), "RCS");

        if (!rcsdir.exists())
        {
            rcsdir.mkdirs();
        }

        if (log.isDebugEnabled())
        {
            log.debug("checkin=" + m_checkinCommand);
            log.debug("checkout=" + m_checkoutCommand);
            log.debug("log=" + m_logCommand);
            log.debug("fulllog=" + m_fullLogCommand);
            log.debug("checkoutversion=" + m_checkoutVersionCommand);
        }
    }

    // NB: This is a very slow method.
    public WikiPage getPageInfo(String page, int version)
            throws ProviderException
    {
        PatternMatcher matcher = new Perl5Matcher();
        PatternCompiler compiler = new Perl5Compiler();

        WikiPage info = super.getPageInfo(page, version);

        if (info == null)
        {
            return null;
        }

        Process process = null;
        BufferedReader stdout = null;

        try
        {
            String cmd = m_fullLogCommand;

            cmd = StringUtils.replace(cmd, "%s", mangleName(page) + FILE_EXT);

            process = Runtime.getRuntime().exec(cmd, null, new File(getPageDirectory()));

            // FIXME: Should this use encoding as well?
            stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            Pattern headpattern = compiler.compile(PATTERN_REVISION);

            // This complicated pattern is required, since on Linux RCS adds
            // quotation marks, but on Windows, it does not.
            Pattern userpattern = compiler.compile(PATTERN_AUTHOR);
            Pattern datepattern = compiler.compile(PATTERN_DATE);
            boolean found = false;

            while ((line = stdout.readLine()) != null)
            {
                if (matcher.contains(line, headpattern))
                {
                    MatchResult result = matcher.getMatch();
                    int vernum = Integer.parseInt(result.group(1));

                    if ((vernum == version) || (version == WikiPageProvider.LATEST_VERSION))
                    {
                        info.setVersion(vernum);
                        found = true;
                    }
                }
                else if (matcher.contains(line, datepattern) && found)
                {
                    MatchResult result = matcher.getMatch();
                    Date d = parseDate(result.group(1));

                    if (d != null)
                    {
                        info.setLastModified(d);
                    }
                    else
                    {
                        if (log.isInfoEnabled())
                        {
                            log.info("WikiPage " + info.getName() + " has null modification date for version " + version);
                        }
                    }
                }
                else if (matcher.contains(line, userpattern) && found)
                {
                    MatchResult result = matcher.getMatch();
                    info.setAuthor(TextUtil.urlDecodeUTF8(result.group(1)));
                }
                else if (found && line.startsWith("----"))
                {
                    // End of line sign from RCS
                    break;
                }
            }

            //
            //  Especially with certain versions of RCS on Windows,
            //  process.waitFor() hangs unless you read all of the
            //  standard output.  So we make sure it's all emptied.
            //
            while ((line = stdout.readLine()) != null)
            {
                ;
            }

            process.waitFor();
        }
        catch (Exception e)
        {
            // This also occurs when 'info' was null.
            log.warn("Failed to read RCS info", e);
        }
        finally
        {
            if (process != null)
            {
                IOUtils.closeQuietly(stdout);

                // we must close all by exec(..) opened streams: http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4784692
                IOUtils.closeQuietly(process.getInputStream());
                IOUtils.closeQuietly(process.getOutputStream());
                IOUtils.closeQuietly(process.getErrorStream());
            }
        }

        return info;
    }

    /**
     * DOCUMENT ME!
     *
     * @param page DOCUMENT ME!
     * @param version DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws ProviderException DOCUMENT ME!
     * @throws InternalWikiException DOCUMENT ME!
     */
    public String getPageText(String page, int version)
            throws ProviderException
    {
        String result = null;
        InputStream stdout = null;
        BufferedReader stderr = null;

        // Let parent handle latest fetches, since the FileSystemProvider
        // can do the file reading just as well.
        if (version == WikiPageProvider.LATEST_VERSION)
        {
            return super.getPageText(page, version);
        }

        if (log.isDebugEnabled())
        {
            log.debug("Fetching specific version " + version + " of page " + page);
        }

        Process process = null;

        try
        {
            PatternMatcher matcher = new Perl5Matcher();
            PatternCompiler compiler = new Perl5Compiler();
            int checkedOutVersion = -1;
            String line;
            String cmd = m_checkoutVersionCommand;

            cmd = StringUtils.replace(cmd, "%s", mangleName(page) + FILE_EXT);
            cmd = StringUtils.replace(cmd, "%v", Integer.toString(version));

            if (log.isDebugEnabled())
            {
                log.debug("Command = '" + cmd + "'");
            }

            process = Runtime.getRuntime().exec(cmd, null, new File(getPageDirectory()));
            stdout = process.getInputStream();
            result = FileUtil.readContents(stdout, m_encoding);

            stderr = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            Pattern headpattern = compiler.compile(PATTERN_REVISION);

            while ((line = stderr.readLine()) != null)
            {
                if (matcher.contains(line, headpattern))
                {
                    MatchResult mr = matcher.getMatch();
                    checkedOutVersion = Integer.parseInt(mr.group(1));
                }
            }

            process.waitFor();

            int exitVal = process.exitValue();

            if (log.isDebugEnabled())
            {
                log.debug("Done, returned = " + exitVal);
            }

            //
            //  If fetching failed, assume that this is because of the user
            //  has just migrated from FileSystemProvider, and check
            //  if he's getting version 1.  Else he might be trying to find
            //  a version that has been deleted.
            //
            if ((exitVal != 0) || (checkedOutVersion == -1))
            {
                if (version == 1)
                {
                    if (log.isDebugEnabled())
                    {
                        log.debug("Migrating, fetching super.");
                    }

                    result = super.getPageText(page, WikiProvider.LATEST_VERSION);
                }
                else
                {
                    throw new NoSuchVersionException("Page: " + page + ", version=" + version);
                }
            }
            else
            {
                //
                //  Check which version we actually got out!
                //
                if (checkedOutVersion != version)
                {
                    throw new NoSuchVersionException("Page: " + page + ", version=" + version);
                }
            }
        }
        catch (MalformedPatternException e)
        {
            throw new InternalWikiException("Malformed pattern in RCSFileProvider!");
        }
        catch (InterruptedException e)
        {
            // This is fine, we'll just log it.
            log.info("RCS process was interrupted, we'll just return whatever we found.");
        }
        catch (IOException e)
        {
            log.error("RCS checkout failed", e);
        }
        finally
        {
            if (process != null)
            {
                IOUtils.closeQuietly(stdout);
                IOUtils.closeQuietly(stderr);

                // we must close all by exec(..) opened streams: http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4784692
                IOUtils.closeQuietly(process.getInputStream());
                IOUtils.closeQuietly(process.getOutputStream());
                IOUtils.closeQuietly(process.getErrorStream());
            }
        }

        return result;
    }

    /**
     * Puts the page into RCS and makes sure there is a fresh copy in the directory as well.
     *
     * @param page DOCUMENT ME!
     * @param text DOCUMENT ME!
     *
     * @throws ProviderException DOCUMENT ME!
     */
    public void putPageText(WikiPage page, String text)
            throws ProviderException
    {
        String pagename = page.getName();

        // Writes it in the dir.
        super.putPageText(page, text);

        log.debug("Checking in text...");

        Process process = null;

        try
        {
            String cmd = m_checkinCommand;

            String author = page.getAuthor();

            if (author == null)
            {
                author = "unknown";
            }

            cmd = StringUtils.replace(cmd, "%s", mangleName(pagename) + FILE_EXT);
            cmd = StringUtils.replace(cmd, "%u", TextUtil.urlEncodeUTF8(author));

            if (log.isDebugEnabled())
            {
                log.debug("Command = '" + cmd + "'");
            }

            process = Runtime.getRuntime().exec(cmd, null, new File(getPageDirectory()));

            process.waitFor();

            if (log.isDebugEnabled())
            {
                log.debug("Done, returned = " + process.exitValue());
            }
        }
        catch (Exception e)
        {
            log.error("RCS checkin failed", e);
        }
        finally
        {
            if (process != null)
            {
                // we must close all by exec(..) opened streams: http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4784692
                IOUtils.closeQuietly(process.getInputStream());
                IOUtils.closeQuietly(process.getOutputStream());
                IOUtils.closeQuietly(process.getErrorStream());
            }
        }
    }

    // FIXME: Put the rcs date formats into properties as well.
    public List getVersionHistory(String page)
    {
        PatternMatcher matcher = new Perl5Matcher();
        PatternCompiler compiler = new Perl5Compiler();

        log.debug("Getting RCS version history");

        ArrayList list = new ArrayList();

        Process process = null;
        BufferedReader stdout = null;

        try
        {
            Pattern revpattern = compiler.compile(PATTERN_REVISION);
            Pattern datepattern = compiler.compile(PATTERN_DATE);

            // This complicated pattern is required, since on Linux RCS adds
            // quotation marks, but on Windows, it does not.
            Pattern userpattern = compiler.compile(PATTERN_AUTHOR);

            String cmd = StringUtils.replace(m_fullLogCommand, "%s", mangleName(page) + FILE_EXT);

            process = Runtime.getRuntime().exec(cmd, null, new File(getPageDirectory()));

            // FIXME: Should this use encoding as well?
            stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;

            WikiPage info = null;

            while ((line = stdout.readLine()) != null)
            {
                if (matcher.contains(line, revpattern))
                {
                    info = new WikiPage(page);

                    MatchResult result = matcher.getMatch();

                    int vernum = Integer.parseInt(result.group(1));
                    info.setVersion(vernum);

                    list.add(info);
                }

                if (matcher.contains(line, datepattern))
                {
                    MatchResult result = matcher.getMatch();

                    Date d = parseDate(result.group(1));

                    info.setLastModified(d);
                }

                if (matcher.contains(line, userpattern))
                {
                    MatchResult result = matcher.getMatch();

                    info.setAuthor(TextUtil.urlDecodeUTF8(result.group(1)));
                }
            }

            process.waitFor();

            //
            // FIXME: This is very slow
            //
            for (Iterator i = list.iterator(); i.hasNext();)
            {
                WikiPage p = (WikiPage) i.next();

                String content = getPageText(p.getName(), p.getVersion());

                p.setSize(content.length());
            }
        }
        catch (Exception e)
        {
            log.error("RCS log failed", e);
        }
        finally
        {
            if (process != null)
            {
                IOUtils.closeQuietly(stdout);

                // we must close all by exec(..) opened streams: http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4784692
                IOUtils.closeQuietly(process.getInputStream());
                IOUtils.closeQuietly(process.getOutputStream());
                IOUtils.closeQuietly(process.getErrorStream());
            }
        }

        return list;
    }

    /**
     * Removes the page file and the RCS archive from the repository. This method assumes that the page archive ends with ",v".
     *
     * @param page DOCUMENT ME!
     *
     * @throws ProviderException DOCUMENT ME!
     */
    public void deletePage(String page)
            throws ProviderException
    {
        if (log.isDebugEnabled())
        {
            log.debug("Deleting page " + page);
        }

        super.deletePage(page);

        File rcsdir = new File(getPageDirectory(), "RCS");

        if (rcsdir.exists() && rcsdir.isDirectory())
        {
            File rcsfile = new File(rcsdir, mangleName(page) + FILE_EXT + ",v");

            if (rcsfile.exists())
            {
                if (!rcsfile.delete())
                {
                    log.warn("Deletion of RCS file " + rcsfile.getAbsolutePath() + " failed!");
                }
            }
            else
            {
                if (log.isInfoEnabled())
                {
                    log.info("RCS file does not exist for page: " + page);
                }
            }
        }
        else
        {
            if (log.isInfoEnabled())
            {
                log.info("No RCS directory at " + rcsdir.getAbsolutePath());
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param page DOCUMENT ME!
     * @param version DOCUMENT ME!
     */
    public void deleteVersion(String page, int version)
    {
        String line = "<rcs not run>";
        BufferedReader stderr = null;
        boolean success = false;
        String cmd = m_deleteVersionCommand;

        if (log.isDebugEnabled())
        {
            log.debug("Deleting version " + version + " of page " + page);
        }

        cmd = StringUtils.replace(cmd, "%s", mangleName(page) + FILE_EXT);
        cmd = StringUtils.replace(cmd, "%v", Integer.toString(version));

        if (log.isDebugEnabled())
        {
            log.debug("Running command " + cmd);
        }

        Process process = null;

        try
        {
            process = Runtime.getRuntime().exec(cmd, null, new File(getPageDirectory()));

            //
            // 'rcs' command outputs to stderr methinks.
            //
            // FIXME: Should this use encoding as well?
            stderr = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            while ((line = stderr.readLine()) != null)
            {
                if (log.isDebugEnabled())
                {
                    log.debug("LINE=" + line);
                }

                if (line.equals("done"))
                {
                    success = true;
                }
            }
        }
        catch (IOException e)
        {
            log.error("Page deletion failed: ", e);
        }
        finally
        {
            if (process != null)
            {
                IOUtils.closeQuietly(stderr);

                // we must close all by exec(..) opened streams: http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4784692
                IOUtils.closeQuietly(process.getInputStream());
                IOUtils.closeQuietly(process.getOutputStream());
                IOUtils.closeQuietly(process.getErrorStream());
            }
        }

        if (!success)
        {
            log.error("Version deletion failed. Last info from RCS is: " + line);
        }
    }

    /**
     * util method to parse a date string in Local and UTC formats
     *
     * @param str DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private Date parseDate(String str)
    {
        Date d = null;

        try
        {
            d = m_rcsdatefmt.parse(str);

            return d;
        }
        catch (ParseException pe)
        {
            // IGNORE
        }

        try
        {
            d = m_rcsdatefmtUTC.parse(str);

            return d;
        }
        catch (ParseException pe)
        {
            // IGNORE
        }

        return d;
    }
}
