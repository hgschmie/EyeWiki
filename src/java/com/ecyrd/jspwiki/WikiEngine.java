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
package com.ecyrd.jspwiki;

import java.io.*;
import java.util.*;
import org.apache.log4j.*;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import com.ecyrd.jspwiki.plugin.PluginManager;
import com.ecyrd.jspwiki.rss.RSSGenerator;
import com.ecyrd.jspwiki.providers.WikiPageProvider;
import com.ecyrd.jspwiki.providers.ProviderException;
import com.ecyrd.jspwiki.attachment.AttachmentManager;
import com.ecyrd.jspwiki.attachment.Attachment;
import com.ecyrd.jspwiki.auth.AuthorizationManager;
import com.ecyrd.jspwiki.auth.UserManager;
import com.ecyrd.jspwiki.auth.UserProfile;

import com.ecyrd.jspwiki.filters.FilterException;
import com.ecyrd.jspwiki.filters.FilterManager;

import com.ecyrd.jspwiki.util.ClassUtil;

/**
 *  Provides Wiki services to the JSP page.
 *
 *  <P>
 *  This is the main interface through which everything should go.
 *
 *  <P>
 *  Using this class:  Always get yourself an instance from JSP page
 *  by using the WikiEngine.getInstance() method.  Never create a new
 *  WikiEngine() from scratch, unless you're writing tests.
 *  <p>
 *  There's basically only a single WikiEngine for each web application, and
 *  you should always get it using the WikiEngine.getInstance() method.
 *
 *  @author Janne Jalkanen
 */
public class WikiEngine
{
    private static final Logger log = Logger.getLogger(WikiEngine.class);

    /** True, if log4j has been configured. */
    // FIXME: If you run multiple applications, the first application
    // to run defines where the log goes.  Not what we want.
    private static boolean   c_configured = false;

    /** Stores properties. */
    private Properties       m_properties;

    /** The web.xml parameter that defines where the config file is to be found. 
     *  If it is not defined, uses the default as defined by DEFAULT_PROPERTYFILE. 
     *  @value jspwiki.propertyfile
     */

    public static final String PARAM_PROPERTYFILE = "jspwiki.propertyfile";

    /** Property start for any interwiki reference. */
    public static final String PROP_INTERWIKIREF = "jspwiki.interWikiRef.";

    /** If true, then the user name will be stored with the page data.*/
    public static final String PROP_STOREUSERNAME= "jspwiki.storeUserName";

    /** Define the used encoding.  Currently supported are ISO-8859-1 and UTF-8 */
    public static final String PROP_ENCODING     = "jspwiki.encoding";

    /** The name for the base URL to use in all references. */
    public static final String PROP_BASEURL      = "jspwiki.baseURL";

    public static final String PROP_REFSTYLE     = "jspwiki.referenceStyle";

    /** Property name for the "spaces in titles" -hack. */
    public static final String PROP_BEAUTIFYTITLE = "jspwiki.breakTitleWithSpaces";

    /** Property name for where the jspwiki work directory should be. 
        If not specified, reverts to ${java.tmpdir}. */
    public static final String PROP_WORKDIR      = "jspwiki.workDir";

    /** The name of the cookie that gets stored to the user browser. */
    public static final String PREFS_COOKIE_NAME = "JSPWikiUserProfile";

    /** Property name for the "match english plurals" -hack. */
    public static final String PROP_MATCHPLURALS     = "jspwiki.translatorReader.matchEnglishPlurals";
    /** Property name for the template that is used. */
    public static final String PROP_TEMPLATEDIR  = "jspwiki.templateDir";

    /** Property name for the default front page. */
    public static final String PROP_FRONTPAGE    = "jspwiki.frontPage";

    /** Property name for setting the url generator instance */

    public static final String PROP_URLCONSTRUCTOR = "jspwiki.urlConstructor";

    private static final String PROP_SPECIALPAGE = "jspwiki.specialPage.";

    /** Path to the default property file. 
     *  @value /WEB_INF/jspwiki.properties
     */
    public static final String DEFAULT_PROPERTYFILE = "/WEB-INF/jspwiki.properties";

    /**
     *  Contains the default properties for JSPWiki.
     */
    private static final String[] DEFAULT_PROPERTIES = 
    { "jspwiki.specialPage.Login",           "Login.jsp",
      "jspwiki.specialPage.UserPreferences", "UserPreferences.jsp",
      "jspwiki.specialPage.Search",          "Search.jsp",
      "jspwiki.specialPage.FindPage",        "FindPage.jsp"};

    /** Stores an internal list of engines per each ServletContext */
    private static Hashtable c_engines = new Hashtable();

    /** Should the user info be saved with the page data as well? */
    private boolean          m_saveUserInfo = true;

    /** If true, uses UTF8 encoding for all data */
    private boolean          m_useUTF8      = true;

    /** If true, we'll also consider english plurals (+s) a match. */
    private boolean          m_matchEnglishPlurals = true;

    /** Stores the base URL. */
    private String           m_baseURL;

    /** Store the file path to the basic URL.  When we're not running as
        a servlet, it defaults to the user's current directory. */
    private String           m_rootPath = System.getProperty("user.dir");

    /** Stores references between wikipages. */
    private ReferenceManager m_referenceManager = null;

    /** Stores the Plugin manager */
    private PluginManager    m_pluginManager;

    /** Stores the Variable manager */
    private VariableManager  m_variableManager;

    /** Stores the Attachment manager */
    private AttachmentManager m_attachmentManager = null;

    /** Stores the Page manager */
    private PageManager      m_pageManager = null;

    /** Stores the authorization manager */
    private AuthorizationManager m_authorizationManager = null;

    /** Stores the user manager.*/
    private UserManager      m_userManager = null;

    private TemplateManager  m_templateManager = null;

    /** Does all our diffs for us. */
    private DifferenceEngine m_differenceEngine;

    /** Handlers page filters. */
    private FilterManager    m_filterManager;

    /** Constructs URLs */
    private URLConstructor   m_urlConstructor;

    /** Generates RSS feed when requested. */
    private RSSGenerator     m_rssGenerator;

    /** Stores the relative URL to the global RSS feed. */
    private String           m_rssURL;

    /** Store the ServletContext that we're in.  This may be null if WikiEngine
        is not running inside a servlet container (i.e. when testing). */
    private ServletContext   m_servletContext = null;

    /** If true, all titles will be cleaned. */
    private boolean          m_beautifyTitle = false;

