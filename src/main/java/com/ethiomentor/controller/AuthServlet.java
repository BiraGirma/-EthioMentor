package com.ethiomentor.controller;

import com.ethiomentor.model.User;
import com.ethiomentor.service.AuthService;
import com.ethiomentor.util.JsonUtil;
import java.io.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
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
}
