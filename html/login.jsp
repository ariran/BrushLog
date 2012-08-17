<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<!-- The HTML 4.01 Transitional DOCTYPE declaration-->
<!-- above set at the top of the file will set     -->
<!-- the browser's rendering engine into           -->
<!-- "Quirks Mode". Replacing this declaration     -->
<!-- with a "Standards Mode" doctype is supported, -->
<!-- but may lead to some differences in layout.   -->

<%@ page import="java.util.List" %>
<%@ page import="brushlog.Users" %>

<%
List<Users.User> users = (new Users()).getUsers();
%>

<html>
<head>
   <meta http-equiv="content-type" content="text/html; charset=UTF-8">
   <title>Hammaspesujen kirjaaminen</title>
</head>
<body>
   <h1>Hammaspesujen kirjaaminen</h1>
   <h3>Sisäänkirjautuminen</h3>
   <form action="/brushlog" method="post">
      <p>Valitse käyttäjänimi:</p>
      <select name="username">
      <option value=""></option>
         <%
         if (users != null) {
            for (Users.User user : users) {
               out.println(
                  "<option name='userid' value='" + user.id + "'>" + user.name + "</option>");
            }
         }
         %>
      </select>
      <p>Muu käyttäjänimi:</p>
      <input type="text" name="otherusername" />
      <br /><br />
      <input type="submit" value="Kirjaudu" />
   </form>
</body>
</html>
