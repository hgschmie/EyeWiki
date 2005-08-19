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
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.io.StringWriter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ecs.xhtml.b;
import org.apache.ecs.xhtml.div;
import org.apache.ecs.xhtml.li;
import org.apache.ecs.xhtml.pre;
import org.apache.ecs.xhtml.ul;
import org.apache.log4j.Logger;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;

import de.softwareforge.eyewiki.WikiConstants;
import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.WikiPage;
import de.softwareforge.eyewiki.WikiProperties;
import de.softwareforge.eyewiki.exception.InternalWikiException;
import de.softwareforge.eyewiki.util.FileUtil;

import org.picocontainer.PicoContainer;
import org.picocontainer.Startable;
import org.picocontainer.defaults.ObjectReference;
import org.picocontainer.defaults.SimpleReference;

/**
 * Manages plugin classes.  There exists a single instance of PluginManager per each instance of WikiEngine, that is, each eyeWiki
 * instance.
 *
 * <P>
 * A plugin is defined to have three parts:
 *
 * <OL>
 * <li>
 * The plugin class
 * </li>
 * <li>
 * The plugin parameters
 * </li>
 * <li>
 * The plugin body
 * </li>
 * </ol>
 *
 * For example, in the following line of code:
 * <pre>
 *  [{INSERT de.softwareforge.eyewiki.plugin.FunnyPlugin  foo='bar'
 *  blob='goo'
 *   abcdefghijklmnopqrstuvw
 *  01234567890}]
 *  </pre>
 * The plugin class is "de.softwareforge.eyewiki.plugin.FunnyPlugin", the parameters are "foo" and "blob" (having values "bar" and
 * "goo", respectively), and the plugin body is then "abcdefghijklmnopqrstuvw\n01234567890".   The plugin body is accessible via a
 * special parameter called "_body".
 * </p>
 *
 * <p>
 * If the parameter "debug" is set to "true" for the plugin, eyeWiki will output debugging information directly to the page if
 * there is an exception.
 * </p>
 *
 * <P>
 * The class name can be shortened, and marked without the package. For example, "FunnyPlugin" would be expanded to
 * "de.softwareforge.eyewiki.plugin.FunnyPlugin" automatically.  It is also possible to defined other packages, by setting the
 * "eyewiki.plugin.searchPath" property.  See the included eyewiki.properties file for examples.
 * </p>
 *
 * <P>
 * Even though the nominal way of writing the plugin is
 * <pre>
 *  [{INSERT pluginclass WHERE param1=value1...}],
 *  </pre>
 * it is possible to shorten this quite a lot, by skipping the INSERT, and WHERE words, and dropping the package name.  For
 * example:
 * <pre>
 *  [{INSERT de.softwareforge.eyewiki.plugin.Counter WHERE name='foo'}]
 *  </pre>
 * is the same as
 * <pre>
 *  [{Counter name='foo'}]
 *  </pre>
 * </p>
 *
 * @author Janne Jalkanen
 *
 * @since 1.6.1
 */
