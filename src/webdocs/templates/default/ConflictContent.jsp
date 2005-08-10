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

<h1 class="conflict">Oops!  Someone modified the page while you were editing it!</h1>

<div class="conflict">
  Since I am stupid and can't figure out what the difference
  between those pages is, you will need to do that for me.  I've
  printed here the text (in Wiki) of the new page, and the
  modifications you made.  You'll now need to copy the text onto a
  scratch pad (Notepad or emacs will do just fine), and then edit
  the page again.
</div>

<div class="conflict">
  Note that when you go back into the editing mode, someone might have
  changed the page again.  So be quick.
</div>

<hr />

<h2 class="conflict">Here is the modified text (by someone else):</h2>

<div class="conflict">
  <%=pageContext.getAttribute("conflicttext",PageContext.REQUEST_SCOPE)%>
</div>

<hr />

<h2 class="conflict">Here is your text:</h2>

<div class="conflict">
  <%=pageContext.getAttribute("usertext",PageContext.REQUEST_SCOPE)%>
</div>

<table class="pageaction">
  <tr>
    <td><a class="wikicontent" href="#Top">Go to top</a></td>
    <td>
      <wiki:CheckVersion mode="latest">
        <wiki:Permission permission="edit">
          <wiki:EditLink>Edit this page</wiki:EditLink>
        </wiki:Permission>
      </wiki:CheckVersion>
    </td>
    <td>
      <wiki:PageInfoLink>More info...</wiki:PageInfoLink>
    </td>
  </tr>
</table>
