<html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<title>Hammaspesujen kirjaaminen</title>
</head>

<body>

<script type="text/javascript">
function logout() {
   document.forms["passwordForm"].elements["ACTION"].value = "logout";
   document.forms["passwordForm"].submit();
}
function changePassword() {
   document.forms["passwordForm"].elements["ACTION"].value = "doPasswordChange";
   document.forms["passwordForm"].submit();
}
function cancel() {
   document.forms["passwordForm"].elements["ACTION"].value = "cancelPasswordChange";
   document.forms["passwordForm"].submit();
}
</script> 

<%
String userName = (String) request.getAttribute("USERNAME");
String pwdmsg = (String) request.getAttribute("PWDMSG");
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
   <h3>Salasanan vaihto</h3>
   <%
   if (pwdmsg != null && pwdmsg.trim().length() > 0) {
      %>
         <p><%= pwdmsg %></p>
         <p><a href="brushlog">Palaa</a></p>
      <%
   }
   else {
      %>
      <form name="passwordForm" action="brushlog" method="post">
         <input type="hidden" name="ACTION" value="" />
         Vanha salasana<br />
         <input type="password" name="oldpwd" /><br />
         Uusi salasana<br />
         <input type="password" name="newpwd" /><br />
         Uusi salasana uudelleen<br />
         <input type="password" name="newpwd2" /><br />
         <p><input type="button" onclick="javascript:changePassword()" value="Vaihda salasana" />
            <input type="button" onclick="javascript:cancel()" value="Peruuta" /></p>
         <p><a href="javascript:logout()">Kirjaudu ulos</a></p>
      </form>
      <%
   }
   %>
<%
}
%>


</body>
</html>
