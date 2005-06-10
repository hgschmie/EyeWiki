/*
* ========================================================================
*
* eyeWiki - a WikiWiki clone written in Java
*
* ========================================================================
*
* Copyright (C) 2005 Henning Schmiedehausen <henning@software-forge.com>
*
* based on
*
* JSPWiki - a JSP-based WikiWiki clone.
* Copyright (C) 2002-2005 Janne Jalkanen (Janne.Jalkanen@iki.fi)
*
* ========================================================================
*
* This program is free software; you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation; either version 2.1 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*
* ========================================================================
*/
<script type="text/javascript">
/*
 *  Chooses a suitable stylesheet based on browser.
 */
    var IE4 = (document.all && !document.getElementById) ? true : false;
    var NS4 = (document.layers) ? true : false;
    var IE5 = (document.all && document.getElementById) ? true : false;
    var NS6 = (document.getElementById && !document.all) ? true : false;
    var IE  = IE4 || IE5;
    var NS  = NS4 || NS6;
    var Mac = (navigator.platform.indexOf("Mac") == -1) ? false : true;

    var sheet = "";

    if( NS4 )
    {
        sheet = "eyewiki_ns.css";
    }
    else if( Mac )
    {
        sheet = "eyewiki_mac.css";
    }
    else if( IE )
    {
        sheet = "eyewiki_ie.css";
    }

    if( sheet != "" )
    {
        document.write("<link rel=\"stylesheet\" href=\"<wiki:BaseURL/>templates/<wiki:TemplateDir />/"+sheet+"\" />");
    }
</script>
