package com.ethiomentor.service;

import com.ethiomentor.dao.AdminDAO;
import com.ethiomentor.model.User;
import com.ethiomentor.model.StudyGroup;

import java.util.ArrayList;
import java.util.List;

public class AdminService {

    private final AdminDAO dao = new AdminDAO();

    /* ===== USERS ===== */
    public List<User> getAllUsers(int currentUserId) {
        try {
            return dao.getAllUsersExcept(currentUserId);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void deleteUser(int userId) {
        try {
            dao.deleteUser(userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* ===== MENTOR REQUESTS ===== */
    public List<User> getPendingMentorRequests() {
        try {
            return dao.getPendingMentorRequests();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void approveMentor(int userId) {
        try {
            dao.updateMentorStatus(userId, "APPROVED");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void rejectMentor(int userId) {
        try {
            dao.updateMentorStatus(userId, "REJECTED");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* ===== STUDY GROUPS ===== */
    public List<StudyGroup> getAllGroups() {
        try {
            return dao.getAllGroups();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void deleteGroup(int groupId) {
        try {
            dao.deleteGroup(groupId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* ===== ANALYTICS ===== */
    public int getTotalUsers() {
        try {
            return dao.countUsers();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int getTotalGroups() {
        try {
            return dao.countGroups();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int getTotalMessages() {
        try {
            return dao.countMessages();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
