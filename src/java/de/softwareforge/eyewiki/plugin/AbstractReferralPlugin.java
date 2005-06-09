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
package de.softwareforge.eyewiki.plugin;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;


import de.softwareforge.eyewiki.StringTransmutator;
import de.softwareforge.eyewiki.TranslatorReader;
import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.util.FileUtil;
import de.softwareforge.eyewiki.util.TextUtil;


/**
 * This is a base class for all plugins using referral things.
 *
 * <p>
 * Parameters:<br> maxwidth: maximum width of generated links<br>
 * separator: separator between generated links (wikitext)<br>
 * after: output after the link before: output before the link
 * </p>
 *
 * @author Janne Jalkanen
 */
public abstract class AbstractReferralPlugin
        implements WikiPlugin
{
    /** DOCUMENT ME! */
    private static Logger log = Logger.getLogger(AbstractReferralPlugin.class);

    /** DOCUMENT ME! */
    public static final int ALL_ITEMS = -1;

    /** DOCUMENT ME! */
    public static final String PARAM_MAXWIDTH = "maxwidth";

    /** DOCUMENT ME! */
    public static final String PARAM_SEPARATOR = "separator";

    /** DOCUMENT ME! */
    public static final String PARAM_AFTER = "after";

    /** DOCUMENT ME! */
    public static final String PARAM_BEFORE = "before";

    /** DOCUMENT ME! */
    protected int m_maxwidth = Integer.MAX_VALUE;

    /** DOCUMENT ME! */
    protected String m_before = ""; // null not blank

    /** DOCUMENT ME! */
    protected String m_separator = ""; // null not blank

    /** DOCUMENT ME! */
    protected String m_after = "\\\\";

    /** DOCUMENT ME! */
    protected final WikiEngine engine;

    public AbstractReferralPlugin(WikiEngine engine)
    {
        this.engine = engine;
    }

    /**
     * Used to initialize some things.  All plugins must call this first.
     *
     * @param context DOCUMENT ME!
     * @param params DOCUMENT ME!
     *
     * @throws PluginException DOCUMENT ME!
     *
     * @since 1.6.4
     */
    public void initialize(WikiContext context, Map params)
            throws PluginException
    {
        m_maxwidth =
            TextUtil.parseIntParameter((String) params.get(PARAM_MAXWIDTH), Integer.MAX_VALUE);

        if (m_maxwidth < 0)
        {
            m_maxwidth = 0;
        }

        String s = (String) params.get(PARAM_SEPARATOR);

        if (s != null)
        {
            m_separator = s;

            // pre-2.1.145 there was a separator at the end of the list
            // if they set the parameters, we use the new format of
            // before Item1 after separator before Item2 after separator before Item3 after
            m_after = "";
        }

        s = (String) params.get(PARAM_BEFORE);

        if (s != null)
        {
            m_before = s;
        }

        s = (String) params.get(PARAM_AFTER);

        if (s != null)
        {
            m_after = s;
        }

        // log.debug( "Requested maximum width is "+m_maxwidth );
    }

    /**
     * Makes WikiText from a Collection.
     *
     * @param links Collection to make into WikiText.
     * @param separator Separator string to use.
     * @param numItems How many items to show.
     *
     * @return DOCUMENT ME!
     */
    protected String wikitizeCollection(Collection links, String separator, int numItems)
    {
        if ((links == null) || links.isEmpty())
        {
            return ("");
        }

        StringBuffer output = new StringBuffer();

        Iterator it = links.iterator();
        int count = 0;

        //
        //  The output will be B Item[1] A S B Item[2] A S B Item[3] A
        //
        while (it.hasNext() && ((count < numItems) || (numItems == ALL_ITEMS)))
        {
            String value = (String) it.next();

            if (count > 0)
            {
                output.append(m_after);
                output.append(m_separator);
            }

            output.append(m_before);

            // Make a Wiki markup link. See TranslatorReader.
            output.append("[" + engine.beautifyTitle(value) + "]");
            count++;
        }

        //
        //  Output final item - if there have been none, no "after" is printed
        //
        if (count > 0)
        {
            output.append(m_after);
        }

        return (output.toString());
    }

    /**
     * Makes HTML with common parameters.
     *
     * @param context DOCUMENT ME!
     * @param wikitext DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @since 1.6.4
     */
    protected String makeHTML(WikiContext context, String wikitext)
    {
        String result = "";
        TranslatorReader in = null;

        try
        {
            in = new TranslatorReader(context, new StringReader(wikitext));
            in.addLinkTransmutator(new CutMutator(m_maxwidth));

            in.enableImageInlining(false);

            result = FileUtil.readContents(in);
        }
        catch (IOException e)
        {
            log.error("Failed to convert page data to HTML", e);
        }
        finally
        {
            IOUtils.closeQuietly(in);
        }

        return result;
    }

    /**
     * A simple class that just cuts a String to a maximum length, adding three dots after the
     * cutpoint.
     */
    private static class CutMutator
            implements StringTransmutator
    {
        /** DOCUMENT ME! */
        private int m_length;

        /**
         * Creates a new CutMutator object.
         *
         * @param length DOCUMENT ME!
         */
        public CutMutator(int length)
        {
            m_length = length;
        }

        /**
         * DOCUMENT ME!
         *
         * @param context DOCUMENT ME!
         * @param text DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        public String mutate(WikiContext context, String text)
        {
            if (text.length() > m_length)
            {
                return text.substring(0, m_length) + "...";
            }

            return text;
        }
    }
}
