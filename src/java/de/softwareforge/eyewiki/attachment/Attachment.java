package de.softwareforge.eyewiki.attachment;

import de.softwareforge.eyewiki.WikiPage;


/**
 * Describes an attachment.  Attachments are actually derivatives of a WikiPage, since they do
 * actually have a WikiName as well.
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
        return "Attachment [" + getName() + ";mod=" + getLastModified() + ";status=" + m_status
        + "]";
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
