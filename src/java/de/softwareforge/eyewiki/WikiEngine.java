/*
  JSPWiki - a JSP-based WikiWiki clone.

  Copyright (C) 2001-2004 Janne Jalkanen (Janne.Jalkanen@iki.fi)

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
package de.softwareforge.eyewiki;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.TreeSet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


import de.softwareforge.eyewiki.attachment.Attachment;
import de.softwareforge.eyewiki.attachment.AttachmentManager;
import de.softwareforge.eyewiki.auth.AuthorizationManager;
import de.softwareforge.eyewiki.auth.UserManager;
import de.softwareforge.eyewiki.auth.UserProfile;
import de.softwareforge.eyewiki.diff.DifferenceManager;
import de.softwareforge.eyewiki.exception.InternalWikiException;
import de.softwareforge.eyewiki.exception.NoRequiredPropertyException;
import de.softwareforge.eyewiki.filters.FilterException;
import de.softwareforge.eyewiki.filters.FilterManager;
import de.softwareforge.eyewiki.manager.PageManager;
import de.softwareforge.eyewiki.manager.ReferenceManager;
import de.softwareforge.eyewiki.manager.TemplateManager;
import de.softwareforge.eyewiki.manager.VariableManager;
import de.softwareforge.eyewiki.plugin.PluginManager;
import de.softwareforge.eyewiki.providers.ProviderException;
import de.softwareforge.eyewiki.providers.WikiPageProvider;
import de.softwareforge.eyewiki.rss.RSSGenerator;
import de.softwareforge.eyewiki.url.URLConstructor;
import de.softwareforge.eyewiki.util.FileUtil;
import de.softwareforge.eyewiki.util.TextUtil;

import org.nanocontainer.integrationkit.ContainerBuilder;
import org.nanocontainer.script.xml.XMLContainerBuilder;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.ObjectReference;
import org.picocontainer.defaults.SimpleReference;


/**
 * Provides Wiki services to the JSP page.
 * 
 * <P>
 * This is the main interface through which everything should go.
 * </p>
 * 
 * <P>
 * Using this class:  Always get yourself an instance from JSP page by using the
 * WikiEngine.getInstance() method.  Never create a new WikiEngine() from scratch, unless you're
 * writing tests.
 * </p>
 * 
 * <p>
 * There's basically only a single WikiEngine for each web application, and you should always get
 * it using the WikiEngine.getInstance() method.
 * </p>
 *
 * @author Janne Jalkanen
 */
