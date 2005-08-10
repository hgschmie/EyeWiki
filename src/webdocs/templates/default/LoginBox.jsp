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

<%-- Provides a small login/logout form to include in a side bar. --%>

<div class="loginbox">
  <wiki:UserCheck status="unvalidated">
    <form action="<wiki:Variable var="baseURL"/>Login.jsp" accept-charset="UTF-8" method="post" >
      <input type="hidden" name="page" value="<wiki:Variable var="pagename"/>" />
      <table>
        <tr><td><input type="text" name="uid" size="16" value="<wiki:UserCheck status="known"><wiki:UserName /></wiki:UserCheck>" /></td></tr>
        <tr><td class="loginboxmsg">User name</td></tr>
        <tr><td><input type="password" name="passwd" size="16" /></td></tr>
        <tr><td class="loginboxmsg">Password</td></tr>
        <tr><td><input type="submit" name="action" value="login" /></td></tr>
      </table>
    </form>
  </wiki:UserCheck>
  <wiki:UserCheck status="validated">
    <form action="<wiki:Variable var="baseURL"/>Login.jsp" accept-charset="UTF-8" method="post" >
      <input type="hidden" name="page" value="<wiki:Variable var="pagename"/>" />
      <table>
        <td><input type="submit" name="action" value="logout" /></td>
      </table>
    </form>
  </wiki:UserCheck>
</div>

