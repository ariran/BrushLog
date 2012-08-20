package brushlog;

import java.util.List;
import com.google.appengine.api.datastore.Key;

public class User implements java.io.Serializable {

public enum Validity {
   VALID, DUPLICATE, PASSWORD_MISSING, WRONG_PASSWORD;
}

public String id;
public String name;
private String password = null;
public Key key = null;

public User(String userId, String userName)  {
   id = userId;
   name = userName;
}

// public User(String userId, String userName, String pwd)  {
   // id = userId;
   // name = userName;
   // password = pwd;
// }

public Validity checkValidity() {
   
   if (DbService.userExists(this.id)) {
      return Validity.DUPLICATE;
   }
   
   if (password == null || password.length() == 0) {
      return Validity.PASSWORD_MISSING;
   }
   
   return Validity.VALID;
}

public void validatePassword(String pwd) throws UserException {
   if (!password.equals(MD5(pwd.trim()))) {
      throw new UserException(Validity.WRONG_PASSWORD);
   }
}

public void setPasswordClearText(String pwd) {
   if (pwd != null && pwd.trim().length() > 0) {
      password = MD5(pwd.trim());
   }
   else {
      password = null;
   }
}

public void setPasswordHash(String pwd) {
   password = pwd;
}

public String getPasswordHash() {
   return password;
}

public static String MD5(String md5) {
   md5 += "suolaus";
   try {
        java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
        byte[] array = md.digest(md5.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; ++i) {
          sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
       }
        return sb.toString();
    } catch (java.security.NoSuchAlgorithmException e) {
    }
    return null;
}
}