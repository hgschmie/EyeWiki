<%@ taglib uri="/WEB-INF/tld/jspwiki.tld" prefix="wiki" %>

<h1 class="conflict">Oops!  Someone modified the page while you were editing it!</h1>

<div class="conflict">
  Since I am stupid and can't figure out what the difference
  between those pages is, you will need to do that for me.  I've
  printed here the text (in Wiki) of the new page, and the
  modifications you made.  You'll now need to copy the text onto a
  scratch pad (Notepad or emacs will do just fine), and then edit
  the page again.
</div>

<div class="conflict">
  Note that when you go back into the editing mode, someone might have
  changed the page again.  So be quick.
</div>

<hr />

<h2 class="conflict">Here is the modified text (by someone else):</h2>

<div class="conflict">
  <%=pageContext.getAttribute("conflicttext",PageContext.REQUEST_SCOPE)%>
</div>

<hr />

<h2 class="conflict">Here is your text:</h2>

<div class="conflict">
  <%=pageContext.getAttribute("usertext",PageContext.REQUEST_SCOPE)%>
</div>

<table class="pageaction">
  <tr>
    <td><a class="wikicontent" href="#Top">Go to top</a></td>
    <td>
      <wiki:CheckVersion mode="latest">
        <wiki:Permission permission="edit">
          <wiki:EditLink>Edit this page</wiki:EditLink>
        </wiki:Permission>
      </wiki:CheckVersion>
    </td>
    <td>
      <wiki:PageInfoLink>More info...</wiki:PageInfoLink>
    </td>
  </tr>
</table>
