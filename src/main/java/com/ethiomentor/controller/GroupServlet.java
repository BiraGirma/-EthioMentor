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
        try {
            dao.createGroup(
                req.getParameter("name"),
                req.getParameter("description"),
                Integer.parseInt(req.getParameter("creatorId"))
            );

            JsonUtil.send(resp, 201, "{\"message\":\"Group created\"}");

        } catch (Exception e) {
            try {
                JsonUtil.send(resp, 400, "{\"error\":\"" + e.getMessage() + "\"}");
            } catch (Exception ignored) {}
        }
    }
}
