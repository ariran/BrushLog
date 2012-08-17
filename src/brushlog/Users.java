package brushlog;

import java.util.List;

public class Users {

private List<Users.User> users;

/*
 * addUser
 */
public Users.User addUser(String userName) {
   
   String newUserId = userName.trim().toUpperCase();
   return addUser(newUserId, userName);
}

/*
 * addUser
 */
public Users.User addUser(String userid, String userName) {
   
   Users.User newUser = new Users.User(userid, userName.trim());
   DbService.addUser(newUser);
   return newUser;
}

/*
 * getUsers
 */
public List<Users.User> getUsers() {
   
   return DbService.getUsers();
}

/*
 * getUserById
 */
public Users.User getUserById(String userid) {
   
   List<Users.User> users = getUsers();
   for (Users.User user : users) {
      if (user.id.equals(userid)) {
         return user;
      }
   }
   return null;
}

////////////////////////////////////////////////////////////////////////////////
public static class User {

public String id;
public String name;

public User(String userId, String userName)  {
   id = userId;
   name = userName;
}
}
}