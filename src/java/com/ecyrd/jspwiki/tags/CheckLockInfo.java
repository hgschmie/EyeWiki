package com.ecyrd.jspwiki.tags;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

import com.ecyrd.jspwiki.PageLock;

public class CheckLockInfo 
    extends TagExtraInfo
{
    public VariableInfo[] getVariableInfo(TagData data)
    {
        VariableInfo var[] = { new VariableInfo( data.getAttributeString("id"),
                    PageLock.class.getName(),
                    true,
                    VariableInfo.NESTED )
        };

        return var;        
    }
}