    /** Stores the template path.  This is relative to "templates". */
    private String           m_templateDir;

    /** The default front page name.  Defaults to "Main". */
    private String           m_frontPage;

    /** The time when this engine was started. */
    private Date             m_startTime;

    /** The location where the work directory is. */
    private String           m_workDir;

    /** Each engine has their own application id. */
    private String           m_appid = "";

    private boolean          m_isConfigured = false; // Flag.
    
    /**
     *  Gets a WikiEngine related to this servlet.  Since this method
     *  is only called from JSP pages (and JspInit()) to be specific,
     *  we throw a RuntimeException if things don't work.
     *  
     *  @param config The ServletConfig object for this servlet.
     *
     *  @return A WikiEngine instance.
     *  @throws InternalWikiException in case something fails.  This
     *          is a RuntimeException, so be prepared for it.
     */

    // FIXME: It seems that this does not work too well, jspInit()
    // does not react to RuntimeExceptions, or something...

    public static synchronized WikiEngine getInstance( ServletConfig config )
        throws InternalWikiException
    {
        return( getInstance( config.getServletContext(), null ) );
    }
    
    /**
     * Gets a WikiEngine related to the servlet. Works like getInstance(ServletConfig),
     * but does not force the Properties object. This method is just an optional way
     * of initializing a WikiEngine for embedded JSPWiki applications; normally, you
     * should use getInstance(ServletConfig).
     * 
     * @param config The ServletConfig of the webapp servlet/JSP calling this method.
     * @param props  A set of properties, or null, if we are to load JSPWiki's default 
     *               jspwiki.properties (this is the usual case).
     */
    public static synchronized WikiEngine getInstance( ServletConfig config,
                                                       Properties props )
    {
        return( getInstance( config.getServletContext(), null ) );
    }

    /**
     * Gets a WikiEngine related to the servlet. Works just like getInstance( ServletConfig )
     * 
     * @param config The ServletContext of the webapp servlet/JSP calling this method.
     * @param props  A set of properties, or null, if we are to load JSPWiki's default 
     *               jspwiki.properties (this is the usual case).
     */

    public static synchronized WikiEngine getInstance( ServletContext context, 
                                                       Properties props )
        throws InternalWikiException
    {
        String appid = Integer.toString(context.hashCode()); //FIXME: Kludge, use real type.

        context.log( "Application "+appid+" requests WikiEngine.");

        WikiEngine engine = (WikiEngine) c_engines.get( appid );

        if( engine == null )
        {
            context.log(" Assigning new log to "+appid);
            try
            {
                if( props == null )
                    props = loadWebAppProps( context );
                engine = new WikiEngine( context, appid, props );
            }
            catch( Exception e )
            {
                context.log( "ERROR: Failed to create a Wiki engine: "+e.getMessage() );
                throw new InternalWikiException( "No wiki engine, check logs." );
            }

            c_engines.put( appid, engine );            
        }

        return engine;
    }
    

    /**
     *  Instantiate the WikiEngine using a given set of properties.
     *  Use this constructor for testing purposes only.
     */
    public WikiEngine( Properties properties )
        throws WikiException
    {
        initialize( properties );
    }

    /**
     * Loads the webapp properties based on servlet context information.
     * Returns a Properties object containing the settings, or null if unable
     * to load it. (The default file is WEB-INF/jspwiki.properties, and can
     * be overridden by setting PARAM_PROPERTYFILE in the server or webapp
     * configuration.)
     */
    private static Properties loadWebAppProps( ServletContext context )
    {
        String      propertyFile   = context.getInitParameter(PARAM_PROPERTYFILE);
        InputStream propertyStream = null;

        try
        {
            //
            //  Figure out where our properties lie.
            //
            if( propertyFile == null )
            {
                context.log("No "+PARAM_PROPERTYFILE+" defined for this context, using default from "+DEFAULT_PROPERTYFILE);
                //  Use the default property file.
                propertyStream = context.getResourceAsStream(DEFAULT_PROPERTYFILE);
            }
            else
            {
                context.log("Reading properties from "+propertyFile+" instead of default.");
                propertyStream = new FileInputStream( new File(propertyFile) );
            }

            if( propertyStream == null )
            {
                throw new WikiException("Property file cannot be found!"+propertyFile);
            }

            Properties props = new Properties( TextUtil.createProperties( DEFAULT_PROPERTIES ) );
            props.load( propertyStream );
            return( props );
        }
        catch( Exception e )
        {
            context.log( Release.APPNAME+": Unable to load and setup properties from jspwiki.properties. "+e.getMessage() );
        }
        finally
        {
            try
            {
                propertyStream.close();
            }
            catch( IOException e )
            {
                context.log("Unable to close property stream - something must be seriously wrong.");
            }
        }

        return( null );
    }

    
    /**
     *  Instantiate using this method when you're running as a servlet and
     *  WikiEngine will figure out where to look for the property
     *  file.
     *  Do not use this method - use WikiEngine.getInstance() instead.
     */
    protected WikiEngine( ServletContext context, String appid, Properties props )
        throws WikiException
    {
        InputStream propertyStream = null;
        String      propertyFile   = context.getInitParameter(PARAM_PROPERTYFILE);

        m_servletContext = context;
        m_appid          = appid;

        try
        {
            //
            //  Note: May be null, if JSPWiki has been deployed in a WAR file.
            //
            m_rootPath = context.getRealPath("/");
            initialize( props );
            log.info("Root path for this Wiki is: '"+m_rootPath+"'");
        }
        catch( Exception e )
        {
            context.log( Release.APPNAME+": Unable to load and setup properties from jspwiki.properties. "+e.getMessage() );
        }
    }

