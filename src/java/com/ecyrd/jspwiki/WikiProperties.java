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

import com.ecyrd.jspwiki.auth.modules.PageAuthorizer;
import com.ecyrd.jspwiki.auth.modules.WikiDatabase;
import com.ecyrd.jspwiki.providers.BasicAttachmentProvider;
import com.ecyrd.jspwiki.providers.FileSystemProvider;

/**
 * This interface holds all the constant names for the properties
 * available in jspwiki.properties
 *
 * @author <a href="hps@intermeta.de">Henning P. Schmiedehausen</a>
 */

public interface WikiProperties
{
    /*
     * ========================================================================
     *
     * Main Configuration
     *
     * ========================================================================
     */

    /** Property for application name */
    String PROP_APPNAME = "jspwiki.applicationName";

    /**
     * Default value of the Application name
     * @value value of Release.APPNAME
     */
    String PROP_APPNAME_DEFAULT = Release.APPNAME;

    /**
     *  Name of the property that defines where page directories are. Must be defined
     */
    String PROP_PAGEDIR = "jspwiki.fileSystemProvider.pageDir";


    /** Property name for where the jspwiki work directory should be. 
        If not specified, reverts to ${java.tmpdir}. */
    String PROP_WORKDIR = "jspwiki.workDir";

    String PROP_STORAGEDIR = "jspwiki.basicAttachmentProvider.storageDir";

    /** Property start for any interwiki reference. */
    String PROP_INTERWIKIREF = "jspwiki.interWikiRef.";

    /** If true, then the user name will be stored with the page data.*/
    String PROP_STOREUSERNAME= "jspwiki.storeUserName";

    /**
     * Default for storing the username with the page data
     * @value true
     */
    boolean PROP_STOREUSERNAME_DEFAULT = true;

    /** Define the used encoding.  Currently supported are ISO-8859-1 and UTF-8 */
    String PROP_ENCODING = "jspwiki.encoding";

    /**
     * Default Encoding Value
     * @value ISO-8859-1
     */
    String PROP_ENCODING_DEFAULT = "ISO-8859-1";

    /** The name for the base URL to use in all references. */
    String PROP_BASEURL = "jspwiki.baseURL";

    /**
     * Default BaseURL Value
     * @value &quot;&quot; (empty String)
     */
    String PROP_BASEURL_DEFAULT = "";

    String PROP_SHORTURL_PREFIX = "jspwiki.shortURLConstructor.prefix";

    String PROP_REFSTYLE = "jspwiki.referenceStyle";

    String PROP_REFSTYLE_DEFAULT = "relative";

    /** Property name for the "spaces in titles" -hack. */
    String PROP_BEAUTIFYTITLE = "jspwiki.breakTitleWithSpaces";

    /**
     * Default value for the "spaces in titles" - hack. 
     * @value false
     */
    boolean PROP_BEAUTIFYTITLE_DEFAULT = false;

    /** Property name for the "match english plurals" -hack. */
    String PROP_MATCHPLURALS = "jspwiki.translatorReader.matchEnglishPlurals";

    /**
     * Default value for the "match english plurals" - hack
     * @value true
     */
    boolean PROP_MATCHPLURALS_DEFAULT = false;

    /** Property name for the template that is used. */
    String PROP_TEMPLATEDIR = "jspwiki.templateDir";

    /**
     * Default values for the template that is used
     * @value default
     */
    String PROP_TEMPLATEDIR_DEFAULT = "default";

    /** Property name for the default front page. */
    String PROP_FRONTPAGE = "jspwiki.frontPage";

    /**
     * Default value for the front page.
     * @value Main
     */
    String PROP_FRONTPAGE_DEFAULT = "Main";

    String PROP_USECACHE = "jspwiki.usePageCache";

    boolean PROP_USECACHE_DEFAULT = false;

    String PROP_LOCKEXPIRY = "jspwiki.lockExpiryTime";

    int PROP_LOCKEXPIRY_DEFAULT = 60;

    /**
     *  This property defines the inline image pattern.  It's current value
     *  is jspwiki.translatorReader.inlinePattern
     */
    String PROP_INLINEIMAGEPTRN = "jspwiki.translatorReader.inlinePattern.";

    /**
     *  The default inlining pattern.  Currently "*.png"
     */
    String PROP_INLINEIMAGEPTRN_DEFAULT = "*.png";

    /** If true, consider CamelCase hyperlinks as well. */
    String PROP_CAMELCASELINKS = "jspwiki.translatorReader.camelCaseLinks";

    boolean PROP_CAMELCASELINKS_DEFAULT = false;

    /** If true, all hyperlinks are translated as well, regardless whether they
        are surrounded by brackets. */
    String PROP_PLAINURIS = "jspwiki.translatorReader.plainUris";

