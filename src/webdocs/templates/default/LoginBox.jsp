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

