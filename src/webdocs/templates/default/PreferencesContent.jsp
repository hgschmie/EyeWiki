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

<h2 class="wikihead">Setting your preferences</h2>

<div class="prefs">
  This is a page which allows you to set up all sorts of interesting things.
  You need to have cookies enabled for this to work, though.
</div>

<form action="<wiki:Variable var="baseURL"/>UserPreferences.jsp"
      method="post"
      accept-charset="UTF-8">

   <table class="prefs">
   <tr>
     <td class="prefsmsg">User name:</td>
     <td class="prefs"><input type="text" name="username" size="30" value="<wiki:UserName/>" /></td>
   </tr>
   <tr>
     <td colspan="2">This must be a proper WikiName, no punctuation.</td>
   </tr>
   <tr>
     <td class="prefsmsg" colspan="2"><input type="submit" name="ok" value="Set my preferences!" /></td>
   </tr>
   </table>
   <input type="hidden" name="action" value="save" />
</form>

<hr />

<h2 class="prefs">Removing your preferences</h2>

<div class="prefs">
  In some cases, you may need to remove the above preferences from the computer.
  Click the button below to do that.  Note that it will remove all preferences
  you've set up, permanently.  You will need to enter them again.
</div>

<form action="<wiki:Variable var="baseURL"/>UserPreferences.jsp"
      method="POST"
      accept-charset="UTF-8">
   <table class="prefs">
   <tr>
     <td><input type="submit" name="clear" value="Remove preferences from this computer" /></td>
   </tr>
   </table>
</form>
