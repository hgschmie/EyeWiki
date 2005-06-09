<%@ taglib uri="/WEB-INF/tld/eyewiki.tld" prefix="wiki" %>
<meta http-equiv="Content-Type" content="text/html; charset=<wiki:ContentEncoding />" />

<link rel="stylesheet" type="text/css" href="<wiki:BaseURL/>templates/<wiki:TemplateDir/>/eyewiki.css" />
<link rel="stylesheet" type="text/css" media="print" href="<wiki:BaseURL/>templates/<wiki:TemplateDir/>/eyewiki_print.css" />

<link rel="alternate stylesheet" type="text/css" href="<wiki:BaseURL/>templates/<wiki:TemplateDir/>/eyewiki.css" title="Standard" />
<link rel="alternate stylesheet" type="text/css" href="<wiki:BaseURL/>templates/<wiki:TemplateDir/>/eyewiki_print.css" title="Print friendly" />

<link rel="help"   href="<wiki:LinkTo format="url" page="TextFormattingRules"/>" title="Help" />
<link rel="icon" type="image/png" href="<wiki:BaseURL/>images/favicon.png" />
<link rel="search" href="<wiki:LinkTo format="url" page="FindPage"/>"            title="Search <wiki:Variable var="ApplicationName" />" />
<link rel="start"  href="<wiki:LinkTo format="url" page="Main"/>"                title="Front page" />

<script src="<wiki:BaseURL/>scripts/search_highlight.js" type="text/javascript"></script>
<script src="<wiki:BaseURL/>scripts/eyewiki-common.js" type="text/javascript"></script>
<wiki:FeedDiscovery />

