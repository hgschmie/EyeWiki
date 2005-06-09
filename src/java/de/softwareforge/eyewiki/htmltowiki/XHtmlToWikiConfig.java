package de.softwareforge.eyewiki.htmltowiki;

import de.softwareforge.eyewiki.WikiContext;


/**
 * Config class for XHtmlToWikiTranslator
 *
 * @author <a href="mailto:sbaltes@gmx.com">Sebastian Baltes</a>
 */
public class XHtmlToWikiConfig
{
    /**
     * DOCUMENT ME!
     */
    private String outlink = "outlink";

    /**
     * DOCUMENT ME!
     */
    private String pageInfoJsp = "PageInfo.jsp";

    /**
     * DOCUMENT ME!
     */
    private String wikiJspPage = "Wiki.jsp?page=";

    /**
     * DOCUMENT ME!
     */
    private String attachPage = "attach?page=";

    /**
     * DOCUMENT ME!
     */
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
