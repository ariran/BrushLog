package brushlog;

import java.util.Calendar;
import java.util.HashMap;

public class Records implements java.io.Serializable {

private String userid;
private HashMap<String, Record> allRecords;

public Records(String uid) {
   userid = uid;
   getAllRecords();
}

public void updateRecord(Calendar cal, String day) {
   
   String key = getKey(cal, day);
   Record record = allRecords.get(key);
   if (record == null) {
      record = new Record(key, "1");
      allRecords.put(key, record);
      // System.out.println("***updateRecord - NEW ENTRY WITH KEY: " + key);
   }
   else {
      record.rollValue();
   }
   DbService.addRecord(record, userid);
}

public HashMap<String, Record> getAllRecords() {
   if (allRecords == null) {
      allRecords = DbService.getAllRecords(userid);
   }
   
   return allRecords;
}

public String getRecordValue(Calendar cal, String day) {
   String key = getKey(cal, day);
   // System.out.println("***getRecordValue - ENTRY KEY: " + key);
   Record record = allRecords.get(key);
   if (record != null) {
      return record.value;
   }
   return null;
}

private String getKey(Calendar cal, String day) {
   String key = String.format("%tY-%tm-%s", cal, cal, day);
   return key;
}
}