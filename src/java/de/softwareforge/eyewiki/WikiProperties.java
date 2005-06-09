package de.softwareforge.eyewiki;

import org.apache.lucene.analysis.standard.StandardAnalyzer;


/**
 * This interface holds all the constant names for the properties available in eyewiki.properties
 *
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 */
public interface WikiProperties
{
    /** The main prefix for all the eyewiki properties. Note the trailing dot. */
    String PROP_PREFIX = "eyewiki.";

    /*
     * ========================================================================
     *
     * Main Configuration
     *
     * ========================================================================
     */

    /** Property for application name */
    String PROP_APPNAME = "eyewiki.applicationName";

    /**
     * Default value of the Application name
     *
     * @value value of Release.APPNAME
     */
    String PROP_APPNAME_DEFAULT = Release.APPNAME;

    /** Property for the components configuration file */
    String PROP_COMPONENTS_FILE = "eyewiki.componentsFile";

    /** Default value for the components configuration file */
    String PROP_COMPONENTS_FILE_DEFAULT = "/WEB-INF/wikiComponents.xml";

    /** Property for the variable configuration file */
    String PROP_VARIABLE_FILE = "eyewiki.variableFile";

    /** Default value for the variable configuration file */
    String PROP_VARIABLE_FILE_DEFAULT = "/WEB-INF/wikiVariables.xml";

    /**
     * Property for the plugin configuration file. This has no default, if this value is empty or
     * missing, no plugins are started.
     */
    String PROP_PLUGIN_FILE = "eyewiki.pluginFile";

    /**
     * Name of the property that defines a directory where the pages are. Must be defined if you
     * use a file based PageProvider.
     */
    String PROP_PAGEDIR = "eyewiki.pageDir";

    /**
     * Property name for where the eyewiki work directory should be. If not specified, reverts to
     * ${java.tmpdir}.
     */
    String PROP_WORKDIR = "eyewiki.workDir";

    /**
     * Name of the property that defines a directory where the attachments are. Must be defined if
     * you use a file based AttachmentProvider.
     */
    String PROP_STORAGEDIR = "eyewiki.storageDir";

    /**
     * This property is used internally to provide the root of the web application to the logging
     * configuration when the Wiki has been configured to use relative pathes with
     * eyewiki.relativePathes = true You can reference it as ${eyewiki.rootDir} but you should
     * never add it directly to the eyewiki.properties file
     */
    String PROP_ROOTDIR = "eyewiki.rootDir";

    /**
     * If this parameter is true, then all the page and string references are relative to the web
     * application root. This allows a wiki to be deployed "as is" as a single war file.
     */
    String PROP_WIKIRELATIVE_PATHES = "eyewiki.relativePathes";

    /**
     * The default is to have absolute pathes for backwards compatibility
     *
     * @value false
     */
    boolean PROP_WIKIRELATIVE_PATHES_DEFAULT = false;

    /** Property start for any interwiki reference. */
    String PROP_INTERWIKIREF = "eyewiki.interWikiRef";

    /** If true, then the user name will be stored with the page data. */
    String PROP_STOREUSERNAME = "eyewiki.storeUserName";

    /**
     * Default for storing the username with the page data
     *
     * @value true
     */
    boolean PROP_STOREUSERNAME_DEFAULT = true;

    /** Define the used encoding.  Currently supported are ISO-8859-1 and UTF-8 */
    String PROP_ENCODING = "eyewiki.encoding";

    /**
     * Default Encoding Value
     *
     * @value ISO-8859-1
     */
    String PROP_ENCODING_DEFAULT = "ISO-8859-1";

    /** The name for the base URL to use in all references. */
    String PROP_BASEURL = "eyewiki.baseURL";

    /**
     * Default BaseURL Value
     *
     * @value &quot;&quot; (empty String)
     */
    String PROP_BASEURL_DEFAULT = "";

