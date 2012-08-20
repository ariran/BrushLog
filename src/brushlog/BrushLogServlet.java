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
   User currentUser = null;
   String errorText = null;

   try {
      currentUser = checkLogin(req);
   }
   catch (UserException ue) {
      switch (ue.reason) {
      case DUPLICATE:
         errorText = "Antamasi käyttäjänimi on jo varattu.";
         break;
      case PASSWORD_MISSING:
         errorText = "Anna salasana.";
         break;
      case WRONG_PASSWORD:
         errorText = "Salasana väärin.";
         break;
      default:
      }
   }

   if (currentUser == null) {
      try {
         req.setAttribute("ERROR_TEXT", errorText);
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

   String responsePage = null;

   if (currentUser.id.equalsIgnoreCase("ADMIN")) {
      responsePage = "admin.jsp";
   }
   else {
      String action = req.getParameter("ACTION");

      Calendar calendar = getCalendarBean(session);
      Records records = getRecordsBean(session, currentUser);

      if ("newEntryAction".equals(action)) {
         String dayNumber = req.getParameter("DAYNUMBER");
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
      else if ("changePassword".equals(action)) {
         responsePage = "changepwd.jsp";
      }
      else if ("doPasswordChange".equals(action)) {
         System.out.println("***doPasswordChange");
         String oldPwd = req.getParameter("oldpwd");
         String newPwd = req.getParameter("newpwd");
         String newPwd2 = req.getParameter("newpwd2");
         if (oldPwd == null || newPwd == null || newPwd2 == null) {
            req.setAttribute("PWDMSG", "Tieto puuttuu.");
         }
         else {
            boolean pwdValid = false;
            try {
               currentUser.validatePassword(oldPwd);
               pwdValid = true;
            }
            catch (Exception e) {
            }
            if (!pwdValid || !newPwd.equals(newPwd2)) {
               req.setAttribute("PWDMSG", "Tiedot eivät täsmää.");
            }
            else {
               if (DbService.updatePassword(currentUser.id, newPwd)) {
                  req.setAttribute("PWDMSG", "Salasanan vaihto onnistui.");
               }
               else {
                  req.setAttribute("PWDMSG", "Salasanan vaihto epäonnistui.");
               }
            }
         }
         responsePage = "changepwd.jsp";
      }
      else if ("logout".equals(action)) {
         session.invalidate();
         resp.sendRedirect("/login.jsp");
         return;
      }
      else { // "cancelPasswordChange" & all others
         req.setAttribute("CALBEAN", new CalendarUIBean(calendar, records));
         responsePage = "calendar.jsp";
      }
   }

   // Forward to response page.

   User u = (User) session.getAttribute("USER");
   req.setAttribute("USERNAME", u.name);
   try {
      req.getRequestDispatcher(responsePage).forward(req, resp);
   }
   catch (javax.servlet.ServletException se) {
      System.err.println(se.toString());
   }
}

/**
 * checkLogin
 */
private User checkLogin(HttpServletRequest req) throws UserException {
   User currentUser = (User) req.getSession().getAttribute("USER");
   if (currentUser != null) {
      return currentUser;
   }

   String username = req.getParameter("username");

   if (username != null && username.trim().length() > 0) {
      Users users = new Users();
      User newUser = users.getUserById(username);
      newUser.validatePassword(req.getParameter("password"));
      return newUser;
   }

   String otherUsername = req.getParameter("otherusername");

   if (otherUsername != null && otherUsername.trim().length() > 0) {
      otherUsername = otherUsername.trim();
      User newUser = new User(otherUsername.toUpperCase(), otherUsername);
      newUser.setPasswordClearText(req.getParameter("password"));
      (new Users()).addUser(newUser);
      return newUser;
   }

   return null;
}

/*
 * getRecordsBean
 */
private Records getRecordsBean(HttpSession session, User currentUser) {

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