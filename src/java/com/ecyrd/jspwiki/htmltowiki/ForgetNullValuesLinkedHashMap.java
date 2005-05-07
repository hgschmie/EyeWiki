package com.ecyrd.jspwiki.htmltowiki;

import java.util.LinkedHashMap;


/**
 * A LinkedHashMap that does not put null values into the map.
 *
 * @author <a href="mailto:sbaltes@gmx.com">Sebastian Baltes</a>
 */
public class ForgetNullValuesLinkedHashMap
        extends LinkedHashMap
{
    /**
     * DOCUMENT ME!
     *
     * @param key DOCUMENT ME!
     * @param value DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Object put(Object key, Object value)
    {
        if (value != null)
        {
            return super.put(key, value);
        }
        else
        {
            return null;
        }
    }
}
