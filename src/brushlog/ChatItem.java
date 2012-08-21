package brushlog;

import java.util.Calendar;
import com.google.appengine.api.datastore.Key;

public class ChatItem implements java.io.Serializable {

public String user = null;
public Calendar timestamp = null;
public String text = null;

public ChatItem(String u, Calendar c, String t) {
   user = u;
   timestamp = c;
   text = t;
}
}