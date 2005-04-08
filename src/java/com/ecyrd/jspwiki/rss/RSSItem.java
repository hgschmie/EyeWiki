package com.ecyrd.jspwiki.rss;

import com.ecyrd.jspwiki.WikiContext;
import com.ecyrd.jspwiki.WikiEngine;
import com.ecyrd.jspwiki.WikiPage;
import com.ecyrd.jspwiki.attachment.Attachment;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class RSSItem
{
    /** DOCUMENT ME! */
    private WikiPage m_page;

    /**
     * Creates a new RSSItem object.
     *
     * @param page DOCUMENT ME!
     */
    public RSSItem(WikiPage page)
    {
        m_page = page;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getName()
    {
        return m_page.getName();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public WikiPage getPage()
    {
        return m_page;
    }

    /**
     * DOCUMENT ME!
     *
     * @param context DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getURL(WikiContext context)
    {
        WikiEngine engine = context.getEngine();
        String url;

        if (m_page instanceof Attachment)
        {
            url = engine.getURL(WikiContext.ATTACH, m_page.getName(), null, false);
        }
        else
        {
            url = engine.getURL(WikiContext.VIEW, m_page.getName(), null, false);
        }

        return url;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getTitle()
    {
        return m_page.getName();
    }

    private String getAttachmentDescription(Attachment att)
    {
        /*
        String author = getAuthor(att);

        if( att.getVersion() != 1 )
        {
            return (author+" uploaded a new version of this attachment on "+att.getLastModified() );
        }
        else
        {
            return (author+" created this attachment on "+att.getLastModified() );
        }
        */
        return "";
    }

    private String getPageDescription(WikiPage page)
    {
        StringBuffer buf = new StringBuffer();

        /*
        String author = getAuthor(page);

        if( page.getVersion() > 1 )
        {
            String diff = m_engine.getDiff( page.getName(),
                                            page.getVersion()-1,
                                            page.getVersion() );

            buf.append(author+" changed this page on "+page.getLastModified()+":<br /><hr /><br />" );
            buf.append(diff);
        }
        else
        {
            buf.append(author+" created this page on "+page.getLastModified()+":<br /><hr /><br />" );
            buf.append(m_engine.getHTML( page.getName() ));
        }
        */
        return buf.toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getDescription()
    {
        String res;

        if (m_page instanceof Attachment)
        {
            res = getAttachmentDescription((Attachment) m_page);
        }
        else
        {
            res = getPageDescription(m_page);
        }

        return res;
    }
}
