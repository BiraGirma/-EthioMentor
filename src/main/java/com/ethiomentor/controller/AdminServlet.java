package com.ethiomentor.controller;

import com.ethiomentor.model.StudyGroup;
import com.ethiomentor.model.User;
import com.ethiomentor.service.AdminService;
import com.ethiomentor.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@WebServlet("/admin")
public class AdminServlet extends HttpServlet {

    private final AdminService service = new AdminService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);

        // Redirect if not logged in
        if (session == null || session.getAttribute("user") == null) {
            res.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");

        // Check admin role
        if (!"admin".equalsIgnoreCase(user.getRole())) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }

        // Store role in session for sidebar JSTL
        session.setAttribute("role", user.getRole());

        int currentUserId = user.getId();
        System.out.println("Current admin user ID: " + currentUserId);

        // Fetch totals safely
        Integer totalUsers = service.getTotalUsers();
        Integer totalGroups = service.getTotalGroups();
        Integer totalMessages = service.getTotalMessages();

        req.setAttribute("totalUsers", totalUsers != null ? totalUsers : 0);
        req.setAttribute("totalGroups", totalGroups != null ? totalGroups : 0);
        req.setAttribute("totalMessages", totalMessages != null ? totalMessages : 0);

        // Fetch lists safely (never null)
        List<User> users = service.getAllUsers(currentUserId);
        List<User> mentors = service.getPendingMentorRequests();
        List<StudyGroup> groups = service.getAllGroups();

        // Debug prints
        if (users != null) {
            System.out.println("Fetched Users:");
            for (User u : users) {
                System.out.println(" -> " + u.getId() + ": " + u.getUsername() + " (" + u.getRole() + ")");
            }
        }

        if (mentors != null) {
            System.out.println("Fetched Pending Mentors:");
            for (User m : mentors) {
                System.out.println(" -> " + m.getId() + ": " + m.getFullName() + " (" + m.getMentorStatus() + ")");
            }
        }

        if (groups != null) {
            System.out.println("Fetched Groups:");
            for (StudyGroup g : groups) {
                System.out.println(" -> " + g.getId() + ": " + g.getName() + " (" + g.getMembersCount() + " members)");
            }
        }

        // Forward to JSP
        req.setAttribute("users", users != null ? users : Collections.emptyList());
        req.setAttribute("mentorRequests", mentors != null ? mentors : Collections.emptyList());
        req.setAttribute("groups", groups != null ? groups : Collections.emptyList());

        req.getRequestDispatcher("admin.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        User user = session != null ? (User) session.getAttribute("user") : null;

        // Validate admin
        if (user == null || !"admin".equalsIgnoreCase(user.getRole())) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String action = req.getParameter("action");
        int id = req.getParameter("id") != null ? Integer.parseInt(req.getParameter("id")) : 0;

        switch (action) {
            case "deleteUser" -> service.deleteUser(id);
            case "deleteGroup" -> service.deleteGroup(id);
            case "approveMentor" -> service.approveMentor(id);
            case "rejectMentor" -> service.rejectMentor(id);
        }

        res.sendRedirect(req.getContextPath() + "/admin");
    }
}
