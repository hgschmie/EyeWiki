/*
    JSPWiki - a JSP-based WikiWiki clone.

    Copyright (C) 2001 Janne Jalkanen (Janne.Jalkanen@iki.fi)

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation; either version 2.1 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package com.ecyrd.jspwiki.manager;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;

import com.ecyrd.jspwiki.Release;
import com.ecyrd.jspwiki.WikiContext;
import com.ecyrd.jspwiki.WikiPage;
import com.ecyrd.jspwiki.WikiProvider;
import com.ecyrd.jspwiki.auth.UserProfile;
import com.ecyrd.jspwiki.exception.NoSuchVariableException;


/**
 * Manages variables.  Variables are case-insensitive.  A list of all available variables is on a
 * Wiki page called "WikiVariables".
 *
 * @author Janne Jalkanen
 *
 * @since 1.9.20.
 */
public class VariableManager
{
    /** DOCUMENT ME! */
    public static final String VAR_ERROR = "error";

    /** DOCUMENT ME! */
    public static final String VAR_MSG = "msg";

    /**
     * Creates a new VariableManager object.
     *
     * @param conf DOCUMENT ME!
     */
    public VariableManager(Configuration conf)
    {
    }

    /**
     * Returns true if the link is really command to insert a variable.
     *
     * <P>
     * Currently we just check if the link starts with "{$".
     * </p>
     *
     * @param link DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static boolean isVariableLink(String link)
    {
        return link.startsWith("{$");
    }

    /**
     * Parses the link and finds a value.
     *
     * <P>
     * A variable is inserted using the notation [{$variablename}].
     * </p>
     *
     * @param context DOCUMENT ME!
     * @param link DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws IllegalArgumentException If the format is not valid (does not start with {$, is zero
     *         length, etc.)
     * @throws NoSuchVariableException If a variable is not known.
     */
    public String parseAndGetValue(WikiContext context, String link)
            throws NoSuchVariableException
    {
        if (!link.startsWith("{$"))
        {
            throw new IllegalArgumentException("Link does not start with {$");
        }

        if (!link.endsWith("}"))
        {
            throw new IllegalArgumentException("Link does not end with }");
        }

        String varName = link.substring(2, link.length() - 1);

        return getValue(context, varName.trim());
    }

    /**
     * This method does in-place expansion of any variables.  However, the expansion is not done
     * twice, that is, a variable containing text $variable will not be expanded.
     *
     * <P>
     * The variables should be in the same format ({$variablename} as in the web pages.
     * </p>
     *
     * @param context The WikiContext of the current page.
     * @param source The source string.
     *
     * @return DOCUMENT ME!
     */

    // FIXME: somewhat slow.
    public String expandVariables(WikiContext context, String source)
    {
        StringBuffer result = new StringBuffer();

        for (int i = 0; i < source.length(); i++)
        {
            if (source.charAt(i) == '{')
            {
                if ((i < (source.length() - 2)) && (source.charAt(i + 1) == '$'))
                {
                    int end = source.indexOf('}', i);

                    if (end != -1)
                    {
                        String varname = source.substring(i + 2, end);
                        String value;

                        try
                        {
                            value = getValue(context, varname);
                        }
                        catch (NoSuchVariableException e)
                        {
                            value = e.getMessage();
                        }
                        catch (IllegalArgumentException e)
                        {
                            value = e.getMessage();
                        }

                        result.append(value);
                        i = end;

                        continue;
                    }
                }
                else
                {
                    result.append('{');
                }
            }
            else
            {
                result.append(source.charAt(i));
            }
        }

        return result.toString();
    }

    /**
     * Returns a value of the named variable.
     *
     * @param context DOCUMENT ME!
     * @param varName Name of the variable.
     *
     * @return DOCUMENT ME!
     *
     * @throws IllegalArgumentException If the name is somehow broken.
     * @throws NoSuchVariableException If a variable is not known.
     */

