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
// 004. Zebra tables  DF / May 2004
// %%zebra-table
//
var zebraCnt ;

// called after loading the page
function validateZebraTable()
{
  if (!document.createElement) return;

  // find a <div class="zebra-table"> element
  var divArr = document.getElementsByTagName("div");
  if (! divArr) return;

  for (var i=0; i<divArr.length; i++)
  {
    if ( divArr[i].className == "zebra-table" )
    {
      zebraCnt = 0;
      validateZebraTableNode(divArr[i]);
    }
  }

}

function validateZebraTableNode(node)
{
  if ( node.nodeName == "TR")
  {
     zebraCnt++;
     if (zebraCnt % 2 == 1) node.className = "odd";
  }

  if (node.hasChildNodes)
  {
    for (var i=0; i<node.childNodes.length; i++)
    {
      validateZebraTableNode(node.childNodes[i]);
    }
  }

}

// Select skin
function skinSelect(skin)
{
  //var skin = document.forms["skinForm"].skinSelector;
  if (! skin) return;

  for (var i=0; i<skin.length; i++)
  {
    if ( skin[i].selected )
    {
      document.cookie = "eyeWikiSkin=" + skin[i].value + "#skin#" ;
    }
  }
  location.reload(); /* reload page */
}

function runOnLoad()
{
  validateZebraTable();
  googleSearchHighlight();
}

window.onload = runOnLoad;
