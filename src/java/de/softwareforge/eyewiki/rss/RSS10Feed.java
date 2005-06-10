package de.softwareforge.eyewiki.rss;

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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;

import org.apache.ecs.xml.XML;

import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.WikiPage;

/**
 *  @author jalkanen
 *
 *  @since 
 */
public class RSS10Feed extends Feed
{
    public RSS10Feed(WikiContext context)
    {
        super(context);
    }
    
    private XML getRDFItems()
    {
        XML items = new XML("items");
        
        XML rdfseq = new XML("rdf:Seq");
        
        for(Iterator i = m_entries.iterator(); i.hasNext();)
        {
            Entry e = (Entry)i.next();

            String url = e.getURL();

            rdfseq.addElement(new XML("rdf:li").addAttribute("rdf:resource", url));
        }
        
        items.addElement(rdfseq);
        
        return items;
    }
    
    private void addItemList(XML root)
    {
        SimpleDateFormat iso8601fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        
        WikiEngine engine = m_wikiContext.getEngine();
        
        for(Iterator i = m_entries.iterator(); i.hasNext();)
        {
            Entry e = (Entry)i.next();
            
            String url = e.getURL();
            
            XML item = new XML("item");
            item.addAttribute("rdf:about", url);
            
            item.addElement(new XML("title").addElement(format(e.getTitle())));

            item.addElement(new XML("link").addElement(url));

            XML content = new XML("description");
            
            // TODO: Add a size limiter here
            content.addElement(format(e.getContent()));

            item.addElement(content);

            WikiPage p = e.getPage();
            
            if(p.getVersion() != -1)
            {
                item.addElement(new XML("wiki:version").addElement(Integer.toString(p.getVersion())));
            }

            if(p.getVersion() > 1)
            {
                item.addElement(new XML("wiki:diff").addElement(engine.getURL(WikiContext.DIFF,
                                                                                 p.getName(),
                                                                                 "r1=-1",
                                                                                 true)));
            }

           
            //
            //  Modification date.
            //
            Calendar cal = Calendar.getInstance();
            cal.setTime(p.getLastModified());
            cal.add(Calendar.MILLISECOND, 
                     - (cal.get(Calendar.ZONE_OFFSET) + 
                        (cal.getTimeZone().inDaylightTime(p.getLastModified()) ? cal.get(Calendar.DST_OFFSET) : 0)));

            item.addElement(new XML("dc:date").addElement(iso8601fmt.format(cal.getTime())));
           
            //
            //  Author
            String author = e.getAuthor();
            if(author == null) author = "unknown";

            XML contributor = new XML("dc:creator");
            
            item.addElement(contributor);
            
            XML description = new XML("rdf:Description");
            if(m_wikiContext.getEngine().pageExists(author))
            {
                description.addAttribute("link", engine.getURL(WikiContext.VIEW, 
                                                                 author,
                                                                 null,
                                                                 true));
            }
            
            description.addElement(new XML("value").addElement(format(author)));
            contributor.addElement(description);
           
            //  PageHistory

            item.addElement(new XML("wiki:history").addElement(engine.getURL(WikiContext.INFO,
                                                                                p.getName(),
                                                                                null,
                                                                                true)));
            
            //
            //  Add to root
            //
            
            root.addElement(item);
        }
    }
    
    private XML getChannelElement()
    {
        XML channel = new XML("channel");

        channel.addAttribute("rdf:about", m_feedURL);
        
        channel.addElement(new XML("link").addElement(m_feedURL));
        
        if(m_channelTitle != null)
            channel.addElement(new XML("title").addElement(format(m_channelTitle)));
        
        if(m_channelDescription != null)
            channel.addElement(new XML("description").addElement(format(m_channelDescription)));
        
        if(m_channelLanguage != null)
            channel.addElement(new XML("dc:language").addElement(m_channelLanguage));

        channel.setPrettyPrint(true);
        
        channel.addElement(getRDFItems());
        
        return channel;
    }
    
    /* (non-Javadoc)
     * @see de.softwareforge.eyewiki.rss.Feed#getString()
     */
    public String getString()
    {
        XML root = new XML("rdf:RDF");
        
        root.addAttribute("xmlns:rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        root.addAttribute("xmlns", "http://purl.org/rss/1.0/");
        root.addAttribute("xmlns:dc", "http://purl.org/dc/elements/1.1/");
        root.addAttribute("xmlns:wiki", "http://purl.org/rss/1.0/modules/wiki/");
        
        root.addElement(getChannelElement());
        
        addItemList(root);
        
        root.setPrettyPrint(true);
                
        return root.toString();
    }

}
