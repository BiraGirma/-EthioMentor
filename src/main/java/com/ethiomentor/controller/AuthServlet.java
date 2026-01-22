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
}
