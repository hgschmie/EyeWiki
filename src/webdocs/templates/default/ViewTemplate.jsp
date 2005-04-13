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

<body class="wiki">

<table class="wiki">
  <tr>
    <td class="wikimenu" width="10%">
       <wiki:Include page="LeftMenu.jsp"/>
       <wiki:Include page="LeftMenuFooter.jsp"/>
       <wiki:RSSImageLink title="Aggregate the RSS feed" />
    </td>

    <td class="wiki" width="85%">
      <table class="wikiview">
         <tr>
            <td class="wikihead"><h1 class="wikihead"><a name="Top"><wiki:PageName/></a></h1></td>
            <td class="wikisearch"><wiki:Include page="SearchBox.jsp"/></td>
         </tr>
         <tr>
            <td colspan="2" class="breadcrumbs">Your trail: <wiki:Breadcrumbs /></td>
         </tr>
         <tr><td><hr /></td></tr>
         <tr><td class="wikiview"><wiki:Content/></td></tr>
      </table> 
    </td>
  </tr>
</table>
</body>
</html>
