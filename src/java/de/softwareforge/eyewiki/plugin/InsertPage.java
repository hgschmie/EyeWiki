package de.softwareforge.eyewiki.plugin;

import java.util.Map;


import de.softwareforge.eyewiki.WikiConstants;
import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.WikiPage;
import de.softwareforge.eyewiki.util.TextUtil;


/**
 * Inserts page contents.  Muchos thanks to Scott Hurlbert for the initial code.
 *
 * @author Scott Hurlbert
 * @author Janne Jalkanen
 *
 * @since 2.1.37
 */
public class InsertPage
        implements WikiPlugin
{
    /** DOCUMENT ME! */
    public static final String PARAM_PAGENAME = "page";

    /** DOCUMENT ME! */
    public static final String PARAM_STYLE = "style";

    /** DOCUMENT ME! */
    public static final String PARAM_MAXLENGTH = "maxlength";

    /** DOCUMENT ME! */
    public static final String PARAM_CLASS = "class";

    /** DOCUMENT ME! */
    public static final String PARAM_SECTION = "section";

    /** DOCUMENT ME! */
    public static final String PARAM_DEFAULT = "default";

    /** DOCUMENT ME! */
    private static final String DEFAULT_STYLE = "";

    private final WikiEngine engine;

    public InsertPage(final WikiEngine engine)
    {
        this.engine = engine;
    }

    /**
     * DOCUMENT ME!
     *
     * @param context DOCUMENT ME!
     * @param params DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws PluginException DOCUMENT ME!
     */
    public String execute(WikiContext context, Map params)
            throws PluginException
    {
        StringBuffer res = new StringBuffer();

        String clazz = (String) params.get(PARAM_CLASS);
        String includedPage = (String) params.get(PARAM_PAGENAME);
        String style = (String) params.get(PARAM_STYLE);
        String defaultstr = (String) params.get(PARAM_DEFAULT);
        int section = TextUtil.parseIntParameter((String) params.get(PARAM_SECTION), -1);
        int maxlen = TextUtil.parseIntParameter((String) params.get(PARAM_MAXLENGTH), -1);

        if (style == null)
        {
            style = DEFAULT_STYLE;
        }

        if (maxlen == -1)
        {
            maxlen = Integer.MAX_VALUE;
        }

        if (includedPage != null)
        {
            WikiPage page = engine.getPage(includedPage);

            if (page != null)
            {
                /*
                  // Disabled, because this seems to fail when used
                  // to insert something from a weblog entry.
                AuthorizationManager mgr = engine.getAuthorizationManager();
                UserProfile currentUser = context.getCurrentUser();

                if( !mgr.checkPermission( page,
                                          currentUser,
                                          new ViewPermission() ) )
                {
                    res.append("<span class=\"" + WikiConstants.CSS_CLASS_ERROR + "\">You do not have permission to view this included page.</span>");
                    return res.toString();
                }
                */

                /**
                 * We want inclusion to occur within the context of its own page, because we need
                 * the links to be correct.
                 */
                WikiContext includedContext = (WikiContext) context.clone();
                includedContext.setPage(page);

                String pageData = engine.getPureText(page);
                String moreLink = "";

                if (section != -1)
                {
                    try
                    {
                        pageData = TextUtil.getSection(pageData, section);
                    }
                    catch (IllegalArgumentException e)
                    {
                        throw new PluginException(e.getMessage());
                    }
                }

                if (pageData.length() > maxlen)
                {
                    pageData = pageData.substring(0, maxlen) + " ...";
                    moreLink =
                        "<p><a href=\"" + context.getURL(WikiContext.VIEW, includedPage)
                        + "\">More...</a></p>";
                }

                res.append("<div ");

                if (style != null)
                {
                    res.append("style=\"").append(style).append("\"");
                }

                if (clazz != null)
                {
                    res.append(" class=\"").append(clazz).append("\"");
                }

                res.append(">")
                        .append(engine.textToHTML(includedContext, pageData))
                        .append(moreLink)
                        .append("</div>");
            }
            else
            {
                if (defaultstr != null)
                {
                    res.append(defaultstr);
                }
                else
                {
                    res.append(
                        "There is no page called '" + includedPage + "'.  Would you like to ");
                    res.append(
                        "<a href=\"" + context.getURL(WikiContext.EDIT, includedPage)
                        + "\">create it?</a>");
                }
            }
        }
        else
        {
            res.append("<span class=\"" + WikiConstants.CSS_CLASS_ERROR + "\">");
            res.append("You have to define a page!");
            res.append("</span>");
        }

        return res.toString();
    }
}
