package com.ethiomentor.controller;

import com.ethiomentor.dao.StudyGroupDAO;
import com.ethiomentor.model.StudyGroup;
import com.ethiomentor.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/studygroups")
public class StudyGroupServlet extends HttpServlet {

    private final StudyGroupDAO dao = new StudyGroupDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        int userId = user.getId();

        // Fetch all lists
        List<StudyGroup> allGroups = dao.getAllGroups(userId);
        List<StudyGroup> randomGroups = dao.getRandomGroups(userId);
        List<StudyGroup> joinedGroups = dao.getJoinedGroups(userId);
        List<StudyGroup> createdGroups = dao.getCreatedGroups(userId);

        // Set attributes for JSP
        req.setAttribute("groups", allGroups);
        req.setAttribute("randomGroups", randomGroups);
        req.setAttribute("joinedGroups", joinedGroups);
        req.setAttribute("createdGroups", createdGroups);
        System.out.println("All groups: " + allGroups.size());
        System.out.println("Random groups: " + randomGroups.size());
        System.out.println("Joined groups: " + joinedGroups.size());
        System.out.println("Created groups: " + createdGroups.size());

        req.getRequestDispatcher("/WEB-INF/jsp/studygroups.jsp").forward(req, resp);
    }
}

