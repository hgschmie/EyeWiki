<%@ taglib uri="/WEB-INF/tld/jspwiki.tld" prefix="wiki" %>

<!DOCTYPE html 
     PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
     "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html>

<head>
  <title><wiki:Variable var="applicationname" />: <wiki:PageName /></title>
  <wiki:Include page="commonheader.jsp"/>
  <wiki:CheckVersion mode="notlatest">
        <meta name="robots" content="noindex,nofollow" />
  </wiki:CheckVersion>
</head>

<body bgcolor="#FFFFFF">

<table border="0" cellspacing="8" width="95%">

  <tr>
    <td class="leftmenu" width="10%" valign="top" nowrap="nowrap">
       <wiki:Include page="LeftMenu.jsp"/>
       <wiki:Include page="LeftMenuFooter.jsp"/>
       <div align="center">
           <wiki:RSSImageLink title="Aggregate the RSS feed" />
       </div>
    </td>

    <td class="page" width="85%" valign="top">

      <table width="100%" cellspacing="0" cellpadding="0" border="0">
         <tr>
            <td align="left"><h1 class="pagename"><a name="Top"><wiki:PageName/></a></h1></td>
            <td align="right"><wiki:Include page="SearchBox.jsp"/></td>
         </tr>
         <tr>
            <td colspan="2" class="breadcrumbs">Your trail: <wiki:Breadcrumbs /></td>
         </tr>
      </table>

      <hr />

      <wiki:Content/>

    </td>
  </tr>

</table>

</body>

</html>

