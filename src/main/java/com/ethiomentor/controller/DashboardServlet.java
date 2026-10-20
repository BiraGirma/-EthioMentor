package com.ethiomentor.controller;

import com.ethiomentor.dao.StatsDAO;
import com.ethiomentor.model.User;
import com.ethiomentor.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private final StatsDAO stats = new StatsDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        User user = SessionUtil.currentUser(req);
        if (user == null) {
            res.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        // Mentors and admins have their own landing pages.
        String home = SessionUtil.homeFor(user);
        if (!"/dashboard".equals(home)) {
            res.sendRedirect(req.getContextPath() + home);
            return;
        }

        req.setAttribute("studyGroupsCount", stats.countStudyGroups());
        req.setAttribute("mentorsCount", stats.countApprovedMentors());
        req.setAttribute("menteesCount", stats.countStudents());
        req.setAttribute("discussionsCount", stats.countConversations());

        req.getRequestDispatcher("/dashboard.jsp").forward(req, res);
    }
}
