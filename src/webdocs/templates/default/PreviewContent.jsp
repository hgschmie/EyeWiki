<%@ taglib uri="/WEB-INF/tld/jspwiki.tld" prefix="wiki" %>

<%-- Inserts page content for preview. --%>

<h1 class="preview">
   This is a PREVIEW!  Hit "Keep Editing" to go back to the editor,
   or hit "Save" if you're happy with what you see.
</h1>
<hr />

<div class="preview">
   <wiki:Translate><%=pageContext.getAttribute("usertext",PageContext.REQUEST_SCOPE)%></wiki:Translate>
</div>

<br clear="all" />

<hr />

<h1 class="preview">
   This is a PREVIEW!  Hit "Keep Editing" to go back to the editor,
   or hit "Save" if you're happy with what you see.
</div>

<hr />

<wiki:Editor>
  <textarea class="editor" rows="4" cols="80" readonly="true" name="text"><%=pageContext.getAttribute("usertext", PageContext.REQUEST_SCOPE) %></textarea>

  <h2 id="previewsavebutton" class="editor">
    <table class="editor">
      <tr>
        <td class="editor"><input type="submit" name="edit" value="Keep editing"/></td>
        <td class="editor"><input type="submit" name="ok" value="Save" /></td>
        <td class="editor"><input type="submit" name="cancel" value="Cancel" /></td>
      </tr>
    </table>
  </h2>
</wiki:Editor>
