package com.ethiomentor.controller;

import com.ethiomentor.dao.StatsDAO;
import com.ethiomentor.model.User;
import com.ethiomentor.service.MentorService;
import com.ethiomentor.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/mentor-dashboard")
public class MentorDashboardServlet extends HttpServlet {

    private final MentorService mentorService = new MentorService();
    private final StatsDAO stats = new StatsDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        User user = SessionUtil.currentUser(req);
        if (user == null) {
            res.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        List<User> mentees = mentorService.getAssignedMentees(user.getId());
        List<String> groups = mentorService.getMentorStudyGroups(user.getId());

        req.setAttribute("mentees", mentees);
        req.setAttribute("studyGroups", groups);
        req.setAttribute("menteesCount", mentees.size());
        req.setAttribute("studyGroupsCount", groups.size());
        req.setAttribute("mentorsCount", stats.countApprovedMentors());
        req.setAttribute("messagesCount", stats.countMessagesFor(user.getId()));
        req.setAttribute("activeGroupsCount", stats.countGroupsJoinedBy(user.getId()));

        req.getRequestDispatcher("/mentorDashboard.jsp").forward(req, res);
    }
}
