package brushlog;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

public class DbService {

/**
 * addUser
 */
public static void addUser(User user) {

   DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

   Key usersKey = KeyFactory.createKey("Users", "ALL_USERS");
   
   Entity newUser = new Entity("User", usersKey);
   newUser.setProperty("id", user.id);
   newUser.setProperty("name", user.name);
   newUser.setProperty("password", user.getPasswordHash());
   datastore.put(newUser);
}

/**
 * updateUser
 */
public static boolean updatePassword(String userid, String pwdClearText) {
   
   User currentUser = getUser(userid);
   DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

   Key key = findUserKey(currentUser.id);
   if (key != null) {
      Entity user = new Entity(key);
      user.setProperty("id", currentUser.id);
      user.setProperty("name", currentUser.name);
      user.setProperty("password", User.MD5(pwdClearText));
      datastore.put(user);
      return true;
   }
   else {
      System.err.println(
           "updatePassword -- No key for user (" + currentUser.id + ") found.");
      return false;
   }
}

/**
 * getAllUsers
 */
public static List<User> getAllUsers() {

   ArrayList<User> newUsers = null;
   DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

   Key usersKey = KeyFactory.createKey("Users", "ALL_USERS");
   Query query = new Query("User", usersKey)
                              .addSort("name", Query.SortDirection.ASCENDING);
   List<Entity> users = datastore.prepare(query)
                                 .asList(FetchOptions.Builder.withDefaults());

   if (users != null) {
      newUsers = new ArrayList<User>();
      for (Entity user : users) {
         User newUser = new User(
            (String) user.getProperty("id"), (String) user.getProperty("name"));
         newUser.setPasswordHash((String) user.getProperty("password"));
         newUser.key = user.getKey();
         
         newUsers.add(newUser);
      }
   }

   return newUsers;
}

/**
 * userExists
 */
public static boolean userExists(String userid) {

   User user = getUser(userid);
   return user != null;
}

/**
 * getUser
 */
public static User getUser(String userid) {
   DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

   Query query = new Query("User").setFilter(
         new Query.FilterPredicate("id", Query.FilterOperator.EQUAL, userid));

   Entity user = null;
   try {
      user = datastore.prepare(query).asSingleEntity();
   }
   catch (PreparedQuery.TooManyResultsException tmre) {
      System.err.println(tmre);
      return null;
   }

   if (user != null) {
      User newUser = new User(
         (String) user.getProperty("id"), (String) user.getProperty("name"));
      newUser.setPasswordHash((String) user.getProperty("password"));
      newUser.key = user.getKey();
      
      return newUser;
   }
   else {
      return null;
   }
}

/**
 * findUserKey
 */
public static Key findUserKey(String userid) {
   DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

   Query query = new Query("User").setFilter(
         new Query.FilterPredicate("id", Query.FilterOperator.EQUAL, userid));

   Entity e = null;
   try {
      e = datastore.prepare(query).asSingleEntity();
   }
   catch (PreparedQuery.TooManyResultsException tmre) {
      System.err.println(tmre);
      return null;
   }

   return e.getKey();
}

/**
 * deleteUser
 */
public static void deleteUser(String userid) {
   DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

   Query query = new Query("User").setFilter(
         new Query.FilterPredicate("id", Query.FilterOperator.EQUAL, userid));

   Entity e = null;
   try {
      e = datastore.prepare(query).asSingleEntity();
   }
   catch (PreparedQuery.TooManyResultsException tmre) {
      System.err.println(tmre);
   }

   if (e != null)  {
      Key key = e.getKey();
      datastore.delete(key);
   }
}

/**
 * addRecord
 */
public static void addRecord(Record record, String userid) {

   DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

   Key recordsKey = KeyFactory.createKey("Records", userid);
   
   Entity newRecord = new Entity("Record", recordsKey);
   newRecord.setProperty("dateKey", record.dateKey);
   newRecord.setProperty("value", record.value);
   datastore.put(newRecord);
}

/*
 * getAllRecords
 */
public static HashMap<String, Record> getAllRecords(String userid) {

   DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

   Key recordsKey = KeyFactory.createKey("Records", userid);
   Query query = new Query("Record", recordsKey);
   List<Entity> recs = datastore.prepare(query)
                                 .asList(FetchOptions.Builder.withDefaults());

   HashMap<String, Record> records = new HashMap<String, Record>();
   if (recs != null) {
      for (Entity rec : recs) {
         String dateKey = (String) rec.getProperty("dateKey");
         Record record = new Record(dateKey, (String) rec.getProperty("value"));
         record.key = rec.getKey();
         
         records.put(dateKey, record);
      }
   }

   return records;
}

public static boolean importData(String inputData) throws java.io.IOException {
   BufferedReader data = new BufferedReader(new StringReader(inputData));

   Users users = new Users();
   String userid = null;
   String row = null;

   while ((row = data.readLine()) != null) {
      System.out.println("***importData -- row = " + row);
      if (row.indexOf("*") > 0) {
         StringTokenizer t = new StringTokenizer(row, "*");
         userid = t.nextToken();
         User user = new User(userid, t.nextToken());
         user.setPasswordHash(t.nextToken());
         addUser(user);
      }
      else if (row.indexOf("#") > 0) {
         StringTokenizer t = new StringTokenizer(row, "#");
         Record record = new Record(t.nextToken(), t.nextToken());
         addRecord(record, userid);
      }
   }
   return true;
}

/**
 * purgeDatabase
 */
public static void purgeDatabase() {

   DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

   List<User> allUsers = getAllUsers();
   if (allUsers != null) {
      for (User u : allUsers) {
         HashMap<String, Record> records = getAllRecords(u.id);
         if (records != null) {
            for (Record rec : records.values()) {
               datastore.delete(rec.key);
            }
            System.out.println(
                     "***purgeDatabase -- Records for " + u.name + " removed.");
         }
         else {
            System.out.println(
                    "***purgeDatabase -- No records found for " + u.name + ".");
         }
         datastore.delete(u.key);
         System.out.println("***purgeDatabase -- User " + u.name + " removed.");
      }
   }
}
}