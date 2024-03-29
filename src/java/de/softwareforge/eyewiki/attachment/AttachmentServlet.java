package de.softwareforge.eyewiki.attachment;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.WikiProperties;
import de.softwareforge.eyewiki.WikiProvider;
import de.softwareforge.eyewiki.auth.AuthorizationManager;
import de.softwareforge.eyewiki.auth.UserProfile;
import de.softwareforge.eyewiki.filters.RedirectException;
import de.softwareforge.eyewiki.providers.ProviderException;
import de.softwareforge.eyewiki.util.HttpUtil;

import http.utils.multipartrequest.MultipartRequest;

/**
 * This is a simple file upload servlet customized for eyeWiki. It receives a mime/multipart POST message, as sent by an Attachment
 * page, stores it temporarily, figures out what WikiName to use to store it, checks for previously existing versions.
 *
 * <p>
 * This servlet does not worry about authentication; we leave that to the container, or a previous servlet that chains to us.
 * </p>
 *
 * @author Erik Bunn
 * @author Janne Jalkanen
 *
 * @since 1.9.45.
 */
public class AttachmentServlet
        extends HttpServlet
{
    /** DOCUMENT ME! */
    public static final String HDR_VERSION = "version";

    /** DOCUMENT ME! */
    public static final String HDR_NAME = "page";

    /** Default expiry period is 1 day */
    protected static final long DEFAULT_EXPIRY = 1 * 24 * 60 * 60 * 1000;

    /** DOCUMENT ME! */
    private WikiEngine m_engine;

    /** DOCUMENT ME! */
    protected Logger log = Logger.getLogger(this.getClass());

    /** DOCUMENT ME! */
    private String m_tmpDir;

    /** The maximum size that an attachment can be. */
    private int m_maxSize = Integer.MAX_VALUE;

    /**
     * Initializes the servlet from WikiEngine properties.
     *
     * @param config DOCUMENT ME!
     *
     * @throws ServletException DOCUMENT ME!
     */
    public void init(ServletConfig config)
            throws ServletException
    {
        super.init(config);

        m_engine = WikiEngine.getInstance(config);

        Configuration conf = m_engine.getWikiConfiguration();

        m_tmpDir = m_engine.getWorkDir() + File.separator + "attach-tmp";

        m_maxSize = conf.getInt(WikiProperties.PROP_MAXSIZE, WikiProperties.PROP_MAXSIZE_DEFAULT);

        File f = new File(m_tmpDir);

        if (!f.exists())
        {
            f.mkdirs();
        }
        else if (!f.isDirectory())
        {
            log.fatal("A file already exists where the temporary dir is supposed to be: " + m_tmpDir + ".  Please remove it.");
        }

        if (log.isDebugEnabled())
        {
            log.debug("UploadServlet initialized. Using " + m_tmpDir + " for temporary storage.");
        }
    }

    /**
     * Serves a GET with two parameters: 'wikiname' specifying the wikiname of the attachment, 'version' specifying the version
     * indicator.
     *
     * @param req DOCUMENT ME!
     * @param res DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     * @throws ServletException DOCUMENT ME!
     */

    // FIXME: Messages would need to be localized somehow.
    public void doGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException
    {
        String version = m_engine.safeGetParameter(req, HDR_VERSION);
        String nextPage = m_engine.safeGetParameter(req, "nextpage");

        String msg = "An error occurred. Ouch.";
        int ver = WikiProvider.LATEST_VERSION;

        AttachmentManager mgr = m_engine.getAttachmentManager();
        AuthorizationManager authmgr = m_engine.getAuthorizationManager();

        UserProfile wup = m_engine.getUserManager().getUserProfile(req);

        WikiContext context = m_engine.createContext(req, WikiContext.ATTACH);
        String page = context.getPage().getName();

        if (page == null)
        {
            log.info("Invalid attachment name.");
            res.sendError(HttpServletResponse.SC_BAD_REQUEST);

            return;
        }
        else
        {
            OutputStream out = null;
            InputStream in = null;

            try
            {
                if (log.isDebugEnabled())
                {
                    log.debug("Attempting to download att " + page + ", version " + version);
                }

                if (version != null)
                {
                    ver = Integer.parseInt(version);
                }

                Attachment att = mgr.getAttachmentInfo(page, ver);

                if (att != null)
                {
                    //
                    //  Check if the user has permission for this attachment
                    //
                    if (!authmgr.checkPermission(att, wup, "view"))
                    {
                        if (log.isDebugEnabled())
                        {
                            log.debug("User does not have permission for this");
                        }

                        res.sendError(HttpServletResponse.SC_FORBIDDEN);

                        return;
                    }

                    //
                    //  Check if the client already has a version of this attachment.
                    //
                    if (HttpUtil.checkFor304(req, att))
                    {
                        log.debug("Client has latest version already, sending 304...");
                        res.sendError(HttpServletResponse.SC_NOT_MODIFIED);

                        return;
                    }

                    String mimetype = getServletConfig().getServletContext().getMimeType(att.getFileName().toLowerCase());

                    if (mimetype == null)
                    {
                        mimetype = "application/binary";
                    }

                    res.setContentType(mimetype);

                    //
                    //  We use 'inline' instead of 'attachment' so that user agents
                    //  can try to automatically open the file.
                    //
                    res.addHeader("Content-Disposition", "inline; filename=\"" + att.getFileName() + "\";");

                    // long expires = new Date().getTime() + DEFAULT_EXPIRY;
                    // res.addDateHeader("Expires", expires);
                    res.addDateHeader("Last-Modified", att.getLastModified().getTime());

                    // If a size is provided by the provider, report it.
                    if (att.getSize() >= 0)
                    {
                        // log.info("size:"+att.getSize());
                        res.setContentLength((int) att.getSize());
                    }

                    out = res.getOutputStream();
                    in = mgr.getAttachmentStream(att);

                    int read = 0;
                    byte [] buffer = new byte[8192];

                    while ((read = in.read(buffer)) > -1)
                    {
                        out.write(buffer, 0, read);
                    }

                    if (log.isDebugEnabled())
                    {
                        log.debug("Attachment " + att.getFileName() + " sent to " + req.getRemoteUser() + " on "
                            + req.getRemoteAddr());
                    }

                    if (nextPage != null)
                    {
                        res.sendRedirect(nextPage);
                    }

                    return;
                }
                else
                {
                    msg = "Attachment '" + page + "', version " + ver + " does not exist.";

                    log.info(msg);
                    res.sendError(HttpServletResponse.SC_NOT_FOUND, msg);

                    return;
                }
            }
            catch (ProviderException pe)
            {
                msg = "Provider error: " + pe.getMessage();
                res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, msg);

                return;
            }
            catch (NumberFormatException nfe)
            {
                msg = "Invalid version number (" + version + ")";
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, msg);

                return;
            }
            catch (IOException ioe)
            {
                msg = "Error: " + ioe.getMessage();
                res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, msg);

                return;
            }
            finally
            {
                IOUtils.closeQuietly(in);
                IOUtils.closeQuietly(out);
            }
        }
    }

    /**
     * Grabs mime/multipart data and stores it into the temporary area. Uses other parameters to determine which name to store as.
     *
     * <p>
     * The input to this servlet is generated by an HTML FORM with two parts. The first, named 'page', is the WikiName identifier
     * for the parent file. The second, named 'content', is the binary content of the file.
     * </p>
     *
     * @param req DOCUMENT ME!
     * @param res DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     * @throws ServletException DOCUMENT ME!
     */
    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException
    {
        try
        {
            String nextPage = upload(req);
            req.getSession().removeAttribute("msg");
            res.sendRedirect(nextPage);
        }
        catch (RedirectException e)
        {
            req.getSession().setAttribute("msg", e.getMessage());
            res.sendRedirect(e.getRedirect());
        }
    }

    /**
     * Uploads a specific mime multipart input set, intercepts exceptions.
     *
     * @param req DOCUMENT ME!
     *
     * @return The page to which we should go next.
     *
     * @throws RedirectException DOCUMENT ME!
     * @throws IOException DOCUMENT ME!
     */
    protected String upload(HttpServletRequest req)
            throws RedirectException, IOException
    {
        String msg = "";
        String attName = "(unknown)";
        String errorPage = m_engine.getURL(WikiContext.ERROR, "", null, false); // If something bad happened, Upload should be able to take care of most stuff
        String nextPage = errorPage;

        try
        {
            MultipartRequest multi;

            multi =
                new MultipartRequest(null, // no debugging
                    req.getContentType(), req.getContentLength(), req.getInputStream(), m_tmpDir, Integer.MAX_VALUE,
                    m_engine.getContentEncoding());

            nextPage = multi.getURLParameter("nextpage");

            String wikipage = multi.getURLParameter("page");

            WikiContext context = m_engine.createContext(req, WikiContext.UPLOAD);
            errorPage = context.getURL(WikiContext.UPLOAD, wikipage);

            //
            //  FIXME: This has the unfortunate side effect that it will receive the
            //  contents.  But we can't figure out the page to redirect to
            //  before we receive the file, due to the stupid constructor of MultipartRequest.
            //
            if (req.getContentLength() > m_maxSize)
            {
                // FIXME: Does not delete the received files.
                throw new RedirectException("File exceeds maximum size (" + m_maxSize + " bytes)", errorPage);
            }

            UserProfile user = context.getCurrentUser();

            //
            //  Go through all files being uploaded.
            //
            AttachmentManager mgr = m_engine.getAttachmentManager();

            for (Enumeration files = multi.getFileParameterNames(); files.hasMoreElements(); )
            {
                String part = (String) files.nextElement();
                File f = multi.getFile(part);
                InputStream in;

                try
                {
                    //
                    //  Is a file to be uploaded.
                    //
                    String filename = multi.getBaseFilename(part);

                    if (StringUtils.isEmpty(filename))
                    {
                        log.error("Empty file name given.");

                        throw new RedirectException("Empty file name given.", errorPage);
                    }

                    //
                    //  Should help with IE 5.22 on OSX
                    //
                    filename = filename.trim();

                    if (log.isDebugEnabled())
                    {
                        log.debug("file=" + filename);
                    }

                    //
                    //  Attempt to open the input stream
                    //
                    if (f != null)
                    {
                        in = new FileInputStream(f);
                    }
                    else
                    {
                        //
                        //  This happens onl when the size of the
                        //  file is small enough to be cached in memory
                        //
                        in = multi.getFileContents(part);
                    }

                    if (in == null)
                    {
                        log.error("File could not be opened.");

                        throw new RedirectException("File could not be opened.", errorPage);
                    }

                    //
                    //  Check whether we already have this kind of a page.
                    //  If the "page" parameter already defines an attachment
                    //  name for an update, then we just use that file.
                    //  Otherwise we create a new attachment, and use the
                    //  filename given.  Incidentally, this will also mean
                    //  that if the user uploads a file with the exact
                    //  same name than some other previous attachment,
                    //  then that attachment gains a new version.
                    //
                    Attachment att = mgr.getAttachmentInfo(wikipage);

                    if (att == null)
                    {
                        att = new Attachment(wikipage, filename);
                    }

                    //
                    //  Check if we're allowed to do this?
                    //
                    if (m_engine.getAuthorizationManager().checkPermission(att, user, "upload"))
                    {
                        if (user != null)
                        {
                            att.setAuthor(user.getName());
                        }

                        mgr.storeAttachment(att, in);

                        if (log.isInfoEnabled())
                        {
                            log.info("User " + user + " uploaded attachment to " + wikipage + " called " + filename + ", size "
                                + multi.getFileSize(part));
                        }
                    }
                    else
                    {
                        throw new RedirectException("No permission to upload a file", errorPage);
                    }
                }
                finally
                {
                    if (f != null)
                    {
                        f.delete();
                    }
                }
            }

            // Inform the JSP page of which file we are handling:
            // req.setAttribute( ATTR_ATTACHMENT, wikiname );
        }
        catch (ProviderException e)
        {
            msg = "Upload failed because the provider failed: " + e.getMessage();
            log.warn(msg + " (attachment: " + attName + ")", e);

            throw new IOException(msg);
        }
        catch (IOException e)
        {
            // Show the submit page again, but with a bit more
            // intimidating output.
            msg = "Upload failure: " + e.getMessage();
            log.warn(msg + " (attachment: " + attName + ")", e);

            // FIXME: In case of exceptions should absolutely
            //        remove the uploaded file.
            throw e;
        }

        return nextPage;
    }

    /**
     * Produces debug output listing parameters and files.
     */

    /*
     *      private void debugContentList( MultipartRequest  multi )
     *      {
     *      StringBuffer sb = new StringBuffer();
     *
     *      sb.append( "Upload information: parameters: [" );
     *
     *      Enumeration params = multi.getParameterNames();
     *      while( params.hasMoreElements() )
     *      {
     *      String name = (String)params.nextElement();
     *      String value = multi.getURLParameter( name );
     *      sb.append( "[" + name + " = " + value + "]" );
     *      }
     *
     *      sb.append( " files: [" );
     *      Enumeration files = multi.getFileParameterNames();
     *      while( files.hasMoreElements() )
     *      {
     *      String name = (String)files.nextElement();
     *      String filename = multi.getFileSystemName( name );
     *      String type = multi.getContentType( name );
     *      File f = multi.getFile( name );
     *      sb.append( "[name: " + name );
     *      sb.append( " temp_file: " + filename );
     *      sb.append( " type: " + type );
     *      if (f != null)
     *      {
     *      sb.append( " abs: " + f.getPath() );
     *      sb.append( " size: " + f.length() );
     *      }
     *      sb.append( "]" );
     *      }
     *      sb.append( "]" );
     *
     *
     *      log.debug( sb.toString() );
     *      }
     */
}
