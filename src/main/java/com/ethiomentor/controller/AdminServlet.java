package com.ethiomentor.controller;

import com.ethiomentor.model.StudyGroup;
import com.ethiomentor.model.User;
import com.ethiomentor.service.AdminService;
import com.ethiomentor.util.InputUtil;
import com.ethiomentor.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

/** Admin dashboard. AuthFilter guarantees an admin session; the role is re-checked here as defence in depth. */
@WebServlet("/admin")
public class AdminServlet extends HttpServlet {

    private final AdminService service = new AdminService();

    private static User requireAdmin(HttpServletRequest req, HttpServletResponse res) throws IOException {
        User user = SessionUtil.currentUser(req);
        if (user == null) {
            res.sendRedirect(req.getContextPath() + "/login.jsp");
            return null;
        }
        if (!"admin".equalsIgnoreCase(user.getRole())) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return null;
        }
        return user;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        User user = requireAdmin(req, res);
        if (user == null) return;

        List<User> users = service.getAllUsers(user.getId());
        List<User> mentors = service.getPendingMentorRequests();
        List<StudyGroup> groups = service.getAllGroups();

        req.setAttribute("totalUsers", service.getTotalUsers());
        req.setAttribute("totalGroups", service.getTotalGroups());
        req.setAttribute("totalMessages", service.getTotalMessages());
        req.setAttribute("users", users);
        req.setAttribute("mentorRequests", mentors);
        req.setAttribute("groups", groups);

        req.getRequestDispatcher("/admin.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        User user = requireAdmin(req, res);
        if (user == null) return;

        String action = req.getParameter("action");
        int id = InputUtil.parseId(req.getParameter("id"));
        if (action == null || id < 0) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing or invalid action/id");
            return;
        }

        switch (action) {
            case "deleteUser", "deactivateUser" -> {
                if (id == user.getId()) {
                    res.sendError(HttpServletResponse.SC_BAD_REQUEST, "You cannot do that to your own account");
                    return;
                }
                if (action.equals("deleteUser")) service.deleteUser(id); else service.deactivateUser(id);
            }
            case "activateUser" -> service.activateUser(id);
            case "deleteGroup" -> service.deleteGroup(id);
            case "approveMentor" -> service.approveMentor(id);
            case "rejectMentor" -> service.rejectMentor(id);
            default -> {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action");
                return;
            }
        }

        res.sendRedirect(req.getContextPath() + "/admin");
    }
}
