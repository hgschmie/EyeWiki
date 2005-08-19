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
<%-- Provides a simple searchbox that can be easily included anywhere
     on the page --%>

<div class="searchbox">
  <form action="<wiki:Variable var="baseURL"/>Search.jsp" accept-charset="<wiki:ContentEncoding />">
    <wiki:LinkTo page="FindPage">Search</wiki:LinkTo>
    <input type="text" name="query" size="15" />
    <input type="submit" name="ok" value="Find!" />
  </form>
</div>

