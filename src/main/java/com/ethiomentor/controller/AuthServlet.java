package com.ethiomentor.controller;

import com.ethiomentor.model.User;
import com.ethiomentor.service.AuthService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/login")
public class AuthServlet extends HttpServlet {

    private final AuthService authService = new AuthService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        String identifier = req.getParameter("email"); // can be email or username
        String password = req.getParameter("password");

        User user = authService.login(identifier, password);

        if (user != null) {
            HttpSession session = req.getSession(true);
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getId());
            session.setAttribute("role", user.getRole());

            // Redirect based on role
            switch (user.getRole().toLowerCase()) {
                case "admin":
                    res.sendRedirect(req.getContextPath() + "/admin");
                    break;
                case "mentor":
                    res.sendRedirect(req.getContextPath() + "/mentor-dashboard");
                    break;
                default: // student / mentee
                    res.sendRedirect(req.getContextPath() + "/dashboard");
            }

        } else {
            req.setAttribute("error", "Invalid credentials");
            req.getRequestDispatcher("login.jsp").forward(req, res);
        }
    }
}
