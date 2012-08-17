package brushlog;

import java.util.Calendar;

public class Record {

public String dateKey = null;
public String value = null;

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