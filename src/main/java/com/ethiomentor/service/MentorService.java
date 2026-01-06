package com.ethiomentor.service;

import com.ethiomentor.dao.MentorDAO;
import com.ethiomentor.model.MentorProfile;

import java.util.List;

public class MentorService {

    private final MentorDAO mentorDAO = new MentorDAO();

    // Save mentor profile
    public void createProfile(MentorProfile profile) throws Exception {
        mentorDAO.saveProfile(profile);
    }

    // Fetch all mentors
    public List<MentorProfile> getAllMentors() {
        return mentorDAO.getAllMentors();
    }
}
