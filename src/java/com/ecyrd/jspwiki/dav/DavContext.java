/*
 * (C) Janne Jalkanen 2005
 *
 */
package com.ecyrd.jspwiki.dav;

import javax.servlet.http.HttpServletRequest;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class DavContext
{
    /**
     * DOCUMENT ME!
     */
    public String m_davcontext = "";

    /**
     * DOCUMENT ME!
     */
    public String m_page = null;

    /**
     * DOCUMENT ME!
     */
    public int m_depth = -1;

    /**
     * Creates a new DavContext object.
     *
     * @param req DOCUMENT ME!
     */
    public DavContext(HttpServletRequest req)
    {
        String path = req.getPathInfo();

        if (path != null)
        {
            if (path.startsWith("/"))
            {
                path = path.substring(1);
            }

            int idx = path.indexOf('/');

            if (idx != -1)
            {
                m_davcontext = path.substring(0, idx);
                m_page = path.substring(idx + 1);
            }
            else
            {
                m_davcontext = path;
            }
        }
        
        String depth = req.getHeader("Depth");
        m_depth = (depth == null) ? -1 : Integer.parseInt(depth);
    }
}
