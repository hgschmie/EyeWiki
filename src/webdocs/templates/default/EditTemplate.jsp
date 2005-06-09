<%@ taglib uri="/WEB-INF/tld/jspwiki.tld" prefix="wiki" %>

<!DOCTYPE html 
     PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
     "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html>

<head>
  <title><wiki:Variable var="applicationname" /> Edit: <wiki:PageName /></title>
  <wiki:Include page="commonheader.jsp"/>
  <!-- <script type="text/javascript" src="scripts/fckeditor/fckeditor.js"></script> -->
  <meta name="robots" content="noindex" />
</head>

<wiki:CheckRequestContext context="edit">
  <body class="wiki" onload="document.editForm.text.focus()">
</wiki:CheckRequestContext>

<wiki:CheckRequestContext context="comment">
  <body class="wiki" onload="document.commentForm.text.focus()">
</wiki:CheckRequestContext>

<table class="wiki">
  <tr>
    <td class="wikimenu">
      <wiki:Include page="LeftMenu.jsp"/>
      <wiki:Include page="LeftMenuFooter.jsp"/>
    </td>

    <td class="wiki">
      <table class="wikiedit">
         <tr class="wikihead">
            <td class="wikihead"><h1 class="wikihead"><a class="wikianchor" name="Top">Edit <wiki:PageName/></a></h1></td>
            <td class="wikisearch"><wiki:Include page="SearchBox.jsp"/></td>
         </tr>
         <tr><td colspan="2"><hr /></td></tr>
         <tr><td colspan="2" class="wikicontent"><wiki:Content/></td></tr>
      </table> 
    </td>
  </tr>
</table>
</body>
</html>
