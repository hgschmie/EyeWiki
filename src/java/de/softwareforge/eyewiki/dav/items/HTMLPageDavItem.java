package de.softwareforge.eyewiki.dav.items;


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
import java.util.Collection;

import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.WikiPage;

import org.jdom.Element;

/**
 * DOCUMENT ME!
 *
 * @author jalkanen
 *
 * @since
 */
public class HTMLPageDavItem
        extends PageDavItem
{
    /**
     * DOCUMENT ME!
     *
     * @param engine
     * @param page
     */
    public HTMLPageDavItem(WikiEngine engine, WikiPage page)
    {
        super(engine, page);

        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see de.softwareforge.eyewiki.dav.DavItem#getHref()
     */
    public String getHref()
    {
        return m_engine.getURL(WikiContext.NONE, "dav/html/" + m_page.getName() + ".html", null, true);
    }

    /* (non-Javadoc)
     * @see de.softwareforge.eyewiki.dav.DavItem#getPropertySet()
     */
    public Collection getPropertySet()
    {
        Collection set = getCommonProperties();

        //
        //  Rendering the page for every single time is not really a very good idea.
        //
        /*
         *        WikiContext ctx = new WikiContext(m_engine,m_page);
         *        ctx.setVariable( TranslatorReader.PROP_RUNPLUGINS, "false" );
         *        ctx.setVariable( WikiEngine.PROP_RUNFILTERS, "false" );
         *
         *        String txt = m_engine.getHTML( ctx, m_page );
         *        try
         *        {
         *            byte[] txtBytes = txt.getBytes("UTF-8");
         *            set.add( new Element("getcontentlength").setText( Long.toString(txt.length())) );
         *        }
         *        catch( UnsupportedEncodingException e ) {} // Never happens
         */
        set.add(new Element("getcontenttype", m_davns).setText("text/html; charset=\"UTF-8\""));

        return set;
    }
}
