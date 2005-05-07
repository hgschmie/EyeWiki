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
public abstract class DavMethod
{
    /**
     * DOCUMENT ME!
     */
    protected WikiEngine m_engine;

    /**
         *
         */
    public DavMethod(WikiEngine engine)
    {
        m_engine = engine;
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
    public abstract void execute(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException;
}
