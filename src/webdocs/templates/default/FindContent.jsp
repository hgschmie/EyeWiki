<%@ taglib uri="/WEB-INF/tld/jspwiki.tld" prefix="wiki" %>

<%-- FIXME: Get rid of the scriptlets. --%>
<%
    String query = (String)pageContext.getAttribute( "query",
                                                     PageContext.REQUEST_SCOPE );
    if( query == null ) query = "";
%>
<h1 class="pagefind">Find pages</h2>

<wiki:SearchResults>
  <h2 class="pagefind">Search results for '<%=query%>'</h2x>

  <div class="pagefind">Found <wiki:SearchResultsSize/> hits, here are the top 20.</div>

  <table class="pagefind">
  <tr>
   <th class="pagefind">Page</th>
   <th class="pagefindscore">Score</th>
  </tr>

  <wiki:SearchResultIterator id="searchref" maxItems="20">
    <tr>
      <td class="pagefind"><wiki:LinkTo><wiki:PageName/></wiki:LinkTo></td>
      <td class="pagefindscore"><%=searchref.getScore()%></td>
    </tr>
  </wiki:SearchResultIterator>

  <wiki:IfNoSearchResults>
    <tr>
      <td class="pagefind">No results.</td>
    </tr>
  </wiki:IfNoSearchResults>

  </table>
  <hr />
</wiki:SearchResults>

<form action="<wiki:Variable var="baseURL"/>Search.jsp" accept-charset="<wiki:ContentEncoding/>">
<table class="pagefindform">
  <tr>
    <td class="pagefindformmsg">Enter your query here:</td>
    <td class="pagefindform"><input type="text" name="query" size="40" value="<%=query%>" /></td>
  </tr>
  <tr>
    <td class="pagefindform" colspan="2"><input type="submit" name="ok" value="Find!" /></td>
  </tr>
</table>
</form>

<div class="pagefind">Use '+' to require a word, '-' to forbid a word.  For example:

<pre class="pagefind">+java -emacs jsp</pre>

finds pages that MUST include the word "java", and MAY NOT include
the word "emacs".  Also, pages that contain the word "jsp" are
ranked before the pages that don't.<br />

All searches are case insensitive.  If a page contains both
forbidden and required keywords, it is not shown.
</div>

