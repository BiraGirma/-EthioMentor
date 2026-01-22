package com.ethiomentor.controller;

import com.ethiomentor.model.MentorProfile;
import com.ethiomentor.service.MentorService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/mentormatching")
public class MentorMatchingServlet extends HttpServlet {

    private final MentorService mentorService = new MentorService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            List<MentorProfile> mentors = mentorService.getAllMentors();
            response.getWriter().write(buildJson(mentors));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Failed to load mentors\"}");
            e.printStackTrace();
        }
    }

    /* ===============================
       JSON Builder
    =============================== */
    private String buildJson(List<MentorProfile> mentors) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        for (int i = 0; i < mentors.size(); i++) {
            MentorProfile m = mentors.get(i);

            sb.append("{")
              .append("\"id\":").append(m.getId()).append(",")
              .append("\"name\":\"").append(escapeJson(m.getName())).append("\",")
              .append("\"username\":\"").append(escapeJson(m.getUsername())).append("\",")
              .append("\"email\":\"").append(escapeJson(m.getEmail())).append("\",")
              .append("\"university\":\"").append(escapeJson(m.getUniversity())).append("\",");

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
        return sb.toString();
    }

    /* ===============================
       JSON escape helper
    =============================== */
    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\b", "\\b")
                  .replace("\f", "\\f")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}
