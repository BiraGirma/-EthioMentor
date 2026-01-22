package com.ethiomentor.controller;

import com.ethiomentor.dao.StudyGroupDAO;
import com.ethiomentor.model.User;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/studygroups/create")
public class CreateStudyGroupServlet extends HttpServlet {

    private final StudyGroupDAO dao = new StudyGroupDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        try {
            User user = (User) req.getSession().getAttribute("user");
            if (user == null) {
                resp.setStatus(401);
                resp.getWriter().write("{\"success\":false,\"error\":\"User not logged in\"}");
                return;
            }

            String name = req.getParameter("name");
            String description = req.getParameter("description");

            dao.createGroup(name, description, user.getId());

            resp.getWriter().write("{\"success\":true}");
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"success\":false}");
            e.printStackTrace();
        }
    }
}