    /**
     *  Does all the real initialization.
     */
    private void initialize( Properties props )
        throws WikiException
    {
        m_startTime  = new Date();
        m_properties = props;

        //
        //  Initialized log4j.  However, make sure that
        //  we don't initialize it multiple times.  Also, if
        //  all of the log4j statements have been removed from
        //  the property file, we do not do any property setting
        //  either.q
        //
        if( !c_configured )
        {
            if( props.getProperty("log4j.rootCategory") != null )
            {
                PropertyConfigurator.configure( props );
            }
            c_configured = true;
        }

        log.info("*******************************************");
        log.info("JSPWiki "+Release.VERSTR+" starting. Whee!");

        log.debug("Configuring WikiEngine...");

        //
        //  Create and find the default working directory.
        //
        m_workDir        = props.getProperty( PROP_WORKDIR );

        if( m_workDir == null )
        {
            m_workDir = System.getProperty("java.io.tmpdir", ".");
            m_workDir += File.separator+Release.APPNAME+"-"+m_appid;
        }

        try
        {
            File f = new File( m_workDir );
            f.mkdirs();
        }
        catch( Exception e )
        {
            log.fatal("Unable to find or create the working directory: "+m_workDir,e);
            throw new IllegalArgumentException("Unable to find or create the working dir: "+m_workDir);
        }

        log.info("JSPWiki working directory is '"+m_workDir+"'");

        m_saveUserInfo   = TextUtil.getBooleanProperty( props,
                                                        PROP_STOREUSERNAME, 
                                                        m_saveUserInfo );

        m_useUTF8        = "UTF-8".equals( props.getProperty( PROP_ENCODING, "ISO-8859-1" ) );
        m_baseURL        = props.getProperty( PROP_BASEURL, "" );


        m_beautifyTitle  = TextUtil.getBooleanProperty( props,
                                                        PROP_BEAUTIFYTITLE, 
                                                        m_beautifyTitle );

        m_matchEnglishPlurals = TextUtil.getBooleanProperty( props,
                                                             PROP_MATCHPLURALS, 
                                                             m_matchEnglishPlurals );

        m_templateDir    = props.getProperty( PROP_TEMPLATEDIR, "default" );
        m_frontPage      = props.getProperty( PROP_FRONTPAGE,   "Main" );

        //
        //  Initialize the important modules.  Any exception thrown by the
        //  managers means that we will not start up.
        //
        try
        {
            Class urlclass = ClassUtil.findClass( "com.ecyrd.jspwiki",
                                                  props.getProperty( PROP_URLCONSTRUCTOR, "DefaultURLConstructor" ) );
            m_urlConstructor = (URLConstructor) urlclass.newInstance();               
            m_urlConstructor.initialize( this, props );

            m_pageManager       = new PageManager( this, props );
            m_pluginManager     = new PluginManager( props );
            m_differenceEngine  = new DifferenceEngine( props, getContentEncoding() );
            m_attachmentManager = new AttachmentManager( this, props );
            m_variableManager   = new VariableManager( props );
            m_filterManager     = new FilterManager( this, props );

            //
            //  ReferenceManager has the side effect of loading all
            //  pages.  Therefore after this point, all page attributes
            //  are available.
            //
            initReferenceManager();

            m_templateManager   = new TemplateManager( this, props );
            m_userManager       = new UserManager( this, props );
            m_authorizationManager = new AuthorizationManager( this, props );

        }
        catch( Exception e )
        {
            // RuntimeExceptions may occur here, even if they shouldn't.
            log.fatal( "Failed to start managers.", e );
            throw new WikiException( "Failed to start managers: "+e.getMessage() );
        }

        //
        //  Initialize the good-to-have-but-not-fatal modules.
        //
        try
        {
            if( TextUtil.getBooleanProperty( props, 
                                             RSSGenerator.PROP_GENERATE_RSS, 
                                             false ) )
            {
                m_rssGenerator = new RSSGenerator( this, props );
            }
        }
        catch( Exception e )
        {
            log.error( "Unable to start RSS generator - JSPWiki will still work, "+
                       "but there will be no RSS feed.", e );
        }

        // FIXME: I wonder if this should be somewhere else.
        if( m_rssGenerator != null )
        {
            new RSSThread().start();
        }

        log.info("WikiEngine configured.");
        m_isConfigured = true;
    }

    /**
     *  Initializes the reference manager. Scans all existing WikiPages for
     *  internal links and adds them to the ReferenceManager object.
     */
    private void initReferenceManager()
    {
        m_pluginManager.setInitStage( true );

        try
        {
            Collection pages = m_pageManager.getAllPages();
            pages.addAll( m_attachmentManager.getAllAttachments() );

            // Build a new manager with default key lists.
            if( m_referenceManager == null )
            {
                m_referenceManager = new ReferenceManager( this );
                m_referenceManager.initialize( pages );
            }
        
        }
        catch( ProviderException e )
        {
            log.fatal("PageProvider is unable to list pages: ", e);
        }

        m_pluginManager.setInitStage( false );

        m_filterManager.addPageFilter( m_referenceManager, -1000 ); // FIXME: Magic number.
    }


    /**
     *  Throws an exception if a property is not found.
     *
     *  @param props A set of properties to search the key in.
     *  @param key   The key to look for.
     *  @return The required property
     *
     *  @throws NoRequiredPropertyException If the search key is not 
     *          in the property set.
     */

    // FIXME: Should really be in some util file.
    public static String getRequiredProperty( Properties props, String key )
        throws NoRequiredPropertyException
    {
        String value = props.getProperty(key);

        if( value == null )
        {
            throw new NoRequiredPropertyException( "Required property not found",
                                                   key );
        }

        return value;
    }

    /**
     *  Internal method for getting a property.  This is used by the
     *  TranslatorReader for example.
     */

    public Properties getWikiProperties()
    {
        return m_properties;
    }

    /**
     *  Returns the JSPWiki working directory.
     *  @since 2.1.100
     */
    public String getWorkDir()
    {
        return m_workDir;
    }

    /**
     *  Don't use.
     *  @since 1.8.0
     */
    public String getPluginSearchPath()
    {
        // FIXME: This method should not be here, probably.
        return m_properties.getProperty( PluginManager.PROP_SEARCHPATH );
    }

    /**
     *  Returns the current template directory.
     *
     *  @since 1.9.20
     */
    public String getTemplateDir()
    {
        return m_templateDir;
    }

    public TemplateManager getTemplateManager()
    {
        return m_templateManager;
    }

    /**
     *  Returns the base URL.  Always prepend this to any reference
     *  you make.
     *
     *  @since 1.6.1
     */

    public String getBaseURL()
    {
        return m_baseURL;
    }

    /**
     *  Returns the moment when this engine was started.
     * 
     *  @since 2.0.15.
     */

    public Date getStartTime()
    {
        return m_startTime;
    }

    /**
     *  Returns the basic URL to a page, without any modifications.
     *  You may add any parameters to this.
     *  @deprecated
     *
     *  @since 2.0.3
     */
    public String getViewURL( String pageName )
    {
        return m_urlConstructor.makeURL( WikiContext.VIEW, pageName, false, null );
    }