    /** DOCUMENT ME! */
    String PROP_SHORTURL_PREFIX = "eyewiki.shortURLConstructor.prefix";

    /** DOCUMENT ME! */
    String PROP_REFSTYLE = "eyewiki.referenceStyle";

    /** DOCUMENT ME! */
    String PROP_REFSTYLE_DEFAULT = "absolute";

    /** Property name for the "spaces in titles" -hack. */
    String PROP_BEAUTIFYTITLE = "eyewiki.breakTitleWithSpaces";

    /**
     * Default value for the "spaces in titles" - hack.
     *
     * @value false
     */
    boolean PROP_BEAUTIFYTITLE_DEFAULT = false;

    /** Property name for the "match english plurals" -hack. */
    String PROP_MATCHPLURALS = "eyewiki.translatorReader.matchEnglishPlurals";

    /**
     * Default value for the "match english plurals" - hack
     *
     * @value true
     */
    boolean PROP_MATCHPLURALS_DEFAULT = false;

    /** Property name for the template that is used. */
    String PROP_TEMPLATEDIR = "eyewiki.templateDir";

    /**
     * Default values for the template that is used
     *
     * @value default
     */
    String PROP_TEMPLATEDIR_DEFAULT = "default";

    /** Property name for the default front page. */
    String PROP_FRONTPAGE = "eyewiki.frontPage";

    /**
     * Default value for the front page.
     *
     * @value Main
     */
    String PROP_FRONTPAGE_DEFAULT = "Main";

    /** DOCUMENT ME! */
    String PROP_LOCKEXPIRY = "eyewiki.lockExpiryTime";

    /** DOCUMENT ME! */
    int PROP_LOCKEXPIRY_DEFAULT = 60;

    /**
     * This property defines the inline image pattern.  It's current value is
     * eyewiki.translatorReader.inlinePattern
     */
    String PROP_INLINEIMAGEPTRN = "eyewiki.translatorReader.inlinePattern";

    /** The default inlining pattern.  Currently ".png" */
    String PROP_INLINEIMAGEPTRN_DEFAULT = "*.png";

    /** If true, consider CamelCase hyperlinks as well. */
    String PROP_CAMELCASELINKS = "eyewiki.translatorReader.camelCaseLinks";

    /** DOCUMENT ME! */
    boolean PROP_CAMELCASELINKS_DEFAULT = false;

    /**
     * If true, all hyperlinks are translated as well, regardless whether they are surrounded by
     * brackets.
     */
    String PROP_PLAINURIS = "eyewiki.translatorReader.plainUris";

    /** DOCUMENT ME! */
    boolean PROP_PLAINURIS_DEFAULT = false;

    /** If true, all outward links (external links) have a small link image appended. */
    String PROP_USEOUTLINKIMAGE = "eyewiki.translatorReader.useOutlinkImage";

    /** DOCUMENT ME! */
    boolean PROP_USEOUTLINKIMAGE_DEFAULT = false;

    /**
     * If set to "true", allows using raw HTML within Wiki text.  Be warned, this is a VERY
     * dangerous option to set - never turn this on in a publicly allowable Wiki, unless you are
     * absolutely certain of what you're doing.
     */
    String PROP_ALLOWHTML = "eyewiki.translatorReader.allowHTML";

    /** DOCUMENT ME! */
    boolean PROP_ALLOWHTML_DEFAULT = false;

    /** If set to "true", all external links are tagged with 'rel="nofollow"' */
    String PROP_USERRELNOFOLLOW = "eyewiki.translatorReader.useRelNofollow";

    /** DOCUMENT ME! */
    boolean PROP_USERRELNOFOLLOW_DEFAULT = false;

    /** If set to "true", enables plugins during parsing */
    String PROP_RUNPLUGINS = "eyewiki.translatorReader.runPlugins";

    /** DOCUMENT ME! */
    boolean PROP_RUNPLUGINS_DEFAULT = true;

