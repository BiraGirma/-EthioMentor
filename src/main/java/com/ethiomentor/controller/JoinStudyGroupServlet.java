<<<<<<< HEAD
package com.ethiomentor.controller;

import com.ethiomentor.dao.StudyGroupDAO;
import com.ethiomentor.model.User;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/studygroups/join")
public class JoinStudyGroupServlet extends HttpServlet {
    private final StudyGroupDAO dao = new StudyGroupDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try {
            User user = (User) req.getSession().getAttribute("user");
            if (user == null) {
                resp.setStatus(401);
                resp.getWriter().write("{\"error\":\"User not logged in\"}");
                return;
            }

            int groupId = Integer.parseInt(req.getParameter("groupId"));
            dao.joinGroup(groupId, user.getId());
            int count = dao.getMembersCount(groupId);

            resp.getWriter().write("{\"membersCount\":" + count + "}");
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"membersCount\":0}");
            e.printStackTrace();
        }
    }
}
=======
package com.ethiomentor.controller;

import com.ethiomentor.dao.StudyGroupDAO;
import com.ethiomentor.model.User;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/studygroups/join")
public class JoinStudyGroupServlet extends HttpServlet {
    private final StudyGroupDAO dao = new StudyGroupDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try {
            User user = (User) req.getSession().getAttribute("user");
            if (user == null) {
                resp.setStatus(401);
                resp.getWriter().write("{\"error\":\"User not logged in\"}");
                return;
            }

            int groupId = Integer.parseInt(req.getParameter("groupId"));
            dao.joinGroup(groupId, user.getId());
            int count = dao.getMembersCount(groupId);

            resp.getWriter().write("{\"membersCount\":" + count + "}");
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"membersCount\":0}");
            e.printStackTrace();
        }
    }
}
>>>>>>> 8fb5b46 (finilized)
