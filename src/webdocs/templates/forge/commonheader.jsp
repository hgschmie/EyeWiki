<%--
  ========================================================================

  eyeWiki - a WikiWiki clone written in Java

  ========================================================================

  Copyright (C) 2005 Henning Schmiedehausen <henning@software-forge.com>

  based on

  JSPWiki - a JSP-based WikiWiki clone.
  Copyright (C) 2002-2005 Janne Jalkanen (Janne.Jalkanen@iki.fi)

  ========================================================================

  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation; either version 2.1 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

  ========================================================================
--%>
<%@ taglib uri="/WEB-INF/tld/eyewiki.tld" prefix="wiki" %>
<meta http-equiv="Content-Type" content="text/html; charset=<wiki:ContentEncoding />" />

<link rel="stylesheet" type="text/css" href="<wiki:BaseURL/>templates/<wiki:TemplateDir/>/eyewiki.css" />
<link rel="stylesheet" type="text/css" media="print" href="<wiki:BaseURL/>templates/<wiki:TemplateDir/>/eyewiki_print.css" />

<link rel="alternate stylesheet" type="text/css" href="<wiki:BaseURL/>templates/<wiki:TemplateDir/>/eyewiki.css" title="Standard" />
<link rel="alternate stylesheet" type="text/css" href="<wiki:BaseURL/>templates/<wiki:TemplateDir/>/eyewiki_print.css" title="Print friendly" />

<link rel="help"   href="<wiki:LinkTo format="url" page="TextFormattingRules"/>" title="Help" />
<link rel="icon" href="<wiki:BaseURL/>images/eyewiki-logo.ico" />
<link rel="search" href="<wiki:LinkTo format="url" page="FindPage"/>"            title="Search <wiki:Variable var="ApplicationName" />" />
<link rel="start"  href="<wiki:LinkTo format="url" page="Main"/>"                title="Front page" />

<script src="<wiki:BaseURL/>scripts/search_highlight.js" type="text/javascript"></script>
<script src="<wiki:BaseURL/>scripts/eyewiki-common.js" type="text/javascript"></script>
<wiki:FeedDiscovery />

