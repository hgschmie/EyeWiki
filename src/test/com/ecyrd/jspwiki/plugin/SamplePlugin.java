package com.ecyrd.jspwiki.plugin;

import java.util.Map;

import com.ecyrd.jspwiki.WikiContext;
import com.ecyrd.jspwiki.WikiEngine;

/**
 *  Implements a simple plugin that just returns its text.
 *  <P>
 *  Parameters: text - text to return.
 *  Any _body content gets appended between brackets.
 *
 *  @author Janne Jalkanen
 */
public class SamplePlugin
    implements WikiPlugin
{
    public void initialize( WikiEngine engine )
        throws PluginException
    {
    }

    public String execute( WikiContext context, Map params )
        throws PluginException
    {
        StringBuffer sb = new StringBuffer();

        String text = (String) params.get("text");

        if( text != null )
        {
            sb.append( text );
        }

        String body = (String)params.get("_body");
        if( body != null )
        {
            sb.append( " ("+body.replace('\n','+')+")" );
        }

        return sb.toString();
    }

}
