package brushlog;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AdminServlet extends HttpServlet {

/**
 * doPost
 */
public void doPost(HttpServletRequest req, HttpServletResponse resp)
   throws IOException {

   String action = getAction(req);

   if (action.equalsIgnoreCase("DOIMPORT")) {
      String inputdata = req.getParameter("inputdata");
      resp.setContentType("text/plain");
      try {
         DbService.purgeDatabase();
         DbService.importData(inputdata);
         resp.getWriter().println("OK");
      }
      catch (java.io.IOException ioe) {
         resp.getWriter().println("ERROR");
      }
   }
}

/**
 * doGet
 */
public void doGet(HttpServletRequest req, HttpServletResponse resp)
   throws IOException {

   String action = getAction(req);

   if (action.equalsIgnoreCase("LIST_ALL_RECORDS")) {
      resp.setContentType("text/plain");

      List<Users.User> allUsers = DbService.getUsers();

      for (Users.User user : allUsers) {
         resp.getWriter().println(user.id + "*" + user.name);

         HashMap<String, Record> allRecs = DbService.getAllRecords(user.id);

         if (allRecs != null) {
            for (String s : allRecs.keySet()) {
               Record r = (Record) allRecs.get(s);
               resp.getWriter().println(r.dateKey + "#" + r.value);
            }
         }
         else {
            resp.getWriter().println("No records found.");
         }
      }
   }
   else if (action.equalsIgnoreCase("IMPORT_RECORDS")) {
      resp.setContentType("text/html");
      resp.getWriter().println("<html><head></head>");
      resp.getWriter().println("<body>");
      resp.getWriter().println("<form action=\"/admin\" method=\"POST\">");
      resp.getWriter().println("<textarea name=\"inputdata\" rows=\"20\" cols=\"25\">");
      resp.getWriter().println("</textarea>");
      resp.getWriter().println("<input type=\"hidden\" name=\"action\" value=\"doimport\" />");
      resp.getWriter().println("<br /><input type=\"submit\" value=\"Import\" />");
      resp.getWriter().println("");
      resp.getWriter().println("");
      resp.getWriter().println("");
      resp.getWriter().println("");
      resp.getWriter().println("</form>");
      resp.getWriter().println("</body>");
      resp.getWriter().println("</html>");
   }
}

private String getAction(HttpServletRequest req) {

   String action = req.getParameter("action");
   if (action == null) {
      action = req.getParameter("ACTION");
   }
   
   return action;
}
}