package com.ethiomentor.controller;

import com.ethiomentor.service.AdminService;
import com.ethiomentor.util.JsonUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/api/admin")
public class AdminServlet extends HttpServlet {

    private final AdminService service = new AdminService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            int userId = Integer.parseInt(req.getParameter("userId"));
            service.deactivateUser(userId);
            JsonUtil.send(resp, 200, "{\"message\":\"User deactivated\"}");

        } catch (Exception e) {
            try {
                JsonUtil.send(resp, 400, "{\"error\":\"" + e.getMessage() + "\"}");
            } catch (Exception ignored) {}
        }
    }
}
