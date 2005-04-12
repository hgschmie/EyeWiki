<%@ taglib uri="/WEB-INF/tld/jspwiki.tld" prefix="wiki" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
        "http://www.w3.org/TR/html4/loose.dtd">

<html>

<head>
  <title><wiki:Variable var="applicationname" />: <wiki:PageName /></title>
  <wiki:Include page="commonheader.jsp"/>
  <wiki:CheckVersion mode="notlatest">
        <meta name="robots" content="noindex,nofollow" />
  </wiki:CheckVersion>
</head>

<body>

<table class="fullpage">
  <tr>
  
    <td class="leftmenu">
       <wiki:Include page="LeftMenu.jsp"/>
       <p>
       <wiki:CheckRequestContext context="view">
          <wiki:Permission permission="edit">
             <wiki:EditLink>Edit this page</wiki:EditLink>
          </wiki:Permission>
       </wiki:CheckRequestContext>
       </p>
       <wiki:Include page="LeftMenuFooter.jsp"/>
       <p />
       <div align="center">
           <wiki:RSSImageLink title="Aggregate the RSS feed" />
       </div>
    </td>

    <td class="page">

      <table class="pageHeader">
         <tr>
            <td align="left">
                <h1 class="pagename"><a name="Top"><wiki:PageName/></a></h1>
            </td>
            <td align="right"><wiki:Include page="SearchBox.jsp"/></td>
         </tr>
         <tr>
            <td colspan="2" class="breadcrumbs">Your trail: <wiki:Breadcrumbs /></td>
         </tr>
         <tr>
            <td colspan="2"><hr /></td>
         </tr>
      </table>

      <hr />

      <wiki:Content/>

    </td>
  </tr>
</table>

</body>

</html>

