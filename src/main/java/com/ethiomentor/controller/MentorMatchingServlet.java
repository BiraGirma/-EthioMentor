package com.ethiomentor.controller;

<<<<<<< HEAD
import com.ethiomentor.dao.MentorDAO;
import com.ethiomentor.model.MentorProfile;
=======
import com.ethiomentor.model.MentorProfile;
import com.ethiomentor.service.MentorService;
>>>>>>> 8fb5b46 (finilized)

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/mentormatching")
public class MentorMatchingServlet extends HttpServlet {

<<<<<<< HEAD
    private final MentorDAO mentorDAO = new MentorDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<MentorProfile> mentors = mentorDAO.getAllMentors();
=======
    private final MentorService mentorService = new MentorService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
>>>>>>> 8fb5b46 (finilized)

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

<<<<<<< HEAD
=======
        try {
            List<MentorProfile> mentors = mentorService.getAllMentors();
            response.getWriter().write(buildJson(mentors));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Failed to load mentors\"}");
        }
    }

    /* ===============================
       JSON Builder
    =============================== */
    private String buildJson(List<MentorProfile> mentors) {
>>>>>>> 8fb5b46 (finilized)
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        for (int i = 0; i < mentors.size(); i++) {
            MentorProfile m = mentors.get(i);
<<<<<<< HEAD
            sb.append("{");
            sb.append("\"id\":").append(m.getId()).append(",");
            sb.append("\"name\":\"").append(escapeJson(m.getName())).append("\",");
            sb.append("\"username\":\"").append(escapeJson(m.getUsername())).append("\",");
            sb.append("\"email\":\"").append(escapeJson(m.getEmail())).append("\",");
            sb.append("\"university\":\"").append(escapeJson(m.getUniversity())).append("\",");
=======

            sb.append("{")
              .append("\"id\":").append(m.getId()).append(",")
              .append("\"name\":\"").append(escapeJson(m.getName())).append("\",")
              .append("\"username\":\"").append(escapeJson(m.getUsername())).append("\",")
              .append("\"email\":\"").append(escapeJson(m.getEmail())).append("\",")
              .append("\"university\":\"").append(escapeJson(m.getUniversity())).append("\",");
>>>>>>> 8fb5b46 (finilized)

            // expertise array
            sb.append("\"expertise\":[");
            String[] skills = m.getExpertise();
            for (int j = 0; j < skills.length; j++) {
                sb.append("\"").append(escapeJson(skills[j])).append("\"");
                if (j < skills.length - 1) sb.append(",");
            }
            sb.append("]");

            sb.append("}");
            if (i < mentors.size() - 1) sb.append(",");
        }

        sb.append("]");
<<<<<<< HEAD
        response.getWriter().write(sb.toString());
    }

    // Simple helper to escape JSON special characters
=======
        return sb.toString();
    }

    /* ===============================
       JSON escape helper
    =============================== */
>>>>>>> 8fb5b46 (finilized)
    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
<<<<<<< HEAD
                  .replace("/", "\\/")
=======
>>>>>>> 8fb5b46 (finilized)
                  .replace("\b", "\\b")
                  .replace("\f", "\\f")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}
