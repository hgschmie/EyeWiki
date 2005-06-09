/*
 * (C) Janne Jalkanen 2005
 *
 */
package de.softwareforge.eyewiki.dav.items;

import java.util.Collection;

import org.jdom.Element;

import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.WikiPage;


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
        return m_engine.getURL(
            WikiContext.NONE, "dav/html/" + m_page.getName() + ".html", null, true);
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
        WikiContext ctx = new WikiContext(m_engine,m_page);
        ctx.setVariable( TranslatorReader.PROP_RUNPLUGINS, "false" );
        ctx.setVariable( WikiEngine.PROP_RUNFILTERS, "false" );

        String txt = m_engine.getHTML( ctx, m_page );
        try
        {
            byte[] txtBytes = txt.getBytes("UTF-8");
            set.add( new Element("getcontentlength").setText( Long.toString(txt.length())) );
        }
        catch( UnsupportedEncodingException e ) {} // Never happens
        */
        set.add(new Element("getcontenttype", m_davns).setText("text/html; charset=\"UTF-8\""));

        return set;
    }
}
