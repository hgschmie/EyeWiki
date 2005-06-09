package de.softwareforge.eyewiki.xmlrpc;

import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.xmlrpc.XmlRpcException;


import de.softwareforge.eyewiki.PageTimeComparator;
import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.WikiPage;
import de.softwareforge.eyewiki.attachment.Attachment;
import de.softwareforge.eyewiki.attachment.AttachmentManager;
import de.softwareforge.eyewiki.plugin.PluginManager;
import de.softwareforge.eyewiki.plugin.WeblogEntryPlugin;
import de.softwareforge.eyewiki.plugin.WeblogPlugin;
import de.softwareforge.eyewiki.providers.ProviderException;


/**
 * Provides handlers for all RPC routines of the MetaWeblog API.
 *
 * <P>
 * eyeWiki does not support categories, and therefore we always return an empty list for
 * getCategories().  Note also that this API is not suitable for general Wiki editing, since
 * eyeWiki formats the entries in a wiki-compatible manner.  And you cannot choose your page names
 * either.  Since 2.1.94 the entire MetaWeblog API is supported.
 * </p>
 *
 * @author Janne Jalkanen
 *
 * @since 2.1.7
 */
public class MetaWeblogHandler
        implements WikiRPCHandler
{
    /** DOCUMENT ME! */
    private static final Logger log = Logger.getLogger(MetaWeblogHandler.class);

    /** DOCUMENT ME! */
    private WikiEngine m_engine;

    /**
     * DOCUMENT ME!
     *
     * @param engine DOCUMENT ME!
     */
    public void initialize(WikiEngine engine)
    {
        m_engine = engine;
    }

    /**
     * Does a quick check against the current user and does he have permissions to do the stuff
     * that he really wants to.
     *
     * <p>
     * If there is no authentication enabled, returns normally.
     * </p>
     *
     * @param page DOCUMENT ME!
     * @param username DOCUMENT ME!
     * @param password DOCUMENT ME!
     * @param permission DOCUMENT ME!
     *
     * @throws XmlRpcException with the correct error message, if auth fails.
     */
    private void checkPermissions(
        WikiPage page, String username, String password, String permission)
            throws XmlRpcException
    {
        /*
          AuthorizationManager mgr = m_engine.getAuthorizationManager();
          UserProfile currentUser  = m_engine.getUserManager().getUserProfile( username );
          currentUser.setPassword( password );

          WikiAuthenticator auth = m_engine.getUserManager().getAuthenticator();

          if( auth != null )
          {
          boolean isValid = auth.authenticate( currentUser );

          if( isValid )
          {
          if( !mgr.checkPermission( page,
          currentUser,
          permission ) )
          {
          return;
          }
          else
          {
          String msg = "Insufficient permissions to do "+permission+" on "+page.getName();
          log.error( msg );
          throw new XmlRpcException(0, msg );
          }
          }
          else
          {
          log.error( "Username '"+username+"' or password not valid." );
          throw new XmlRpcException(0, "Password or username not valid.");
          }
          }
        */
    }

    /**
     * eyeWiki does not support categories, therefore eyeWiki always returns an empty list for
     * categories.
     *
     * @param blogid DOCUMENT ME!
     * @param username DOCUMENT ME!
     * @param password DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws XmlRpcException DOCUMENT ME!
     */
    public Hashtable getCategories(String blogid, String username, String password)
            throws XmlRpcException
    {
        WikiPage page = m_engine.getPage(blogid);

        checkPermissions(page, username, password, "view");

        Hashtable ht = new Hashtable();

        return ht;
    }

    private String getURL(String page)
    {
        return m_engine.getURL(WikiContext.VIEW, page, null, true); // Force absolute urls
    }

    /**
     * Takes a wiki page, and creates a metaWeblog struct out of it.
     *
     * @param page The actual entry page
     *
     * @return A metaWeblog entry struct.
     */
    private Hashtable makeEntry(WikiPage page)
    {
        Hashtable ht = new Hashtable();

        WikiPage firstVersion = m_engine.getPage(page.getName(), 1);

        ht.put("dateCreated", firstVersion.getLastModified());
        ht.put("link", getURL(page.getName()));
        ht.put("permaLink", getURL(page.getName()));
        ht.put("postid", page.getName());
        ht.put("userid", page.getAuthor());

        String pageText = m_engine.getText(page.getName());
        String title = "";
        int firstLine = pageText.indexOf('\n');

        if (firstLine > 0)
        {
            title = pageText.substring(0, firstLine);
        }

        if (StringUtils.isBlank(title))
        {
            title = page.getName();
        }

        // Remove wiki formatting
        while (title.startsWith("!"))
        {
            title = title.substring(1);
        }

        ht.put("title", title);
        ht.put("description", pageText);

        return ht;
    }

    /**
     * Returns a list of the recent posts to this weblog.
     *
     * @param blogid DOCUMENT ME!
     * @param username DOCUMENT ME!
     * @param password DOCUMENT ME!
     * @param numberOfPosts DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws XmlRpcException DOCUMENT ME!
     */

    // FIXME: The implementation is suboptimal, as it
    //        goes through all of the blog entries.
    public Hashtable getRecentPosts(
        String blogid, String username, String password, int numberOfPosts)
            throws XmlRpcException
    {
        Hashtable result = new Hashtable();

        log.info("metaWeblog.getRecentPosts() called");

        WikiPage page = m_engine.getPage(blogid);

        checkPermissions(page, username, password, "view");

        try
        {
            PluginManager pluginManager = m_engine.getPluginManager();

            if (pluginManager != null)
            {
                WeblogPlugin plugin = (WeblogPlugin) pluginManager.findPlugin("WeblogPlugin");

                List changed =
                        plugin.findBlogEntries(blogid, new Date(0L), new Date());

                Collections.sort(changed, new PageTimeComparator());

                int items = 0;

                for (Iterator i = changed.iterator(); i.hasNext() && (items < numberOfPosts);
                     items++)
                {
                    WikiPage p = (WikiPage) i.next();

                    result.put("entry", makeEntry(p));
                }
            }
        }
        catch (ProviderException e)
        {
            log.error("Failed to list recent posts", e);

            throw new XmlRpcException(0, e.getMessage());
        }

        return result;
    }

    /**
     * Adds a new post to the blog.
     *
     * @param blogid DOCUMENT ME!
     * @param username DOCUMENT ME!
     * @param password DOCUMENT ME!
     * @param content DOCUMENT ME!
     * @param publish This parameter is ignored for eyeWiki.
     *
     * @return DOCUMENT ME!
     *
     * @throws XmlRpcException DOCUMENT ME!
     */
    public String newPost(
        String blogid, String username, String password, Hashtable content, boolean publish)
            throws XmlRpcException
    {
        log.info("metaWeblog.newPost() called");

        WikiPage page = m_engine.getPage(blogid);
        checkPermissions(page, username, password, "create");

        try
        {
            PluginManager pluginManager = m_engine.getPluginManager();

            if (pluginManager != null)
            {
                WeblogEntryPlugin plugin = (WeblogEntryPlugin) pluginManager.findPlugin("WeblogEntryPlugin");

                String pageName = plugin.getNewEntryPage(blogid);

                WikiPage entryPage = new WikiPage(pageName);
                entryPage.setAuthor(username);

                WikiContext context = new WikiContext(m_engine, entryPage);

                StringBuffer text = new StringBuffer();
                text.append("!" + content.get("title"));
                text.append("\n\n");
                text.append(content.get("description"));

                if (log.isDebugEnabled())
                {
                    log.debug("Writing entry: " + text);
                }

                m_engine.saveText(context, text.toString());
            }
        }
        catch (Exception e)
        {
            log.error("Failed to create weblog entry", e);
            throw new XmlRpcException(0, "Failed to create weblog entry: " + e.getMessage());
        }

        return ""; // FIXME:
    }

    /**
     * Creates an attachment and adds it to the blog.  The attachment is created into the main blog
     * page, not the actual post page, because we do not know it at this point.
     *
     * @param blogid DOCUMENT ME!
     * @param username DOCUMENT ME!
     * @param password DOCUMENT ME!
     * @param content DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws XmlRpcException DOCUMENT ME!
     */
    public Hashtable newMediaObject(
        String blogid, String username, String password, Hashtable content)
            throws XmlRpcException
    {
        String url = "";

        log.info("metaWeblog.newMediaObject() called");

        WikiPage page = m_engine.getPage(blogid);
        checkPermissions(page, username, password, "upload");

        String name = (String) content.get("name");
        byte [] data = (byte []) content.get("bits");

        AttachmentManager attmgr = m_engine.getAttachmentManager();

        try
        {
            Attachment att = new Attachment(blogid, name);
            att.setAuthor(username);
            attmgr.storeAttachment(att, new ByteArrayInputStream(data));

            url = m_engine.getURL(WikiContext.ATTACH, att.getName(), null, true);
        }
        catch (Exception e)
        {
            log.error("Failed to upload attachment", e);
            throw new XmlRpcException(0, "Failed to upload media object: " + e.getMessage());
        }

        Hashtable result = new Hashtable();
        result.put("url", url);

        return result;
    }

    /**
     * Allows the user to edit a post.  It does not allow general editability of wiki pages,
     * because of the limitations of the metaWeblog API.
     *
     * @param postid DOCUMENT ME!
     * @param username DOCUMENT ME!
     * @param password DOCUMENT ME!
     * @param content DOCUMENT ME!
     * @param publish DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws XmlRpcException DOCUMENT ME!
     */
    boolean editPost(
        String postid, String username, String password, Hashtable content, boolean publish)
            throws XmlRpcException
    {
        if (log.isInfoEnabled())
        {
            log.info("metaWeblog.editPost(" + postid + ") called");
        }

        // FIXME: Is postid correct?  Should we determine it from the page name?
        WikiPage page = m_engine.getPage(postid);
        checkPermissions(page, username, password, "edit");

        try
        {
            WikiPage entryPage = (WikiPage) page.clone();
            entryPage.setAuthor(username);

            WikiContext context = new WikiContext(m_engine, entryPage);

            StringBuffer text = new StringBuffer();
            text.append("!" + content.get("title"));
            text.append("\n\n");
            text.append(content.get("description"));

            if (log.isDebugEnabled())
            {
                log.debug("Updating entry: " + text);
            }

            m_engine.saveText(context, text.toString());
        }
        catch (Exception e)
        {
            log.error("Failed to create weblog entry", e);
            throw new XmlRpcException(0, "Failed to update weblog entry: " + e.getMessage());
        }

        return true;
    }

    /**
     * Gets the text of any page.  The title of the page is parsed (if any is provided).
     *
     * @param postid DOCUMENT ME!
     * @param username DOCUMENT ME!
     * @param password DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws XmlRpcException DOCUMENT ME!
     */
    Hashtable getPost(String postid, String username, String password)
            throws XmlRpcException
    {
        String wikiname = "FIXME";

        WikiPage page = m_engine.getPage(wikiname);

        checkPermissions(page, username, password, "view");

        return makeEntry(page);
    }
}