public class PluginManager
        implements WikiProperties, Startable
{
    /** DOCUMENT ME! */
    private static Logger log = Logger.getLogger(PluginManager.class);

    /** The name of the body content.  Current value is "_body". */
    public static final String PARAM_BODY = "_body";

    /** A special name to be used in case you want to see debug output */
    public static final String PARAM_DEBUG = "debug";

    /** DOCUMENT ME! */
    private Pattern m_pluginPattern;

    /** DOCUMENT ME! */
    private boolean m_pluginsEnabled = true;

    /** DOCUMENT ME! */
    private boolean m_initStage = false;

    /** Is the Manager started? */
    private boolean started = false;

    /** The Wiki Engine */
    private final WikiEngine engine;

    /** The Container to manage the Plugins */
    private PicoContainer pluginContainer = null;

    /**
     * Create a new PluginManager.
     *
     * @param engine DOCUMENT ME!
     * @param conf Contents of a "eyewiki.properties" file.
     *
     * @throws Exception DOCUMENT ME!
     * @throws InternalWikiException DOCUMENT ME!
     */
    public PluginManager(WikiEngine engine, Configuration conf)
            throws Exception
    {
        this.engine = engine;

        PatternCompiler compiler = new Perl5Compiler();

        try
        {
            m_pluginPattern = compiler.compile("\\{?(INSERT)?\\s*([\\w\\._]+)[ \\t]*(WHERE)?[ \\t]*");
        }
        catch (MalformedPatternException e)
        {
            log.fatal("Internal error: someone messed with pluginmanager patterns.", e);
            throw new InternalWikiException("PluginManager patterns are broken");
        }

        String wikiPluginFile = conf.getString(PROP_PLUGIN_FILE, null);

        if (StringUtils.isEmpty(wikiPluginFile))
        {
            return;
        }

        ObjectReference parentRef = new SimpleReference();
        parentRef.set(engine.getComponentContainer());

        ObjectReference pluginContainerRef = new SimpleReference();

        engine.setupContainer(pluginContainerRef, parentRef, wikiPluginFile);
        pluginContainer = (PicoContainer) pluginContainerRef.get();
    }

    /**
     * DOCUMENT ME!
     */
    public synchronized void start()
    {
        if (pluginContainer == null)
        {
            setStarted(true);

            return;
        }

        pluginContainer.start();

        try
        {
            setInitStage(true);

            // Run through all the pages, find the init plugins and fire them up.
            Collection pages = engine.getPageManager().getAllPages();

            for (Iterator it = pages.iterator(); it.hasNext();)
            {
                WikiPage page = (WikiPage) it.next();

                // content evaluation runs the plugins
                engine.getHTML(page);
            }
        }
        catch (Exception e)
        {
            log.fatal("While indexing the Plugins", e);
        }
        finally
        {
            setInitStage(false);
        }

        setStarted(true);
    }

    /**
     * DOCUMENT ME!
     */
    public synchronized void stop()
    {
        if (pluginContainer == null)
        {
            setStarted(false);

            return;
        }

        pluginContainer.stop();
        setStarted(false);
    }

    /**
     * DOCUMENT ME!
     *
     * @param started DOCUMENT ME!
     */
    protected void setStarted(final boolean started)
    {
        this.started = started;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isStarted()
    {
        return started;
    }

    /**
     * Enables or disables plugin execution.
     *
     * @param enabled DOCUMENT ME!
     */
    public void enablePlugins(final boolean enabled)
    {
        m_pluginsEnabled = enabled;
    }

    /**
     * Sets the initialization stage for the initial page scan.
     *
     * @param value DOCUMENT ME!
     */
    private void setInitStage(final boolean value)
    {
        m_initStage = value;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected boolean isInitStage()
    {
        return m_initStage;
    }

    /**
     * Returns plugin execution status. If false, plugins are not executed when they are encountered on a WikiPage, and an empty
     * string is returned in their place.
     *
     * @return DOCUMENT ME!
     */
    public boolean pluginsEnabled()
    {
        return m_pluginsEnabled;
    }

    /**
     * Returns true if the link is really command to insert a plugin.
     *
     * <P>
     * Currently we just check if the link starts with "{INSERT", or just plain "{" but not "{$".
     * </p>
     *
     * @param link Link text, i.e. the contents of text between [].
     *
     * @return True, if this link seems to be a command to insert a plugin here.
     */
    public static boolean isPluginLink(final String link)
    {
        return link.startsWith("{INSERT") || (link.startsWith("{") && !link.startsWith("{$"));
    }

    /**
     * Outputs a HTML-formatted version of a stack trace.
     *
     * @param params DOCUMENT ME!
     * @param t DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private String stackTrace(Map params, Throwable t)
    {
        div d = new div();
        d.setClass(WikiConstants.CSS_CLASS_ERROR);
        d.addElement("Plugin execution failed, stack trace follows:");

        StringWriter out = new StringWriter();
        pre pre = new pre(out.toString());
        pre.setClass(WikiConstants.CSS_CLASS_ERROR);

        b b = new b("Parameters to the plugin");
        b.setClass(WikiConstants.CSS_CLASS_ERROR);

        t.printStackTrace(new PrintWriter(out));
        d.addElement(pre);
        d.addElement(b);

        ul list = new ul();
        list.setClass(WikiConstants.CSS_CLASS_ERROR);

        for (Iterator i = params.keySet().iterator(); i.hasNext();)
        {
            String key = (String) i.next();

            li li = new li(key + "'='" + params.get(key));
            li.setClass(WikiConstants.CSS_CLASS_ERROR);
            list.addElement(li);
        }

        d.addElement(list);

        return d.toString();
    }

    /**
     * Executes a plugin class in the given context.
     *
     * <P>
     * Used to be private, but is public since 1.9.21.
     * </p>
     *
     * @param context The current WikiContext.
     * @param classname The name of the class.  Can also be a shortened version without the package name, since the class name is
     *        searched from the package search path.
     * @param params A parsed map of key-value pairs.
     *
     * @return Whatever the plugin returns.
     *
     * @throws PluginException If the plugin execution failed for some reason.
     *
     * @since 2.0
     */
    public String execute(WikiContext context, String classname, Map params)
            throws PluginException
    {
        if (!m_pluginsEnabled || (pluginContainer == null))
        {
            return ("");
        }

        WikiPlugin plugin = (WikiPlugin) findPlugin(classname);

        if (plugin == null)
        {
            return "Plugin " + classname + " not found!";
        }

        //
        //  ...and launch.
        //
        boolean debug = BooleanUtils.toBoolean((String) params.get(PARAM_DEBUG));

        try
        {
            if (isInitStage())
            {
                if (plugin instanceof InitializablePlugin)
                {
                    ((InitializablePlugin) plugin).initialize(context, params);
                }

                return "";
            }
            else
            {
                return plugin.execute(context, params);
            }
        }
        catch (PluginException e)
        {
            if (debug)
            {
                return stackTrace(params, e);
            }

            // Just pass this exception onward.
            throw (PluginException) e.fillInStackTrace();
        }
        catch (Throwable t)
        {
            // But all others get captured here.
            log.info("Plugin failed while executing:", t);

            if (debug)
            {
                return stackTrace(params, t);
            }

            throw new PluginException("Plugin failed", t);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param pluginName DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public WikiPlugin findPlugin(String pluginName)
    {
        return (WikiPlugin) pluginContainer.getComponentInstance(pluginName);
    }

    /**
     * Parses plugin arguments.  Handles quotes and all other kewl stuff.
     *
     * @param argstring The argument string to the plugin.  This is typically a list of key-value pairs, using "'" to escape spaces
     *        in strings, followed by an empty line and then the plugin body.  In case the parameter is null, will return an empty
     *        parameter list.
     *
     * @return A parsed list of parameters.  The plugin body is put into a special parameter defined by PluginManager.PARAM_BODY.
     *
     * @throws IOException If the parsing fails.
     */
    public Map parseArgs(String argstring)
            throws IOException
    {
        HashMap arglist = new HashMap();
        StringReader in = new StringReader(argstring);
        StreamTokenizer tok = new StreamTokenizer(in);
        int type;

        //
        //  Protection against funny users.
        //
        if (argstring == null)
        {
            return arglist;
        }

        String param = null;
        String value = null;

        tok.eolIsSignificant(true);

        boolean potentialEmptyLine = false;
        boolean quit = false;

        while (!quit)
        {
            String s;

            type = tok.nextToken();

            switch (type)
            {
            case StreamTokenizer.TT_EOF:
                quit = true;
                s = null;

                break;

            case StreamTokenizer.TT_WORD:
                s = tok.sval;
                potentialEmptyLine = false;

                break;

            case StreamTokenizer.TT_EOL:
                quit = potentialEmptyLine;
                potentialEmptyLine = true;
                s = null;

                break;

            case StreamTokenizer.TT_NUMBER:
                s = Integer.toString(new Double(tok.nval).intValue());
                potentialEmptyLine = false;

                break;

            case '\'':
                s = tok.sval;

                break;

            default:
                s = null;
            }

            //
            //  Assume that alternate words on the line are
            //  parameter and value, respectively.
            //
            if (s != null)
            {
                if (param == null)
                {
                    param = s;
                }
                else
                {
                    value = s;

                    arglist.put(param, value);

                    // log.debug("ARG: "+param+"="+value);
                    param = null;
                }
            }
        }

        //
        //  Now, we'll check the body.
        //
        if (potentialEmptyLine)
        {
            StringWriter out = new StringWriter();
            FileUtil.copyContents(in, out);

            String bodyContent = out.toString();

            if (bodyContent != null)
            {
                arglist.put(PARAM_BODY, bodyContent);
            }
        }

        return arglist;
    }

    /**
     * Parses a plugin.  Plugin commands are of the form: [{INSERT myplugin WHERE param1=value1, param2=value2}] myplugin may
     * either be a class name or a plugin alias.
     *
     * <P>
     * This is the main entry point that is used.
     * </p>
     *
     * @param context The current WikiContext.
     * @param commandline The full command line, including plugin name, parameters and body.
     *
     * @return HTML as returned by the plugin, or possibly an error message.
     *
     * @throws PluginException DOCUMENT ME!
     */
    public String execute(WikiContext context, String commandline)
            throws PluginException
    {
        if (!m_pluginsEnabled)
        {
            return ("");
        }

        PatternMatcher matcher = new Perl5Matcher();

        try
        {
            if (matcher.contains(commandline, m_pluginPattern))
            {
                MatchResult res = matcher.getMatch();

                String plugin = res.group(2);
                String args =
                    commandline.substring(res.endOffset(0),
                        commandline.length() - ((commandline.charAt(commandline.length() - 1) == '}') ? 1 : 0));
                Map arglist = parseArgs(args);

                return execute(context, plugin, arglist);
            }
        }
        catch (NoSuchElementException e)
        {
            String msg = "Missing parameter in plugin definition: " + commandline;
            log.warn(msg, e);
            throw new PluginException(msg, e);
        }
        catch (IOException e)
        {
            String msg = "Problems with parsing plugin arguments: " + commandline;
            log.warn(msg, e);
            throw new PluginException(msg, e);
        }

        // FIXME: We could either return an empty string "", or
        // the original line.  If we want unsuccessful requests
        // to be invisible, then we should return an empty string.
        return commandline;
    }
}
