package com.ecyrd.jspwiki.filters;

import java.util.Properties;

public class TestFilter
    extends BasicPageFilter
{
    public Properties m_properties;

    public void initialize( Properties props )
    {
        m_properties = props;
    }
}
