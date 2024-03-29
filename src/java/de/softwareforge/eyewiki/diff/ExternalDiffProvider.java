package de.softwareforge.eyewiki.diff;

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
import java.io.StringReader;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.softwareforge.eyewiki.WikiConstants;
import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.WikiProperties;
import de.softwareforge.eyewiki.util.FileUtil;
import de.softwareforge.eyewiki.util.TextUtil;

/**
 * This DiffProvider allows external command line tools to be used to generate the diff.
 */
public class ExternalDiffProvider
        implements DiffProvider
{
    /** DOCUMENT ME! */
    private static final Logger log = Logger.getLogger(ExternalDiffProvider.class);

    /** DOCUMENT ME! */
    private static final char DIFF_ADDED_SYMBOL = '+';

    /** DOCUMENT ME! */
    private static final char DIFF_REMOVED_SYMBOL = '-';

    /** DOCUMENT ME! */
    private static final String DIFF_ADDED = "<tr><td class=\"" + WikiConstants.CSS_DIFF_ADD + "\">";

    /** DOCUMENT ME! */
    private static final String DIFF_REMOVED = "<tr><td class=\"" + WikiConstants.CSS_DIFF_REM + "\">";

    /** DOCUMENT ME! */
    private static final String DIFF_COMMENT = "<tr><td class=\"" + WikiConstants.CSS_DIFF + "\">";

    /** DOCUMENT ME! */
    private static final String DIFF_CLOSE = "</td></tr>";

    /** DOCUMENT ME! */
    private String m_diffCommand = null;

    /** DOCUMENT ME! */
    private String m_encoding;

    //FIXME This could/should be a property for this provider, there is not guarentee that
    //the external program generates a format suitible for the colorization code of the
    //TraditionalDiffProvider, currently set to true for legacy compatibility.
    //I don't think this 'feature' ever worked right, did it?...

    /** DOCUMENT ME! */
    private boolean m_traditionalColorization = true;

    /**
     * Creates a new ExternalDiffProvider object.
     *
     * @param engine DOCUMENT ME!
     * @param conf DOCUMENT ME!
     */
    public ExternalDiffProvider(WikiEngine engine, Configuration conf)
    {
        m_diffCommand = conf.getString(WikiProperties.PROP_DIFFCOMMAND);
        m_encoding = engine.getContentEncoding();
    }

    /**
     * @see de.softwareforge.eyewiki.WikiProvider#getProviderInfo()
     */
    public String getProviderInfo()
    {
        return this.getClass().getName();
    }

    /**
     * Makes the diff by calling "diff" program.
     *
     * @param p1 DOCUMENT ME!
     * @param p2 DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String makeDiff(String p1, String p2)
    {
        File f1 = null;
        File f2 = null;
        String diff = null;

        try
        {
            f1 = FileUtil.newTmpFile(p1, m_encoding);
            f2 = FileUtil.newTmpFile(p2, m_encoding);

            String cmd = StringUtils.replace(m_diffCommand, "%s1", f1.getPath());
            cmd = StringUtils.replace(cmd, "%s2", f2.getPath());

            String output = FileUtil.runSimpleCommand(cmd, f1.getParent());

            // FIXME: Should this rely on the system default encoding?
            String rawWikiDiff = new String(output.getBytes("ISO-8859-1"), m_encoding);

            String htmlWikiDiff = TextUtil.replaceEntities(rawWikiDiff);

            if (m_traditionalColorization)
            { //FIXME, see comment near declaration...
                diff = colorizeDiff(diff);
            }
            else
            {
                diff = htmlWikiDiff;
            }
        }
        catch (IOException e)
        {
            log.error("Failed to do file diff", e);
        }
        catch (InterruptedException e)
        {
            log.error("Interrupted", e);
        }
        finally
        {
            if (f1 != null)
            {
                f1.delete();
            }

            if (f2 != null)
            {
                f2.delete();
            }
        }

        return diff;
    }

    /**
     * Goes through output provided by a diff command and inserts HTML tags to make the result more legible. Currently colors lines
     * starting with a + green, those starting with - reddish (hm, got to think of color blindness here...).
     *
     * @param diffText DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    static String colorizeDiff(String diffText)
            throws IOException
    {
        if (diffText == null)
        {
            return "Invalid diff - probably something wrong with server setup.";
        }

        String line = null;
        String start = null;
        String stop = null;

        BufferedReader in = new BufferedReader(new StringReader(diffText));
        StringBuffer out = new StringBuffer();

        out.append("<table class=\"" + WikiConstants.CSS_DIFF_BLOCK + "\">\n");

        while ((line = in.readLine()) != null)
        {
            stop = DIFF_CLOSE;

            if (line.length() > 0)
            {
                switch (line.charAt(0))
                {
                case DIFF_ADDED_SYMBOL:
                    start = DIFF_ADDED;

                    break;

                case DIFF_REMOVED_SYMBOL:
                    start = DIFF_REMOVED;

                    break;

                default:
                    start = DIFF_COMMENT;
                }
            }
            else
            {
                start = DIFF_COMMENT;
            }

            out.append(start);
            out.append(line.trim());
            out.append(stop + "\n");
        }

        out.append("</table>\n");

        return (out.toString());
    }
}