    /** If set to "true", enables filters during parsing */
    String PROP_RUNFILTERS = "eyewiki.translatorReader.runFilters";

    /** gets evaluated with getValue(). A String, not a boolean! */
    String PROP_RUNFILTERS_DEFAULT = "true";

    /**
     * Determines the command to be used for 'diff'.  This program must be able to output diffs in
     * the unified format. It defaults to 'diff -u %s1 %s2'.
     */
    String PROP_DIFFCOMMAND = "eyewiki.diffCommand";

    /** The maximum size of attachments that can be uploaded. */
    String PROP_MAXSIZE = "eyewiki.attachment.maxsize";

    /** DOCUMENT ME! */
    int PROP_MAXSIZE_DEFAULT = 100000;

    /**
     * Defines, in seconds, the amount of time a text will live in the cache at most before
     * requiring a refresh.
     */
    String PROP_CACHECHECKINTERVAL = "eyewiki.cachingProvider.cacheCheckInterval";

    /** DOCUMENT ME! */
    int PROP_CACHECHECKINTERVAL_DEFAULT = 30;

    /** DOCUMENT ME! */
    String PROP_CACHECAPACITY = "eyewiki.cachingProvider.capacity";

    /** DOCUMENT ME! */
    int PROP_CACHECAPACITY_DEFAULT = 1000; // Good most wikis

    /** Shall we use Lucene with this Wiki? */
    String PROP_USE_LUCENE = "eyewiki.useLucene";

    /** DOCUMENT ME! */
    boolean PROP_USE_LUCENE_DEFAULT = true;

    /**
     * DOCUMENT ME!
     */
    String PROP_LUCENE_ANALYZER = "eyewiki.lucene.analyzer";

    /**
     * DOCUMENT ME!
     */
    String PROP_LUCENE_ANALYZER_DEFAULT = StandardAnalyzer.class.getName();

    /** Prefix for the Wiki Special pages */
    String PROP_SPECIAL_PAGES_PREFIX = "eyewiki.specialPage";

    /*
     * ========================================================================
     *
     * RSS Configuration
     *
     * ========================================================================
     */

    /**
     * Defines the property name for the RSS channel description.  Default value for the channel
     * description is an empty string.
     *
     * @since 1.7.6.
     */
    String PROP_RSS_CHANNEL_DESCRIPTION = "eyewiki.rss.channelDescription";

    /**
     * Default channel description
     *
     * @value empty string
     */
    String PROP_RSS_CHANNEL_DESCRIPTION_DEFAULT = "";

    /**
     * Defines the property name for the RSS channel language.  Default value for the language is
     * "en-us".
     *
     * @since 1.7.6.
     */
    String PROP_RSS_CHANNEL_LANGUAGE = "eyewiki.rss.channelLanguage";

    /**
     * Default channel language
     *
     * @value en-us
     */
    String PROP_RSS_CHANNEL_LANGUAGE_DEFAULT = "en-us";

    /**
     * Defines the property name for the RSS file that the wiki should generate.
     *
     * @since 1.7.6.
     */
    String PROP_RSS_FILE = "eyewiki.rss.fileName";

    /**
     * Default value of the RSS File
     *
     * @value rss.rdf
     */
    String PROP_RSS_FILE_DEFAULT = "rss.rdf";

    /**
     * Defines the property name for the RSS generation interval in seconds.
     *
     * @since 1.7.6.
     */
    String PROP_RSS_INTERVAL = "eyewiki.rss.interval";

    /**
     * Default value of the RSS generation interval.
     *
     * @value 3600
     */
    int PROP_RSS_INTERVAL_DEFAULT = 3600;

    /** Do we want the page specific ATOM feeds? */
    String PROP_ATOM_FEEDS = "eyewiki.enableAtomFeeds";

    /** Default for ATOM Feeds: no */
    boolean PROP_ATOM_FEEDS_DEFAULT = false;

