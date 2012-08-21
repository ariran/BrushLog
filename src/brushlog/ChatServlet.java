package brushlog;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ChatServlet extends HttpServlet {

/**
 * doPost
 */
public void doPost(HttpServletRequest req, HttpServletResponse resp)
   throws IOException {

   performTask(req, resp);
}

/**
 * doGet
 */
public void doGet(HttpServletRequest req, HttpServletResponse resp)
   throws IOException {

   performTask(req, resp);
}

/**
 * doPost
 */
public void performTask(HttpServletRequest req, HttpServletResponse resp)
   throws IOException {

   HttpSession session = req.getSession();
   User currentUser = checkLogin(req);

   if (currentUser == null) {
      return;
   }

   String action = req.getParameter("ACTION");

   if ("newChatEntry".equals(action)) {
      String text = req.getParameter("CHAT_TEXT");
      if (text != null && text.trim().length() > 0) {
         ChatItem item = new ChatItem(currentUser.name, getLocalCalendar(), text);
         DbService.insertChatItem(item);
      }
      resp.sendRedirect("/calendar.jsp");
   }
   else if ("listEntries".equals(action)) {
      ChatItem[] items = DbService.getAllChatItems();
      resp.setContentType("text/html");
      resp.getWriter().println("<html><head></head>");
      resp.getWriter().println("<body><table>");
      for (int i = 0; i < items.length; i++) {
         resp.getWriter().println("<tr>");
         resp.getWriter().println(
            "<td style=\"{font-family:sans-serif;font-size:12px;font-weight:bold;}\">" + 
            formatTimestamp(items[i].timestamp) + 
            " - "  + items[i].user + "</td>" +
            "<td style=\"{font-family:sans-serif;font-size:12px;}\">" +
            items[i].text + "</td>");
         resp.getWriter().println("</tr>");
      }
      resp.getWriter().println("</table></body>");
      resp.getWriter().println("</html>");
   }
}

/**
 * checkLogin
 */
private User checkLogin(HttpServletRequest req) {
   User currentUser = (User) req.getSession().getAttribute("USER");
   return currentUser;
}

private String formatTimestamp(Calendar cal) {

   SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm");
   return df.format(cal.getTime());
}

/*
 * getLocalCalendar
 */
private Calendar getLocalCalendar() {
   
   Calendar calendar = 
                  Calendar.getInstance(TimeZone.getTimeZone("Europe/Helsinki"));
   return calendar;
}
}