package de.softwareforge.eyewiki.tags;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

import de.softwareforge.eyewiki.attachment.Attachment;


/**
 * Just provides the TEI data for AttachmentsIteratorTag.
 *
 * @since 2.0
 */
public class AttachmentsIteratorInfo
        extends TagExtraInfo
{
    /**
     * DOCUMENT ME!
     *
     * @param data DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public VariableInfo [] getVariableInfo(TagData data)
    {
        VariableInfo [] var =
            {
                new VariableInfo(
                    data.getAttributeString("id"), Attachment.class.getName(), true,
                    VariableInfo.NESTED)
            };

        return var;
    }
}
