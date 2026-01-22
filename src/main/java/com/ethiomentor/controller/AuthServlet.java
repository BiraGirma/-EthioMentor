package com.ethiomentor.controller;

import com.ethiomentor.model.User;
import com.ethiomentor.service.AuthService;
<<<<<<< HEAD
=======
<<<<<<< HEAD
import com.ethiomentor.util.JsonUtil;
import java.io.*;
=======
>>>>>>> 8fb5b46 (finilized)
>>>>>>> 2bdd21ccaf16834af801aaad4a078e24691fa627

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
<<<<<<< HEAD
=======
<<<<<<< HEAD
@WebServlet("/login")
public class AuthServlet extends HttpServlet {

  private AuthService authService = new AuthService();

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {

    String email = req.getParameter("email");
    String password = req.getParameter("password");
    String role = req.getParameter("role");

 // Map frontend role to DB role
 if (role != null) {
     role = role.trim().toLowerCase();
     switch (role) {
         case "mentee":
             role = "student";
             break;
         case "mentor":
             role = "mentor";
             break;
         case "admin":
             role = "admin";
             break;
         default:
             role = "student"; // default fallback
     }
 }

    User user = authService.login(email, password, role);

    if (user != null) {
      HttpSession session = req.getSession();
      session.setAttribute("user", user);
      res.sendRedirect(req.getContextPath() + "/dashboard");

    } else {
      req.setAttribute("error", "Invalid email, password, or role");
      req.getRequestDispatcher("login.jsp")
         .forward(req, res);
    }
  }
=======
>>>>>>> 2bdd21ccaf16834af801aaad4a078e24691fa627
import java.io.IOException;

@WebServlet("/login")
public class AuthServlet extends HttpServlet {

    private final AuthService authService = new AuthService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        String identifier = req.getParameter("email"); // email OR username
        String password = req.getParameter("password");

        User user = authService.login(identifier, password);

        if (user != null) {
            HttpSession session = req.getSession(true);
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getId());
            session.setAttribute("role", user.getRole());

            if ("admin".equalsIgnoreCase(user.getRole())) {
                res.sendRedirect(req.getContextPath() + "/admin");
            } else if ("mentor".equalsIgnoreCase(user.getRole())) {
                res.sendRedirect(req.getContextPath() + "/mentor-dashboard");
            } else {
                res.sendRedirect(req.getContextPath() + "/dashboard");
            }

        } else {
            req.setAttribute("error", "Invalid credentials");
            req.getRequestDispatcher("login.jsp").forward(req, res);
        }
    }
<<<<<<< HEAD
=======
>>>>>>> 8fb5b46 (finilized)
>>>>>>> 2bdd21ccaf16834af801aaad4a078e24691fa627
}
