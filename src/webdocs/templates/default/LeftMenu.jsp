<%@ taglib uri="/WEB-INF/tld/eyewiki.tld" prefix="wiki" %>
<div class="logo">
    <a href="<wiki:LinkTo page="SystemInfo" format="url"/>"
       onmouseover="document.eyewiki_logo.src='<wiki:BaseURL/>images/jspwiki_logo.png'"
       onmouseout="document.eyewiki_logo.src='<wiki:BaseURL/>images/jspwiki_logo_s.png'">
      <img src="<wiki:BaseURL/>images/jspwiki_logo_s.png" name="jspwiki_logo" alt="JSPWiki logo"/>
    </a>
</div>

<!-- LeftMenu is automatically generated from a Wiki page called "LeftMenu" -->

<div class="wikicontent">
  <wiki:InsertPage page="LeftMenu" />
  <wiki:NoSuchPage page="LeftMenu">
    <div class="nopage">No left menu found. Please <wiki:EditLink page="LeftMenu">create it</wiki:EditLink>?</div>
  </wiki:NoSuchPage>

  <wiki:UserCheck status="known">
    <div class="username">
      G'day,<br /><wiki:Translate>[<wiki:UserName />]</wiki:Translate>
    </div>
    <%@ include file="LoginBox.jsp" %>
  </wiki:UserCheck>

  <wiki:UserCheck status="unknown">
    <div class="username">
      Set your name in<br /><wiki:LinkTo page="UserPreferences">UserPreferences</wiki:LinkTo>
    </div>
  </wiki:UserCheck>
</div>

<!-- End of automatically generated page -->

