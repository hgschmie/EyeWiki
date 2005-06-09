<%@ taglib uri="/WEB-INF/tld/jspwiki.tld" prefix="wiki" %>
<div align="center">
    <a href="<wiki:LinkTo page="SystemInfo" format="url"/>"
       onmouseover="document.jspwiki_logo.src='<wiki:BaseURL/>images/jspwiki_logo.png'"
       onmouseout="document.jspwiki_logo.src='<wiki:BaseURL/>images/jspwiki_logo_s.png'">
      <img src="<wiki:BaseURL/>images/jspwiki_logo_s.png" border="0" name="jspwiki_logo" alt="JSPWiki logo"/
    </a>
</div>

<!-- LeftMenu is automatically generated from a Wiki page called "LeftMenu" -->

<div class="innerleftmenu">
  <wiki:InsertPage page="LeftMenu" />
  <wiki:NoSuchPage page="LeftMenu">
    <hr />
    <p align="center">
      <i>No LeftMenu!</i><br />
      <wiki:EditLink page="LeftMenu">Please make one.</wiki:EditLink><br />
    </p>
    <hr />
  </wiki:NoSuchPage>
  
  <div class="username">
    <wiki:UserCheck status="known">
      <wiki:Translate>[<wiki:UserName />]</wiki:Translate><br />
      <wiki:UserCheck status="unvalidated">
        <b>known</b>
      </wiki:UserCheck>
      <wiki:UserCheck status="validated">
        <b>logged in</b>
      </wiki:UserCheck>
    </wiki:UserCheck>
    <wiki:UserCheck status="unknown">
      Set your name with<br />
      <wiki:LinkTo page="UserPreferences">UserPreferences</wiki:LinkTo>
    </wiki:UserCheck>
  </div>
  <wiki:Include page="LoginBox.jsp" />
  
  <!-- End of automatically generated page -->
</div>
