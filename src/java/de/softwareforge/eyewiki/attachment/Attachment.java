package de.softwareforge.eyewiki.attachment;

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

import de.softwareforge.eyewiki.WikiPage;

/**
 * Describes an attachment.  Attachments are actually derivatives of a WikiPage, since they do actually have a WikiName as well.
 *
 * @author Erik Bunn
 * @author Janne Jalkanen
 */
public class Attachment
        extends WikiPage
{
    /** DOCUMENT ME! */
    public static final int CREATED = 0;

    /** DOCUMENT ME! */
    public static final int UPLOADING = 1;

    /** DOCUMENT ME! */
    public static final int COMPLETE = 2;

    /** DOCUMENT ME! */
    private String m_fileName;

    /** DOCUMENT ME! */
    private String m_parentName;

    /** DOCUMENT ME! */
    private int m_status = CREATED;

    /**
     * Creates a new Attachment object.
     *
     * @param parentPage DOCUMENT ME!
     * @param fileName DOCUMENT ME!
     */
    public Attachment(String parentPage, String fileName)
    {
        super(parentPage + "/" + fileName);

        m_parentName = parentPage;
        m_fileName = fileName;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String toString()
    {
        return "Attachment [" + getName() + ";mod=" + getLastModified() + ";status=" + m_status + "]";
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getFileName()
    {
        return (m_fileName);
    }

    /**
     * DOCUMENT ME!
     *
     * @param name DOCUMENT ME!
     */
    public void setFileName(String name)
    {
        m_fileName = name;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getStatus()
    {
        return m_status;
    }

    /**
     * DOCUMENT ME!
     *
     * @param status DOCUMENT ME!
     */
    public void setStatus(int status)
    {
        m_status = status;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getParentName()
    {
        return m_parentName;
    }
}
