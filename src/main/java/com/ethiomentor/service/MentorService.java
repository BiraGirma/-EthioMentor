package com.ethiomentor.service;

<<<<<<< HEAD
import com.ethiomentor.dao.MentorDAO;
=======
import com.ethiomentor.dao.GroupDAO;
import com.ethiomentor.dao.UserDAO;
import com.ethiomentor.dao.MentorDAO;
import com.ethiomentor.model.User;
>>>>>>> 8fb5b46 (finilized)
import com.ethiomentor.model.MentorProfile;

import java.util.List;

public class MentorService {

<<<<<<< HEAD
    private final MentorDAO mentorDAO = new MentorDAO();

    // Save mentor profile
    public void createProfile(MentorProfile profile) throws Exception {
        mentorDAO.saveProfile(profile);
    }

    // Fetch all mentors
=======
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
>>>>>>> 8fb5b46 (finilized)
    public List<MentorProfile> getAllMentors() {
        return mentorDAO.getAllMentors();
    }
}
