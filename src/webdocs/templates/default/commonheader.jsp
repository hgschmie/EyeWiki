<%@ taglib uri="/WEB-INF/jspwiki.tld" prefix="wiki" %>
  <link rel="stylesheet" type="text/css" href="templates/<wiki:TemplateDir/>/jspwiki.css" />
  <script src="scripts/cssinclude.js" type="text/javascript"></script>
  <script src="scripts/search_highlight.js" type="text/javascript"></script>
  <meta http-equiv="Content-Type" content="text/html; charset=<wiki:ContentEncoding />" />
  <link rel="search" href="<wiki:LinkTo format="url" page="FindPage"/>"            title="Search <wiki:Variable var="ApplicationName" />" />
  <link rel="help"   href="<wiki:LinkTo format="url" page="TextFormattingRules"/>" title="Help" />
  <link rel="start"  href="<wiki:LinkTo format="url" page="Main"/>"                title="Front page" />
  <link rel="stylesheet" type="text/css" media="print" href="templates/<wiki:TemplateDir/>/jspwiki_print.css" />
  <link rel="alternate stylesheet" type="text/css" href="templates/<wiki:TemplateDir/>/jspwiki_print.css" title="Print friendly" />
  <link rel="alternate stylesheet" type="text/css" href="templates/<wiki:TemplateDir/>/jspwiki.css" title="Standard" />
  <wiki:FeedDiscovery />

