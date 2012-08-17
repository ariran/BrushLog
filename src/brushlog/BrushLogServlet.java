package brushlog;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class BrushLogServlet extends HttpServlet {

/**
 * doPost
 */
public void doPost(HttpServletRequest req, HttpServletResponse resp)
   throws IOException {

   HttpSession session = req.getSession();

   Users.User currentUser = checkLogin(req);
   // System.out.println("***doPost -- currentUser " + currentUser);

   if (currentUser == null) {
      try {
         req.getRequestDispatcher("login.jsp").forward(req, resp);
      }
      catch (javax.servlet.ServletException se) {
         System.err.println(se.toString());
      }
      return;
   }
   else {
      session.setAttribute("USER", currentUser);
   }

   String action = req.getParameter("ACTION");
   String responsePage = null;

   Calendar calendar = getCalendarBean(session);
   Records records = getRecordsBean(session, currentUser);

   if ("newEntryAction".equals(action)) {
      String dayNumber = req.getParameter("DAYNUMBER");
      // System.out.println("***DAYNUMBER = " + dayNumber);

      records.updateRecord(calendar, dayNumber);

      req.setAttribute("CALBEAN", new CalendarUIBean(calendar, records));
      responsePage = "calendar.jsp";
   }
   else if ("moveBackwards".equals(action)) {
      calendar.add(Calendar.MONTH, -1);
      req.setAttribute("CALBEAN", new CalendarUIBean(calendar, records));
      responsePage = "calendar.jsp";
   }
   else if ("moveForwards".equals(action)) {
      calendar.add(Calendar.MONTH, 1);
      req.setAttribute("CALBEAN", new CalendarUIBean(calendar, records));
      responsePage = "calendar.jsp";
   }
   else if ("logout".equals(action)) {
      session.invalidate();
      resp.sendRedirect("/login.jsp");
      return;
   }
   else {
      req.setAttribute("CALBEAN", new CalendarUIBean(calendar, records));
      responsePage = "calendar.jsp";
   }

   // Forward to response page.

   Users.User u = (Users.User) session.getAttribute("USER");
   // System.out.println("***USER is now " + u);
   req.setAttribute("USERNAME", u.name);
   try {
      req.getRequestDispatcher(responsePage).forward(req, resp);
   }
   catch (javax.servlet.ServletException se) {
      System.err.println(se.toString());
   }
}

/**
 * doGet
 */
public void doGet(HttpServletRequest req, HttpServletResponse resp)
   throws IOException {

}

/**
 * handleLogin
 */
private Users.User checkLogin(HttpServletRequest req) {
   Users.User currentUser = (Users.User) req.getSession().getAttribute("USER");
   // System.out.println("***checkLogin -- currentUser " + currentUser);
   if (currentUser != null) {
      return currentUser;
   }

   String otherUsername = req.getParameter("otherusername");

   if (otherUsername != null && otherUsername.trim().length() > 0) {
      if (otherUsername.equalsIgnoreCase("admin")) {
         // TODO: Käsittele admin-käyttäjä !!
         //admin = true;
      }
      else {
         return (new Users()).addUser(otherUsername);
      }
   }

   String username = req.getParameter("username");
   // System.out.println("***checkLogin -- username " + username);

   if (username != null && username.trim().length() > 0) {
      Users users = new Users();
      Users.User newUser = users.getUserById(username);
      return newUser;
   }

   return null;
}

/*
 * getRecordsBean
 */
private Records getRecordsBean(HttpSession session, Users.User currentUser) {

   Records records = (Records) session.getAttribute("RECBEAN");
   if (records == null) {
      records = new Records(currentUser.id);
      session.setAttribute("RECBEAN", records);
   }
   return records;
}

/*
 * getCalendarBean
 */
private Calendar getCalendarBean(HttpSession session) {

   Calendar calendar = (Calendar) session.getAttribute("CALBEAN");
   if (calendar == null) {
      calendar = Calendar.getInstance();
      session.setAttribute("CALBEAN", calendar);
   }
   return calendar;
}
}