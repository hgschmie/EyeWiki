<%@ taglib uri="/WEB-INF/tld/jspwiki.tld" prefix="wiki" %>

<%-- Provides a small login/logout form to include in a side bar. --%>

<div class="loginbox">
  <wiki:UserCheck status="unvalidated">
    <form action="<wiki:Variable var="baseURL"/>Login.jsp" accept-charset="UTF-8" method="post" >
      <input type="hidden" name="page" value="<wiki:Variable var="pagename"/>" />
      <table class="loginbox">
        <td class="loginbox"><input type="text" name="uid" size="8" value="<wiki:UserCheck status="known"><wiki:UserName /></wiki:UserCheck>" /></td>
        <td class="loginboxmsg">User name</td>
        <td class="loginbox"><input type="password" name="passwd" size="8" /></td>
        <td class="loginboxmsg">Password</td>
        <td class="loginbox"><input type="submit" name="action" value="login" /></td>
      </table>
    </form>
  </wiki:UserCheck>
  <wiki:UserCheck status="validated">
    <form action="<wiki:Variable var="baseURL"/>Login.jsp" accept-charset="UTF-8" method="post" >
      <input type="hidden" name="page" value="<wiki:Variable var="pagename"/>" />
      <table class="loginbox">
        <td class="loginbox"><input type="submit" name="action" value="logout" /></td>
      </table>
    </form>
  </wiki:UserCheck>
</div>

