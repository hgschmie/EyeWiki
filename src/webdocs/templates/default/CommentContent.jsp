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

<wiki:InsertPage/>

<h1 class="comment">Please enter your comments below:</h1>

<wiki:Editor name="commentForm">
  <wiki:EditorArea/>

  <h2 class="comment">
    <table class="comment">
      <tr>
        <td class="commentmsg"><label for="authorname">Your name</label></td>
        <td class="comment"><input type="text" name="author" id="authorname" value="<wiki:UserName/>" /></td> 
      </tr>
      <tr>
        <td class="commentmsg"><label for="rememberme">Remember me?</label></td>
        <td class="comment"><input type="checkbox" name="remember" id="rememberme" /></td>
      </tr>
      <tr>
        <td class="commentmsg"><label for="link">Homepage or email</label></td>
        <td class="comment"><input type="text" name="link" id="link" value="<%=pageContext.getAttribute("link",PageContext.REQUEST_SCOPE)%>" /></td>
      </tr>
      <tr>
        <td class="comment" colspan="2">
          <table>
            <tr>
              <td><input type="submit" name="ok" value="Save" /></td>
              <td><input type="submit" name="preview" value="Preview" /></td>
              <td><input type="submit" name="cancel" value="Cancel" /></td>
            </tr>
          </table>
        </td>
      </tr>
    </table>
  </h2>
</wiki:Editor>

<wiki:NoSuchPage page="EditPageHelp">
  <div class="error">
    The EditPageHelp<wiki:EditLink page="EditPageHelp">?</wiki:EditLink>
    page is missing.
  </div>
</wiki:NoSuchPage>

<div id="wikiedit">
  <wiki:InsertPage page="EditPageHelp" />
</div>
