package de.softwareforge.eyewiki.tags;

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

import java.io.IOException;

import java.util.Collection;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import org.apache.log4j.Logger;

import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.WikiPage;
import de.softwareforge.eyewiki.attachment.Attachment;
import de.softwareforge.eyewiki.attachment.AttachmentManager;
import de.softwareforge.eyewiki.providers.ProviderException;

/**
 * Iterates through the list of attachments one has.
 *
 * <P>
 * <B>Attributes</B>
 * </p>
 *
 * <UL>
 * <li>
 * page - Page name to refer to.  Default is the current page.
 * </li>
 * </ul>
 *
 *
 * @author Janne Jalkanen
 *
 * @since 2.0
 */

// FIXME: Too much in common with IteratorTag - REFACTOR
public class AttachmentsIteratorTag
        extends IteratorTag
{
    /** DOCUMENT ME! */
    private static final Logger log = Logger.getLogger(AttachmentsIteratorTag.class);

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public final int doStartTag()
    {
        m_wikiContext = (WikiContext) pageContext.getAttribute(WikiTagBase.ATTR_CONTEXT, PageContext.REQUEST_SCOPE);

        WikiEngine engine = m_wikiContext.getEngine();
        AttachmentManager mgr = engine.getAttachmentManager();
        WikiPage page;

        page = m_wikiContext.getPage();

        if (!mgr.attachmentsEnabled())
        {
            return SKIP_BODY;
        }

        try
        {
            if ((page != null) && engine.pageExists(page))
            {
                Collection atts = mgr.listAttachments(page);

                if (atts == null)
                {
                    log.debug("No attachments to display.");

                    // There are no attachments included
                    return SKIP_BODY;
                }

                m_iterator = atts.iterator();

                if (m_iterator.hasNext())
                {
                    Attachment att = (Attachment) m_iterator.next();

                    WikiContext context = (WikiContext) m_wikiContext.clone();
                    context.setPage(att);
                    pageContext.setAttribute(WikiTagBase.ATTR_CONTEXT, context, PageContext.REQUEST_SCOPE);

                    pageContext.setAttribute(getId(), att);
                }
                else
                {
                    return SKIP_BODY;
                }
            }

            return EVAL_BODY_AGAIN;
        }
        catch (ProviderException e)
        {
            log.fatal("Provider failed while trying to iterator through history", e);

            // FIXME: THrow something.
        }

        return SKIP_BODY;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public final int doAfterBody()
    {
        if (bodyContent != null)
        {
            try
            {
                JspWriter out = getPreviousOut();
                out.print(bodyContent.getString());
                bodyContent.clearBody();
            }
            catch (IOException e)
            {
                log.error("Unable to get inner tag text", e);

                // FIXME: throw something?
            }
        }

        if ((m_iterator != null) && m_iterator.hasNext())
        {
            Attachment att = (Attachment) m_iterator.next();

            WikiContext context = (WikiContext) m_wikiContext.clone();
            context.setPage(att);
            pageContext.setAttribute(WikiTagBase.ATTR_CONTEXT, context, PageContext.REQUEST_SCOPE);

            pageContext.setAttribute(getId(), att);

            return EVAL_BODY_AGAIN;
        }
        else
        {
            return SKIP_BODY;
        }
    }
}
