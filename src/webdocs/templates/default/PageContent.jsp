<%@ taglib uri="/WEB-INF/tld/jspwiki.tld" prefix="wiki" %>

<%-- Inserts page content. --%>

<%-- If the page is an older version, then offer a note and a possibility
     to restore this version as the latest one. --%>

<wiki:CheckVersion mode="notlatest">
   <div class="notlatest">
     This is version <wiki:PageVersion/>.  
     It is not the current version, and thus it cannot be edited.<br />
     <wiki:LinkTo>[Back to current version]</wiki:LinkTo>&nbsp;&nbsp;
     <wiki:Permission permission="edit">
       <wiki:EditLink version="this">[Restore this version]</wiki:EditLink></p>
     </wiki:Permission>
   </div>
   <hr />
</wiki:CheckVersion>

<%-- Inserts no text if there is no page. --%>

<wiki:InsertPage />

<!-- FIXME: Should also note when a wrong version has been fetched. -->
<wiki:NoSuchPage>
  <div class="nopage">This page does not exist.  Why don't you go and<wiki:EditLink>create it</wiki:EditLink>?</div>
</wiki:NoSuchPage>

<br clear="all" />

<wiki:HasAttachments>
  <h1 class="attachment">Currently existing attachments:</h1>

  <table class="attachment">
    <wiki:AttachmentsIterator id="att">
      <tr>
        <td><wiki:LinkTo><%=att.getFileName()%></wiki:LinkTo></td>
        <td><wiki:PageInfoLink><img src="<wiki:BaseURL/>images/attachment_big.png" border="0" alt="Info on <%=att.getFileName()%>"></wiki:PageInfoLink></td>
        <td><%=att.getSize()%> bytes</td>
      </tr>
    </wiki:AttachmentsIterator>
  </table>

  <hr />
</wiki:HasAttachments>

<hr />

<table class="pageaction">
  <tr>
    <td class="pageaction"><a href="#Top">Go to top</a></td>
    <td class="pageaction">
      <wiki:CheckVersion mode="latest">
        <wiki:Permission permission="edit">
          <wiki:EditLink>Edit this page</wiki:EditLink>
        </wiki:Permission>
      </wiki:CheckVersion>
    </td>
    <td class="pageaction">
      <wiki:PageInfoLink>More info...</wiki:PageInfoLink>
    </td>
    <td class="pageaction">
      <wiki:Permission permission="upload">
        <a href="#" onclick="javascript:window.open('<wiki:UploadLink format="url" />','Upload','width=640,height=480,toolbar=1,menubar=1,scrollbars=1,resizable=1,').focus()">Attach file...</a>
      </wiki:Permission>
    </td>
  </tr>
  <tr>
    <td class="pageprops" colspan="4">
      <wiki:CheckVersion mode="latest">
        This page last changed on <wiki:DiffLink version="latest" newVersion="previous"><wiki:PageDate/></wiki:DiffLink> by <wiki:Author />.
      </wiki:CheckVersion>
      <wiki:CheckVersion mode="notlatest">
        This particular version was published on <wiki:PageDate/> by <wiki:Author />.
      </wiki:CheckVersion>
      <wiki:NoSuchPage>
        Page not created yet.
      </wiki:NoSuchPage>
    </td>
  </tr>
</table>
