<%@ taglib uri="/WEB-INF/tld/eyewiki.tld" prefix="wiki" %>

<script language="javascript">
function confirmDelete() {
   return confirm("Please confirm that you want to delete content permanently!");
}
</script>

<wiki:PageExists>
  <form name="deleteForm" method="post"
        action="<wiki:BaseURL/>Delete.jsp?page=<wiki:Variable var="pagename" />" 
        accept-charset="<wiki:ContentEncoding />"
        onsubmit="return confirmDelete()">

   <table class="pageinfo">
     <tr>
       <td class="pageinfomsg">Page name</td>
       <td class="pageinfo"><wiki:LinkTo><wiki:PageName /></wiki:LinkTo></td>
     </tr>

     <wiki:PageType type="attachment">
       <tr>
         <td class="pageinfomsg">Parent page</td>
         <td class="pageinfo"><wiki:LinkToParent><wiki:ParentPageName /></wiki:LinkToParent></td>
       </tr>
     </wiki:PageType>

     <tr>
       <td class="pageinfomsg">Page last modified</td>
       <td class="pageinfo"><wiki:PageDate /></td>
     </tr>

     <tr>
       <td class="pageinfomsg">Current page version</td>
       <td class="pageinfo"><wiki:PageVersion>No versions.</wiki:PageVersion></td>
     </tr>

     <tr>
           <td class="pageinfomsg">Page feed</td>
           <td><a class="pageinfo" href="<wiki:BaseURL/>rss.jsp?page=<wiki:Variable var="pagename" />&amp;mode=wiki"><img src="<wiki:BaseURL/>images/xml.png" border="0" alt="[RSS]"></a></td>
       </tr>

       <tr>
       <td class="pageinfomsg">Page revision history</td>
       <td><div class="zebra-table">
         <table class="pagerev">
         <tr>
           <th class="pagerev">Version</th>                        
           <th class="pagerev">Date <wiki:PageType type="page">(and differences to current)</wiki:PageType></th>
           <th class="pagerev">Author</th>
           <th class="pagerev">Size</th>
           <wiki:PageType type="page">                        
             <th class="pagerev">Changes from previous</th>
           </wiki:PageType>
           <wiki:Permission permission="delete">
             <th class="pagerev">Delete</th>
           </wiki:Permission>
         </tr>
         <wiki:HistoryIterator id="currentPage">
           <tr>
             <td class="pagerev"><wiki:LinkTo version="<%=Integer.toString(currentPage.getVersion())%>"><wiki:PageVersion/></wiki:LinkTo></td>
             <td class="pagerev">
               <wiki:PageType type="page"><wiki:DiffLink version="latest" newVersion="current"><wiki:PageDate/></wiki:DiffLink></wiki:PageType>
               <wiki:PageType type="attachment"><wiki:PageDate/></wiki:PageType>
             </td>
             <td class="pagerev"><wiki:Author /></td>
             <td class="pagerev"><wiki:PageSize /></td>
  
             <wiki:PageType type="page">
               <td class="pagerev">
                  <wiki:CheckVersion mode="notfirst">
                       <wiki:DiffLink version="current" newVersion="previous">from version <wiki:PreviousVersion/> to <wiki:PageVersion/></wiki:DiffLink>
                  </wiki:CheckVersion>
               </td>
             </wiki:PageType>
  
             <wiki:Permission permission="delete">
               <td class="pagerev"><input type="checkbox" name="delver-<%=currentPage.getVersion()%>" /></td>
             </wiki:Permission>
           </tr>
         </wiki:HistoryIterator>
         </table></div>
       </td>
     </tr>
     <wiki:Permission permission="delete">
       <tr>
         <td class="pageinfo">
           <input type="submit" name="delete" value="Delete marked versions only"/>
         </td>
         <td>
           <input type="submit" name="delete-all" value="Delete entire page"/>
         </td>
       </tr>
     </wiki:Permission>
   </table>

  </form>
             
  <hr />

  <wiki:PageType type="page">
    <table class="pageaction">
      <tr>
        <td><wiki:LinkTo>Back to <wiki:PageName/></wiki:LinkTo></td>
      </tr>
    </table>
  </wiki:PageType>

  <wiki:PageType type="attachment">
    <table>
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
          <input type="hidden" name="nextpage" value="<wiki:PageInfoLink format="url"/>" />
        </form>
        <span class="error"><wiki:Variable var="msg"/></span>
      </td>
    </tr>
    </table>
  </wiki:PageType>
</wiki:PageExists>

<wiki:NoSuchPage>
  <div class="nopage">This page does not exist. Why don't you go and <wiki:EditLink>create it</wiki:EditLink>?</div>
</wiki:NoSuchPage>
