package com.ethiomentor.controller;

import com.ethiomentor.dao.StudyGroupDAO;
import com.ethiomentor.model.User;
import com.ethiomentor.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/studygroups")
public class StudyGroupServlet extends HttpServlet {

    private final StudyGroupDAO dao = new StudyGroupDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User user = SessionUtil.currentUser(req);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        int userId = user.getId();
        req.setAttribute("groups", dao.getAllGroups(userId));
        req.setAttribute("randomGroups", dao.getRandomGroups(userId));
        req.setAttribute("joinedGroups", dao.getJoinedGroups(userId));
        req.setAttribute("createdGroups", dao.getCreatedGroups(userId));

        req.getRequestDispatcher("/studygroups.jsp").forward(req, resp);
    }
}
