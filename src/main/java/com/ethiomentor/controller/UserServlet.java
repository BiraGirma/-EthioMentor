package com.ethiomentor.controller;

import com.ethiomentor.model.User;
import com.ethiomentor.service.UserService;
import com.ethiomentor.util.JsonUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/api/user")
public class UserServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("application/json");

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            JsonUtil.send(resp, 401, "{\"error\":\"Unauthorized\"}");
            return;
        }

        User user = (User) session.getAttribute("user");

        String json = """
            {
              "id": %d,
              "username": "%s",
              "fullName": "%s",
              "email": "%s",
              "role": "%s",
              "university": "%s"
            }
            """.formatted(
                user.getId(),
                escape(user.getUsername()),
                escape(user.getFullName()),
                escape(user.getEmail()),
                escape(user.getRole()),
                escape(user.getUniversity())
            );

        JsonUtil.send(resp, 200, json);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("application/json");

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            JsonUtil.send(resp, 401, "{\"error\":\"Unauthorized\"}");
            return;
        }

        User user = (User) session.getAttribute("user");

        String fullName = req.getParameter("fullName");
        String email = req.getParameter("email");
        String university = req.getParameter("university");
        String password = req.getParameter("password");

        if (fullName != null && !fullName.isBlank())
            user.setFullName(fullName.trim());

        if (email != null && !email.isBlank())
            user.setEmail(email.trim());

        if (university != null && !university.isBlank())
            user.setUniversity(university.trim());

        if (password != null && !password.isBlank())
            user.setPassword(password); // hashed in service

        boolean updated = userService.updateUser(user);

        if (updated) {
            session.setAttribute("user", user); // keep session in sync
            JsonUtil.send(resp, 200, "{\"message\":\"User updated successfully\"}");
        } else {
            JsonUtil.send(resp, 500, "{\"error\":\"Update failed\"}");
        }
    }

    // Simple JSON escape (enough for now)
    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}
