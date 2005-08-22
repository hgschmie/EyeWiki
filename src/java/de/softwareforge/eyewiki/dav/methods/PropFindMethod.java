package de.softwareforge.eyewiki.dav.methods;

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

import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.dav.DavContext;
import de.softwareforge.eyewiki.dav.WebdavServlet;
import de.softwareforge.eyewiki.dav.items.DavItem;
import de.softwareforge.eyewiki.dav.items.DavItemFactory;
import de.softwareforge.eyewiki.providers.ProviderException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

/**
 * DOCUMENT ME!
 *
 * @author jalkanen
 *
 * @since
 */
public class PropFindMethod
        extends DavMethod
{
    /** DOCUMENT ME! */
    private static Logger log = Logger.getLogger(PropFindMethod.class);

    /** DOCUMENT ME! */
    private DavItemFactory m_difactory;

    /**
                 *
                 */
    public PropFindMethod(WikiEngine engine)
    {
        super(engine);
        m_difactory = new DavItemFactory(m_engine);
    }

    /**
     * DOCUMENT ME!
     *
     * @param res DOCUMENT ME!
     * @param response DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    public void sendMultiResponse(HttpServletResponse res, Element response)
            throws IOException
    {
        res.setContentType("application/xml; charset=\"UTF-8\"");
        res.setStatus(WebdavServlet.SC_MULTISTATUS);

        Document doc = new Document();

        doc.setRootElement(response);

        XMLOutputter output = new XMLOutputter();

        output.setFormat(Format.getPrettyFormat());
        output.output(doc, res.getWriter());
    }

    private Element getPropertyNames(DavContext dc)
    {
        log.debug("Retrieving all property names for context " + dc);

        Namespace davns = Namespace.getNamespace("DAV:");
        Element root = new Element("multistatus", davns);

        DavItem di = m_difactory.newItem(dc);

        for (Iterator i = di.iterator(dc.m_depth); i.hasNext();)
        {
            di = (DavItem) i.next();

            Element response = new Element("response", davns);

            response.addContent(new Element("href", davns).setText(di.getHref()));

            Element propstat = new Element("propstat", davns);

            //
            //  Wiki specifics.
            //
            Collection c = di.getPropertySet();

            Element prop = new Element("prop", davns);

            for (Iterator j = c.iterator(); j.hasNext();)
            {
                Element el = (Element) j.next();
                el.removeContent();
                prop.addContent(el);
            }

            propstat.addContent(prop);
            propstat.addContent(new Element("status", davns).setText("HTTP/1.1 200 OK"));

            response.addContent(propstat);

            root.addContent(response);
        }

        return root;
    }

    private Element getProperties(DavContext dc, List askedprops)
            throws IOException
    {
        Namespace davns = Namespace.getNamespace("DAV:");

        Element root = new Element("multistatus", davns);

        DavItem di = m_difactory.newItem(dc);

        if (di == null)
        {
            throw new FileNotFoundException(dc.m_page);
        }

        for (Iterator i = di.iterator(dc.m_depth); i.hasNext();)
        {
            di = (DavItem) i.next();

            Element response = new Element("response", davns);

            response.addContent(new Element("href", davns).setText(di.getHref()));

            Element props = new Element("prop", davns);
            Element failedprops = new Element("prop", davns);

            //
            //  Get the matching property set
            //
            Collection c = di.getPropertySet();

            if (askedprops == null)
            {
                for (Iterator j = c.iterator(); j.hasNext();)
                {
                    Element el = (Element) j.next();

                    props.addContent(el);
                }
            }
            else
            {
                for (Iterator x = askedprops.iterator(); x.hasNext();)
                {
                    Element askedElement = (Element) x.next();

                    boolean found = false;

                    for (Iterator j = c.iterator(); j.hasNext();)
                    {
                        Element el = (Element) j.next();

                        if (askedElement.getNamespaceURI().equals(el.getNamespaceURI())
                                        && askedElement.getName().equals(el.getName()))
                        {
                            props.addContent(el);
                            found = true;

                            break;
                        }
                    }

                    if (!found)
                    {
                        Element el = (Element) askedElement.clone();
                        failedprops.addContent(el);
                    }
                }
            }

            if (props.getContentSize() > 0)
            {
                Element ps = new Element("propstat", davns);
                ps.addContent(props);
                ps.addContent(new Element("status", davns).setText("HTTP/1.1 200 OK"));
                response.addContent(ps);
            }

            if (failedprops.getContentSize() > 0)
            {
                Element ps = new Element("propstat", davns);
                ps.addContent(failedprops);
                ps.addContent(new Element("status", davns).setText("HTTP/1.1 404 Not found"));
                response.addContent(ps);
            }

            root.addContent(response);
        }

        return root;
    }

    private Element getAllProps(DavContext dc)
            throws ProviderException, IOException
    {
        log.debug("Retrieving all properties for context " + dc.m_davcontext);

        return getProperties(dc, null);
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
        DavContext dc = new DavContext(req);

        try
        {
            Document doc = new SAXBuilder().build(req.getInputStream());

            XPath xpath = XPath.newInstance("/D:propfind/*");
            xpath.addNamespace("D", "DAV:");

            Element firstnode = (Element) xpath.selectSingleNode(doc);

            Element davresponse = null;

            log.debug("First node is:" + firstnode);

            if ((firstnode == null) || firstnode.getName().equals("allprop"))
            {
                davresponse = getAllProps(dc);
            }
            else if (firstnode.getName().equals("propname"))
            {
                davresponse = getPropertyNames(dc);
            }
            else if (firstnode.getName().equals("prop"))
            {
                XPath ndxp = XPath.newInstance("/D:propfind/D:prop/*");
                ndxp.addNamespace("D", "DAV:");

                List nodes = ndxp.selectNodes(doc);

                davresponse = getProperties(dc, nodes);
            }

            sendMultiResponse(res, davresponse);
        }
        catch (JDOMException e)
        {
            log.error("Broken XML received", e);

            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parse error");
        }
        catch (ProviderException e)
        {
            log.error("Provider failed", e);

            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
        catch (FileNotFoundException e)
        {
            res.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        }
    }
}
