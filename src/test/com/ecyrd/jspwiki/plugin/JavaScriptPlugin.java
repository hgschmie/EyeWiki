package com.ecyrd.jspwiki.plugin;

import java.util.Map;

import com.ecyrd.jspwiki.WikiContext;
import com.ecyrd.jspwiki.WikiEngine;

/**
 *  Implements a simple plugin that just returns a piece of Javascript
 *  <P>
 *  Parameters: text - text to return.
 *
 *  @author Janne Jalkanen
 */
public class JavaScriptPlugin
    implements WikiPlugin
{
    public void initialize( WikiEngine engine )
        throws PluginException
    {
    }

    public String execute( WikiContext context, Map params )
        throws PluginException
    {
        return "<script language=\"JavaScript\"><!--\nfoo='';\n--></script>\n";
    }

}
