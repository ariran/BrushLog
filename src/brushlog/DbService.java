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
public static void addUser(Users.User user) {

   if (userExists(user)) {
      return;
   }

   DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
   
   Key usersKey = KeyFactory.createKey("Users", "ALL_USERS");
   Entity users = new Entity("Users", usersKey);
   datastore.put(users);

   Entity newUser = new Entity("Users.User", usersKey);
   newUser.setProperty("id", user.id);
   newUser.setProperty("name", user.name);
   datastore.put(newUser);
}

/**
 * getUsers
 */
public static List<Users.User> getUsers() {

   ArrayList<Users.User> newUsers = null;
   DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

   Key usersKey = KeyFactory.createKey("Users", "ALL_USERS");
   Query query = new Query("Users.User", usersKey)
                              .addSort("name", Query.SortDirection.ASCENDING);
   List<Entity> users = datastore.prepare(query)
                                 .asList(FetchOptions.Builder.withDefaults());

   if (users != null) {
      newUsers = new ArrayList<Users.User>();
      for (Entity user : users) {
         Users.User newUser = new Users.User(
            (String) user.getProperty("id"), (String) user.getProperty("name"));
         newUsers.add(newUser);
      }
   }
   
   return newUsers;
}

/**
 * userExists
 */
private static boolean userExists(Users.User user) {
   DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
   
   Query query = new Query("Users.User").setFilter(
         new Query.FilterPredicate("id", Query.FilterOperator.EQUAL, user.id));
   
   Entity e = null;
   try {
      e = datastore.prepare(query).asSingleEntity();
   }
   catch (PreparedQuery.TooManyResultsException tmre) {
      System.err.println(tmre);
      return true;
   }
   
   return e != null;
}

/**
 * addRecord
 */
public static void addRecord(Record record, String userid) {

   DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
   
   Key recordsKey = KeyFactory.createKey("Records", userid);
   Entity records = new Entity("Records", recordsKey);
   datastore.put(records);

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
         Users.User user = new Users.User(userid, t.nextToken());
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
   
   List<Users.User> allUsers = getUsers();
   if (allUsers != null) {
      for (Users.User u : allUsers) {
         datastore.delete(KeyFactory.createKey("Records", u.id));
         System.out.println("***purgeDatabase -- Records for " + u.id + " removed.");
      }
   }

   datastore.delete(KeyFactory.createKey("Users", "ALL_USERS"));
   System.out.println("***purgeDatabase -- All users removed.");
}
}