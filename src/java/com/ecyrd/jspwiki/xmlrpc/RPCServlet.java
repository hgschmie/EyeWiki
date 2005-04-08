/*
   JSPWiki - a JSP-based WikiWiki clone.

   Copyright (C) 2001-2002 Janne Jalkanen (Janne.Jalkanen@iki.fi)

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
package com.ecyrd.jspwiki.xmlrpc;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.xmlrpc.XmlRpcServer;

import com.ecyrd.jspwiki.WikiEngine;


/**
 * Handles all incoming servlet requests for XML-RPC calls.
 *
 * <P>
 * Uses two initialization parameters:
 *
 * <UL>
 * <li>
 * <B>handler</B> : the class which is used to handle the RPC calls.
 * </li>
 * <li>
 * <B>prefix</B> : The command prefix for that particular handler.
 * </li>
 * </ul>
 * </p>
 *
 * @author Janne Jalkanen
 *
 * @since 1.6.6
 */
public class RPCServlet
        extends HttpServlet
{
    /** This is what is appended to each command, if the handler has not been specified. */

    // FIXME: Should this be $default?
    public static final String XMLRPC_PREFIX = "wiki";

    /** DOCUMENT ME! */
    private WikiEngine m_engine;

    /** DOCUMENT ME! */
    private XmlRpcServer m_xmlrpcServer = new XmlRpcServer();

    /** DOCUMENT ME! */
    Logger log = Logger.getLogger(RPCServlet.class);

    /**
     * DOCUMENT ME!
     *
     * @param prefix DOCUMENT ME!
     * @param handlerName DOCUMENT ME!
     *
     * @throws ClassNotFoundException DOCUMENT ME!
     * @throws InstantiationException DOCUMENT ME!
     * @throws IllegalAccessException DOCUMENT ME!
     */
    public void initHandler(String prefix, String handlerName)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        Class handlerClass = Class.forName(handlerName);
        WikiRPCHandler rpchandler = (WikiRPCHandler) handlerClass.newInstance();
        rpchandler.initialize(m_engine);
        m_xmlrpcServer.addHandler(prefix, rpchandler);
    }

    /**
     * Initializes the servlet.
     *
     * @param config DOCUMENT ME!
     *
     * @throws ServletException DOCUMENT ME!
     */
    public void init(ServletConfig config)
            throws ServletException
    {
        m_engine = WikiEngine.getInstance(config);

        String handlerName = config.getInitParameter("handler");
        String prefix = config.getInitParameter("prefix");

        if (handlerName == null)
        {
            handlerName = RPCHandler.class.getName();
        }

        if (prefix == null)
        {
            prefix = XMLRPC_PREFIX;
        }

        try
        {
            initHandler(prefix, handlerName);

            //
            // FIXME: The metaweblog API should be possible to turn off.
            //
            initHandler("metaWeblog", MetaWeblogHandler.class.getName());
        }
        catch (Exception e)
        {
            log.fatal("Unable to start RPC interface: ", e);
            throw new ServletException("No RPC interface", e);
        }
    }

    /**
     * Handle HTTP POST.  This is an XML-RPC call, and we'll just forward the query to an
     * XmlRpcServer.
     *
     * @param request DOCUMENT ME!
     * @param response DOCUMENT ME!
     *
     * @throws ServletException DOCUMENT ME!
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException
    {
        log.debug("Received POST to RPCServlet");

        try
        {
            byte [] result = m_xmlrpcServer.execute(request.getInputStream());

            //
            //  I think it's safe to write the output as UTF-8:
            //  The XML-RPC standard never creates other than USASCII
            //  (which is UTF-8 compatible), and our special UTF-8
            //  hack just creates UTF-8.  So in all cases our butt
            //  should be covered.
            //
            response.setContentType("text/xml; charset=utf-8");
            response.setContentLength(result.length);

            OutputStream out = response.getOutputStream();
            out.write(result);
            out.flush();

            // log.debug("Result = "+new String(result) );
        }
        catch (IOException e)
        {
            throw new ServletException("Failed to build RPC result", e);
        }
    }

    /**
     * Handles HTTP GET.  However, we do not respond to GET requests, other than to show an
     * explanatory text.
     *
     * @param request DOCUMENT ME!
     * @param response DOCUMENT ME!
     *
     * @throws ServletException DOCUMENT ME!
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException
    {
        log.debug("Received HTTP GET to RPCServlet");

        try
        {
            String msg = "We do not support HTTP GET here.  Sorry.";
            response.setContentType("text/plain");
            response.setContentLength(msg.length());

            PrintWriter writer =
                new PrintWriter(new OutputStreamWriter(response.getOutputStream()));

            writer.println(msg);
            writer.flush();
        }
        catch (IOException e)
        {
            throw new ServletException("Failed to build RPC result", e);
        }
    }
}
