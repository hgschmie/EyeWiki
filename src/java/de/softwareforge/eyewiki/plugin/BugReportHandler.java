package de.softwareforge.eyewiki.plugin;

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

import java.io.PrintWriter;
import java.io.StringWriter;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import de.softwareforge.eyewiki.TranslatorReader;
import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.WikiException;
import de.softwareforge.eyewiki.WikiPage;
import de.softwareforge.eyewiki.auth.UserProfile;

/**
 * Provides a handler for bug reports.  Still under construction.
 *
 * <ul>
 * <li>
 * "title" = title of the bug.  This is required.  If it is empty (as in "") it is a signal to the handler to return quietly.
 * </li>
 * </ul>
 *
 *
 * @author Janne Jalkanen
 */
public class BugReportHandler
        implements WikiPlugin
{
    /** DOCUMENT ME! */
    private static Logger log = Logger.getLogger(BugReportHandler.class);

    /** DOCUMENT ME! */
    public static final String TITLE = "title";

    /** DOCUMENT ME! */
    public static final String DESCRIPTION = "description";

    /** DOCUMENT ME! */
    public static final String VERSION = "version";

    /** DOCUMENT ME! */
    public static final String MAPPINGS = "map";

    /** DOCUMENT ME! */
    public static final String PAGE = "page";

    /** DOCUMENT ME! */
    public static final String DEFAULT_DATEFORMAT = "dd-MMM-yyyy HH:mm:ss zzz";

    /** DOCUMENT ME! */
    protected final WikiEngine engine;

    /**
     * Creates a new BugReportHandler object.
     *
     * @param engine DOCUMENT ME!
     */
    public BugReportHandler(final WikiEngine engine)
    {
        this.engine = engine;
    }

    /**
     * DOCUMENT ME!
     *
     * @param context DOCUMENT ME!
     * @param params DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws PluginException DOCUMENT ME!
     */
    public String execute(WikiContext context, Map params)
            throws PluginException
    {
        String title;
        String description;
        String version;
        String submitter = null;
        SimpleDateFormat format = new SimpleDateFormat(DEFAULT_DATEFORMAT);

        title = (String) params.get(TITLE);
        description = (String) params.get(DESCRIPTION);
        version = (String) params.get(VERSION);

        UserProfile wup = context.getCurrentUser();

        if (wup != null)
        {
            submitter = wup.getName();
        }

        if (title == null)
        {
            throw new PluginException("Title is required");
        }

        if (title.length() == 0)
        {
            return "";
        }

        if (description == null)
        {
            description = "";
        }

        if (version == null)
        {
            version = "unknown";
        }

        Properties mappings = parseMappings((String) params.get(MAPPINGS));

        //
        //  Start things
        //
        try
        {
            StringWriter str = new StringWriter();
            PrintWriter out = new PrintWriter(str);

            Date d = new Date();

            //
            //  Outputting of basic data
            //
            out.println("|" + mappings.getProperty(TITLE, "Title") + "|" + title);
            out.println("|" + mappings.getProperty("date", "Date") + "|" + format.format(d));
            out.println("|" + mappings.getProperty(VERSION, "Version") + "|" + version);

            if (submitter != null)
            {
                out.println("|" + mappings.getProperty("submitter", "Submitter") + "|" + submitter);
            }

            //
            //  Outputting the other parameters added to this.
            //
            for (Iterator i = params.entrySet().iterator(); i.hasNext();)
            {
                Map.Entry entry = (Map.Entry) i.next();

                if (entry.getKey().equals(TITLE) || entry.getKey().equals(DESCRIPTION) || entry.getKey().equals(VERSION)
                                || entry.getKey().equals(MAPPINGS) || entry.getKey().equals(PAGE)
                                || entry.getKey().equals(PluginManager.PARAM_BODY))
                {
                    continue; // Ignore this
                }

                //
                //  If no mapping has been defined, just ignore
                //  it.
                //
                String head = mappings.getProperty((String) entry.getKey(), (String) entry.getKey());

                if (head.length() > 0)
                {
                    out.println("|" + head + "|" + entry.getValue());
                }
            }

            out.println();
            out.println(description);

            out.close();

            //
            //  Now create a new page for this bug report
            //
            String pageName = findNextPage(title, (String) params.get(PAGE));

            WikiPage newPage = new WikiPage(pageName);
            WikiContext newContext = (WikiContext) context.clone();
            newContext.setPage(newPage);

            engine.saveText(newContext, str.toString());

            return "A new bug report has been created: <a href=\"" + context.getViewURL(pageName) + "\">" + pageName + "</a>";
        }
        catch (WikiException e)
        {
            log.error("Unable to save page!", e);

            return "Unable to create bug report";
        }
    }

    /**
     * Finds a free page name for adding the bug report.  Tries to construct a page, and if it's found, adds a number to it and
     * tries again.
     *
     * @param title DOCUMENT ME!
     * @param baseName DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private synchronized String findNextPage(String title, String baseName)
    {
        String basicPageName = ((baseName != null) ? baseName : "Bug") + TranslatorReader.cleanLink(title);

        String pageName = basicPageName;
        long lastbug = 2;

        while (engine.pageExists(pageName))
        {
            pageName = basicPageName + lastbug++;
        }

        return pageName;
    }

    /**
     * Just parses a mappings list in the form of "a=b;b=c;c=d".
     *
     * <p>
     * FIXME: Should probably be in TextUtil or somewhere.
     * </p>
     *
     * @param mappings DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private Properties parseMappings(String mappings)
    {
        Properties props = new Properties();

        if (mappings == null)
        {
            return props;
        }

        StringTokenizer tok = new StringTokenizer(mappings, ";");

        while (tok.hasMoreTokens())
        {
            String t = tok.nextToken();

            int colon = t.indexOf("=");

            String key;
            String value;

            if (colon > 0)
            {
                key = t.substring(0, colon);
                value = t.substring(colon + 1);
            }
            else
            {
                key = t;
                value = "";
            }

            props.setProperty(key, value);
        }

        return props;
    }
}
