package de.softwareforge.eyewiki.htmltowiki;


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
import de.softwareforge.eyewiki.WikiContext;

/**
 * Config class for XHtmlToWikiTranslator
 *
 * @author <a href="mailto:sbaltes@gmx.com">Sebastian Baltes</a>
 */
public class XHtmlToWikiConfig
{
    /** DOCUMENT ME! */
    private String outlink = "outlink";

    /** DOCUMENT ME! */
    private String pageInfoJsp = "PageInfo.jsp";

    /** DOCUMENT ME! */
    private String wikiJspPage = "Wiki.jsp?page=";

    /** DOCUMENT ME! */
    private String attachPage = "attach?page=";

    /** DOCUMENT ME! */
    private String pageName;

    /**
     * Creates a new XHtmlToWikiConfig object.
     */
    public XHtmlToWikiConfig()
    {
    }

    /**
     * Creates a new XHtmlToWikiConfig object.
     *
     * @param wikiContext DOCUMENT ME!
     */
    public XHtmlToWikiConfig(WikiContext wikiContext)
    {
        setWikiContext(wikiContext);
    }

    private void setWikiContext(WikiContext wikiContext)
    {
        if (wikiContext.getPage() != null)
        {
            setPageName(wikiContext.getPage().getName() + "/");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getAttachPage()
    {
        return attachPage;
    }

    /**
     * DOCUMENT ME!
     *
     * @param attachPage DOCUMENT ME!
     */
    public void setAttachPage(String attachPage)
    {
        this.attachPage = attachPage;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getOutlink()
    {
        return outlink;
    }

    /**
     * DOCUMENT ME!
     *
     * @param outlink DOCUMENT ME!
     */
    public void setOutlink(String outlink)
    {
        this.outlink = outlink;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getPageInfoJsp()
    {
        return pageInfoJsp;
    }

    /**
     * DOCUMENT ME!
     *
     * @param pageInfoJsp DOCUMENT ME!
     */
    public void setPageInfoJsp(String pageInfoJsp)
    {
        this.pageInfoJsp = pageInfoJsp;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getPageName()
    {
        return pageName;
    }

    /**
     * DOCUMENT ME!
     *
     * @param pageName DOCUMENT ME!
     */
    public void setPageName(String pageName)
    {
        this.pageName = pageName;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getWikiJspPage()
    {
        return wikiJspPage;
    }

    /**
     * DOCUMENT ME!
     *
     * @param wikiJspPage DOCUMENT ME!
     */
    public void setWikiJspPage(String wikiJspPage)
    {
        this.wikiJspPage = wikiJspPage;
    }
}
