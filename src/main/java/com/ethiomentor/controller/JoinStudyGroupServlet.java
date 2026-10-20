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

@WebServlet("/studygroups/join")
public class JoinStudyGroupServlet extends HttpServlet {

    private final StudyGroupDAO dao = new StudyGroupDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = SessionUtil.currentUser(req);
        if (user == null) {
            JsonUtil.error(resp, 401, "Not logged in");
            return;
        }

        int groupId = InputUtil.parseId(req.getParameter("groupId"));
        if (groupId < 0) {
            JsonUtil.error(resp, 400, "Invalid groupId");
            return;
        }

        try {
            if (!dao.joinGroup(groupId, user.getId())) {
                JsonUtil.error(resp, 404, "Group not found");
                return;
            }
            JsonUtil.send(resp, 200, new JSONObject().put("membersCount", dao.getMembersCount(groupId)));
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.error(resp, 500, "Could not join group");
        }
    }
}
