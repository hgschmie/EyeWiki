<%@ taglib uri="/WEB-INF/tld/jspwiki.tld" prefix="wiki" %>

<h1 class="prefs">Setting your preferences</h3>

<div class="prefs">
  This is a page which allows you to set up all sorts of interesting things.
  You need to have cookies enabled for this to work, though.
</div>

<form action="<wiki:Variable var="baseURL"/>UserPreferences.jsp" 
      method="post"
      accept-charset="UTF-8">

   <table class="prefs">
   <tr>
     <td class="prefsmsg">User name:</td>
     <td class="prefs"><input type="text" name="username" size="30" value="<wiki:UserName/>" /></td>
   </tr>
   <tr>
     <td class="prefsmsg" colspan="2">This must be a proper WikiName, no punctuation.</td>
   </tr>
   <tr>
     <td class="prefsmsg" colspan="2"><input type="submit" name="ok" value="Set my preferences!" /></td>
   </tr>
   </table>
   <input type="hidden" name="action" value="save" />
</form>

<hr />

<h1 class="prefs">Removing your preferences</h3>

<div class="prefs">
  In some cases, you may need to remove the above preferences from the computer.
  Click the button below to do that.  Note that it will remove all preferences
  you've set up, permanently.  You will need to enter them again.
</div>

<form action="<wiki:Variable var="baseURL"/>UserPreferences.jsp"
      method="POST"
      accept-charset="UTF-8">
   <table class="prefs">
   <tr>
     <td class="prefsmsg" colspan="2"><input type="submit" name="clear" value="Remove preferences from this computer" /></td>
   </tr>
   </table>
</form>