    /*
     * ========================================================================
     *
     * RCSFileProvider Configuration
     *
     * ========================================================================
     */

    /** DOCUMENT ME! */
    String PROP_RCS_CHECKIN = "eyewiki.rcsFileProvider.checkinCommand";

    /** DOCUMENT ME! */
    String PROP_RCS_CHECKIN_DEFAULT = "ci -q -m\"author=%u\" -l -t-none %s";

    /** DOCUMENT ME! */
    String PROP_RCS_CHECKOUT = "eyewiki.rcsFileProvider.checkoutCommand";

    /** DOCUMENT ME! */
    String PROP_RCS_CHECKOUT_DEFAULT = "co -l %s";

    /** DOCUMENT ME! */
    String PROP_RCS_LOG = "eyewiki.rcsFileProvider.logCommand";

    /** DOCUMENT ME! */
    String PROP_RCS_LOG_DEFAULT = "rlog -zLT -r %s";

    /** DOCUMENT ME! */
    String PROP_RCS_FULLLOG = "eyewiki.rcsFileProvider.fullLogCommand";

    /** DOCUMENT ME! */
    String PROP_RCS_FULLLOG_DEFAULT = "rlog -zLT %s";

    /** DOCUMENT ME! */
    String PROP_RCS_CHECKOUTVERSION = "eyewiki.rcsFileProvider.checkoutVersionCommand";

    /** DOCUMENT ME! */
    String PROP_RCS_CHECKOUTVERSION_DEFAULT = "co -p -r1.%v %s";

    /** DOCUMENT ME! */
    String PROP_RCS_DELETEVERSION = "eyewiki.rcsFileProvider.deleteVersionCommand";

    /** DOCUMENT ME! */
    String PROP_RCS_DELETEVERSION_DEFAULT = "rcs -o1.%v %s";

    /*
     * ========================================================================
     *
     * Filter Properties
     *
     * ========================================================================
     */

    /** DOCUMENT ME! */
    String PROP_FILTERXML = "eyewiki.filterConfig";

    /** DOCUMENT ME! */
    String PROP_FILTERXML_DEFAULT = "WEB-INF/filters.xml";

    /*
     * ========================================================================
     *
     * Authentication
     *
     * ========================================================================
     */

    /** DOCUMENT ME! */
    String PROP_AUTH_STRICTLOGINS = "eyewiki.policy.strictLogins";

    /** DOCUMENT ME! */
    boolean PROP_AUTH_STRICTLOGINS_DEFAULT = false;

    /** DOCUMENT ME! */
    String PROP_AUTH_USEOLDAUTH = "eyewiki.auth.useOldAuth";

    /** DOCUMENT ME! */
    boolean PROP_AUTH_USEOLDAUTH_DEFAULT = false;

    /** If true, logs the IP address of the editor on saving. */
    String PROP_AUTH_STOREIPADDRESS = "eyewiki.storeIPAddress";

    /** DOCUMENT ME! */
    boolean PROP_AUTH_STOREIPADDRESS_DEFAULT = true;

    /** DOCUMENT ME! */
    String PROP_AUTH_ADMINISTRATOR = "eyewiki.auth.administrator";

    /** The default administrator group is called "AdminGroup" */
    String PROP_AUTH_ADMINISTRATOR_DEFAULT = "AdminGroup";

    /** DOCUMENT ME! */
    String PROP_AUTH_FILENAME = "eyewiki.fileAuthenticator.fileName";

    /*
     * ========================================================================
     *
     * Class defaults
     *
     * ========================================================================
     */

    /**
     * Default Prefix for searching classes
     *
     * @value de.softwareforge.eyewiki
     */
    String DEFAULT_CLASS_PREFIX = "de.softwareforge.eyewiki";

    /** The default package for filter classes */
    String DEFAULT_FILTER_CLASS_PREFIX = "de.softwareforge.eyewiki.filters";
}
