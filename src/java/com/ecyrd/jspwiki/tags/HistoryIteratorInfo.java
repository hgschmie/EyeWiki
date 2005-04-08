package com.ecyrd.jspwiki.tags;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

import com.ecyrd.jspwiki.WikiPage;


/**
 * Just provides the TEI data for HistoryIteratorTag.
 *
 * @since 2.0
 */
public class HistoryIteratorInfo
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
                    data.getAttributeString("id"), WikiPage.class.getName(), true,
                    VariableInfo.NESTED)
            };

        return var;
    }
}