    /**
     *  Returns the basic URL to an editor.
     *  @deprecated
     *
     *  @since 2.0.3
     */
    public String getEditURL( String pageName )
    {
        return m_urlConstructor.makeURL( WikiContext.EDIT, pageName, false, null );
    }

    /**
     *  Returns the basic attachment URL.
     *  @since 2.0.42.
     *  @deprecated
     */
    public String getAttachmentURL( String attName )
    {
        return m_urlConstructor.makeURL( WikiContext.ATTACH, attName, false, null );
    }

    /*
    public String getURL( String name, boolean absolute )
    {
        return m_urlConstructor.makeURL( WikiContext.NONE, name, absolute, null );
    }

    public String getURL( String context, String pageName )
    {
        return m_urlConstructor.makeURL( context, pageName, false, null );
    }

    public String getURL( String context, String pageName, String params )
    {
        return m_urlConstructor.makeURL( context, pageName, false, params );
    }

    public String getAbsoluteURL( String context, String pageName )
    {
        return m_urlConstructor.makeURL( context, pageName, true, null );
    }

    public String getAbsoluteURL( String context, String pageName, String params )
    {
        return m_urlConstructor.makeURL( context, pageName, true, params );
    }
    */

    /**
     *  Returns an URL if a WikiContext is not available.
     */
    public String getURL( String context, String pageName, String params, boolean absolute )
    {
        return m_urlConstructor.makeURL( context, pageName, absolute, params );
    }
    /**
     *  Returns the default front page, if no page is used.
     */

    public String getFrontPage()
    {
        return m_frontPage;
    }

    /**
     *  Returns the ServletContext that this particular WikiEngine was
     *  initialized with.  <B>It may return null</B>, if the WikiEngine is not
     *  running inside a servlet container!
     *
     *  @since 1.7.10
     *  @return ServletContext of the WikiEngine, or null.
     */

    public ServletContext getServletContext()
    {
        return m_servletContext;
    }

    /**
     *  This is a safe version of the Servlet.Request.getParameter() routine.
     *  Unfortunately, the default version always assumes that the incoming
     *  character set is ISO-8859-1, even though it was something else.
     *  This means that we need to make a new string using the correct
     *  encoding.
     *  <P>
     *  For more information, see:
     *     <A HREF="http://www.jguru.com/faq/view.jsp?EID=137049">JGuru FAQ</A>.
     *  <P>
     *  Incidentally, this is almost the same as encodeName(), below.
     *  I am not yet entirely sure if it's safe to merge the code.
     *
     *  @since 1.5.3
     */

    public String safeGetParameter( ServletRequest request, String name )
    {
        try
        {
            String res = request.getParameter( name );
            if( res != null ) 
            {
                res = new String(res.getBytes("ISO-8859-1"),
                                 getContentEncoding() );
            }

            return res;
        }
        catch( UnsupportedEncodingException e )
        {
            log.fatal( "Unsupported encoding", e );
            return "";
        }

    }

    /**
     *  Returns the query string (the portion after the question mark).
     *
     *  @return The query string.  If the query string is null,
     *   returns an empty string. 
     *
     *  @since 2.1.3
     */
    public String safeGetQueryString( HttpServletRequest request )
    {
        if (request == null)
	{
            return "";
	}

        try
        {
            String res = request.getQueryString();
            if( res != null ) 
            {
                res = new String(res.getBytes("ISO-8859-1"),
                                 getContentEncoding() );

                //
                // Ensure that the 'page=xyz' attribute is removed
                // FIXME: Is it really the mandate of this routine to
                //        do that?
                // 
                int pos1 = res.indexOf("page=");
                if (pos1 >= 0)
                {
                    String tmpRes = res.substring(0, pos1);
                    int pos2 = res.indexOf("&",pos1) + 1;   
                    if ( (pos2 > 0) && (pos2 < res.length()) )
                    {
                        tmpRes = tmpRes + res.substring(pos2);
                    }
                    res = tmpRes;
                }
            }

            return res;
        }
        catch( UnsupportedEncodingException e )
        {
            log.fatal( "Unsupported encoding", e );
            return "";
        }
    }

    /**
     *  Returns an URL to some other Wiki that we know.
     *
     *  @return null, if no such reference was found.
     */
    public String getInterWikiURL( String wikiName )
    {
        return m_properties.getProperty(PROP_INTERWIKIREF+wikiName);
    }

    /**
     *  Returns a collection of all supported InterWiki links.
     */
    public Collection getAllInterWikiLinks()
    {
        Vector v = new Vector();

        for( Enumeration i = m_properties.propertyNames(); i.hasMoreElements(); )
        {
            String prop = (String) i.nextElement();

            if( prop.startsWith( PROP_INTERWIKIREF ) )
            {
                v.add( prop.substring( prop.lastIndexOf(".")+1 ) );
            }
        }

        return v;
    }

    /**
     *  Returns a collection of all image types that get inlined.
     */

    public Collection getAllInlinedImagePatterns()
    {
        return TranslatorReader.getImagePatterns( this );
    }

    /**
     *  If the page is a special page, then returns a direct URL
     *  to that page.  Otherwise returns null.
     *  <P>
     *  Special pages are non-existant references to other pages.
     *  For example, you could define a special page reference
     *  "RecentChanges" which would always be redirected to "RecentChanges.jsp"
     *  instead of trying to find a Wiki page called "RecentChanges".
     */
    public String getSpecialPageReference( String original )
    {
        String propname = PROP_SPECIALPAGE+original;
        String specialpage = m_properties.getProperty( propname );

        return specialpage;
    }

    /**
     *  Returns the name of the application.
     */

    // FIXME: Should use servlet context as a default instead of a constant.
    public String getApplicationName()
    {
        String appName = m_properties.getProperty("jspwiki.applicationName");

        if( appName == null )
            return Release.APPNAME;

        return appName;
    }

    /**
     *  Beautifies the title of the page by appending spaces in suitable
     *  places.
     *
     *  @since 1.7.11
     */
    public String beautifyTitle( String title )
    {
        if( m_beautifyTitle )
        {
            return TextUtil.beautifyString( title );
        }

        return title;
    }

    /**
     *  Beautifies the title of the page by appending non-breaking spaces 
     *  in suitable places.  This is really suitable only for HTML output,
     *  as it uses the &amp;nbsp; -character.
     *
     *  @since 2.1.127
     */
    public String beautifyTitleNoBreak( String title )
    {
        if( m_beautifyTitle )
        {
            return TextUtil.beautifyString( title, "&nbsp;" );
        }

        return title;        
    }

