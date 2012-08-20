package brushlog;

import java.util.Calendar;
import com.google.appengine.api.datastore.Key;

public class Record implements java.io.Serializable {

public String dateKey = null;
public String value = null;
public Key key = null;

public Record(String key, String val) {
   dateKey = key;
   value = val;
}

public void rollValue() {
   int newValue = Integer.parseInt(value) + 1;
   if (newValue > 2) {
      newValue = 0;
   }
   value = Integer.toString(newValue);
}
}