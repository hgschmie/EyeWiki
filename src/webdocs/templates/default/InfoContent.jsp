<%@ taglib uri="/WEB-INF/jspwiki.tld" prefix="wiki" %>

<script language="javascript">
function confirmDelete()
{
  var reallydelete = confirm("Please confirm that you want to delete content permanently!");

  return reallydelete;
}
</script>

<wiki:PageExists>

   <form name="deleteForm" action="<wiki:BaseURL/>Delete.jsp?page=<wiki:Variable var="pagename" />" method="post" 
                 accept-charset="<wiki:ContentEncoding />" onsubmit="return confirmDelete()">

   <table cellspacing="4">
       <tr>
           <td><b>Page name</b></td>
           <td><wiki:LinkTo><wiki:PageName /></wiki:LinkTo></td>
       </tr>

       <wiki:PageType type="attachment">
           <tr>
              <td><b>Parent page</b></td>
              <td><wiki:LinkToParent><wiki:ParentPageName /></wiki:LinkToParent></td>
           </tr>
       </wiki:PageType>

       <tr>
           <td><b>Page last modified</b></td>
           <td><wiki:PageDate /></td>
       </tr>

       <tr>
           <td><b>Current page version</b></td>
           <td><wiki:PageVersion>No versions.</wiki:PageVersion></td>
       </tr>

       <tr>
           <td valign="top"><b>Page revision history</b></td>
           <td>
               <table border="1" cellpadding="4">
                   <tr>
                        <th>Version</th>                        
                        <th>Date <wiki:PageType type="page">(and differences to current)</wiki:PageType></th>
                        <th>Author</th>
                        <th>Size</th>
                        <wiki:PageType type="page">                        
                            <th>Changes from previous</th>
                        </wiki:PageType>
                        <wiki:Permission permission="delete">
                            <th>Delete</th>
                        </wiki:Permission>
                   </tr>
                   <wiki:HistoryIterator id="currentPage">
                     <tr>
                         <td>
                             <wiki:LinkTo version="<%=Integer.toString(currentPage.getVersion())%>">
                                  <wiki:PageVersion/>
                             </wiki:LinkTo>
                         </td>

                         <td>
                             <wiki:PageType type="page">
                             <wiki:DiffLink version="latest" 
                                            newVersion="current">
                                 <wiki:PageDate/>
                             </wiki:DiffLink>
                             </wiki:PageType>

                             <wiki:PageType type="attachment">
                                 <wiki:PageDate/>
                             </wiki:PageType>
                         </td>

                         <td><wiki:Author /></td>
                         <td><wiki:PageSize /></td>

                         <wiki:PageType type="page">
                           <td>
                              <wiki:CheckVersion mode="notfirst">
                                   <wiki:DiffLink version="current" 
                                                  newVersion="previous">
                                       from version <wiki:PreviousVersion/> to <wiki:PageVersion/>
                                   </wiki:DiffLink>
                              </wiki:CheckVersion>
                           </td>
                         </wiki:PageType>

                         <wiki:Permission permission="delete">
                             <td>
                                 <input type="checkbox" name="delver-<%=currentPage.getVersion()%>" />
                             </td>
                         </wiki:Permission>
                     </tr>
                   </wiki:HistoryIterator>
               </table>
               <wiki:Permission permission="delete">
                   <br /><br />
                   <input type="submit" name="delete" value="Delete marked versions only"/>
                   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                   <input type="submit" name="delete-all" value="Delete entire page"/>
               </wiki:Permission>
           </td>
      </tr>
</table>

    </form>
             
    <br />
    <wiki:PageType type="page">
       <wiki:LinkTo>Back to <wiki:PageName/></wiki:LinkTo>
    </wiki:PageType>
    <wiki:PageType type="attachment">

       <form action="<wiki:Variable var="baseurl"/>attach" method="post" enctype="multipart/form-data">

           <%-- Do NOT change the order of wikiname and content, otherwise the 
                servlet won't find its parts. --%>

           <input type="hidden" name="page" value="<wiki:Variable var="pagename"/>" />

           In order to update this attachment with a newer version, find the
           file using "Browse", then click on "Update".

           <p>
           <input type="file" name="content" />
           <input type="submit" name="upload" value="Update" />
           <input type="hidden" name="action" value="upload" />
           <input type="hidden" name="nextpage" value="<wiki:PageInfoLink format="url"/>" />
           </p>
           </form>


    </wiki:PageType>

</wiki:PageExists>


<wiki:NoSuchPage>
    This page does not exist.  Why don't you go and
    <wiki:EditLink>create it</wiki:EditLink>?
</wiki:NoSuchPage>

