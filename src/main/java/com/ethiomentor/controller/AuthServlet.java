package com.ethiomentor.controller;

import com.ethiomentor.model.User;
import com.ethiomentor.service.AuthService;
import com.ethiomentor.util.InputUtil;
import com.ethiomentor.util.LoginThrottle;
import com.ethiomentor.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/login")
public class AuthServlet extends HttpServlet {

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.sendRedirect(req.getContextPath() + "/login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        String identifier = InputUtil.trim(req.getParameter("email")); // email or username
        String password = req.getParameter("password");

        if (InputUtil.isBlank(identifier) || password == null || password.isEmpty()) {
            fail(req, res, 400, "Please enter your email/username and password.");
            return;
        }

        String throttleKey = identifier.toLowerCase() + "|" + req.getRemoteAddr();
        if (LoginThrottle.isBlocked(throttleKey)) {
            fail(req, res, 429, "Too many failed attempts. Please wait a few minutes and try again.");
            return;
        }

        User user = authService.login(identifier, password);
        if (user == null) {
            LoginThrottle.recordFailure(throttleKey);
            fail(req, res, 401, "Invalid credentials");
            return;
        }

        LoginThrottle.reset(throttleKey);
        SessionUtil.login(req, user);
        res.sendRedirect(req.getContextPath() + SessionUtil.homeFor(user));
    }

    private void fail(HttpServletRequest req, HttpServletResponse res, int status, String message)
            throws ServletException, IOException {
        req.setAttribute("error", message);
        res.setStatus(status);
        req.getRequestDispatcher("/login.jsp").forward(req, res);
    }
}
