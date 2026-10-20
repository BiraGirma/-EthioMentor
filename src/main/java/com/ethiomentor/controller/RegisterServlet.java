package com.ethiomentor.controller;

import com.ethiomentor.model.User;
import com.ethiomentor.service.UserService;
import com.ethiomentor.util.InputUtil;
import com.ethiomentor.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User user = new User();
        user.setUsername(InputUtil.trim(req.getParameter("username")));
        user.setFullName(InputUtil.trim(req.getParameter("fullName")));
        user.setEmail(InputUtil.trim(req.getParameter("email")));
        user.setPassword(req.getParameter("password"));      // plaintext; hashed in the service
        user.setRole(req.getParameter("role"));
        user.setUniversity(InputUtil.trim(req.getParameter("uni")));

        String error = userService.registerUser(user, req.getParameter("expertise"));

        if (error == null) {
            SessionUtil.login(req, user);
            resp.sendRedirect(req.getContextPath() + SessionUtil.homeFor(user));
        } else {
            req.setAttribute("error", error);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
        }
    }
}
