package de.softwareforge.eyewiki.plugin;

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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import java.util.Map;
import java.util.Properties;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.commons.lang.BooleanUtils;
import org.apache.log4j.Logger;

import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.WikiPage;
import de.softwareforge.eyewiki.attachment.Attachment;
import de.softwareforge.eyewiki.attachment.AttachmentManager;
import de.softwareforge.eyewiki.providers.ProviderException;

/**
 * Implements a simple voting system.  WARNING: The storage method is still experimental; I will probably change it at some point.
 */
public class VotePlugin
        implements WikiPlugin
{
    /** DOCUMENT ME! */
    private static final Logger log = Logger.getLogger(VotePlugin.class);

    /** DOCUMENT ME! */
    public static final String ATTACHMENT_NAME = "VotePlugin.properties";

    /** DOCUMENT ME! */
    public static final String VAR_VOTES = "VotePlugin.votes";

    /** DOCUMENT ME! */
    protected final WikiEngine engine;

    /** DOCUMENT ME! */
    protected final AttachmentManager attachmentManager;

    /**
     * Creates a new VotePlugin object.
     *
     * @param engine DOCUMENT ME!
     * @param attachmentManager DOCUMENT ME!
     */
    public VotePlugin(final WikiEngine engine, final AttachmentManager attachmentManager)
    {
        this.engine = engine;
        this.attachmentManager = attachmentManager;
    }

    /**
     * +1 for yes, -1 for no.
     *
     * @param context DOCUMENT ME!
     * @param vote DOCUMENT ME!
     *
     * @return number of votes, or -1 if an error occurred.
     */
    public int vote(WikiContext context, int vote)
    {
        if (vote > 0)
        {
            int nVotes = getYesVotes(context);

            putVotes(context, "yes", ++nVotes);

            return nVotes;
        }
        else if (vote < 0)
        {
            int nVotes = getNoVotes(context);

            putVotes(context, "no", ++nVotes);

            return nVotes;
        }

        return -1; // Error
    }

    private void putVotes(WikiContext context, String yesno, int nVotes)
    {
        WikiPage page = context.getPage();

        Properties props = getVotes(context);

        props.setProperty(yesno, Integer.toString(nVotes));

        page.setAttribute(VAR_VOTES, props);

        storeAttachment(context, props);
    }

    private void storeAttachment(WikiContext context, Properties props)
    {
        try
        {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            props.store(out, "eyeWiki Votes plugin stores its votes here.  Don't modify!");

            out.close();

            Attachment att = findAttachment(context);

            InputStream in = new ByteArrayInputStream(out.toByteArray());

            attachmentManager.storeAttachment(att, in);

            in.close();
        }
        catch (Exception ex)
        {
            log.error("Unable to write properties", ex);
        }
    }

    private Attachment findAttachment(WikiContext context)
            throws ProviderException
    {
        Attachment att = attachmentManager.getAttachmentInfo(context, ATTACHMENT_NAME);

        if (att == null)
        {
            att = new Attachment(context.getPage().getName(), ATTACHMENT_NAME);
        }

        return att;
    }

    private Properties getVotes(WikiContext context)
    {
        WikiPage page = context.getPage();

        Properties props = (Properties) page.getAttribute(VAR_VOTES);

        //
        //  Not loaded yet
        //
        if (props == null)
        {
            props = new Properties();

            try
            {
                Attachment att = attachmentManager.getAttachmentInfo(context, ATTACHMENT_NAME);

                if (att != null)
                {
                    props.load(attachmentManager.getAttachmentStream(att));
                }
            }
            catch (Exception e)
            {
                log.error("Unable to load attachment ", e);
            }
        }

        return props;
    }

    private int getYesVotes(WikiContext context)
    {
        Configuration props = ConfigurationConverter.getConfiguration(getVotes(context));

        return props.getInt("yes", 0);
    }

    private int getNoVotes(WikiContext context)
    {
        Configuration props = ConfigurationConverter.getConfiguration(getVotes(context));

        return props.getInt("no", 0);
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
        String posneg = (String) params.get("value");

        if (BooleanUtils.toBoolean(posneg))
        {
            return Integer.toString(getYesVotes(context));
        }
        else
        {
            return Integer.toString(getNoVotes(context));
        }
    }
}
