/* 
    JSPWiki - a JSP-based WikiWiki clone.

    Copyright (C) 2001-2005 Janne Jalkanen (Janne.Jalkanen@iki.fi)

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

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;

/**
 */
public class WikiServlet
    extends HttpServlet
{
    private WikiEngine m_engine = null;

    Logger log = Logger.getLogger(this.getClass().getName());

    public void init( ServletConfig config )
        throws ServletException 
    {
        super.init( config );

        m_engine         = WikiEngine.getInstance( config );
        Configuration conf = m_engine.getWikiConfiguration();

        log.info("WikiServlet initialized.");
    }

    public void doPost( HttpServletRequest req, HttpServletResponse res )
        throws IOException, ServletException
    {
        doGet( req, res );
    }
    
    public void doGet( HttpServletRequest req, HttpServletResponse res ) 
        throws IOException, ServletException 
    {
        String pageName = DefaultURLConstructor.parsePageFromURL(
                req,
                m_engine.getContentEncoding() );

        log.info("Request for page: "+pageName);

        if( pageName == null)
        {
            pageName = m_engine.getFrontPage(); // FIXME: Add special pages as well
        }
        
        String jspPage = req.getParameter( "do" );

        if( jspPage == null )
        {
            jspPage = "Wiki";
        }

        StringBuffer sb = new StringBuffer("/")
                .append(jspPage)
                .append(".jsp?page=")
                .append(m_engine.encodeName(pageName))
                .append("&")
                .append(req.getQueryString());
        
        RequestDispatcher dispatcher = req.getRequestDispatcher(sb.toString());

        dispatcher.forward( req, res );
    }
}


