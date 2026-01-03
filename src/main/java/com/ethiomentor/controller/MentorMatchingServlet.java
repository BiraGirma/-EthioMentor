package com.ethiomentor.controller;

import com.ethiomentor.dao.MentorDAO;
import com.ethiomentor.model.MentorProfile;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/mentormatching")
public class MentorMatchingServlet extends HttpServlet {

    private final MentorDAO mentorDAO = new MentorDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<MentorProfile> mentors = mentorDAO.getAllMentors();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        StringBuilder sb = new StringBuilder();
        sb.append("[");

        for (int i = 0; i < mentors.size(); i++) {
            MentorProfile m = mentors.get(i);
            sb.append("{");
            sb.append("\"id\":").append(m.getId()).append(",");
            sb.append("\"name\":\"").append(escapeJson(m.getName())).append("\",");
            sb.append("\"username\":\"").append(escapeJson(m.getUsername())).append("\",");
            sb.append("\"email\":\"").append(escapeJson(m.getEmail())).append("\",");
            sb.append("\"university\":\"").append(escapeJson(m.getUniversity())).append("\",");

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
        response.getWriter().write(sb.toString());
    }

    // Simple helper to escape JSON special characters
    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("/", "\\/")
                  .replace("\b", "\\b")
                  .replace("\f", "\\f")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}
