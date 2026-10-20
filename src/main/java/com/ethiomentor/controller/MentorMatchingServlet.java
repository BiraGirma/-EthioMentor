package com.ethiomentor.controller;

import com.ethiomentor.model.MentorProfile;
import com.ethiomentor.service.MentorService;
import com.ethiomentor.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

/**
 * JSON list of approved mentors for the Mentor Matching page. Login is enforced
 * by AuthFilter. Contact details (email, username) are deliberately not exposed.
 */
@WebServlet("/mentormatching")
public class MentorMatchingServlet extends HttpServlet {

    private final MentorService mentorService = new MentorService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            List<MentorProfile> mentors = mentorService.getAllMentors();
            JSONArray arr = new JSONArray();
            for (MentorProfile m : mentors) {
                arr.put(new JSONObject()
                        .put("id", m.getId())
                        .put("name", m.getName() == null ? "" : m.getName())
                        .put("university", m.getUniversity() == null ? "" : m.getUniversity())
                        .put("expertise", new JSONArray(m.getExpertise() == null ? new String[0] : m.getExpertise())));
            }
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(arr.toString());
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.error(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to load mentors");
        }
    }
}
