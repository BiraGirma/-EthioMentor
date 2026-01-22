package com.ethiomentor.controller;

import com.ethiomentor.model.User;
import com.ethiomentor.service.MentorService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/mentor-dashboard")
public class MentorDashboardServlet extends HttpServlet {

    private final MentorService mentorService = new MentorService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            res.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");

        // Fetch mentor-specific info
        req.setAttribute("mentees", mentorService.getAssignedMentees(user.getId()));
        req.setAttribute("studyGroups", mentorService.getMentorStudyGroups(user.getId()));

        req.getRequestDispatcher("/mentorDashboard.jsp").forward(req, res);
    }
}
