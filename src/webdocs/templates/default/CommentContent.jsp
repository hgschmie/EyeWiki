<%@ taglib uri="/WEB-INF/tld/eyewiki.tld" prefix="wiki" %>

<wiki:InsertPage/>

<h1 class="comment">Please enter your comments below:</h1>

<wiki:Editor name="commentForm">
  <wiki:EditorArea/>

  <h2 class="comment">
    <table class="comment">
      <tr>
        <td class="commentmsg"><label for="authorname">Your name</label></td>
        <td class="comment"><input type="text" name="author" id="authorname" value="<wiki:UserName/>" /></td> 
      </tr>
      <tr>
        <td class="commentmsg"><label for="rememberme">Remember me?</label></td>
        <td class="comment"><input type="checkbox" name="remember" id="rememberme" /></td>
      </tr>
      <tr>
        <td class="commentmsg"><label for="link">Homepage or email</label></td>
        <td class="comment"><input type="text" name="link" id="link" value="<%=pageContext.getAttribute("link",PageContext.REQUEST_SCOPE)%>" /></td>
      </tr>
      <tr>
        <td class="comment" colspan="2">
          <table>
            <tr>
              <td><input type="submit" name="ok" value="Save" /></td>
              <td><input type="submit" name="preview" value="Preview" /></td>
              <td><input type="submit" name="cancel" value="Cancel" /></td>
            </tr>
          </table>
        </td>
      </tr>
    </table>
  </h2>
</wiki:Editor>

<wiki:NoSuchPage page="EditPageHelp">
  <div class="error">
    The EditPageHelp<wiki:EditLink page="EditPageHelp">?</wiki:EditLink>
    page is missing.
  </div>
</wiki:NoSuchPage>

<div id="wikiedit">
  <wiki:InsertPage page="EditPageHelp" />
</div>
