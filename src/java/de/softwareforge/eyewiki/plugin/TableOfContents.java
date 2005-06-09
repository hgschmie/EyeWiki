package de.softwareforge.eyewiki.plugin;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

import org.apache.log4j.Logger;


import de.softwareforge.eyewiki.HeadingListener;
import de.softwareforge.eyewiki.TranslatorReader;
import de.softwareforge.eyewiki.WikiConstants;
import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.WikiPage;
import de.softwareforge.eyewiki.exception.InternalWikiException;
import de.softwareforge.eyewiki.util.FileUtil;
import de.softwareforge.eyewiki.util.TextUtil;


/**
 * Provides a table of contents.
 *
 * @author Janne Jalkanen
 *
 * @since 2.2
 */
public class TableOfContents
        implements WikiPlugin
{
    /** DOCUMENT ME! */
    private static Logger log = Logger.getLogger(TableOfContents.class);

    /** DOCUMENT ME! */
    public static final String PARAM_TITLE = "title";

    private final WikiEngine engine;

    public TableOfContents(final WikiEngine engine)
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
        WikiPage page = context.getPage();

        StringBuffer sb = new StringBuffer();

        sb.append("<div class=\"" + WikiConstants.CSS_TOC + "\">\n");

        String title = (String) params.get(PARAM_TITLE);

        if (title != null)
        {
            sb.append("<h1 class=\"" + WikiConstants.CSS_TOC + "\">" + TextUtil.replaceEntities(title) + "</h1>\n");
        }
        else
        {
            sb.append("<h1 class=\"" + WikiConstants.CSS_TOC + "\">Table of Contents</h1>\n");
        }

        try
        {
            String wikiText = engine.getPureText(page);
            ToCListener listener = new ToCListener();

            TranslatorReader in = new TranslatorReader(context, new StringReader(wikiText));
            in.enablePlugins(false);
            in.addHeadingListener(listener);

            FileUtil.readContents(in);

            in.close();

            in = new TranslatorReader(context, new StringReader(listener.getResult()));
            sb.append(FileUtil.readContents(in));

            in.close();
        }
        catch (IOException e)
        {
            log.error("Could not construct table of contents", e);
            throw new PluginException("Unable to construct table of contents (see logs)");
        }

        sb.append("</div>\n");

        return sb.toString();
    }

    public static class ToCListener
            implements HeadingListener
    {
        /** DOCUMENT ME! */
        private StringBuffer m_buf = new StringBuffer();

        /**
         * DOCUMENT ME!
         *
         * @param context DOCUMENT ME!
         * @param hd DOCUMENT ME!
         *
         * @throws InternalWikiException DOCUMENT ME!
         */
        public void headingAdded(WikiContext context, TranslatorReader.Heading hd)
        {
            if (log.isDebugEnabled())
            {
                log.debug("HD: " + hd.getLevel() + ", " + hd.getTitleText() + ", " + hd.getTitleAnchor());
            }

            switch (hd.getLevel())
            {
            case TranslatorReader.Heading.HEADING_SMALL:
                m_buf.append("***");

                break;

            case TranslatorReader.Heading.HEADING_MEDIUM:
                m_buf.append("**");

                break;

            case TranslatorReader.Heading.HEADING_LARGE:
                m_buf.append("*");

                break;

            default:
                throw new InternalWikiException("Unknown depth in toc! (Please submit a bug report.)");
            }

            m_buf.append(
                    " [" + hd.getTitleText() + "|" + context.getPage().getName() + "#" + hd.getTitleSection()
                    + "]\n");
        }

        public String getResult()
        {
            return m_buf.toString();
        }
    }
}
