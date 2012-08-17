package brushlog;

import brushlog.Records;
import java.util.Calendar;

public class CalendarUIBean implements java.io.Serializable {

private String days[];
private Calendar originalCalendar;
private Calendar calendar;
private String monthYear;
private Records records;

public CalendarUIBean(Calendar cal, Records recs) {
   monthYear = String.format("%tB %tY", cal, cal);
   calendar = (Calendar) cal.clone();
   originalCalendar = cal;
   records = recs;
   days = new String[42];

   calendar.set(Calendar.DAY_OF_MONTH, 1);
   int firstWeekday = getFirstWeekday(calendar);

   fillDayArray(firstWeekday);
}

public String toHtml() {
   StringBuffer out = new StringBuffer();
   out.append("<table>");
   out.append("<tr><th colspan=\"7\">"  + monthYear + "</th></tr>");
   out.append("<tr><th>MA</th><th>TI</th><th>KE</th><th>TO</th><th>PE</th>" +
                                                "<th>LA</th><th>SU</th></tr>");
   boolean firstRow = true;
   for (int i = 0; i < 42; i++) {
      if (i % 7 == 0) {
         if (firstRow) {
            firstRow = false;
         }
         else {
            out.append("</tr>");
         }
         out.append("<tr>");
      }
      boolean inFuture = dayInFuture(days[i]);
      String color = getColor(days[i], inFuture);
      out.append("<td id=\"" + days[i] + "\" ")
         .append("style=\"{background-color:" + color + "}\"");
      if (!inFuture) {
         out.append(" onclick=\"submitForm(this.id)\"");
      }
      out.append(">" + days[i] + "</td>");
   }
   out.append("</tr></table>");
   return out.toString();
}

private int getFirstWeekday(Calendar cal) {
   int firstWeekday = cal.get(Calendar.DAY_OF_WEEK) - 2;
   if (firstWeekday < 0) {
      firstWeekday = 6;
   }
   return firstWeekday;
}

private void fillDayArray(int firstWeekday) {
   for (int i = 0; i < firstWeekday; i++) {
      days[i] = "";
   }

   days[firstWeekday] = "1";
   for (int i = firstWeekday + 1; i < 42; i++) {
      int day = getNextDay();
      if (day > 0) {
         days[i] = Integer.toString(day);
      }
      else {
         for (int j = i; j < 42; j++) {
            days[j] = "";
         }
         break;
      }
   }
}

private int getNextDay() {
   int thisMonth = calendar.get(Calendar.MONTH);
   calendar.add(Calendar.DAY_OF_MONTH, 1);
   int otherMonth = calendar.get(Calendar.MONTH);
   if (thisMonth == otherMonth) {
      return calendar.get(Calendar.DAY_OF_MONTH);
   }
   else {
      return -1;
   }
}

private String getColor(String day, boolean dayInFuture) {

   if (day.trim().length() == 0 || dayInFuture) {
      return "White";
   }

   String recordValue = records.getRecordValue(originalCalendar, day);
   // System.out.println("***getColor - recordValue: " + recordValue);
   int valueAsInt = 0;
   if (recordValue != null) {
      valueAsInt = Integer.parseInt(recordValue);
   }
   // System.out.println("***getColor - valueAsInt: " + valueAsInt);
   switch (valueAsInt) {
   case 1:
      return "Yellow";
   case 2:
      return "Green";
   default:
      return "Red";
   }
}

private boolean dayInFuture(String day) {

   if (day.trim().length() == 0) {
      return true;
   }
   
   Calendar tempCal = (Calendar) originalCalendar.clone();
   tempCal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
   Calendar today = Calendar.getInstance();

   return tempCal.after(today);
}
}