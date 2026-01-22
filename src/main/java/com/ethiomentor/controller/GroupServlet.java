package com.ethiomentor.controller;

import com.ethiomentor.dao.GroupDAO;
import com.ethiomentor.util.JsonUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/api/group")
public class GroupServlet extends HttpServlet {

    private final GroupDAO dao = new GroupDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {

        resp.setContentType("application/json");

        try {
            String name = req.getParameter("name");
            String description = req.getParameter("description");
            String creatorIdParam = req.getParameter("creatorId");

            if (name == null || creatorIdParam == null) {
                JsonUtil.send(resp, 400, "{\"error\":\"Missing required parameters\"}");
                return;
            }

            int creatorId = Integer.parseInt(creatorIdParam);

            dao.createGroup(name, description, creatorId);

            JsonUtil.send(resp, 201, "{\"message\":\"Group created\"}");

        } catch (NumberFormatException e) {
            try {
                JsonUtil.send(resp, 400, "{\"error\":\"Invalid creatorId\"}");
            } catch (Exception ignored) {}
        } catch (Exception e) {
            try {
                JsonUtil.send(resp, 400, "{\"error\":\"" + e.getMessage() + "\"}");
            } catch (Exception ignored) {}
        }
    }
}