    // FIXME: Currently a bit complicated.  Perhaps should use reflection
    //        or something to make an easy way of doing stuff.
    public String getValue(WikiContext context, String varName)
            throws NoSuchVariableException
    {
        if (varName == null)
        {
            throw new IllegalArgumentException("Null variable name.");
        }

        if (StringUtils.isEmpty(varName))
        {
            throw new IllegalArgumentException("Zero length variable name.");
        }

        // Faster than doing equalsIgnoreCase()
        String name = varName.toLowerCase();
        String res = "";

        if (name.equals("pagename"))
        {
            res = context.getPage().getName();
        }
        else if (name.equals("applicationname"))
        {
            res = context.getEngine().getApplicationName();
        }
        else if (name.equals("jspwikiversion"))
        {
            res = Release.getVersionString();
        }
        else if (name.equals("encoding"))
        {
            res = context.getEngine().getContentEncoding();
        }
        else if (name.equals("totalpages"))
        {
            res = Integer.toString(context.getEngine().getPageCount());
        }
        else if (name.equals("pageprovider"))
        {
            res = context.getEngine().getCurrentProvider();
        }
        else if (name.equals("pageproviderdescription"))
        {
            res = context.getEngine().getCurrentProviderInfo();
        }
        else if (name.equals("attachmentprovider"))
        {
            WikiProvider p = context.getEngine().getAttachmentManager().getCurrentProvider();
            res = (p != null)
                ? p.getClass().getName()
                : "-";
        }
        else if (name.equals("attachmentproviderdescription"))
        {
            WikiProvider p = context.getEngine().getAttachmentManager().getCurrentProvider();

            res = (p != null)
                ? p.getProviderInfo()
                : "-";
        }
        else if (name.equals("interwikilinks"))
        {
            StringBuffer sb = new StringBuffer();
            for (Iterator i = context.getEngine().getAllInterWikiLinks().iterator(); i.hasNext();)
            {
                String link = (String) i.next();
                sb.append(link)
                        .append(" --&gt; ")
                        .append(context.getEngine().getInterWikiURL(link))
                        .append("<br />\n");
            }

            res = sb.toString();
        }
        else if (name.equals("inlinedimages"))
        {
            StringBuffer sb = new StringBuffer();
            for (
                Iterator i = context.getEngine().getAllInlinedImagePatterns().iterator();
                            i.hasNext();)
            {
                String ptrn = (String) i.next();
                sb.append(ptrn)
                        .append("<br />\n");
            }

            res = sb.toString();
        }
        else if (name.equals("pluginpath"))
        {
            res = context.getEngine().getPluginSearchPath();

            res = (res == null)
                ? "-"
                : res;
        }
        else if (name.equals("baseurl"))
        {
            res = context.getEngine().getBaseURL();
        }
        else if (name.equals("uptime"))
        {
            Date now = new Date();
            long secondsRunning =
                (now.getTime() - context.getEngine().getStartTime().getTime()) / 1000L;

            long seconds = secondsRunning % 60;
            long minutes = (secondsRunning /= 60) % 60;
            long hours = (secondsRunning /= 60) % 24;
            long days = secondsRunning /= 24;

            return days + "d, " + hours + "h " + minutes + "m " + seconds + "s";
        }
        else if (name.equals("loginstatus"))
        {
            UserProfile wup = context.getCurrentUser();

            int status = (wup != null)
                ? wup.getLoginStatus()
                : UserProfile.NONE;

            switch (status)
            {
            case UserProfile.NONE:
                return "unknown (not logged in)";

            case UserProfile.COOKIE:
                return "named (cookie)";

            case UserProfile.CONTAINER:
                return "validated (container)";

            case UserProfile.PASSWORD:
                return "validated (password)";

            default:
                return "ILLEGAL STATUS!";
            }
        }
        else if (name.equals("username"))
        {
            UserProfile wup = context.getCurrentUser();

            return (wup != null)
            ? wup.getName()
            : "not logged in";
        }
        else if (name.equals("requestcontext"))
        {
            return context.getRequestContext();
        }
        else if (name.equals("pagefilters"))
        {
            List filters = context.getEngine().getFilterManager().getFilterList();
            StringBuffer sb = new StringBuffer();

            for (Iterator i = filters.iterator(); i.hasNext();)
            {
                String f = i.next().getClass().getName();

                //
                //  Skip some known internal filters.
                //  FIXME: Quite a klugde.
                //
                if (f.endsWith("ReferenceManager") || f.endsWith("WikiDatabase$SaveFilter"))
                {
                    continue;
                }

                if (sb.length() > 0)
                {
                    sb.append(", ");
                }

                sb.append(f);
            }

            return sb.toString();
        }
        else
        {
            //
            // Check if such a context variable exists,
            // returning its string representation.
            //
            if ((context.getVariable(varName)) != null)
            {
                return context.getVariable(varName).toString();
            }

            // Next-to-final straw: attempt to fetch using property name
            // We don't allow fetching any other properties than those starting
            // with "jspwiki.".  I know my own code, but I can't vouch for bugs
            // in other people's code... :-)
            if (varName.startsWith("jspwiki."))
            {
                Configuration conf = context.getEngine().getWikiConfiguration();

                res = conf.getString(res, null);

                if (res != null)
                {
                    return res;
                }
            }

            // And the final straw: see if the current page has named metadata.
            WikiPage pg = context.getPage();

            if (pg != null)
            {
                Object metadata = pg.getAttribute(varName);

                if (metadata != null)
                {
                    return (metadata.toString());
                }
            }

            //
            //  Well, I guess it wasn't a final straw.  We also allow
            //  variables from the session and the request (in this order).
            //
            HttpServletRequest req = context.getHttpRequest();

            if (req != null)
            {
                HttpSession session = req.getSession();

                Object attribute = session.getAttribute(varName);

                if (attribute != null)
                {
                    return String.valueOf(attribute);
                }

                if ((res = context.getHttpParameter(varName)) != null)
                {
                    return res;
                }
            }

            //
            //  Final defaults for some known quantities.
            //
            if (varName.equals(VAR_ERROR) || varName.equals(VAR_MSG))
            {
                return "";
            }

            throw new NoSuchVariableException("No variable " + varName + " defined.");
        }

        return res;
    }
}
