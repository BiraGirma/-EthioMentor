package com.ethiomentor.model;

public class User {
    private int id;
    private String username;
    private String fullName;
    private String email;
    private String password;       // password hash (null in sessions)
    private String role;           // e.g., student, mentor, admin
    private String university;
    
    // Optional / new fields
    private String mentorStatus;   // PENDING, APPROVED or REJECTED (mentors only)
    private boolean active = true; // false = deactivated by an admin

    // Constructors
    public User() {}

    public User(int id, String email, String role) {
        this.id = id;
        this.email = email;
        this.role = role;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getUniversity() { return university; }
    public void setUniversity(String university) { this.university = university; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getMentorStatus() { return mentorStatus; }
    public void setMentorStatus(String mentorStatus) { this.mentorStatus = mentorStatus; }
}