    /**
     *  Returns true, if the requested page (or an alias) exists.  Will consider
     *  any version as existing.  Will also consider attachments.
     *
     *  @param page WikiName of the page.
     */
    public boolean pageExists( String page )
    {

        Attachment att = null;

        try
        {
            if( getSpecialPageReference(page) != null ) return true;

            if( getFinalPageName( page ) != null )
            {
                return true;
            }

            att = getAttachmentManager().getAttachmentInfo( (WikiContext)null, page );
        }
        catch( ProviderException e )
        {
            log.debug("pageExists() failed to find attachments",e);
        }

        return att != null;
    }

    /**
     *  Returns true, if the requested page (or an alias) exists with the
     *  requested version.
     *
     *  @param page Page name
     */
    public boolean pageExists( String page, int version )
        throws ProviderException
    {
        if( getSpecialPageReference(page) != null ) return true;

        String finalName = getFinalPageName( page );
        WikiPage p = null;

        if( finalName != null )
        {
            //
            //  Go and check if this particular version of this page
            //  exists.
            //
            p = m_pageManager.getPageInfo( finalName, version );
        }

        if( p == null )
        {
            try
            {
                p = getAttachmentManager().getAttachmentInfo( (WikiContext)null, page, version );
            }
            catch( ProviderException e )
            {
                log.debug("pageExists() failed to find attachments",e);
            }
        }

        return (p != null);
    }

    /**
     *  Returns true, if the requested page (or an alias) exists, with the
     *  specified version in the WikiPage.
     *
     *  @since 2.0
     */
    public boolean pageExists( WikiPage page )
        throws ProviderException
    {
        if( page != null )
        {
            return pageExists( page.getName(), page.getVersion() );
        }
        return false;
    }

    /**
     *  Returns the correct page name, or null, if no such
     *  page can be found.  Aliases are considered.
     *  <P>
     *  In some cases, page names can refer to other pages.  For example,
     *  when you have matchEnglishPlurals set, then a page name "Foobars"
     *  will be transformed into "Foobar", should a page "Foobars" not exist,
     *  but the page "Foobar" would.  This method gives you the correct
     *  page name to refer to.
     *  <P>
     *  This facility can also be used to rewrite any page name, for example,
     *  by using aliases.  It can also be used to check the existence of any
     *  page.
     *
     *  @since 2.0
     *  @param page Page name.
     *  @return The rewritten page name, or null, if the page does not exist.
     */

    public String getFinalPageName( String page )
        throws ProviderException
    {
        boolean isThere = simplePageExists( page );

        if( !isThere && m_matchEnglishPlurals )
        {
            if( page.endsWith("s") )
            {
                page = page.substring( 0, page.length()-1 );
            }
            else
            {
                page += "s";
            }

            isThere = simplePageExists( page );
        }

        return isThere ? page : null ;
    }

    /**
     *  Just queries the existing pages directly from the page manager.
     *  We also check overridden pages from jspwiki.properties
     */
    private boolean simplePageExists( String page )
        throws ProviderException
    {
        if( getSpecialPageReference(page) != null ) return true;

        return m_pageManager.pageExists( page );
    }

    /**
     *  Turns a WikiName into something that can be 
     *  called through using an URL.
     *
     *  @since 1.4.1
     */
    public String encodeName( String pagename )
    {
        try
        {
            if( m_useUTF8 )
                return TextUtil.urlEncodeUTF8( pagename );
            else
                return java.net.URLEncoder.encode( pagename, "ISO-8859-1" );
        }
        catch( UnsupportedEncodingException e )
        {
            throw new InternalWikiException("ISO-8859-1 not a supported encoding!?!  Your platform is borked.");
        }
    }

    public String decodeName( String pagerequest )
    {
        try
        {
            if( m_useUTF8 )
                return TextUtil.urlDecodeUTF8( pagerequest );
            else
                return java.net.URLDecoder.decode( pagerequest, "ISO-8859-1" );
        }
        catch( UnsupportedEncodingException e )
        {
            throw new InternalWikiException("ISO-8859-1 not a supported encoding!?!  Your platform is borked.");
        }
    }

    /**
     *  Returns the IANA name of the character set encoding we're
     *  supposed to be using right now.
     *
     *  @since 1.5.3
     */
    public String getContentEncoding()
    {
        if( m_useUTF8 ) 
            return "UTF-8";

        return "ISO-8859-1";
    }

    /**
     *  Returns the un-HTMLized text of the latest version of a page.
     *  This method also replaces the &lt; and &amp; -characters with
     *  their respective HTML entities, thus making it suitable
     *  for inclusion on an HTML page.  If you want to have the
     *  page text without any conversions, use getPureText().
     *
     *  @param page WikiName of the page to fetch.
     *  @return WikiText.
     */
    public String getText( String page )
    {
        return getText( page, WikiPageProvider.LATEST_VERSION );
    }

    /**
     *  Returns the un-HTMLized text of the given version of a page.
     *  This method also replaces the &lt; and &amp; -characters with
     *  their respective HTML entities, thus making it suitable
     *  for inclusion on an HTML page.  If you want to have the
     *  page text without any conversions, use getPureText().
     *
     *
     * @param page WikiName of the page to fetch
     * @param version  Version of the page to fetch
     * @return WikiText.
     */
    public String getText( String page, int version )
    {
        String result = getPureText( page, version );

        //
        //  Replace ampersand first, or else all quotes and stuff
        //  get replaced as well with &quot; etc.
        //
        /*
        result = TextUtil.replaceString( result, "&", "&amp;" );
        */

        result = TextUtil.replaceEntities( result );

        return result;
    }

    /**
     *  Returns the un-HTMLized text of the given version of a page in
     *  the given context.  USE THIS METHOD if you don't know what
     *  doing.
     *  <p>
     *  This method also replaces the &lt; and &amp; -characters with
     *  their respective HTML entities, thus making it suitable
     *  for inclusion on an HTML page.  If you want to have the
     *  page text without any conversions, use getPureText().
     *
     *  @since 1.9.15.
     */
    public String getText( WikiContext context, WikiPage page )
    {
        return getText( page.getName(), page.getVersion() );
    }


