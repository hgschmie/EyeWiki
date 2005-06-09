package de.softwareforge.eyewiki.tags;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

import de.softwareforge.eyewiki.SearchResult;


/**
 * Just provides the TEI data for IteratorTag.
 *
 * @since 2.0
 */
public class SearchResultIteratorInfo
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
                    data.getAttributeString("id"), SearchResult.class.getName(), true,
                    VariableInfo.NESTED)
            };

        return var;
    }
}
