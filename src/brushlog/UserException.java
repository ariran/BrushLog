package brushlog;

public class UserException extends Exception {

public User.Validity reason;

public UserException(User.Validity r) {
   reason = r;
}
}
