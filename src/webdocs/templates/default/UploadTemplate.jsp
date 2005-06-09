<%@ taglib uri="/WEB-INF/tld/eyewiki.tld" prefix="wiki" %>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
        "http://www.w3.org/TR/html4/loose.dtd">

<html>

<head>
  <title><wiki:Variable var="applicationname"/>: Add Attachment</title>
  <wiki:Include page="commonheader.jsp" />
  <meta name="robots" content="noindex">
</head>

<body class="wiki">
  <h1 class="wikihead">Upload new attachment to <wiki:PageName /></h1>
  <hr />

  <wiki:HasAttachments>
    <h1 class="attachment">Currently existing attachments:</h1>

    <div class="zebra-table">
    <table class="attachment">
      <wiki:AttachmentsIterator id="att">
        <tr>
          <td><wiki:LinkTo><%=att.getFileName()%></wiki:LinkTo></td>
          <td><wiki:PageInfoLink><img src="<wiki:BaseURL/>images/attachment_big.png" border="0" alt="Info on <%=att.getFileName()%>"></wiki:PageInfoLink></td>
          <td><%=att.getSize()%> bytes</td>
        </tr>
      </wiki:AttachmentsIterator>
    </table>
    </div>

    <hr />
  </wiki:HasAttachments>

  <table class="wikibody">
    <tr>
      <td>
        <form action="<wiki:BaseURL/>attach" method="post" enctype="multipart/form-data" accept-charset="UTF-8">
          <%-- Do NOT change the order of wikiname and content, otherwise the 
               servlet won't find its parts. --%>
          <input type="hidden" name="page" value="<wiki:Variable var="pagename"/>">

          <table class="upload">
            <tr><td class="upload">
                 In order to upload a new attachment to this page, please use the following
                 box to find the file, then click on "Upload".
            </td></tr>
            <tr><td class="upload"><input type="file" name="content"></td></tr>
            <tr><td class="uploadmsg"><input type="submit" name="upload" value="Upload"></td></tr>
          </table>
          <input type="hidden" name="action" value="upload">
          <input type="hidden" name="nextpage" value="<wiki:UploadLink format="url"/>">
        </form>
        <span class="error"><wiki:Variable var="msg"/></span>
      </td>
    </tr>
  </table>
</body>
</html>
