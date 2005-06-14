package de.softwareforge.eyewiki.dav.methods;

import java.io.IOException;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.WikiPage;
import de.softwareforge.eyewiki.dav.DavContext;
import de.softwareforge.eyewiki.dav.DavUtil;
import de.softwareforge.eyewiki.providers.ProviderException;

/**
 * DOCUMENT ME!
 *
 * @author jalkanen
 *
 * @since
 */
public class GetMethod
        extends DavMethod
{
    /** Logger for this class */
    private static final Log log = LogFactory.getLog(GetMethod.class);

    /**
                 *
                 */
    public GetMethod(WikiEngine engine)
    {
        super(engine);
    }

    private void returnMainCollection(HttpServletRequest req, HttpServletResponse res)
            throws IOException
    {
        res.getWriter().println("raw");
    }

    private WikiContext createContext(HttpServletRequest req, DavContext dc)
    {
        String pagename = dc.m_page;

        if ("raw".equals(dc.m_davcontext))
        {
            if (pagename.endsWith(".txt"))
            {
                pagename = pagename.substring(0, pagename.length() - 4);
            }
        }
        else if ("html".equals(dc.m_davcontext))
        {
            if (pagename.endsWith(".html"))
            {
                pagename = pagename.substring(0, pagename.length() - 5);
            }
        }

        WikiPage page = m_engine.getPage(pagename);
        WikiContext wc = new WikiContext(m_engine, page);
        wc.setRequestContext("dav");
        wc.setHttpRequest(req);

        return wc;
    }

    /**
     * DOCUMENT ME!
     *
     * @param req DOCUMENT ME!
     * @param res DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    public void execute(HttpServletRequest req, HttpServletResponse res)
            throws IOException
    {
        DavContext dc = new DavContext(req);

        WikiContext wc = createContext(req, dc);

        try
        {
            if ("".equals(dc.m_davcontext))
            {
                returnMainCollection(req, res);
            }
            else
            {
                if ("raw".equals(dc.m_davcontext))
                {
                    if (dc.m_page == null)
                    {
                        Collection pages = m_engine.getPageManager().getAllPages();

                        DavUtil.sendHTMLResponse(res, DavUtil.getCollectionInHTML(wc, pages));
                    }
                    else
                    {
                        if (wc.getPage() != null)
                        {
                            String content = m_engine.getPureText(wc.getPage());

                            res.setContentLength(content.length());
                            res.setContentType("text/plain; charset=\"UTF8\"");
                            res.getWriter().print(content);
                        }
                        else
                        {
                            res.sendError(HttpServletResponse.SC_NOT_FOUND);
                        }
                    }
                }
                else if ("html".equals(dc.m_davcontext))
                {
                    if (dc.m_page == null)
                    {
                        Collection pages = m_engine.getPageManager().getAllPages();
                        DavUtil.sendHTMLResponse(res, DavUtil.getCollectionInHTML(wc, pages));
                    }
                    else
                    {
                        if (wc.getPage() != null)
                        {
                            String result = m_engine.getHTML(wc, wc.getPage());

                            if (log.isDebugEnabled())
                            {
                                log.debug("RESULT=" + result);
                            }

                            res.setContentLength(result.length());
                            res.setContentType("text/html; charset=\"UTF-8\"");
                            res.getWriter().print(result);
                        }
                        else
                        {
                            res.sendError(HttpServletResponse.SC_NOT_FOUND);
                        }
                    }
                }
                else
                {
                    res.sendError(HttpServletResponse.SC_NOT_FOUND, "Could not find " + dc.m_davcontext);
                }
            }
        }
        catch (ProviderException e)
        {
            log.error("Caught Provider Exception:", e);
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