    boolean PROP_PLAINURIS_DEFAULT = false;

    /** If true, all outward links (external links) have a small link image appended. */
    String PROP_USEOUTLINKIMAGE = "jspwiki.translatorReader.useOutlinkImage";

    boolean PROP_USEOUTLINKIMAGE_DEFAULT = false;

    /** If set to "true", allows using raw HTML within Wiki text.  Be warned,
        this is a VERY dangerous option to set - never turn this on in a publicly
        allowable Wiki, unless you are absolutely certain of what you're doing. */
    String PROP_ALLOWHTML = "jspwiki.translatorReader.allowHTML";

    boolean PROP_ALLOWHTML_DEFAULT = false;

    /** If set to "true", all external links are tagged with 'rel="nofollow"' */
    String PROP_USERRELNOFOLLOW = "jspwiki.translatorReader.useRelNofollow";

    boolean PROP_USERRELNOFOLLOW_DEFAULT = false;

    /** If set to "true", enables plugins during parsing */
    String PROP_RUNPLUGINS = "jspwiki.translatorReader.runPlugins";
    
    boolean PROP_RUNPLUGINS_DEFAULT = true;

    /** Determines the command to be used for 'diff'.  This program must
        be able to output diffs in the unified format. It defaults to
        'diff -u %s1 %s2'.*/
    String PROP_DIFFCOMMAND = "jspwiki.diffCommand";

    /**
     * Default for the diff command.
     * @value null
     */
    String PROP_DIFFCOMMAND_DEFAULT = null;

    /**
     *  The maximum size of attachments that can be uploaded.
     */
    String PROP_MAXSIZE = "jspwiki.attachment.maxsize";

    int PROP_MAXSIZE_DEFAULT = 100000;

    /**
     *  Defines, in seconds, the amount of time a text will live in the cache
     *  at most before requiring a refresh.
     */
    String PROP_CACHECHECKINTERVAL = "jspwiki.cachingProvider.cacheCheckInterval";

    int PROP_CACHECHECKINTERVAL_DEFAULT = 30;

    String PROP_CACHECAPACITY = "jspwiki.cachingProvider.capacity";

    int PROP_CACHECAPACITY_DEFAULT = 1000; // Good most wikis


    /*
     * ========================================================================
     *
     * RSS Configuration
     *
     * ========================================================================
     */

    /**
     *  Defines the property name for the RSS channel description.  Default value for the 
     *  channel description is an empty string.
     *  @since 1.7.6.
     */
    String PROP_RSS_CHANNEL_DESCRIPTION = "jspwiki.rss.channelDescription";

    /**
     * Default channel description
     * @value empty string
     */
    String PROP_RSS_CHANNEL_DESCRIPTION_DEFAULT = "";


    /**
     *  Defines the property name for the RSS channel language.  Default value for the
     *  language is "en-us".
     *  @since 1.7.6.
     */
    String PROP_RSS_CHANNEL_LANGUAGE = "jspwiki.rss.channelLanguage";

    /**
     * Default channel language
     * @value en-us
     */
    String PROP_RSS_CHANNEL_LANGUAGE_DEFAULT = "en-us";

    /**
     *  Defines the property name for the RSS generator main switch.
     *  @since 1.7.6.
     */
    String PROP_RSS_GENERATE = "jspwiki.rss.generate";

    /**
     * Whether an RSS Feed should be generated or not.
     * @value false
     */
    boolean PROP_RSS_GENERATE_DEFAULT = false;

    /**
     *  Defines the property name for the RSS file that the wiki should generate.
     *  @since 1.7.6.
     */
    String PROP_RSS_FILE = "jspwiki.rss.fileName";

    /**
     *  Default value of the RSS File
     *  @value rss.rdf
     */
    String PROP_RSS_FILE_DEFAULT = "rss.rdf";

    /**
     *  Defines the property name for the RSS generation interval in seconds.
     *  @since 1.7.6.
     */
    String PROP_RSS_INTERVAL = "jspwiki.rss.interval";

    /**
     *  Default value of the RSS generation interval.
     *  @value 3600
     */
    int PROP_RSS_INTERVAL_DEFAULT = 3600;

    /**
     * Shall we use Lucene with this Wiki?
     */
    String PROP_USE_LUCENE = "jspwiki.useLucene";

    boolean PROP_USE_LUCENE_DEFAULT = true;

    /*
     * ========================================================================
     *
     * RCSFileProvider Configuration
     *
     * ========================================================================
     */

    String PROP_RCS_CHECKIN = "jspwiki.rcsFileProvider.checkinCommand";
    String PROP_RCS_CHECKIN_DEFAULT = "ci -q -m\"author=%u\" -l -t-none %s";

