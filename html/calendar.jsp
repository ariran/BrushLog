<html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="brushlog.CalendarUIBean" %>
<%@ page import="brushlog.User" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<title>Hammaspesujen kirjaaminen</title>
</head>

<body>

<script type="text/javascript">
function submitForm(day) {
   document.forms["calendarForm"].elements["ACTION"].value = "newEntryAction";
   document.forms["calendarForm"].elements["DAYNUMBER"].value = day;
   document.forms["calendarForm"].submit();
}
function moveBackwards() {
   document.forms["calendarForm"].elements["ACTION"].value = "moveBackwards";
   document.forms["calendarForm"].submit();
}
function moveForwards() {
   document.forms["calendarForm"].elements["ACTION"].value = "moveForwards";
   document.forms["calendarForm"].submit();
}
function logout() {
   document.forms["calendarForm"].elements["ACTION"].value = "logout";
   document.forms["calendarForm"].submit();
}
function changePassword() {
   document.forms["calendarForm"].elements["ACTION"].value = "changePassword";
   document.forms["calendarForm"].submit();
}
</script> 

<%
User user = (User) session.getAttribute("USER");
boolean isLoggedIn = user != null;
%>
<h1>Hammaspesujen kirjaaminen</h1>
<%
if (!isLoggedIn) {
%>
   <strong>Et ole sisäänkirjautunut.</strong>
   <p><a href="login.jsp">Kirjaudu sisään</a></p>
<%
}
else {
   //String userName = user.name;
%>
   <table><tr>
   <td>
      <h3>Pesukalenteri (<%= user.name%>)</h3>
      <%= ((CalendarUIBean) session.getAttribute("CALUIBEAN")).toHtml() %>

      <form name="calendarForm" action="brushlog" method="post">
      <input type="hidden" name="ACTION" />
      <input type="hidden" name="DAYNUMBER" />
      <input type="button" value="<<" onclick="javascript:moveBackwards()" />
      <input type="button" value=">>" onclick="javascript:moveForwards()" />
      </form>

      <table>
         <tr><td bgcolor="#FF0000" style="{background-color:#FF0000}">&nbsp;&nbsp;&nbsp;&nbsp;</td><td>= 0 pesukertaa</td></tr>
         <tr><td bgcolor="#FFFF00" style="{background-color:#FFFF00}">&nbsp;&nbsp;&nbsp;&nbsp;</td><td>= 1 pesukerta</td></tr>
         <tr><td bgcolor="#32CD32" style="{background-color:#32CD32}">&nbsp;&nbsp;&nbsp;&nbsp;</td><td>= 2 pesukertaa</td></tr>
      </table>
   </td>
   <td valign="top">
      <h3>Chat</h3>
      <iframe src="/chat?ACTION=listEntries" width="350" height="150" frameborder="1">
      <p>Your browser does not support iframes.</p>
      </iframe><br />
      <form name="chatForm" action="/chat" method="post">
         <input type="text" name="CHAT_TEXT" size="40" />
         <input type="submit" value="Sano" />
         <input type="hidden" name="ACTION" value="newChatEntry" />
      </form>
   </td></tr>
   <tr>
      <td>&nbsp;</td>
      <td align="right" valign="middle">
         <a href="javascript:changePassword()">Vaihda salasana</a>&nbsp;
         <a href="javascript:logout()">Kirjaudu ulos</a>
      </td>
   </tr></table>
<%
}
%>
</body>
</html>
