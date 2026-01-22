package com.ethiomentor.service;

<<<<<<< HEAD
=======
<<<<<<< HEAD
import com.ethiomentor.dao.MentorDAO;
=======
>>>>>>> 2bdd21ccaf16834af801aaad4a078e24691fa627
import com.ethiomentor.dao.GroupDAO;
import com.ethiomentor.dao.UserDAO;
import com.ethiomentor.dao.MentorDAO;
import com.ethiomentor.model.User;
<<<<<<< HEAD
=======
>>>>>>> 8fb5b46 (finilized)
>>>>>>> 2bdd21ccaf16834af801aaad4a078e24691fa627
import com.ethiomentor.model.MentorProfile;

import java.util.List;

public class MentorService {

<<<<<<< HEAD
=======
<<<<<<< HEAD
    private final MentorDAO mentorDAO = new MentorDAO();

    // Save mentor profile
    public void createProfile(MentorProfile profile) throws Exception {
        mentorDAO.saveProfile(profile);
    }

    // Fetch all mentors
=======
>>>>>>> 2bdd21ccaf16834af801aaad4a078e24691fa627
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
<<<<<<< HEAD
=======
>>>>>>> 8fb5b46 (finilized)
>>>>>>> 2bdd21ccaf16834af801aaad4a078e24691fa627
    public List<MentorProfile> getAllMentors() {
        return mentorDAO.getAllMentors();
    }
}