    /**
     *  Returns the pure text of a page, no conversions.  Use this
     *  if you are writing something that depends on the parsing
     *  of the page.  Note that you should always check for page
     *  existence through pageExists() before attempting to fetch
     *  the page contents.
     *
     *  @param page    The name of the page to fetch.
     *  @param version If WikiPageProvider.LATEST_VERSION, then uses the 
     *  latest version.
     *  @return The page contents.  If the page does not exist,
     *          returns an empty string.
     */
    // FIXME: Should throw an exception on unknown page/version?
    public String getPureText( String page, int version )
    {
        String result = null;

        try
        {
            result = m_pageManager.getPageText( page, version );
        }
        catch( ProviderException e )
        {
            // FIXME
        }
        finally
        {
            if( result == null )
                result = "";
        }

        return result;
    }

    /**
     *  Returns the pure text of a page, no conversions.  Use this
     *  if you are writing something that depends on the parsing
     *  the page. Note that you should always check for page
     *  existence through pageExists() before attempting to fetch
     *  the page contents.
     *  
     *  @param page A handle to the WikiPage
     *  @return String of WikiText.
     *  @since 2.1.13.
     */
    public String getPureText( WikiPage page )
    {
        return getPureText( page.getName(), page.getVersion() );
    }

    /**
     *  Returns the converted HTML of the page using a different
     *  context than the default context.
     */

    public String getHTML( WikiContext context, WikiPage page )
    {
	String pagedata = null;

        pagedata = getPureText( page.getName(), page.getVersion() );

        String res = textToHTML( context, pagedata );

	return res;
    }
    
    /**
     *  Returns the converted HTML of the page.
     *
     *  @param page WikiName of the page to convert.
     */
    public String getHTML( String page )
    {
        return getHTML( page, WikiPageProvider.LATEST_VERSION );
    }

    /**
     *  Returns the converted HTML of the page's specific version.
     *  The version must be a positive integer, otherwise the current
     *  version is returned.
     *
     *  @param pagename WikiName of the page to convert.
     *  @param version Version number to fetch
     *  @deprecated
     */
    public String getHTML( String pagename, int version )
    {
        WikiPage page = new WikiPage( pagename );
        page.setVersion( version );

        WikiContext context = new WikiContext( this,
                                               page );
        
        String res = getHTML( context, page );

	return res;
    }

    /**
     *  Converts raw page data to HTML.
     *
     *  @param pagedata Raw page data to convert to HTML
     */
    public String textToHTML( WikiContext context, String pagedata )
    {
        return textToHTML( context, pagedata, null, null );
    }

    /**
     *  Reads a WikiPageful of data from a String and returns all links
     *  internal to this Wiki in a Collection.
     */
    protected Collection scanWikiLinks( WikiPage page, String pagedata )
    {
        LinkCollector localCollector = new LinkCollector();        

        textToHTML( new WikiContext(this,page),
                    pagedata,
                    localCollector,
                    null,
                    localCollector,
                    false );

        return localCollector.getLinks();
    }

    /**
     *  Just convert WikiText to HTML.
     */

    public String textToHTML( WikiContext context, 
                              String pagedata, 
                              StringTransmutator localLinkHook,
                              StringTransmutator extLinkHook )
    {
        return textToHTML( context, pagedata, localLinkHook, extLinkHook, null, true );
    }

    /**
     *  Just convert WikiText to HTML.
     */

    public String textToHTML( WikiContext context, 
                              String pagedata, 
                              StringTransmutator localLinkHook,
                              StringTransmutator extLinkHook,
                              StringTransmutator attLinkHook )
    {
        return textToHTML( context, pagedata, localLinkHook, extLinkHook, attLinkHook, true );
    }

    /**
     *  Helper method for doing the HTML translation.
     */
    private String textToHTML( WikiContext context, 
                               String pagedata, 
                               StringTransmutator localLinkHook,
                               StringTransmutator extLinkHook,
                               StringTransmutator attLinkHook,
                               boolean            parseAccessRules )
    {
        String result = "";

        if( pagedata == null ) 
        {
            log.error("NULL pagedata to textToHTML()");
            return null;
        }

        TranslatorReader in = null;
        Collection links = null;

        try
        {
            pagedata = m_filterManager.doPreTranslateFiltering( context, pagedata );

            in = new TranslatorReader( context,
                                       new StringReader( pagedata ) );

            in.addLocalLinkHook( localLinkHook );
            in.addExternalLinkHook( extLinkHook );
            in.addAttachmentLinkHook( attLinkHook );

            if( !parseAccessRules ) in.disableAccessRules();
            result = FileUtil.readContents( in );

            result = m_filterManager.doPostTranslateFiltering( context, result );
        }
        catch( IOException e )
        {
            log.error("Failed to scan page data: ", e);
        }
        catch( FilterException e )
        {
            // FIXME: Don't yet know what to do
        }
        finally
        {
            try
            {
                if( in  != null ) in.close();
            }
            catch( Exception e ) 
            {
                log.fatal("Closing failed",e);
            }
        }

        return( result );
    }

    /**
     *  Updates all references for the given page.
     */

    public void updateReferences( WikiPage page )
    {
        String pageData = getPureText( page.getName(), WikiProvider.LATEST_VERSION );

        m_referenceManager.updateReferences( page.getName(),
                                             scanWikiLinks( page, pageData ) );
    }


    /**
     *  Writes the WikiText of a page into the
     *  page repository.
     *
     *  @since 2.1.28
     *  @param context The current WikiContext
     *  @param text    The Wiki markup for the page.
     */
    public void saveText( WikiContext context, String text )
        throws WikiException
    {
        WikiPage page = context.getPage();

        if( page.getAuthor() == null )
        {
            UserProfile wup = context.getCurrentUser();

            if( wup != null ) page.setAuthor( wup.getName() );
        }

        text = TextUtil.normalizePostData(text);

        text = m_filterManager.doPreSaveFiltering( context, text );

        // Hook into cross reference collection.
        
        m_pageManager.putPageText( page, text );

        m_filterManager.doPostSaveFiltering( context, text );
    }

    /**
     *  Returns the number of pages in this Wiki
     */
    public int getPageCount()
    {
        return m_pageManager.getTotalPageCount();
    }

    /**
     *  Returns the provider name
     */

    public String getCurrentProvider()
    {
        return m_pageManager.getProvider().getClass().getName();
    }

    /**
     *  return information about current provider.
     *  @since 1.6.4
     */
    public String getCurrentProviderInfo()
    {
        return m_pageManager.getProviderDescription();
    }