public class WikiEngine
        implements WikiProperties
{
    /** DOCUMENT ME! */
    private static final Logger log = Logger.getLogger(WikiEngine.class);

    /** True, if log4j has been configured. */

    // FIXME: If you run multiple applications, the first application
    // to run defines where the log goes.  Not what we want.
    private static boolean c_configured = false;

    /**
     * The web.xml parameter that defines where the config file is to be found. If it is not
     * defined, uses the default as defined by PARAM_PROPERTYFILE_DEFAULT.
     *
     * @value jspwiki.propertyfile
     */
    public static final String PARAM_CONFIGFILE = "jspwiki.propertyfile";

    /**
     * Path to the default property file.
     *
     * @value /WEB_INF/jspwiki.properties
     */
    public static final String PARAM_CONFIGFILE_DEFAULT = "/WEB-INF/jspwiki.properties";

    /** Contains the default properties for JSPWiki. */
    private static final String [] PROP_SPECIAL_PAGES_DEFAULT =
        {
            "Login",
            "Login.jsp",
            "UserPreferences",
            "UserPreferences.jsp",
            "Search",
            "Search.jsp",
            "FindPage",
            "FindPage.jsp"
        };

    /** The name of the cookie that gets stored to the user browser. */
    public static final String PREFS_COOKIE_NAME = "JSPWikiUserProfile";

    /** Stores an internal list of engines per each ServletContext */
    private static Hashtable c_engines = new Hashtable();

    /** Stores Configuration per WikiEngine. */
    private Configuration conf = null;

    /** Should the user info be saved with the page data as well? */

    // NOT YET USED private boolean m_saveUserInfo = true;

    /** If true, uses UTF8 encoding for all data */
    private boolean m_useUTF8 = true;

    /** If true, we'll also consider english plurals (+s) a match. */
    private boolean m_matchEnglishPlurals = true;

    /** Stores the base URL. */
    private String m_baseURL;

    /**
     * Store the file path to the basic URL.  When we're not running as a servlet, it defaults to
     * the user's current directory.
     */
    private String m_rootPath = System.getProperty("user.dir");

    /**
     * Store the ServletContext that we're in.  This may be null if WikiEngine is not running
     * inside a servlet container (i.e. when testing).
     */
    private ServletContext m_servletContext = null;

    /** If true, all titles will be cleaned. */
    private boolean m_beautifyTitle = false;

    /** Stores the template path.  This is relative to "templates". */
    private String m_templateDir;

    /** The default front page name.  Defaults to "Main". */
    private String m_frontPage;

    /** The time when this engine was started. */
    private Date m_startTime;

    /** The location where the work directory is. */
    private String m_workDir;

    /** The location where the pages directory is. */
    private String m_pageDir;

    /** The location where the storage directory is. */
    private String m_storageDir;

    /** Each engine has their own application id. */
    private String m_appid = "";

    /** DOCUMENT ME! */
    private boolean m_isConfigured = false; // Flag.

    /**
     * If true, all the pathes from the various file providers are relative to the root of the web
     * application
     */
    private boolean wikiRelativePathes = PROP_WIKIRELATIVE_PATHES_DEFAULT;

    /** The main container reference */
    private final ObjectReference mainContainerRef = new SimpleReference();

    /** The component container reference */
    private final ObjectReference componentContainerRef = new SimpleReference();

    /**
     * Instantiate the WikiEngine using a given set of properties. Use this constructor for testing
     * purposes only.
     *
     * @param conf DOCUMENT ME!
     *
     * @throws WikiException DOCUMENT ME!
     */
    public WikiEngine(final Configuration conf)
            throws WikiException
    {
        setRootPath(null); // No root dir defined
        initialize(conf);
    }

    /**
     * Instantiate using this method when you're running as a servlet and WikiEngine will figure
     * out where to look for the configuration file. Do not use this method - use
     * WikiEngine.getInstance() instead.
     *
     * @param context DOCUMENT ME!
     * @param appid DOCUMENT ME!
     * @param conf DOCUMENT ME!
     *
     * @throws WikiException DOCUMENT ME!
     */
    protected WikiEngine(ServletContext context, String appid, Configuration conf)
            throws WikiException
    {
        m_servletContext = context;
        m_appid = appid;

        try
        {
            //
            //  Note: May be null, if JSPWiki has been deployed in a WAR file.
            //
            setRootPath(context.getRealPath("/"));
            initialize(conf);

            if (log.isInfoEnabled())
            {
                log.info("Root path for this Wiki is: '" + getRootPath() + "'");
            }
        }
        catch (Exception e)
        {
            context.log(Release.APPNAME + ": Unable to load and setup configuration.", e);
        }
    }

    /**
     * Gets a WikiEngine related to this servlet.  Since this method is only called from JSP pages
     * (and JspInit()) to be specific, we throw a RuntimeException if things don't work.
     *
     * @param config The ServletConfig object for this servlet.
     *
     * @return A WikiEngine instance.
     *
     * @throws InternalWikiException in case something fails.  This is a RuntimeException, so be
     *         prepared for it.
     */

    // FIXME: It seems that this does not work too well, jspInit()
    // does not react to RuntimeExceptions, or something...
    public static synchronized WikiEngine getInstance(ServletConfig config)
            throws InternalWikiException
    {
        return (getInstance(config.getServletContext(), null));
    }

    /**
     * Gets a WikiEngine related to the servlet. Works like getInstance(ServletConfig), but does
     * not force the Properties object. This method is just an optional way of initializing a
     * WikiEngine for embedded JSPWiki applications; normally, you should use
     * getInstance(ServletConfig).
     *
     * @param config The ServletConfig of the webapp servlet/JSP calling this method.
     * @param props A set of properties, or null, if we are to load JSPWiki's default
     *        jspwiki.properties (this is the usual case).
     *
     * @return DOCUMENT ME!
     */
    public static synchronized WikiEngine getInstance(ServletConfig config, Properties props)
    {
        return (getInstance(config.getServletContext(), null));
    }

    /**
     * Gets a WikiEngine related to the servlet. Works just like getInstance( ServletConfig)
     *
     * @param context The ServletContext of the webapp servlet/JSP calling this method.
     * @param conf A set of properties, or null, if we are to load JSPWiki's default
     *        jspwiki.properties (this is the usual case).
     *
     * @return DOCUMENT ME!
     *
     * @throws InternalWikiException DOCUMENT ME!
     */
    public static synchronized WikiEngine getInstance(
        final ServletContext context, final Configuration conf)
            throws InternalWikiException
    {
        Configuration wikiConf = conf;
        String appid = Integer.toString(context.hashCode()); //FIXME: Kludge, use real type.

        context.log("Application " + appid + " requests WikiEngine.");

        WikiEngine engine = (WikiEngine) c_engines.get(appid);

        if (engine == null)
        {
            context.log(" Assigning new log to " + appid);

            try
            {
                if (wikiConf == null)
                {
                    wikiConf = loadWebAppProps(context);
                }

                engine = new WikiEngine(context, appid, wikiConf);
            }
            catch (Exception e)
            {
                context.log("ERROR: Failed to create a Wiki engine: ", e);

                throw new InternalWikiException("No wiki engine, check logs.");
            }

            c_engines.put(appid, engine);
        }

        return engine;
    }

    /**
     * Loads the webapp properties based on servlet context information. Returns a Properties
     * object containing the settings, or null if unable to load it. (The default file is
     * WEB-INF/jspwiki.properties, and can be overridden by setting PARAM_PROPERTYFILE in the
     * server or webapp configuration.)
     *
     * @param context DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private static Configuration loadWebAppProps(ServletContext context)
    {
        String configFile = context.getInitParameter(PARAM_CONFIGFILE);
        InputStream configStream = null;

        try
        {
            //
            //  Figure out where our configuration lies.
            //
            if (configFile == null)
            {
                context.log(
                    "No " + PARAM_CONFIGFILE + " defined for this context, "
                    + "using default from " + PARAM_CONFIGFILE_DEFAULT);

                //  Use the default config file.
                configStream = context.getResourceAsStream(PARAM_CONFIGFILE_DEFAULT);
            }
            else
            {
                context.log("Reading Configuration from " + configFile);
                configStream = new FileInputStream(new File(configFile));
            }

            if (configStream == null)
            {
                throw new WikiException("Config file cannot be found!" + configFile);
            }

            InputStreamReader isr =
                new InputStreamReader(configStream, WikiConstants.DEFAULT_ENCODING);
            PropertiesConfiguration conf = new PropertiesConfiguration();
            conf.setThrowExceptionOnMissing(true);
            conf.load(isr);

            Map pageMap = TextUtil.createMap(PROP_SPECIAL_PAGES_PREFIX, PROP_SPECIAL_PAGES_DEFAULT);

            for (Iterator it = pageMap.keySet().iterator(); it.hasNext();)
            {
                String key = (String) it.next();

                try
                {
                    // Do not remove!
                    String val = conf.getString(key);
                }
                catch (NoSuchElementException e)
                {
                    conf.addProperty(key, pageMap.get(key));
                }
            }

            return conf;
        }
        catch (Exception e)
        {
            context.log(
                Release.APPNAME
                + ": Unable to load and setup configuration from jspwiki.properties", e);
        }
        finally
        {
            IOUtils.closeQuietly(configStream);
        }

        return null;
    }

    /**
     * Does all the real initialization.
     *
     * @param conf DOCUMENT ME!
     *
     * @throws WikiException DOCUMENT ME!
     */
    private void initialize(Configuration conf)
            throws WikiException
    {
        m_startTime = new Date();

        wikiRelativePathes =
            conf.getBoolean(PROP_WIKIRELATIVE_PATHES, PROP_WIKIRELATIVE_PATHES_DEFAULT);

        conf.setProperty(PROP_ROOTDIR, wikiRelativePathes
            ? getRootPath()
            : "");

        this.conf = conf;

        //
        //  Initialized log4j.  However, make sure that
        //  we don't initialize it multiple times.  Also, if
        //  all of the log4j statements have been removed from
        //  the property file, we do not do any property setting
        //  either.
        //
        if (!c_configured)
        {
            if (conf.getProperty("log4j.rootCategory") != null)
            {
                Properties p = ConfigurationConverter.getProperties(conf);
                PropertyConfigurator.configure(p);
            }

            c_configured = true;
        }

        if (log.isInfoEnabled())
        {
            log.info("*******************************************");
            log.info("JSPWiki " + Release.VERSTR + " starting. Whee!");
        }

        log.debug("Configuring WikiEngine...");

        //
        //  Create and find the default working directory.
        //
        m_workDir = conf.getString(PROP_WORKDIR, null);

        if (m_workDir == null)
        {
            m_workDir = System.getProperty("java.io.tmpdir", ".");
            m_workDir += (File.separator + Release.APPNAME + "-" + m_appid);
        }
        else
        {
            m_workDir = getValidPath(m_workDir);
        }

        createDirectory(m_workDir);

        if (log.isInfoEnabled())
        {
            log.info("JSPWiki working directory is '" + m_workDir + "'");
        }

        //
        //  Create and find the pages directory (might be null e.g. for JDBC)
        //
        m_pageDir = conf.getString(PROP_PAGEDIR, null);

        if (m_pageDir != null)
        {
            m_pageDir = getValidPath(m_pageDir);
            createDirectory(m_pageDir);

            if (log.isInfoEnabled())
            {
                log.info("JSPWiki pages directory is '" + m_pageDir + "'");
            }
        }
        else
        {
            log.info(
                "No JSPWiki pages directory defined, be sure to use a non-filesystem Page Provider.");
        }

        //
        //  Create and find the storages directory (might be null e.g. for JDBC)
        //
        m_storageDir = conf.getString(PROP_STORAGEDIR, null);

        if (m_storageDir != null)
        {
            m_storageDir = getValidPath(m_storageDir);
            createDirectory(m_storageDir);

            if (log.isInfoEnabled())
            {
                log.info("JSPWiki storage directory is '" + m_storageDir + "'");
            }
        }
        else
        {
            log.info(
                "No JSPWiki storage directory defined, be sure to use a non-filesystem AttachmentProvider.");
        }

        // NOT YET USED m_saveUserInfo = conf.getBoolean(PROP_STOREUSERNAME, PROP_STOREUSERNAME_DEFAULT);
        m_useUTF8 = "UTF-8".equals(conf.getString(PROP_ENCODING, PROP_ENCODING_DEFAULT));

        m_baseURL = conf.getString(PROP_BASEURL, PROP_BASEURL_DEFAULT);

        m_beautifyTitle = conf.getBoolean(PROP_BEAUTIFYTITLE, PROP_BEAUTIFYTITLE_DEFAULT);

        m_matchEnglishPlurals = conf.getBoolean(PROP_MATCHPLURALS, PROP_MATCHPLURALS_DEFAULT);

        m_templateDir = conf.getString(PROP_TEMPLATEDIR, PROP_TEMPLATEDIR_DEFAULT);

        m_frontPage = conf.getString(PROP_FRONTPAGE, PROP_FRONTPAGE_DEFAULT);

        //
        // Start up the Picocontainer
        //
        try
        {
            setupMainContainer();

            String wikiComponentsFile =
                conf.getString(PROP_COMPONENTS_FILE, PROP_COMPONENTS_FILE_DEFAULT);
            setupContainer(componentContainerRef, mainContainerRef, wikiComponentsFile);
            ((PicoContainer) componentContainerRef.get()).start();
        }
        catch (Exception e)
        {
            // RuntimeExceptions may occur here, even if they shouldn't.
            log.fatal("Failed to start component container.", e);
            throw new WikiException("Failed to start component container: " + e.getMessage());
        }

        log.info("WikiEngine configured.");
        m_isConfigured = true;
    }

    /**
     * DOCUMENT ME!
     *
     * @param dir DOCUMENT ME!
     *
     * @throws WikiException DOCUMENT ME!
     */
    public static void createDirectory(final String dir)
            throws WikiException
    {
        if (dir != null)
        {
            try
            {
                File d = new File(dir);

                if (!d.exists())
                {
                    d.mkdirs();
                }

                if (!d.isDirectory())
                {
                    throw new WikiException(
                        "Requested Directory " + dir + " exists, but is no directory!");
                }

                if (!d.canRead())
                {
                    throw new WikiException("No permission to read directory " + dir);
                }

                if (!d.canWrite())
                {
                    throw new WikiException("No permission to write to directory " + dir);
                }
            }
            catch (SecurityException e)
            {
                String err = "Unable to find or create the requested directory: " + dir;
                log.fatal(err, e);
                throw new WikiException(err);
            }
        }
    }

    /**
     * Throws an exception if a property is not found.
     *
     * @param props A set of properties to search the key in.
     * @param key The key to look for.
     *
     * @return The required property
     *
     * @throws NoRequiredPropertyException If the search key is not in the property set.
     */

    // FIXME: Should really be in some util file.
    public static String getRequiredProperty(Properties props, String key)
            throws NoRequiredPropertyException
    {
        String value = props.getProperty(key);

        if (value == null)
        {
            throw new NoRequiredPropertyException("Required property not found", key);
        }

        return value;
    }

    /**
     * Internal method for getting a property.  This is used by the TranslatorReader for example.
     *
     * @return DOCUMENT ME!
     */
    public Configuration getWikiConfiguration()
    {
        return conf;
    }

    /**
     * Returns the JSPWiki working directory.
     *
     * @return DOCUMENT ME!
     *
     * @since 2.1.100
     */
    public String getWorkDir()
    {
        return m_workDir;
    }

    /**
     * Returns a page Directory for use with the JSP Wiki
     *
     * @return DOCUMENT ME!
     *
     * @since 2.2
     */
    public String getPageDir()
    {
        return m_pageDir;
    }

    /**
     * Returns a storage Directory for use with the JSP Wiki
     *
     * @return DOCUMENT ME!
     *
     * @since 2.2
     */
    public String getStorageDir()
    {
        return m_storageDir;
    }

    /**
     * Returns the current template directory.
     *
     * @return DOCUMENT ME!
     *
     * @since 1.9.20
     */
    public String getTemplateDir()
    {
        return m_templateDir;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public RSSGenerator getRSSGenerator()
    {
        return (RSSGenerator) getComponentContainer().getComponentInstance(
            WikiConstants.RSS_GENERATOR);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public TemplateManager getTemplateManager()
    {
        return (TemplateManager) getComponentContainer().getComponentInstance(
            WikiConstants.TEMPLATE_MANAGER);
    }

    /**
     * Returns the base URL.  Always prepend this to any reference you make.
     *
     * @return DOCUMENT ME!
     *
     * @since 1.6.1
     */
    public String getBaseURL()
    {
        return m_baseURL;
    }

    /**
     * Returns the moment when this engine was started.
     *
     * @return DOCUMENT ME!
     *
     * @since 2.0.15.
     */
    public Date getStartTime()
    {
        return new Date((m_startTime != null)
            ? m_startTime.getTime()
            : 0);
    }

    /**
     * Returns an URL if a WikiContext is not available.
     *
     * @param context The WikiContext (VIEW, EDIT, etc...)
     * @param pageName Name of the page, as usual
     * @param params List of parameters. May be null, if no parameters.
     * @param absolute If true, will generate an absolute URL regardless of properties setting.
     *
     * @return DOCUMENT ME!
     */
    public String getURL(String context, String pageName, String params, boolean absolute)
    {
        return getURLConstructor().makeURL(context, pageName, absolute, params);
    }

    /**
     * Returns the default front page, if no page is used.
     *
     * @return DOCUMENT ME!
     */
    public String getFrontPage()
    {
        return m_frontPage;
    }

    /**
     * Returns the ServletContext that this particular WikiEngine was initialized with.  <B>It may
     * return null</B>, if the WikiEngine is not running inside a servlet container!
     *
     * @return ServletContext of the WikiEngine, or null.
     *
     * @since 1.7.10
     */
    public ServletContext getServletContext()
    {
        return m_servletContext;
    }

    /**
     * This is a safe version of the Servlet.Request.getParameter() routine. Unfortunately, the
     * default version always assumes that the incoming character set is ISO-8859-1, even though
     * it was something else. This means that we need to make a new string using the correct
     * encoding.
     * 
     * <P>
     * For more information, see: <A HREF="http://www.jguru.com/faq/view.jsp?EID=137049">JGuru
     * FAQ</A>.
     * </p>
     * 
     * <P>
     * Incidentally, this is almost the same as encodeName(), below. I am not yet entirely sure if
     * it's safe to merge the code.
     * </p>
     *
     * @param request DOCUMENT ME!
     * @param name DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @since 1.5.3
     */
    public String safeGetParameter(ServletRequest request, String name)
    {
        try
        {
            String res = request.getParameter(name);

            if (res != null)
            {
                res = new String(res.getBytes("ISO-8859-1"), getContentEncoding());
            }

            return res;
        }
        catch (UnsupportedEncodingException e)
        {
            log.fatal("Unsupported encoding", e);

            return "";
        }
    }

    /**
     * Returns the query string (the portion after the question mark).
     *
     * @param request DOCUMENT ME!
     *
     * @return The query string.  If the query string is null, returns an empty string.
     *
     * @since 2.1.3
     */
    public String safeGetQueryString(HttpServletRequest request)
    {
        if (request == null)
        {
            return "";
        }

        try
        {
            String res = request.getQueryString();

            if (res != null)
            {
                res = new String(res.getBytes("ISO-8859-1"), getContentEncoding());

                //
                // Ensure that the 'page=xyz' attribute is removed
                // FIXME: Is it really the mandate of this routine to
                //        do that?
                //
                int pos1 = res.indexOf("page=");

                if (pos1 >= 0)
                {
                    String tmpRes = res.substring(0, pos1);
                    int pos2 = res.indexOf("&", pos1) + 1;

                    if ((pos2 > 0) && (pos2 < res.length()))
                    {
                        tmpRes = tmpRes + res.substring(pos2);
                    }

                    res = tmpRes;
                }
            }

            return res;
        }
        catch (UnsupportedEncodingException e)
        {
            log.fatal("Unsupported encoding", e);

            return "";
        }
    }

    /**
     * Returns an URL to some other Wiki that we know.
     *
     * @param wikiName DOCUMENT ME!
     *
     * @return null, if no such reference was found.
     */
    public String getInterWikiURL(String wikiName)
    {
        Configuration iwConf = conf.subset(PROP_INTERWIKIREF);

        try
        {
            return iwConf.getString(wikiName);
        }
        catch (NoSuchElementException nsee)
        {
            return "";
        }
    }

    /**
     * Returns a collection of all supported InterWiki links.
     *
     * @return DOCUMENT ME!
     */
    public Collection getAllInterWikiLinks()
    {
        List l = new ArrayList();

        Configuration iwConf = conf.subset(PROP_INTERWIKIREF);

        for (Iterator it = iwConf.getKeys(); it.hasNext();)
        {
            String key = (String) it.next();
            l.add(key);
        }

        return l;
    }

    /**
     * Returns a collection of all image types that get inlined.
     *
     * @return DOCUMENT ME!
     */
    public Collection getAllInlinedImagePatterns()
    {
        return TranslatorReader.getImagePatterns(this);
    }

    /**
     * If the page is a special page, then returns a direct URL to that page.  Otherwise returns
     * null.
     * 
     * <P>
     * Special pages are non-existant references to other pages. For example, you could define a
     * special page reference "RecentChanges" which would always be redirected to
     * "RecentChanges.jsp" instead of trying to find a Wiki page called "RecentChanges".
     * </p>
     *
     * @param original DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getSpecialPageReference(String original)
    {
        Configuration pagesConf = conf.subset(PROP_SPECIAL_PAGES_PREFIX);

        try
        {
            String specialPage = pagesConf.getString(original);

            return getURL(WikiContext.NONE, specialPage, null, true);
        }
        catch (NoSuchElementException nse)
        {
            return null;
        }
    }

    /**
     * Returns the name of the application.
     *
     * @return DOCUMENT ME!
     */

    // FIXME: Should use servlet context as a default instead of a constant.
    public String getApplicationName()
    {
        String appName = conf.getString(PROP_APPNAME, PROP_APPNAME_DEFAULT);

        return appName;
    }

    /**
     * Beautifies the title of the page by appending spaces in suitable places, if the user has so
     * decreed in the properties when constructing this WikiEngine.  However, attachment names are
     * not beautified, no matter what.
     *
     * @param title DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @since 1.7.11
     */
    public String beautifyTitle(String title)
    {
        if (m_beautifyTitle)
        {
            try
            {
                if (getAttachmentManager().getAttachmentInfo(title) == null)
                {
                    return TextUtil.beautifyString(title);
                }
            }
            catch (ProviderException e)
            {
                return title;
            }
        }

        return title;
    }

    /**
     * Beautifies the title of the page by appending non-breaking spaces in suitable places.  This
     * is really suitable only for HTML output, as it uses the &amp;nbsp; -character.
     *
     * @param title DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @since 2.1.127
     */
    public String beautifyTitleNoBreak(String title)
    {
        if (m_beautifyTitle)
        {
            return TextUtil.beautifyString(title, "&nbsp;");
        }

        return title;
    }

    /**
     * Returns true, if the requested page (or an alias) exists.  Will consider any version as
     * existing.  Will also consider attachments.
     *
     * @param page WikiName of the page.
     *
     * @return DOCUMENT ME!
     */
    public boolean pageExists(String page)
    {
        Attachment att = null;

        try
        {
            if (getSpecialPageReference(page) != null)
            {
                return true;
            }

            if (getFinalPageName(page) != null)
            {
                return true;
            }

            att = getAttachmentManager().getAttachmentInfo((WikiContext) null, page);
        }
        catch (ProviderException e)
        {
            log.debug("pageExists() failed to find attachments", e);
        }

        return att != null;
    }

    /**
     * Returns true, if the requested page (or an alias) exists with the requested version.
     *
     * @param page Page name
     * @param version DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws ProviderException DOCUMENT ME!
     */
    public boolean pageExists(String page, int version)
            throws ProviderException
    {
        if (getSpecialPageReference(page) != null)
        {
            return true;
        }

        String finalName = getFinalPageName(page);
        WikiPage p = null;

        if (finalName != null)
        {
            //
            //  Go and check if this particular version of this page
            //  exists.
            //
            p = getPageManager().getPageInfo(finalName, version);
        }

        if (p == null)
        {
            try
            {
                p = getAttachmentManager().getAttachmentInfo((WikiContext) null, page, version);
            }
            catch (ProviderException e)
            {
                log.debug("pageExists() failed to find attachments", e);
            }
        }

        return (p != null);
    }

    /**
     * Returns true, if the requested page (or an alias) exists, with the specified version in the
     * WikiPage.
     *
     * @param page DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws ProviderException DOCUMENT ME!
     *
     * @since 2.0
     */
    public boolean pageExists(WikiPage page)
            throws ProviderException
    {
        if (page != null)
        {
            return pageExists(page.getName(), page.getVersion());
        }

        return false;
    }

    /**
     * Returns the correct page name, or null, if no such page can be found.  Aliases are
     * considered.
     * 
     * <P>
     * In some cases, page names can refer to other pages.  For example, when you have
     * matchEnglishPlurals set, then a page name "Foobars" will be transformed into "Foobar",
     * should a page "Foobars" not exist, but the page "Foobar" would.  This method gives you the
     * correct page name to refer to.
     * </p>
     * 
     * <P>
     * This facility can also be used to rewrite any page name, for example, by using aliases.  It
     * can also be used to check the existence of any page.
     * </p>
     *
     * @param page Page name.
     *
     * @return The rewritten page name, or null, if the page does not exist.
     *
     * @throws ProviderException DOCUMENT ME!
     *
     * @since 2.0
     */
    public String getFinalPageName(String page)
            throws ProviderException
    {
        boolean isThere = simplePageExists(page);

        if (!isThere && m_matchEnglishPlurals)
        {
            if (page.endsWith("s"))
            {
                page = page.substring(0, page.length() - 1);
            }
            else
            {
                page += "s";
            }

            isThere = simplePageExists(page);
        }

        return isThere
        ? page
        : null;
    }

    /**
     * Just queries the existing pages directly from the page manager. We also check overridden
     * pages from jspwiki.properties
     *
     * @param page DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws ProviderException DOCUMENT ME!
     */
    private boolean simplePageExists(String page)
            throws ProviderException
    {
        if (getSpecialPageReference(page) != null)
        {
            return true;
        }

        return getPageManager().pageExists(page);
    }

    /**
     * Turns a WikiName into something that can be called through using an URL.
     *
     * @param pagename DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @since 1.4.1
     */
    public String encodeName(String pagename)
    {
        return TextUtil.urlEncode(pagename, (m_useUTF8
            ? "UTF-8"
            : "ISO-8859-1"));
    }

    /**
     * DOCUMENT ME!
     *
     * @param pagerequest DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws InternalWikiException DOCUMENT ME!
     */
    public String decodeName(String pagerequest)
    {
        try
        {
            return TextUtil.urlDecode(pagerequest, (m_useUTF8
                ? "UTF-8"
                : "ISO-8859-1"));
        }
        catch (UnsupportedEncodingException e)
        {
            throw new InternalWikiException(
                "ISO-8859-1 not a supported encoding!?!  Your platform is borked.");
        }
    }

    /**
     * Returns the IANA name of the character set encoding we're supposed to be using right now.
     *
     * @return DOCUMENT ME!
     *
     * @since 1.5.3
     */
    public String getContentEncoding()
    {
        if (m_useUTF8)
        {
            return "UTF-8";
        }

        return "ISO-8859-1";
    }

    /**
     * Returns the un-HTMLized text of the latest version of a page. This method also replaces the
     * &lt; and &amp; -characters with their respective HTML entities, thus making it suitable for
     * inclusion on an HTML page.  If you want to have the page text without any conversions, use
     * getPureText().
     *
     * @param page WikiName of the page to fetch.
     *
     * @return WikiText.
     */
    public String getText(String page)
    {
        return getText(page, WikiPageProvider.LATEST_VERSION);
    }

    /**
     * Returns the un-HTMLized text of the given version of a page. This method also replaces the
     * &lt; and &amp; -characters with their respective HTML entities, thus making it suitable for
     * inclusion on an HTML page.  If you want to have the page text without any conversions, use
     * getPureText().
     *
     * @param page WikiName of the page to fetch
     * @param version Version of the page to fetch
     *
     * @return WikiText.
     */
    public String getText(String page, int version)
    {
        String result = getPureText(page, version);

        //
        //  Replace ampersand first, or else all quotes and stuff
        //  get replaced as well with &quot; etc.
        //
        /*
          result = TextUtil.replaceString( result, "&", "&amp;");
        */
        result = TextUtil.replaceEntities(result);

        return result;
    }

    /**
     * Returns the un-HTMLized text of the given version of a page in the given context.  USE THIS
     * METHOD if you don't know what doing.
     * 
     * <p>
     * This method also replaces the &lt; and &amp; -characters with their respective HTML
     * entities, thus making it suitable for inclusion on an HTML page.  If you want to have the
     * page text without any conversions, use getPureText().
     * </p>
     *
     * @param context DOCUMENT ME!
     * @param page DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @since 1.9.15.
     */
    public String getText(WikiContext context, WikiPage page)
    {
        return getText(page.getName(), page.getVersion());
    }

    /**
     * Returns the pure text of a page, no conversions.  Use this if you are writing something that
     * depends on the parsing of the page.  Note that you should always check for page existence
     * through pageExists() before attempting to fetch the page contents.
     *
     * @param page The name of the page to fetch.
     * @param version If WikiPageProvider.LATEST_VERSION, then uses the latest version.
     *
     * @return The page contents.  If the page does not exist, returns an empty string.
     */

    // FIXME: Should throw an exception on unknown page/version?
    public String getPureText(String page, int version)
    {
        String result = null;

        try
        {
            result = getPageManager().getPageText(page, version);
        }
        catch (ProviderException e)
        {
            log.error("Caught ProviderException", e);
        }
        finally
        {
            if (result == null)
            {
                result = "";
            }
        }

        return result;
    }

    /**
     * Returns the pure text of a page, no conversions.  Use this if you are writing something that
     * depends on the parsing the page. Note that you should always check for page existence
     * through pageExists() before attempting to fetch the page contents.
     *
     * @param page A handle to the WikiPage
     *
     * @return String of WikiText.
     *
     * @since 2.1.13.
     */
    public String getPureText(WikiPage page)
    {
        return getPureText(page.getName(), page.getVersion());
    }

    /**
     * Returns the converted HTML of the page using a different context than the default context.
     *
     * @param context DOCUMENT ME!
     * @param page DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getHTML(WikiContext context, WikiPage page)
    {
        if (page != null)
        {
            String pagedata = null;
            pagedata = getPureText(page.getName(), page.getVersion());

            return textToHTML(context, pagedata);
        }
        else
        {
            return "";
        }
    }

    /**
     * Returns the converted HTML of the page.
     *
     * @param pagename WikiName of the page to convert.
     *
     * @return DOCUMENT ME!
     */
    public String getHTML(String pagename)
    {
        return getHTML(getPage(pagename));
    }

    /**
     * DOCUMENT ME!
     *
     * @param page DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getHTML(WikiPage page)
    {
        WikiContext context = new WikiContext(this, page);
        context.setRequestContext(WikiContext.NONE);

        return getHTML(context, page);
    }

    /**
     * Returns the converted HTML of the page's specific version. The version must be a positive
     * integer, otherwise the current version is returned.
     *
     * @param pagename WikiName of the page to convert.
     * @param version Version number to fetch
     *
     * @return DOCUMENT ME!
     */
    public String getHTML(String pagename, int version)
    {
        WikiPage page = getPage(pagename, version);
        WikiContext context = new WikiContext(this, page);
        context.setRequestContext(WikiContext.NONE);

        return getHTML(context, page);
    }

    /**
     * Converts raw page data to HTML.
     *
     * @param context DOCUMENT ME!
     * @param pagedata Raw page data to convert to HTML
     *
     * @return DOCUMENT ME!
     */
    public String textToHTML(WikiContext context, String pagedata)
    {
        return textToHTML(context, pagedata, null, null);
    }

    /**
     * Reads a WikiPageful of data from a String and returns all links internal to this Wiki in a
     * Collection.
     *
     * @param page DOCUMENT ME!
     * @param pagedata DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Collection scanWikiLinks(WikiPage page, String pagedata)
    {
        LinkCollector localCollector = new LinkCollector();

        textToHTML(
            new WikiContext(this, page), pagedata, localCollector, null, localCollector, false);

        return localCollector.getLinks();
    }

    /**
     * Just convert WikiText to HTML.
     *
     * @param context DOCUMENT ME!
     * @param pagedata DOCUMENT ME!
     * @param localLinkHook DOCUMENT ME!
     * @param extLinkHook DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String textToHTML(
        WikiContext context, String pagedata, StringTransmutator localLinkHook,
        StringTransmutator extLinkHook)
    {
        return textToHTML(context, pagedata, localLinkHook, extLinkHook, null, true);
    }

    /**
     * Just convert WikiText to HTML.
     *
     * @param context DOCUMENT ME!
     * @param pagedata DOCUMENT ME!
     * @param localLinkHook DOCUMENT ME!
     * @param extLinkHook DOCUMENT ME!
     * @param attLinkHook DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String textToHTML(
        WikiContext context, String pagedata, StringTransmutator localLinkHook,
        StringTransmutator extLinkHook, StringTransmutator attLinkHook)
    {
        return textToHTML(context, pagedata, localLinkHook, extLinkHook, attLinkHook, true);
    }

    /**
     * Helper method for doing the HTML translation.
     *
     * @param context DOCUMENT ME!
     * @param pagedata DOCUMENT ME!
     * @param localLinkHook DOCUMENT ME!
     * @param extLinkHook DOCUMENT ME!
     * @param attLinkHook DOCUMENT ME!
     * @param parseAccessRules DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private String textToHTML(
        WikiContext context, String pagedata, StringTransmutator localLinkHook,
        StringTransmutator extLinkHook, StringTransmutator attLinkHook, boolean parseAccessRules)
    {
        String result = "";

        if (pagedata == null)
        {
            log.error("NULL pagedata to textToHTML()");

            return null;
        }

        TranslatorReader in = null;

        boolean runFilters =
            "true".equals(
                getVariableManager().getValue(context, PROP_RUNFILTERS, PROP_RUNFILTERS_DEFAULT));

        try
        {
            FilterManager filterManager = getFilterManager();

            if (runFilters)
            {
                pagedata = filterManager.doPreTranslateFiltering(context, pagedata);
            }

            in = new TranslatorReader(context, new StringReader(pagedata));

            in.addLocalLinkHook(localLinkHook);
            in.addExternalLinkHook(extLinkHook);
            in.addAttachmentLinkHook(attLinkHook);

            if (!parseAccessRules)
            {
                in.disableAccessRules();
            }

            result = FileUtil.readContents(in);

            if (runFilters)
            {
                result = filterManager.doPostTranslateFiltering(context, result);
            }
        }
        catch (IOException e)
        {
            log.error("Failed to scan page data: ", e);
        }
        catch (FilterException e)
        {
            log.error("Caught Filter Exception:", e);
        }
        finally
        {
            IOUtils.closeQuietly(in);
        }

        return (result);
    }

    /**
     * Updates all references for the given page.
     *
     * @param page DOCUMENT ME!
     */
    public void updateReferences(WikiPage page)
    {
        String pageData = getPureText(page.getName(), WikiProvider.LATEST_VERSION);

        getReferenceManager().updateReferences(page.getName(), scanWikiLinks(page, pageData));
    }

    /**
     * Writes the WikiText of a page into the page repository.
     *
     * @param context The current WikiContext
     * @param text The Wiki markup for the page.
     *
     * @throws WikiException DOCUMENT ME!
     *
     * @since 2.1.28
     */
    public void saveText(WikiContext context, String text)
            throws WikiException
    {
        WikiPage page = context.getPage();
        FilterManager filterManager = getFilterManager();

        if (page.getAuthor() == null)
        {
            UserProfile wup = context.getCurrentUser();

            if (wup != null)
            {
                page.setAuthor(wup.getName());
            }
        }

        text = TextUtil.normalizePostData(text);

        text = filterManager.doPreSaveFiltering(context, text);

        // Hook into cross reference collection.
        getPageManager().putPageText(page, text);

        filterManager.doPostSaveFiltering(context, text);
    }

    /**
     * Returns the number of pages in this Wiki
     *
     * @return DOCUMENT ME!
     */
    public int getPageCount()
    {
        return getPageManager().getTotalPageCount();
    }

    /**
     * Returns the provider name
     *
     * @return DOCUMENT ME!
     */
    public String getCurrentProvider()
    {
        return getPageManager().getProvider().getClass().getName();
    }

    /**
     * return information about current provider.
     *
     * @return DOCUMENT ME!
     *
     * @since 1.6.4
     */
    public String getCurrentProviderInfo()
    {
        return getPageManager().getProviderDescription();
    }

    /**
     * Returns a Collection of WikiPages, sorted in time order of last change.
     *
     * @return DOCUMENT ME!
     */

    // FIXME: Should really get a Date object and do proper comparisons.
    //        This is terribly wasteful.
    public Collection getRecentChanges()
    {
        try
        {
            Collection pages = getPageManager().getAllPages();
            Collection atts = getAttachmentManager().getAllAttachments();

            TreeSet sortedPages = new TreeSet(new PageTimeComparator());

            sortedPages.addAll(pages);
            sortedPages.addAll(atts);

            return sortedPages;
        }
        catch (ProviderException e)
        {
            log.error("Unable to fetch all pages: ", e);

            return null;
        }
    }

    /**
     * Parses an incoming search request, then does a search.
     * 
     * <P>
     * Search language is simple: prepend a word with a + to force a word to be included (all files
     * not containing that word are automatically rejected), '-' to cause the rejection of all
     * those files that contain that word.
     * </p>
     *
     * @param query DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */

    // FIXME: does not support phrase searches yet, but for them
    // we need a version which reads the whole page into the memory
    // once.
    //
    // FIXME: Should also have attributes attached.
    //
    public Collection findPages(String query)
    {
        StringTokenizer st = new StringTokenizer(query, " \t,");

        QueryItem [] items = new QueryItem[st.countTokens()];
        int word = 0;

        if (log.isDebugEnabled())
        {
            log.debug("Expecting " + items.length + " items");
        }

        //
        //  Parse incoming search string
        //
        while (st.hasMoreTokens())
        {
            if (log.isDebugEnabled())
            {
                log.debug("Item " + word);
            }

            String token = st.nextToken().toLowerCase();

            items[word] = new QueryItem();

            switch (token.charAt(0))
            {
            case '+':
                items[word].setType(QueryItem.REQUIRED);
                token = token.substring(1);

                if (log.isDebugEnabled())
                {
                    log.debug("Required word: " + token);
                }

                break;

            case '-':
                items[word].setType(QueryItem.FORBIDDEN);
                token = token.substring(1);

                if (log.isDebugEnabled())
                {
                    log.debug("Forbidden word: " + token);
                }

                break;

            default:
                items[word].setType(QueryItem.REQUESTED);

                if (log.isDebugEnabled())
                {
                    log.debug("Requested word: " + token);
                }

                break;
            }

            items[word++].setWord(token);
        }

        Collection results = getPageManager().findPages(items);

        return results;
    }

    /**
     * Return a bunch of information from the web page.
     *
     * @param pagereq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public WikiPage getPage(String pagereq)
    {
        return getPage(pagereq, WikiProvider.LATEST_VERSION);
    }

    /**
     * Returns specific information about a Wiki page.
     *
     * @param pagereq DOCUMENT ME!
     * @param version DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @since 1.6.7.
     */
    public WikiPage getPage(String pagereq, int version)
    {
        try
        {
            WikiPage p = getPageManager().getPageInfo(pagereq, version);

            if (p == null)
            {
                p = getAttachmentManager().getAttachmentInfo((WikiContext) null, pagereq);
            }

            return p;
        }
        catch (ProviderException e)
        {
            log.error("Unable to fetch page info", e);

            return null;
        }
    }

    /**
     * Returns a Collection of WikiPages containing the version history of a page.
     *
     * @param page DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public List getVersionHistory(String page)
    {
        List c = null;

        try
        {
            c = getPageManager().getVersionHistory(page);

            if (c == null)
            {
                c = getAttachmentManager().getVersionHistory(page);
            }
        }
        catch (ProviderException e)
        {
            log.error("FIXME");
        }

        return c;
    }

    /**
     * Returns a diff of two versions of a page.
     *
     * @param page Page to return
     * @param version1 Version number of the old page.  If WikiPageProvider.LATEST_VERSION (-1),
     *        then uses current page.
     * @param version2 Version number of the new page.  If WikiPageProvider.LATEST_VERSION (-1),
     *        then uses current page.
     * @param wantHtml DOCUMENT ME!
     *
     * @return A HTML-ized difference between two pages.  If there is no difference, returns an
     *         empty string.
     */
    public String getDiff(String page, int version1, int version2, boolean wantHtml)
    {
        String page1 = getPureText(page, version1);
        String page2 = getPureText(page, version2);

        // Kludge to make diffs for new pages to work this way.
        if (version1 == WikiPageProvider.LATEST_VERSION)
        {
            page1 = "";
        }

        return getDifferenceManager().makeDiff(page1, page2, wantHtml);
    }

    /**
     * Returns the current URL constructor
     *
     * @return DOCUMENT ME!
     */
    protected URLConstructor getURLConstructor()
    {
        return (URLConstructor) getComponentContainer().getComponentInstance(
            WikiConstants.URL_CONSTRUCTOR);
    }

    /**
     * Returns the current Difference Manager
     *
     * @return DOCUMENT ME!
     */
    public DifferenceManager getDifferenceManager()
    {
        return (DifferenceManager) getComponentContainer().getComponentInstance(
            WikiConstants.DIFFERENCE_MANAGER);
    }

    /**
     * Returns this object's ReferenceManager.
     *
     * @return DOCUMENT ME!
     *
     * @since 1.6.1
     */

    // (FIXME: We may want to protect this, though...)
    public ReferenceManager getReferenceManager()
    {
        return (ReferenceManager) getComponentContainer().getComponentInstance(
            WikiConstants.REFERENCE_MANAGER);
    }

    /**
     * Returns the current plugin manager.
     *
     * @return DOCUMENT ME!
     *
     * @since 1.6.1
     */
    public PluginManager getPluginManager()
    {
        return (PluginManager) getComponentContainer().getComponentInstance(
            WikiConstants.PLUGIN_MANAGER);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public VariableManager getVariableManager()
    {
        return (VariableManager) getComponentContainer().getComponentInstance(
            WikiConstants.VARIABLE_MANAGER);
    }

    /**
     * Shortcut to getVariableManager().getValue(). However, this method does not throw a
     * NoSuchVariableException, but returns null in case the variable does not exist.
     *
     * @param context DOCUMENT ME!
     * @param name DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @since 2.2
     */
    public String getVariable(WikiContext context, String name)
    {
        return getVariableManager().getValue(context, name, null);
    }

    /**
     * Returns the current PageManager.
     *
     * @return DOCUMENT ME!
     */
    public PageManager getPageManager()
    {
        return (PageManager) getComponentContainer().getComponentInstance(
            WikiConstants.PAGE_MANAGER);
    }

    /**
     * Returns the current AttachmentManager.
     *
     * @return DOCUMENT ME!
     *
     * @since 1.9.31.
     */
    public AttachmentManager getAttachmentManager()
    {
        return (AttachmentManager) getComponentContainer().getComponentInstance(
            WikiConstants.ATTACHMENT_MANAGER);
    }

    /**
     * Returns the currently used authorization manager.
     *
     * @return DOCUMENT ME!
     */
    public AuthorizationManager getAuthorizationManager()
    {
        return (AuthorizationManager) getComponentContainer().getComponentInstance(
            WikiConstants.AUTHORIZATION_MANAGER);
    }

    /**
     * Returns the currently used user manager.
     *
     * @return DOCUMENT ME!
     */
    public UserManager getUserManager()
    {
        return (UserManager) getComponentContainer().getComponentInstance(
            WikiConstants.USER_MANAGER);
    }

    /**
     * Returns the manager responsible for the filters.
     *
     * @return DOCUMENT ME!
     *
     * @since 2.1.88
     */
    public FilterManager getFilterManager()
    {
        return (FilterManager) getComponentContainer().getComponentInstance(
            WikiConstants.FILTER_MANAGER);
    }

    /**
     * Parses the given path and attempts to match it against the list of specialpages to see if
     * this path exists.  It is used to map things like "UserPreferences.jsp" to page "User
     * Preferences".
     *
     * @param path DOCUMENT ME!
     *
     * @return WikiName, or null if a match could not be found.
     */
    private String matchSpecialPagePath(String path)
    {
        //
        //  Remove servlet root marker.
        //
        if (path.startsWith("/"))
        {
            path = path.substring(1);
        }

        Configuration pagesConf = conf.subset(PROP_SPECIAL_PAGES_PREFIX);

        for (Iterator it = pagesConf.getKeys(); it.hasNext();)
        {
            String key = (String) it.next();
            String value = pagesConf.getString(key);

            if (value.equals(path))
            {
                return key;
            }
        }

        return null;
    }

    /**
     * Figure out to which page we are really going to.  Considers special page names from the
     * jspwiki.properties, and possible aliases.
     *
     * @param context The Wiki Context in which the request is being made.
     *
     * @return A complete URL to the new page to redirect to
     *
     * @since 2.2
     */
    public String getRedirectURL(WikiContext context)
    {
        String pagename = context.getPage().getName();
        String redirURL = null;

        redirURL = getSpecialPageReference(pagename);

        if (redirURL == null)
        {
            String alias = (String) context.getPage().getAttribute(WikiPage.ALIAS);

            if (alias != null)
            {
                redirURL = getURL(WikiContext.VIEW, alias, null, false);
            }
            else
            {
                redirURL = (String) context.getPage().getAttribute(WikiPage.REDIRECT);
            }
        }

        return redirURL;
    }

    /**
     * Shortcut to create a WikiContext from the Wiki page.
     *
     * @param request DOCUMENT ME!
     * @param requestContext DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws InternalWikiException DOCUMENT ME!
     *
     * @since 2.1.15.
     */

    // FIXME: We need to have a version which takes a fixed page
    //        name as well, or check it elsewhere.
    public WikiContext createContext(HttpServletRequest request, String requestContext)
    {
        String pagereq;

        if (!m_isConfigured)
        {
            throw new InternalWikiException(
                "WikiEngine has not been properly started.  It is likely that the configuration is faulty.  Please check all logs for the possible reason.");
        }

        try
        {
            pagereq = getURLConstructor().parsePage(requestContext, request, getContentEncoding());
        }
        catch (IOException e)
        {
            log.error("Unable to create context", e);
            throw new InternalWikiException("Big internal booboo, please check logs.");
        }

        String template = safeGetParameter(request, "skin");

        //
        //  Figure out the page name.
        //  We also check the list of special pages, which incidentally
        //  allows us to localize them, too.
        //
        if (StringUtils.isEmpty(pagereq))
        {
            String servlet = request.getServletPath();

            if (log.isDebugEnabled())
            {
                log.debug("Servlet path is: " + servlet);
            }

            pagereq = matchSpecialPagePath(servlet);

            if (log.isDebugEnabled())
            {
                log.debug("Mapped to " + pagereq);
            }

            if (pagereq == null)
            {
                pagereq = getFrontPage();
            }
        }

        int hashMark = pagereq.indexOf('#');

        if (hashMark != -1)
        {
            pagereq = pagereq.substring(0, hashMark);
        }

        int version = WikiProvider.LATEST_VERSION;
        String rev = request.getParameter("version");

        if (rev != null)
        {
            version = Integer.parseInt(rev);
        }

        //
        //  Find the WikiPage object
        //
        String pagename = pagereq;
        WikiPage wikipage;

        try
        {
            pagename = getFinalPageName(pagereq);
        }
        catch (ProviderException e)
        {
            log.error("Caught ProviderException", e);
        }

        if (pagename != null)
        {
            wikipage = getPage(pagename, version);
        }
        else
        {
            wikipage = getPage(pagereq, version);
        }

        if (wikipage == null)
        {
            pagereq = TranslatorReader.cleanLink(pagereq);
            wikipage = new WikiPage(pagereq);
        }

        //
        //  Figure out which template we should be using for this page.
        //
        if (template == null)
        {
            template = (String) wikipage.getAttribute(PROP_TEMPLATEDIR);

            // FIXME: Most definitely this should be checked for
            //        existence, or else it is possible to create pages that
            //        cannot be shown.
            if (StringUtils.isEmpty(template))
            {
                template = getTemplateDir();
            }
        }

        WikiContext context = new WikiContext(this, wikipage);
        context.setRequestContext(requestContext);
        context.setHttpRequest(request);
        context.setTemplate(template);

        UserProfile user = getUserManager().getUserProfile(request);
        context.setCurrentUser(user);

        return context;
    }

    /**
     * Deletes a page or an attachment completely, including all versions.
     *
     * @param pageName
     *
     * @throws ProviderException
     */
    public void deletePage(String pageName)
            throws ProviderException
    {
        WikiPage p = getPage(pageName);

        if (p instanceof Attachment)
        {
            getAttachmentManager().deleteAttachment((Attachment) p);
        }
        else
        {
            getPageManager().deletePage(p);
        }
    }

    /**
     * Deletes a specific version of a page or an attachment.
     *
     * @param page
     *
     * @throws ProviderException
     */
    public void deleteVersion(WikiPage page)
            throws ProviderException
    {
        if (page instanceof Attachment)
        {
            getAttachmentManager().deleteVersion((Attachment) page);
        }
        else
        {
            getPageManager().deleteVersion(page);
        }
    }

    /**
     * Returns the URL of the global RSS file.  May be null, if the RSS file generation is not
     * operational.
     *
     * @return DOCUMENT ME!
     *
     * @since 1.7.10
     */
    public String getGlobalRSSURL()
    {
        RSSGenerator rssGenerator = getRSSGenerator();

        if (rssGenerator == null)
        {
            return null;
        }

        String rssURL = rssGenerator.getGlobalRSSURL();

        if (rssURL == null)
        {
            return null;
        }

        return getBaseURL() + rssURL;
    }

    /**
     * Sets the internal path of the webapp base.
     *
     * @param rootPath DOCUMENT ME!
     *
     * @since 2.2
     */
    protected void setRootPath(final String rootPath)
    {
        m_rootPath = rootPath;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @since 2.2
     */
    public String getRootPath()
    {
        return m_rootPath;
    }

    /**
     * Checks whether a supplied directory path is valid for the current Wiki configuration. A path
     * is valid if - The Wiki is in "jspwiki.relativePathes = false" mode and the path is absolute
     * - The Wiki is in "jspwiki.relativePathes = true" mode and the path is relative and the
     * rootDirectory is not null.
     *
     * @param pathName The Directory path to check
     *
     * @return A valid path
     *
     * @throws WikiException if the supplied directory path is invalid.
     */
    public String getValidPath(final String pathName)
            throws WikiException
    {
        File path = new File(pathName);
        String rootPath = getRootPath();

        // If we have a relative path reference and a root directory has been
        // set, then return the path relative to it.
        if ((rootPath != null) && !path.isAbsolute())
        {
            return new File(rootPath, pathName).getAbsolutePath();
        }

        // In the "Absolute Path" configuration (default), we return everything "as is".
        if (!wikiRelativePathes)
        {
            return pathName;
        }

        throw new WikiException(
            "The path name " + pathName + " is invalid in the current Wiki configuration!");
    }

    /**
     * builds and starts a PicoContainer which contains a set of Elements.
     *
     * @param containerRef DOCUMENT ME!
     * @param parentRef DOCUMENT ME!
     * @param confFile DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     * @throws IllegalArgumentException DOCUMENT ME!
     */
    public void setupContainer(
        ObjectReference containerRef, ObjectReference parentRef, String confFile)
            throws Exception
    {
        InputStream configStream = null;
        InputStreamReader isr = null;

        try
        {
            ServletContext context = getServletContext();

            if (context != null)
            {
                configStream = context.getResourceAsStream(confFile);
            }
            else
            {
                File configFile = new File(confFile);
                configStream = new FileInputStream(configFile);
            }

            if (configStream == null)
            {
                throw new IllegalArgumentException("Could not open configuration " + confFile);
            }

            isr = new InputStreamReader(configStream, WikiConstants.DEFAULT_ENCODING);

            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            ContainerBuilder builder = new WikiContainerBuilder(isr, classLoader);

            builder.buildContainer(containerRef, parentRef, "wiki", false);
        }
        finally
        {
            IOUtils.closeQuietly(configStream);
            IOUtils.closeQuietly(isr);
        }
    }

    /**
     * Prepares the 'root' container which all other containers use as their base to get
     * configuration and Wiki reference
     *
     * @throws Exception DOCUMENT ME!
     */
    private void setupMainContainer()
            throws Exception
    {
        MutablePicoContainer mainContainer = new DefaultPicoContainer();

        // Register ourselves with the Container
        mainContainer.registerComponentInstance("WikiEngine", this);

        // Register our configuration with the Container
        mainContainer.registerComponentInstance("Configuration", this.getWikiConfiguration());

        mainContainerRef.set(mainContainer);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public PicoContainer getComponentContainer()
    {
        return (PicoContainer) componentContainerRef.get();
    }

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Revision$
     */
    private static class WikiContainerBuilder
            extends XMLContainerBuilder
            implements ContainerBuilder
    {
        /**
         * Creates a new WikiContainerBuilder object.
         *
         * @param reader DOCUMENT ME!
         * @param classLoader DOCUMENT ME!
         */
        private WikiContainerBuilder(Reader reader, ClassLoader classLoader)
        {
            super(reader, classLoader);
        }

        /**
         * DOCUMENT ME!
         *
         * @param container DOCUMENT ME!
         */
        protected void autoStart(PicoContainer container)
        {
            // don't start container automatically
        }
    }
}
