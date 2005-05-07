/*
 * (C) Janne Jalkanen 2005
 *
 */
package com.ecyrd.jspwiki.dav.methods;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ecyrd.jspwiki.WikiEngine;


/**
 * DOCUMENT ME!
 *
 * @author jalkanen
 *
 * @since
 */
public class PropPatchMethod
        extends DavMethod
{
    /**
     * DOCUMENT ME!
     *
     * @param engine
     */
    public PropPatchMethod(WikiEngine engine)
    {
        super(engine);
    }

    /**
     * DOCUMENT ME!
     *
     * @param req DOCUMENT ME!
     * @param res DOCUMENT ME!
     *
     * @throws ServletException DOCUMENT ME!
     * @throws IOException DOCUMENT ME!
     */
    public void execute(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException
    {
        res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JSPWiki is read-only");
    }
}
