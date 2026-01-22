package com.ethiomentor.controller;

import com.ethiomentor.model.User;
import com.ethiomentor.service.UserService;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import javax.servlet.ServletException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
<<<<<<< HEAD
=======
<<<<<<< HEAD
=======
>>>>>>> 2bdd21ccaf16834af801aaad4a078e24691fa627
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // show registration page
        req.getRequestDispatcher("/register.jsp").forward(req, resp);
    }

    @Override
<<<<<<< HEAD
=======
>>>>>>> 8fb5b46 (finilized)
>>>>>>> 2bdd21ccaf16834af801aaad4a078e24691fa627
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String username = req.getParameter("username").trim();
        String fullName = req.getParameter("fullName").trim();
        String email = req.getParameter("email").trim();
        String password = req.getParameter("password");
        String role = req.getParameter("role");
        String uni = req.getParameter("uni").trim();

        User user = new User();
        user.setUsername(username);
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);
        user.setUniversity(uni);

        boolean success = userService.registerUser(user);

        if (success) {
            HttpSession session = req.getSession();
<<<<<<< HEAD
=======
<<<<<<< HEAD
            session.setAttribute("user", user); // ✅ FIX
            resp.sendRedirect(req.getContextPath() + "/dashboard");
        } else {
            req.setAttribute("error", "Registration failed.");
            req.getRequestDispatcher("register.jsp").forward(req, resp);
=======
>>>>>>> 2bdd21ccaf16834af801aaad4a078e24691fa627
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getId());
            session.setAttribute("role", user.getRole());
            resp.sendRedirect(req.getContextPath() + "/dashboard");
        } else {
            req.setAttribute("error", "Registration failed.");
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
<<<<<<< HEAD
=======
>>>>>>> 8fb5b46 (finilized)
>>>>>>> 2bdd21ccaf16834af801aaad4a078e24691fa627
        }
    }
}
