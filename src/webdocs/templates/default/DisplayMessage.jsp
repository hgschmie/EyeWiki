<%@ taglib uri="/WEB-INF/tld/eyewiki.tld" prefix="wiki" %>

<%-- Inserts a string message. --%>
<div class="message"><%=pageContext.getAttribute("message",PageContext.REQUEST_SCOPE)%></div>
<br clear="all" />
