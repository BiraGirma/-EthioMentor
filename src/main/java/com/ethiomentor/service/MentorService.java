package com.ethiomentor.service;

import com.ethiomentor.dao.GroupDAO;
import com.ethiomentor.dao.UserDAO;
import com.ethiomentor.dao.MentorDAO;
import com.ethiomentor.model.User;
import com.ethiomentor.model.MentorProfile;

import java.util.List;

/**
 * Service layer for mentor-related operations.
 */
public class MentorService {

    private final UserDAO userDAO = new UserDAO();
    private final GroupDAO groupDAO = new GroupDAO();
    private final MentorDAO mentorDAO = new MentorDAO();

    /**
     * Fetch all mentees assigned to a specific mentor.
     *
     * @param mentorId mentor's user ID
     * @return list of mentees
     */
    public List<User> getAssignedMentees(int mentorId) {
        try {
            return userDAO.getMenteesByMentor(mentorId);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of(); // return empty list on error
        }
    }

    /**
     * Fetch names of study groups created by this mentor.
     *
     * @param mentorId mentor's user ID
     * @return list of group names
     */
    public List<String> getMentorStudyGroups(int mentorId) {
        try {
            return groupDAO.getGroupsCreatedByMentor(mentorId);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of(); // return empty list on error
        }
    }

    /**
     * Fetch all mentor profiles for mentor matching.
     *
     * @return list of mentor profiles
     */
    public List<MentorProfile> getAllMentors() {
        try {
            return mentorDAO.getAllMentors();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of(); // return empty list on error
        }
    }
}