    /**
     *  Returns a Collection of WikiPages, sorted in time
     *  order of last change.
     */

    // FIXME: Should really get a Date object and do proper comparisons.
    //        This is terribly wasteful.
    public Collection getRecentChanges()
    {
        try
        {
            Collection pages = m_pageManager.getAllPages();
            Collection  atts = m_attachmentManager.getAllAttachments();

            TreeSet sortedPages = new TreeSet( new PageTimeComparator() );

            sortedPages.addAll( pages );
            sortedPages.addAll( atts );

            return sortedPages;
        }
        catch( ProviderException e )
        {
            log.error( "Unable to fetch all pages: ",e);
            return null;
        }
    }

    /**
     *  Parses an incoming search request, then
     *  does a search.
     *  <P>
     *  Search language is simple: prepend a word
     *  with a + to force a word to be included (all files
     *  not containing that word are automatically rejected),
     *  '-' to cause the rejection of all those files that contain
     *  that word.
     */

    // FIXME: does not support phrase searches yet, but for them
    // we need a version which reads the whole page into the memory
    // once.

    //
    // FIXME: Should also have attributes attached.
    //
    public Collection findPages( String query )
    {
        StringTokenizer st = new StringTokenizer( query, " \t," );

        QueryItem[] items = new QueryItem[st.countTokens()];
        int word = 0;

        log.debug("Expecting "+items.length+" items");

        //
        //  Parse incoming search string
        //

        while( st.hasMoreTokens() )
        {
            log.debug("Item "+word);
            String token = st.nextToken().toLowerCase();

            items[word] = new QueryItem();

            switch( token.charAt(0) )
            {
              case '+':
                items[word].type = QueryItem.REQUIRED;
                token = token.substring(1);
                log.debug("Required word: "+token);
                break;
                
              case '-':
                items[word].type = QueryItem.FORBIDDEN;
                token = token.substring(1);
                log.debug("Forbidden word: "+token);
                break;

              default:
                items[word].type = QueryItem.REQUESTED;
                log.debug("Requested word: "+token);
                break;
            }

            items[word++].word = token;
        }

        Collection results = m_pageManager.findPages( items );
        
        return results;
    }

    /**
     *  Return a bunch of information from the web page.
     */

    public WikiPage getPage( String pagereq )
    {
        return getPage( pagereq, WikiProvider.LATEST_VERSION );
    }

    /**
     *  Returns specific information about a Wiki page.
     *  @since 1.6.7.
     */

    public WikiPage getPage( String pagereq, int version )
    {
        try
        {
            WikiPage p = m_pageManager.getPageInfo( pagereq, version );

            if( p == null )
            {
                p = m_attachmentManager.getAttachmentInfo( (WikiContext)null, pagereq );
            }

            return p;
        }
        catch( ProviderException e )
        {
            log.error( "Unable to fetch page info",e);
            return null;
        }
    }


    /**
     *  Returns a Collection of WikiPages containing the
     *  version history of a page.
     */

    public List getVersionHistory( String page )
    {
        List c = null;

        try
        {
            c = m_pageManager.getVersionHistory( page );

            if( c == null )
            {
                c = m_attachmentManager.getVersionHistory( page );
            }
        }
        catch( ProviderException e )
        {
            log.error("FIXME");
        }

        return c;
    }

    /**
     *  Returns a diff of two versions of a page.
     *
     *  @param page Page to return
     *  @param version1 Version number of the old page.  If 
     *         WikiPageProvider.LATEST_VERSION (-1), then uses current page.
     *  @param version2 Version number of the new page.  If 
     *         WikiPageProvider.LATEST_VERSION (-1), then uses current page.
     *
     *  @return A HTML-ized difference between two pages.  If there is no difference,
     *          returns an empty string.
     */
    public String getDiff( String page, int version1, int version2 )
    {
        String page1 = getPureText( page, version1 );
        String page2 = getPureText( page, version2 );

        // Kludge to make diffs for new pages to work this way.

        if( version1 == WikiPageProvider.LATEST_VERSION )
        {
            page1 = "";
        }

        String diff  = m_differenceEngine.makeDiff( page1, page2 );

        diff = TextUtil.replaceEntities( diff );
        
        try
        {
            if( diff.length() > 0 )
            {
                diff = m_differenceEngine.colorizeDiff( diff );
            }
        }
        catch( IOException e )
        {
            log.error("Failed to colorize diff result.", e);
        }

        return diff;
    }

    /**
     *  Returns this object's ReferenceManager.
     *  @since 1.6.1
     */
    // (FIXME: We may want to protect this, though...)
    public ReferenceManager getReferenceManager()
    {
        return m_referenceManager;
    }

    /**      
     *  Returns the current plugin manager.
     *  @since 1.6.1
     */

    public PluginManager getPluginManager()
    {
        return m_pluginManager;
    }

    public VariableManager getVariableManager()
    {
        return m_variableManager;
    }

    /**
     *  Shortcut to getVariableManager().getValue(). However, this method does not 
     *  throw a NoSuchVariableException, but returns null in case the variable does
     *  not exist.
     *
     *  @since 2.2
     */
    public String getVariable( WikiContext context, String name )
    {
        try
        {
            return m_variableManager.getValue( context, name );
        }
        catch( NoSuchVariableException e )
        {
            return null;
        }
    }

    /**
     *  Returns the current PageManager.
     */
    public PageManager getPageManager()
    {
        return m_pageManager;
    }

    /**
     *  Returns the current AttachmentManager.
     *  @since 1.9.31.
     */
    public AttachmentManager getAttachmentManager()
    {
        return m_attachmentManager;
    }

    /**
     *  Returns the currently used authorization manager.
     */
    public AuthorizationManager getAuthorizationManager()
    {
        return m_authorizationManager;
    }

    /**
     *  Returns the currently used user manager.
     */
    public UserManager getUserManager()
    {
        return m_userManager;
    }

    /**
     *  Returns the manager responsible for the filters.
     *  @since 2.1.88
     */
    public FilterManager getFilterManager()
    {
        return m_filterManager;
    }

