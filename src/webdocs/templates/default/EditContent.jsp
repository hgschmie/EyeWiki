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
