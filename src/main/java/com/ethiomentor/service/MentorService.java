package com.ethiomentor.service;

import com.ethiomentor.dao.GroupDAO;
import com.ethiomentor.dao.UserDAO;
import com.ethiomentor.dao.MentorDAO;
import com.ethiomentor.model.User;
import com.ethiomentor.model.MentorProfile;

import java.util.List;

public class MentorService {

    private final UserDAO userDAO = new UserDAO();
    private final GroupDAO groupDAO = new GroupDAO();
    private final MentorDAO mentorDAO = new MentorDAO();

    // Fetch mentees assigned to this mentor
    public List<User> getAssignedMentees(int mentorId) {
        return userDAO.getMenteesByMentor(mentorId);
    }

    // Fetch study groups created by this mentor
    public List<String> getMentorStudyGroups(int mentorId) {
        return groupDAO.getGroupsCreatedByMentor(mentorId);
    }

    // Fetch all mentor profiles for mentor matching
    public List<MentorProfile> getAllMentors() {
        return mentorDAO.getAllMentors();
    }
}
