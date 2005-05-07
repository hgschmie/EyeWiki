/*
 * (C) Janne Jalkanen 2005
 *
 */
package com.ecyrd.jspwiki.dav;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.ecyrd.jspwiki.WikiEngine;
import com.ecyrd.jspwiki.dav.methods.DavMethod;
import com.ecyrd.jspwiki.dav.methods.GetMethod;
import com.ecyrd.jspwiki.dav.methods.PropFindMethod;
import com.ecyrd.jspwiki.dav.methods.PropPatchMethod;


/**
 * DOCUMENT ME!
 *
 * @author jalkanen
 *
 * @since
 */
public class WikiDavServlet
        extends WebdavServlet
{
    /**
     * DOCUMENT ME!
     */
    private WikiEngine m_engine;

    /**
     * DOCUMENT ME!
     */
    Logger log = Logger.getLogger(this.getClass().getName());

    /**
     * DOCUMENT ME!
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
    }

    private String parsePage(HttpServletRequest req)
    {
        return req.getContextPath();
    }

    /**
     * DOCUMENT ME!
     *
     * @param req DOCUMENT ME!
     * @param res DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     * @throws ServletException DOCUMENT ME!
     */
    public void doPropFind(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException
    {
        PropFindMethod m = new PropFindMethod(m_engine);

        m.execute(req, res);
    }

    /**
     * DOCUMENT ME!
     *
     * @param req DOCUMENT ME!
     * @param res DOCUMENT ME!
     */
    protected void doOptions(HttpServletRequest req, HttpServletResponse res)
    {
        res.setHeader("DAV", "1"); // We support only Class 1
        res.setHeader("Allow", "GET, PUT, POST, OPTIONS, PROPFIND, PROPPATCH, MOVE, COPY, DELETE");
        res.setStatus(HttpServletResponse.SC_OK);
    }

    /**
     * DOCUMENT ME!
     *
     * @param request DOCUMENT ME!
     * @param response DOCUMENT ME!
     *
     * @throws ServletException DOCUMENT ME!
     * @throws IOException DOCUMENT ME!
     */
    public void doMkCol(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        if (request.getContentLength() > 0)
        {
            response.sendError(
                HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "Message may contain no body");
        }
        else
        {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JSPWiki is read-only.");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param request DOCUMENT ME!
     * @param response DOCUMENT ME!
     *
     * @throws ServletException DOCUMENT ME!
     * @throws IOException DOCUMENT ME!
     */
    public void doPropPatch(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        DavMethod dm = new PropPatchMethod(m_engine);

        dm.execute(request, response);
    }

    /**
     * DOCUMENT ME!
     *
     * @param request DOCUMENT ME!
     * @param response DOCUMENT ME!
     *
     * @throws ServletException DOCUMENT ME!
     * @throws IOException DOCUMENT ME!
     */
    public void doCopy(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JSPWiki is read-only.");
    }

    /**
     * DOCUMENT ME!
     *
     * @param request DOCUMENT ME!
     * @param response DOCUMENT ME!
     *
     * @throws ServletException DOCUMENT ME!
     * @throws IOException DOCUMENT ME!
     */
    public void doMove(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JSPWiki is read-only.");
    }

    /**
     * DOCUMENT ME!
     *
     * @param arg0 DOCUMENT ME!
     * @param response DOCUMENT ME!
     *
     * @throws ServletException DOCUMENT ME!
     * @throws IOException DOCUMENT ME!
     */
    protected void doDelete(HttpServletRequest arg0, HttpServletResponse response)
            throws ServletException, IOException
    {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JSPWiki is read-only.");
    }

    /**
     * DOCUMENT ME!
     *
     * @param arg0 DOCUMENT ME!
     * @param response DOCUMENT ME!
     *
     * @throws ServletException DOCUMENT ME!
     * @throws IOException DOCUMENT ME!
     */
    protected void doPost(HttpServletRequest arg0, HttpServletResponse response)
            throws ServletException, IOException
    {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JSPWiki is read-only.");
    }

    /**
     * DOCUMENT ME!
     *
     * @param arg0 DOCUMENT ME!
     * @param response DOCUMENT ME!
     *
     * @throws ServletException DOCUMENT ME!
     * @throws IOException DOCUMENT ME!
     */
    protected void doPut(HttpServletRequest arg0, HttpServletResponse response)
            throws ServletException, IOException
    {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JSPWiki is read-only.");
    }

    /*
     * GET /dav/raw/WikiPage.txt
     * GET /dav/html/WikiPage.html
     * GET /dav/pdf/WikiPage.pdf
     * GET /dav/raw/WikiPage/attachment1.png
     *
     */
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException
    {
        DavMethod dm = new GetMethod(m_engine);

        dm.execute(req, res);
    }
}
