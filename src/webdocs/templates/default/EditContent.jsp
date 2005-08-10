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

<wiki:CheckVersion mode="notlatest">
  <div class="notlatest">
    You are about to restore version <wiki:PageVersion/>.
    Click on "Save" to restore.  You may also edit the page before restoring it.
  </div>
</wiki:CheckVersion>

<wiki:CheckLock mode="locked" id="lock">
   <div class="lockwarn">
     User '<%=lock.getLocker()%>' has started to edit this page, but has not yet
     saved.  I won't stop you from editing this page anyway, BUT be aware that
     the other person might be quite annoyed.  It would be courteous to wait for his lock
     to expire or until he stops editing the page.  The lock expires in 
     <%=lock.getTimeLeft()%> minutes.
   </p>
</wiki:CheckLock>

<wiki:Editor />

<wiki:NoSuchPage page="EditPageHelp">
  <div class="error">
    The EditPageHelp<wiki:EditLink page="EditPageHelp">?</wiki:EditLink>
    page is missing.
  </div>
</wiki:NoSuchPage>

<div id="wikiedit">
  <wiki:InsertPage page="EditPageHelp" />
</div>
