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

<%-- Inserts page content for preview. --%>

<h1 class="preview">
   This is a PREVIEW!  Hit "Keep Editing" to go back to the editor,
   or hit "Save" if you're happy with what you see.
</h1>
<hr />

<div class="preview">
   <wiki:Translate><%=pageContext.getAttribute("usertext",PageContext.REQUEST_SCOPE)%></wiki:Translate>
</div>

<br clear="all" />

<hr />

<h1 class="preview">
   This is a PREVIEW!  Hit "Keep Editing" to go back to the editor,
   or hit "Save" if you're happy with what you see.
</div>

<hr />

<wiki:Editor>
  <textarea class="editor" rows="4" cols="80" readonly="true" name="text"><%=pageContext.getAttribute("usertext", PageContext.REQUEST_SCOPE) %></textarea>

  <h2 id="previewsavebutton" class="editor">
    <table class="editor">
      <tr>
        <td class="editor"><input type="submit" name="edit" value="Keep editing"/></td>
        <td class="editor"><input type="submit" name="ok" value="Save" /></td>
        <td class="editor"><input type="submit" name="cancel" value="Cancel" /></td>
      </tr>
    </table>
  </h2>
</wiki:Editor>
