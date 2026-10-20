package com.ethiomentor.controller;

import com.ethiomentor.model.User;
import com.ethiomentor.service.UserService;
import com.ethiomentor.util.JsonUtil;
import com.ethiomentor.util.SessionUtil;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;

/** Profile API for the logged-in user: GET reads it, PUT updates name/email/university/password. */
@WebServlet("/api/user")
public class UserServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = SessionUtil.currentUser(req);
        if (user == null) {
            JsonUtil.error(resp, 401, "Unauthorized");
            return;
        }
        JsonUtil.send(resp, 200, new JSONObject()
                .put("id", user.getId())
                .put("username", nz(user.getUsername()))
                .put("fullName", nz(user.getFullName()))
                .put("email", nz(user.getEmail()))
                .put("role", nz(user.getRole()))
                .put("university", nz(user.getUniversity())));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = SessionUtil.currentUser(req);
        if (user == null) {
            JsonUtil.error(resp, 401, "Unauthorized");
            return;
        }

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            char[] buf = new char[1024];
            int n;
            while ((n = reader.read(buf)) != -1) {
                sb.append(buf, 0, n);
                if (sb.length() > 16_384) {
                    JsonUtil.error(resp, 413, "Request too large");
                    return;
                }
            }
        }

        JSONObject body;
        try {
            body = new JSONObject(sb.toString());
        } catch (JSONException e) {
            JsonUtil.error(resp, 400, "Invalid JSON");
            return;
        }

        String error = userService.updateProfile(user,
                body.optString("fullName", null),
                body.optString("email", null),
                body.optString("university", null),
                body.optString("password", null));

        if (error == null) {
            JsonUtil.send(resp, 200, new JSONObject().put("message", "User updated successfully"));
        } else {
            JsonUtil.error(resp, 400, error);
        }
    }

    private static String nz(String s) {
        return s == null ? "" : s;
    }
}
