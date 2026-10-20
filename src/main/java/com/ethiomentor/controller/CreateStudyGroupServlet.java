package com.ethiomentor.controller;

import com.ethiomentor.dao.StudyGroupDAO;
import com.ethiomentor.model.User;
import com.ethiomentor.util.InputUtil;
import com.ethiomentor.util.JsonUtil;
import com.ethiomentor.util.SessionUtil;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/studygroups/create")
public class CreateStudyGroupServlet extends HttpServlet {

    private final StudyGroupDAO dao = new StudyGroupDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = SessionUtil.currentUser(req);
        if (user == null) {
            JsonUtil.send(resp, 401, new JSONObject().put("success", false).put("error", "Not logged in"));
            return;
        }

        String name = InputUtil.trim(req.getParameter("name"));
        String description = InputUtil.trim(req.getParameter("description"));
        if (description == null) description = "";

        if (!InputUtil.lengthBetween(name, 1, 100) || description.length() > 1000) {
            JsonUtil.send(resp, 400, new JSONObject().put("success", false)
                    .put("error", "Name is required (max 100 characters); description max 1000."));
            return;
        }

        try {
            int id = dao.createGroup(name, description, user.getId());
            JsonUtil.send(resp, 201, new JSONObject().put("success", true).put("id", id));
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.send(resp, 500, new JSONObject().put("success", false).put("error", "Could not create group"));
        }
    }
}
