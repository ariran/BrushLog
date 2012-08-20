<html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<title>Admin</title>
</head>

<body>

<script type="text/javascript">
</script> 

<%
String userName = (String) request.getAttribute("USERNAME");
String resp = (String) request.getAttribute("RESPONSE");
if (resp == null) {
   resp = "";
}
%>
<h2>Admin</h2>
<%
if (userName == null || userName.trim().length() == 0) {
%>
   <strong>Not logged in.</strong>
<%
}
else {
%>
<form name="adminForm" action="admin" method="post">
   Enter command:<br />
   <input type="text" size="60" name="COMMAND" />
   <input type="submit" value="Enter" />
   <p><pre><%= resp %></pre></p>
</form>
<%
}
%>
</body>
</html>
