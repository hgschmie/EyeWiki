<%@ page import="de.softwareforge.eyewiki.tags.InsertDiffTag" %>
<%@ taglib uri="/WEB-INF/tld/eyewiki.tld" prefix="wiki" %>
<%!
    String getVersionText( Integer ver )
    {
        return ver.intValue() > 0 ? ("version "+ver) : "current version";
    }
%>

<wiki:PageExists>
  <h2 class="diff">Difference between 
  <%=getVersionText((Integer)pageContext.getAttribute(InsertDiffTag.ATTR_OLDVERSION, PageContext.REQUEST_SCOPE))%> 
  and 
  <%=getVersionText((Integer)pageContext.getAttribute(InsertDiffTag.ATTR_NEWVERSION, PageContext.REQUEST_SCOPE))%>:
  </h2>
  <wiki:InsertDiff>
    <div class="diff">No difference detected.</div>
  </wiki:InsertDiff>
</wiki:PageExists>

<wiki:NoSuchPage>
  <div class="nopage">This page does not exist. Why don't you go and <wiki:EditLink>create it</wiki:EditLink>?</div>
</wiki:NoSuchPage>

<table class="pageaction">
  <tr>
    <td><wiki:LinkTo>Back to <wiki:PageName/></wiki:LinkTo></td>
    <td>
      <wiki:PageInfoLink>More info...</wiki:PageInfoLink>
    </td>
  </tr>
</table>
