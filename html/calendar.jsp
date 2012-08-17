<html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="brushlog.CalendarUIBean" %>
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
</script> 

<%
String userName = (String) request.getAttribute("USERNAME");
%>
<h1>Hammaspesujen kirjaaminen</h1>
<%
if (userName == null || userName.trim().length() == 0) {
%>
   <strong>Et ole sisäänkirjautunut.</strong>
<%
}
else {
%>
<h3>Pesukalenteri (<%= userName%>)</h3>
<%= ((CalendarUIBean) request.getAttribute("CALBEAN")).toHtml() %>
<%
}
%>

<form name="calendarForm" action="brushlog" method="post">
   <input type="hidden" name="ACTION" />
   <input type="hidden" name="DAYNUMBER" />
   <input type="button" value="<<" onclick="javascript:moveBackwards()">
   <input type="button" value=">>" onclick="javascript:moveForwards()">
   <p><a href="javascript:logout()">Kirjaudu ulos</a></p>
</form>

</body>
</html>
