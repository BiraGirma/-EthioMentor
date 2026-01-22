package com.ethiomentor.controller;

import com.ethiomentor.model.User;
import com.ethiomentor.service.UserService;
import com.ethiomentor.util.PasswordUtil;
import com.ethiomentor.util.JsonUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.BufferedReader;
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
        String json = "{"
                + "\"id\":" + user.getId() + ","
                + "\"username\":\"" + escape(user.getUsername()) + "\","
                + "\"fullName\":\"" + escape(user.getFullName()) + "\","
                + "\"email\":\"" + escape(user.getEmail()) + "\","
                + "\"role\":\"" + escape(user.getRole()) + "\","
                + "\"university\":\"" + escape(user.getUniversity()) + "\""
                + "}";

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

        // Read request body
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
        }
        String body = sb.toString();

        // Extract JSON values manually
        String fullName = getJsonValue(body, "fullName");
        String email = getJsonValue(body, "email");
        String university = getJsonValue(body, "university");
        String password = getJsonValue(body, "password");

        if (fullName != null) user.setFullName(fullName.trim());
        if (email != null) user.setEmail(email.trim());
        if (university != null) user.setUniversity(university.trim());
        if (password != null && !password.isEmpty()) {
            try {
                user.setPassword(PasswordUtil.hash(password));
            } catch (Exception e) {
                e.printStackTrace();
                JsonUtil.send(resp, 500, "{\"error\":\"Password hashing failed\"}");
                return;
            }
        }

        boolean updated = userService.updateUser(user);
        if (updated) {
            session.setAttribute("user", user);
            JsonUtil.send(resp, 200, "{\"message\":\"User updated successfully\"}");
        } else {
            JsonUtil.send(resp, 500, "{\"error\":\"Update failed\"}");
        }
    }

    /** Escape JSON special characters */
    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    /** Extract value from flat JSON like {"key":"value"} without regex */
    private String getJsonValue(String json, String key) {
        if (json == null || key == null) return null;
        String pattern = "\"" + key + "\"";
        int index = json.indexOf(pattern);
        if (index == -1) return null;

        // Find the colon
        int colon = json.indexOf(":", index);
        if (colon == -1) return null;

        // Find starting quote
        int startQuote = json.indexOf("\"", colon);
        if (startQuote == -1) return null;

        int endQuote = json.indexOf("\"", startQuote + 1);
        if (endQuote == -1) return null;

        return json.substring(startQuote + 1, endQuote);
    }
}
