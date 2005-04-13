<%@ page import="com.ecyrd.jspwiki.Release" %>
<%@ taglib uri="/WEB-INF/tld/jspwiki.tld" prefix="wiki" %>
<!-- LeftMenuFooter is automatically generated from a Wiki page called "LeftMenuFooter" -->

<wiki:InsertPage page="LeftMenuFooter" />
<wiki:NoSuchPage page="LeftMenuFooter">
  <div class="nopage">No left menu footer found. Please <wiki:EditLink page="LeftMenuFooter">create it</wiki:EditLink>?</div>
</wiki:NoSuchPage>

<!-- End of automatically generated page -->

<div class="release"><%=Release.APPNAME%> v<%=Release.getVersionString()%></div>
