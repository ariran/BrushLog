package brushlog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

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

   if (!isLoggedIn(req)) {
      resp.setContentType("text/plain");
      resp.getWriter().println("Not logged in.");
      return;
   }

   String response = "";
   
   String commandString = req.getParameter("COMMAND");
   if (commandString == null) {
      sendResponse(req, resp, response);
      return;
   }
   
   String command = getParameters(commandString)[0];

   if (command.equals("list-all-records") || command.equals("lar")) {
      resp.setContentType("text/plain");

      List<User> allUsers = DbService.getAllUsers();

      for (User user : allUsers) {
         resp.getWriter().println(user.id + "*" + user.name + "*"
                                                      + user.getPasswordHash());

         HashMap<String, Record> allRecs = DbService.getAllRecords(user.id);

         if (allRecs != null) {
            for (String s : allRecs.keySet()) {
               Record r = (Record) allRecs.get(s);
               resp.getWriter().println(r.dateKey + "#" + r.value);
            }
         }
      }
      return;
   }
   else if (command.equals("import")) {
      resp.setContentType("text/html");
      resp.getWriter().println("<html><head></head>");
      resp.getWriter().println("<body>");
      resp.getWriter().println("<form action=\"/admin\" method=\"POST\">");
      resp.getWriter().println("<textarea name=\"inputdata\" rows=\"20\" cols=\"25\">");
      resp.getWriter().println("</textarea>");
      resp.getWriter().println("<input type=\"hidden\" name=\"command\" value=\"doimport\" />");
      resp.getWriter().println("<br /><input type=\"submit\" value=\"Import\" />");
      resp.getWriter().println("</form>");
      resp.getWriter().println("</body>");
      resp.getWriter().println("</html>");
      return;
   }
   else if (command.equals("doimport")) {
      String inputdata = req.getParameter("inputdata");
      if (inputdata == null) {
         response = "No input.";
      }
      else {
         try {
            DbService.purgeDatabase();
            DbService.importData(inputdata);
            response = "OK";
         }
         catch (java.io.IOException ioe) {
            response = "ERROR";
         }
      }
   }
   else if (command.equals("remove-user") || command.equals("rmu")) {
      String params[] = getParameters(commandString);
      if (params.length > 0) {
         DbService.deleteUser(params[0]);
         response = "OK";
      }
      else {
         response = "Missing parameter.";
      }
   }
   else if (command.equals("change-password") || command.equals("pwd")) {
      String params[] = getParameters(commandString);
      if (params.length > 2) {
         if (DbService.updatePassword(params[1], params[2])) {
            response = "Salasanan vaihto onnistui.";
         }
         else {
            response = "Salasanan vaihto epäonnistui.";
         }
      }
      else {
         response = "Missing parameter.";
      }
   }
   else if ("logout".equals(command)) {
      req.getSession().invalidate();
      resp.sendRedirect("/login.jsp");
      return;
   }
   else {
      response = "Command not recognized.";
      return;
   }

   sendResponse(req, resp, response);
}

/**
 * doGet
 */
public void doGet(HttpServletRequest req, HttpServletResponse resp)
   throws IOException {
}

/**
 * getParameter
 *
 * Returns the command line parameters given for a command.
 */
private String[] getParameters(String commandString) {
   
   ArrayList<String> tokens = new ArrayList<String>();
   StringTokenizer st = new StringTokenizer(commandString);
   
   while (st.hasMoreTokens()) {
      tokens.add(st.nextToken());
   }
   
   return tokens.toArray(new String[0]);
}

/**
 * isLoggedIn
 */
private boolean isLoggedIn(HttpServletRequest req) {
   return (req.getSession().getAttribute("USER") != null);
}

/**
 * sendResponse
 */
private void sendResponse(HttpServletRequest req, HttpServletResponse resp, String response) {
   // Forward to response page.

   User u = (User) req.getSession().getAttribute("USER");
   req.setAttribute("USERNAME", u.name);
   req.setAttribute("RESPONSE", response);
   
   try {
      req.getRequestDispatcher("admin.jsp").forward(req, resp);
   }
   catch (javax.servlet.ServletException se) {
      System.err.println(se.toString());
   }
   catch (java.io.IOException ioe) {
      System.err.println(ioe.toString());
   }
}
}