/*
  JSPWiki - a JSP-based WikiWiki clone.

  Copyright (C) 2001-2004 Janne Jalkanen (Janne.Jalkanen@iki.fi)

  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation; either version 2.1 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
package com.ecyrd.jspwiki.tags;

import org.apache.ecs.ConcreteElement;
import org.apache.ecs.xhtml.textarea;

import com.ecyrd.jspwiki.WikiConstants;
import com.ecyrd.jspwiki.WikiContext;

/**
 *  @author jalkanen
 *
 *  @since 
 */
public class EditorAreaTag
        extends WikiTagBase
{
    public int doWikiStartTag() throws Exception
    {
        pageContext.getOut().print(getEditorArea(m_wikiContext).toString());
        
        return SKIP_BODY;
    }
    
    public static ConcreteElement getEditorArea(WikiContext context)
    {
        textarea area = new textarea();

        area.setClass(WikiConstants.CSS_EDITOR);
        area.setWrap("virtual");
        area.setName("text");
        area.setRows(25);
        area.setCols(80);
        area.setStyle("width:100%;");
       
        if (context.getRequestContext().equals(WikiContext.EDIT))
        {
            String usertext = context.getHttpParameter("text");
            if (usertext == null)
            {
                usertext = context.getEngine().getText(context, context.getPage());
            }
            
            area.addElement(usertext);
        }
        else if (context.getRequestContext().equals(WikiContext.COMMENT))
        {
            String usertext = context.getHttpParameter("text");
            
            if (usertext != null)
            {
                area.addElement(usertext);
            }
        }
        
        return area;
    }
}
