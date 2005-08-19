<%--
 ========================================================================

 eyeWiki - a WikiWiki clone written in Java

 ========================================================================

 Copyright (C) 2005 Henning Schmiedehausen <henning@software-forge.com>

 based on

 JSPWiki - a JSP-based WikiWiki clone.
 Copyright (C) 2002-2005 Janne Jalkanen (Janne.Jalkanen@iki.fi)

 ========================================================================

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

 ========================================================================
--%>
<%@ taglib uri="/WEB-INF/tld/eyewiki.tld" prefix="wiki" %>

<!DOCTYPE html
     PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
     "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html>

<head>
  <title><wiki:Variable var="applicationname" /> Edit: <wiki:PageName /></title>
  <wiki:Include page="commonheader.jsp"/>
  <!-- <script type="text/javascript" src="scripts/fckeditor/fckeditor.js"></script> -->
  <meta name="robots" content="noindex" />
</head>

<wiki:CheckRequestContext context="edit">
  <body class="wiki" onload="document.editForm.text.focus()">
</wiki:CheckRequestContext>

<wiki:CheckRequestContext context="comment">
  <body class="wiki" onload="document.commentForm.text.focus()">
</wiki:CheckRequestContext>

<table class="wiki">
  <tr>
    <td class="wikimenu">
      <wiki:Include page="LeftMenu.jsp"/>
      <wiki:Include page="LeftMenuFooter.jsp"/>
    </td>

    <td class="wiki">
      <table class="wikiedit">
         <tr class="wikihead">
            <td class="wikihead"><h1 class="wikihead"><a class="wikianchor" name="Top">Edit <wiki:PageName/></a></h1></td>
            <td class="wikisearch"><wiki:Include page="SearchBox.jsp"/></td>
         </tr>
         <tr><td colspan="2"><hr /></td></tr>
         <tr><td colspan="2" class="wikicontent"><wiki:Content/></td></tr>
      </table>
    </td>
  </tr>
</table>
</body>
</html>
