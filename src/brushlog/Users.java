package brushlog;

import java.util.List;
import com.google.appengine.api.datastore.Key;

public class Users implements java.io.Serializable {

private List<User> users;

/*
 * addUser
 */
public void addUser(User user) throws UserException {
   
   User.Validity valid = user.checkValidity();
   
   if (valid != User.Validity.VALID) {
      throw new UserException(valid);
   }

   DbService.addUser(user);
}

/*
 * addUser. This is used when user data is being imported into database.
 */
// public User addUser(String userid, String userName, String pwd) {
   
   // User newUser = new User(userid, userName.trim(), pwd);
   // DbService.addUser(newUser);
   // return newUser;
// }

/*
 * getUsers
 */
public List<User> getUsers() {
   
   return DbService.getAllUsers();
}

/*
 * getUserById
 */
public User getUserById(String userid) {
   
   List<User> users = getUsers();
   for (User user : users) {
      if (user.id.equals(userid)) {
         return user;
      }
   }
   return null;
}
}