package com.ecyrd.jspwiki.filters;

import java.util.Properties;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class TestFilter
        extends BasicPageFilter
{
    /** DOCUMENT ME! */
    public Properties m_properties;

    /**
     * DOCUMENT ME!
     *
     * @param props DOCUMENT ME!
     */
    public TestFilter(Properties props)
    {
        m_properties = props;
    }
    
    public boolean isVisible()
    {
        return true;
    }
    
    public int getPriority()
    {
        return PageFilter.NORMAL_PRIORITY;
    }

}
