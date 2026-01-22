package com.ethiomentor.controller;

<<<<<<< HEAD
import com.ethiomentor.model.MentorProfile;
import com.ethiomentor.service.MentorService;
=======
<<<<<<< HEAD
import com.ethiomentor.dao.MentorDAO;
import com.ethiomentor.model.MentorProfile;
=======
import com.ethiomentor.model.MentorProfile;
import com.ethiomentor.service.MentorService;
>>>>>>> 8fb5b46 (finilized)
>>>>>>> 2bdd21ccaf16834af801aaad4a078e24691fa627

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/mentormatching")
public class MentorMatchingServlet extends HttpServlet {

<<<<<<< HEAD
=======
<<<<<<< HEAD
    private final MentorDAO mentorDAO = new MentorDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<MentorProfile> mentors = mentorDAO.getAllMentors();
=======
>>>>>>> 2bdd21ccaf16834af801aaad4a078e24691fa627
    private final MentorService mentorService = new MentorService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
<<<<<<< HEAD
=======
>>>>>>> 8fb5b46 (finilized)
>>>>>>> 2bdd21ccaf16834af801aaad4a078e24691fa627

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

<<<<<<< HEAD
=======
<<<<<<< HEAD
=======
>>>>>>> 2bdd21ccaf16834af801aaad4a078e24691fa627
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
<<<<<<< HEAD
=======
>>>>>>> 8fb5b46 (finilized)
>>>>>>> 2bdd21ccaf16834af801aaad4a078e24691fa627
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        for (int i = 0; i < mentors.size(); i++) {
            MentorProfile m = mentors.get(i);
<<<<<<< HEAD
=======
<<<<<<< HEAD
            sb.append("{");
            sb.append("\"id\":").append(m.getId()).append(",");
            sb.append("\"name\":\"").append(escapeJson(m.getName())).append("\",");
            sb.append("\"username\":\"").append(escapeJson(m.getUsername())).append("\",");
            sb.append("\"email\":\"").append(escapeJson(m.getEmail())).append("\",");
            sb.append("\"university\":\"").append(escapeJson(m.getUniversity())).append("\",");
=======
>>>>>>> 2bdd21ccaf16834af801aaad4a078e24691fa627

            sb.append("{")
              .append("\"id\":").append(m.getId()).append(",")
              .append("\"name\":\"").append(escapeJson(m.getName())).append("\",")
              .append("\"username\":\"").append(escapeJson(m.getUsername())).append("\",")
              .append("\"email\":\"").append(escapeJson(m.getEmail())).append("\",")
              .append("\"university\":\"").append(escapeJson(m.getUniversity())).append("\",");
<<<<<<< HEAD
=======
>>>>>>> 8fb5b46 (finilized)
>>>>>>> 2bdd21ccaf16834af801aaad4a078e24691fa627

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
=======
<<<<<<< HEAD
        response.getWriter().write(sb.toString());
    }

    // Simple helper to escape JSON special characters
=======
>>>>>>> 2bdd21ccaf16834af801aaad4a078e24691fa627
        return sb.toString();
    }

    /* ===============================
       JSON escape helper
    =============================== */
<<<<<<< HEAD
=======
>>>>>>> 8fb5b46 (finilized)
>>>>>>> 2bdd21ccaf16834af801aaad4a078e24691fa627
    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
<<<<<<< HEAD
=======
<<<<<<< HEAD
                  .replace("/", "\\/")
=======
>>>>>>> 8fb5b46 (finilized)
>>>>>>> 2bdd21ccaf16834af801aaad4a078e24691fa627
                  .replace("\b", "\\b")
                  .replace("\f", "\\f")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}
