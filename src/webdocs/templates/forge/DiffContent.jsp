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
<%@ page import="de.softwareforge.eyewiki.tags.InsertDiffTag" %>
<%@ taglib uri="/WEB-INF/tld/eyewiki.tld" prefix="wiki" %>
<%!
    String getVersionText( Integer ver )
    {
        return ver.intValue() > 0 ? ("version "+ver) : "current version";
    }
%>

<wiki:PageExists>
  <h2 class="diff">Difference between
  <%=getVersionText((Integer)pageContext.getAttribute(InsertDiffTag.ATTR_OLDVERSION, PageContext.REQUEST_SCOPE))%>
  and
  <%=getVersionText((Integer)pageContext.getAttribute(InsertDiffTag.ATTR_NEWVERSION, PageContext.REQUEST_SCOPE))%>:
  </h2>
  <wiki:InsertDiff>
    <div class="diff">No difference detected.</div>
  </wiki:InsertDiff>
</wiki:PageExists>

<wiki:NoSuchPage>
  <div class="nopage">This page does not exist. Why don't you go and <wiki:EditLink>create it</wiki:EditLink>?</div>
</wiki:NoSuchPage>

<table class="pageaction">
  <tr>
    <td><wiki:LinkTo>Back to <wiki:PageName/></wiki:LinkTo></td>
    <td>
      <wiki:PageInfoLink>More info...</wiki:PageInfoLink>
    </td>
  </tr>
</table>
