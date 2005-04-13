<%@ page import="com.ecyrd.jspwiki.tags.InsertDiffTag" %>
<%@ taglib uri="/WEB-INF/tld/jspwiki.tld" prefix="wiki" %>
<%!
    String getVersionText( Integer ver )
    {
        return ver.intValue() > 0 ? ("version "+ver) : "current version";
    }
%>

<wiki:PageExists>
  <h1 class="diff">Difference between 
  <%=getVersionText((Integer)pageContext.getAttribute(InsertDiffTag.ATTR_OLDVERSION, PageContext.REQUEST_SCOPE))%> 
  and 
  <%=getVersionText((Integer)pageContext.getAttribute(InsertDiffTag.ATTR_NEWVERSION, PageContext.REQUEST_SCOPE))%>:
  </h1>
  <wiki:InsertDiff>
    <div class="diff">No difference detected.</div>
  </wiki:InsertDiff>
</wiki:PageExists>

<wiki:NoSuchPage>
  <div class="nopage">This page does not exist.  Why don't you go and<wiki:EditLink>create it</wiki:EditLink>?</div>
</wiki:NoSuchPage>

<table class="pageaction">
  <tr>
    <td class="pageaction"><wiki:LinkTo>Back to <wiki:PageName/></wiki:LinkTo></td>
    <td class="pageaction">
      <wiki:PageInfoLink>More info...</wiki:PageInfoLink>
    </td>
  </tr>
</table>