    String PROP_RCS_CHECKOUT = "jspwiki.rcsFileProvider.checkoutCommand";
    String PROP_RCS_CHECKOUT_DEFAULT = "co -l %s";

    String PROP_RCS_LOG = "jspwiki.rcsFileProvider.logCommand";
    String PROP_RCS_LOG_DEFAULT = "rlog -zLT -r %s";

    String PROP_RCS_FULLLOG = "jspwiki.rcsFileProvider.fullLogCommand";
    String PROP_RCS_FULLLOG_DEFAULT = "rlog -zLT %s";

    String PROP_RCS_CHECKOUTVERSION = "jspwiki.rcsFileProvider.checkoutVersionCommand";
    String PROP_RCS_CHECKOUTVERSION_DEFAULT = "co -p -r1.%v %s";

    String PROP_RCS_DELETEVERSION = "jspwiki.rcsFileProvider.deleteVersionCommand";
    String PROP_RCS_DELETEVERSION_DEFAULT = "rcs -o1.%v %s";

    /*
     * ========================================================================
     *
     * Filter Properties
     *
     * ========================================================================
     */

    String PROP_FILTERXML = "jspwiki.filterConfig";

    String PROP_FILTERXML_DEFAULT = "WEB-INF/filters.xml";

    /*
     * ========================================================================
     *
     * Authentication
     *
     * ========================================================================
     */

    String PROP_AUTH_STRICTLOGINS = "jspwiki.policy.strictLogins";
 
    boolean PROP_AUTH_STRICTLOGINS_DEFAULT = false;

    String PROP_AUTH_USEOLDAUTH = "jspwiki.auth.useOldAuth";
    
    boolean PROP_AUTH_USEOLDAUTH_DEFAULT = false;
    
    /** If true, logs the IP address of the editor on saving. */
    String PROP_AUTH_STOREIPADDRESS= "jspwiki.storeIPAddress";

    boolean PROP_AUTH_STOREIPADDRESS_DEFAULT = true;

    String PROP_AUTH_ADMINISTRATOR = "jspwiki.auth.administrator";

    /**
     *  The default administrator group is called "AdminGroup"
     */
    String PROP_AUTH_ADMINISTRATOR_DEFAULT = "AdminGroup";

    String PROP_AUTH_FILENAME = "jspwiki.fileAuthenticator.fileName";

    /*
     * ========================================================================
     *
     * Class defaults
     *
     * ========================================================================
     */

    /** 
     * Default Prefix for searching classes
     * @value com.ecyrd.jspwiki
     */
    String DEFAULT_CLASS_PREFIX = "com.ecyrd.jspwiki";

    String DEFAULT_PROVIDER_CLASS_PREFIX = "com.ecyrd.jspwiki.providers";

    String DEFAULT_AUTH_MODULES_CLASS_PREFIX = "com.ecyrd.jspwiki.auth.modules";

    /**
     *  The property name defining which packages will be searched for properties.
     */
    String PROP_CLASS_PLUGIN_SEARCHPATH = "jspwiki.plugin.searchPath";

    /**
     *  These are the default packages
     */
    String PROP_CLASS_PLUGIN_SEARCHPATH_DEFAULT = "com.ecyrd.jspwiki.plugin,com.ecyrd.jspwiki.forms";

    /** Property name for setting the url generator instance */
    String PROP_CLASS_URLCONSTRUCTOR = "jspwiki.urlConstructor";

    /**
     * Default value for the URL Constructor
     * @value DefaultURLConstructor
     */
    String PROP_CLASS_URLCONSTRUCTOR_DEFAULT = DefaultURLConstructor.class.getName();

    
    String PROP_CLASS_PAGEPROVIDER = "jspwiki.pageProvider";

    String PROP_CLASS_PAGEPROVIDER_DEFAULT = FileSystemProvider.class.getName();

    /**
     *  The property name for defining the attachment provider class name.
     */
    String PROP_CLASS_ATTACHMENTPROVIDER = "jspwiki.attachmentProvider";

    String PROP_CLASS_ATTACHMENTPROVIDER_DEFAULT = BasicAttachmentProvider.class.getName();

    String PROP_CLASS_AUTHORIZER = "jspwiki.authorizer";

    String PROP_CLASS_AUTHORIZER_DEFAULT = PageAuthorizer.class.getName();

    String PROP_CLASS_USERDATABASE = "jspwiki.userdatabase";

    String PROP_CLASS_USERDATABASE_DEFAULT = WikiDatabase.class.getName();

    String PROP_CLASS_AUTHENTICATOR = "jspwiki.authenticator";


}
