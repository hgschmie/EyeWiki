package de.softwareforge.eyewiki.tags;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

import de.softwareforge.eyewiki.PageLock;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class CheckLockInfo
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
                    data.getAttributeString("id"), PageLock.class.getName(), true,
                    VariableInfo.NESTED)
            };

        return var;
    }
}