    /**
     *  Parses the given path and attempts to match it against the list
     *  of specialpages to see if this path exists.  It is used to map things
     *  like "UserPreferences.jsp" to page "User Preferences".
     *
     *  @return WikiName, or null if a match could not be found.
     */
    private String matchSpecialPagePath( String path )
    {
        //
        //  Remove servlet root marker.
        //
        if( path.startsWith("/") )
        {
            path = path.substring(1);
        }

        for( Iterator i = m_properties.entrySet().iterator(); i.hasNext(); )
        {
            Map.Entry entry = (Map.Entry) i.next();
           
            String key = (String)entry.getKey();

            if( key.startsWith( PROP_SPECIALPAGE ) )
            {
                String value = (String)entry.getValue();

                if( value.equals( path ) )
                {                    
                    return key.substring( PROP_SPECIALPAGE.length() );
                }
            }
        }

        return null;
    }

    /**
     *  Figure out to which page we are really going to.  Considers
     *  special page names from the jspwiki.properties, and possible aliases.
     *
     *  @param context The Wiki Context in which the request is being made.
     *  @return A complete URL to the new page to redirect to
     *  @since 2.2
     */

    public String getRedirectURL( WikiContext context )
    {
        String pagename = context.getPage().getName();
        String redirURL = null;
        
        redirURL = getSpecialPageReference( pagename );

        if( redirURL == null )
        {
            String alias = (String)context.getPage().getAttribute( WikiPage.ALIAS );
            
            if( alias != null )
            {
                redirURL = getViewURL( alias );
            }
            else
            {
                redirURL = (String)context.getPage().getAttribute( WikiPage.REDIRECT );
            }
        }

        return redirURL;
    }

    /**
     *  Shortcut to create a WikiContext from the Wiki page.
     *
     *  @since 2.1.15.
     */
    // FIXME: We need to have a version which takes a fixed page
    //        name as well, or check it elsewhere.
    public WikiContext createContext( HttpServletRequest request,
                                      String requestContext )
    {
        String pagereq;

        if( !m_isConfigured )
        {
            throw new InternalWikiException("WikiEngine has not been properly started.  It is likely that the configuration is faulty.  Please check all logs for the possible reason.");
        }

        try
        {
            pagereq  = m_urlConstructor.parsePage( requestContext,
                                                   request,
                                                   getContentEncoding() );
        }
        catch( IOException e )
        {
            log.error("Unable to create context",e);
            throw new InternalWikiException("Big internal booboo, please check logs.");
        }

        String template = safeGetParameter( request, "skin" );

        //
        //  Figure out the page name.
        //  We also check the list of special pages, which incidentally
        //  allows us to localize them, too.
        //

        if( pagereq == null || pagereq.length() == 0 )
        {
            String servlet = request.getServletPath();
            log.debug("Servlet path is: "+servlet);

            pagereq = matchSpecialPagePath( servlet );

            log.debug("Mapped to "+pagereq);
            if( pagereq == null )
            {
                pagereq = getFrontPage();
            }
        }

        int hashMark = pagereq.indexOf('#');

        if( hashMark != -1 )
        {
            pagereq = pagereq.substring( 0, hashMark );
        }

        int version          = WikiProvider.LATEST_VERSION;
        String rev           = request.getParameter("version");

        if( rev != null )
        {
            version = Integer.parseInt( rev );
        }

        //
        //  Find the WikiPage object
        //
        String pagename = pagereq;
        WikiPage wikipage; 

        try
        {
            pagename = getFinalPageName( pagereq );
        }
        catch( ProviderException e ) {} // FIXME: Should not ignore!

        if( pagename != null )
        {
            wikipage = getPage( pagename, version );
        }
        else
        {
            wikipage = getPage( pagereq, version );
        }

        if( wikipage == null ) wikipage = new WikiPage( pagereq );

        //
        //  Figure out which template we should be using for this page.
        //
        if( template == null )
        {
            template = (String)wikipage.getAttribute( PROP_TEMPLATEDIR );

            // FIXME: Most definitely this should be checked for
            //        existence, or else it is possible to create pages that
            //        cannot be shown.

            if( template == null || template.length() == 0 )
            {
                template = getTemplateDir();
            }
        }

        WikiContext context = new WikiContext( this, 
                                               wikipage );
        context.setRequestContext( requestContext );
        context.setHttpRequest( request );
        context.setTemplate( template );

        UserProfile user = getUserManager().getUserProfile( request );
        context.setCurrentUser( user );

        return context;
    }


    /**
     *  Returns the URL of the global RSS file.  May be null, if the
     *  RSS file generation is not operational.
     *  @since 1.7.10
     */
    public String getGlobalRSSURL()
    {
        if( m_rssURL != null )
        {
            return getBaseURL()+m_rssURL;
        }

        return null;
    }

    /**
     * @since 2.2
     */
    public String getRootPath()
    {
        return m_rootPath;
    }

    /**
     *  Runs the RSS generation thread.
     *  FIXME: MUST be somewhere else, this is not a good place.
     */
    private class RSSThread extends Thread
    {
        public void run()
        {
            try
            {
                String fileName = m_properties.getProperty( RSSGenerator.PROP_RSSFILE,
                                                            "rss.rdf" );
                int rssInterval = TextUtil.parseIntParameter( m_properties.getProperty( RSSGenerator.PROP_INTERVAL ),
                                                              3600 );

                log.debug("RSS file will be at "+fileName);
                log.debug("RSS refresh interval (seconds): "+rssInterval);

                while(true)
                {
                    Writer out = null;
                    Reader in  = null;

                    try
                    {
                        //
                        //  Generate RSS file, output it to
                        //  default "rss.rdf".
                        //
                        log.debug("Regenerating RSS feed to "+fileName);

                        String feed = m_rssGenerator.generate();

                        File file = new File( m_rootPath, fileName );

                        in  = new StringReader(feed);
                        out = new BufferedWriter( new OutputStreamWriter( new FileOutputStream(file), "UTF-8") );

                        FileUtil.copyContents( in, out );

                        m_rssURL = fileName;
                    }
                    catch( IOException e )
                    {
                        log.error("Cannot generate RSS feed to "+fileName, e );
                        m_rssURL = null;
                    }
                    finally
                    {
                        try
                        {
                            if( in != null )  in.close();
                            if( out != null ) out.close();
                        }
                        catch( IOException e )
                        {
                            log.fatal("Could not close I/O for RSS", e );
                            break;
                        }
                    }

                    Thread.sleep(rssInterval*1000L);
                } // while
                
            }
            catch(InterruptedException e)
            {
                log.error("RSS thread interrupted, no more RSS feeds", e);
            }
            
            //
            // Signal: no more RSS feeds.
            //
            m_rssURL = null;
        }
    }

}
