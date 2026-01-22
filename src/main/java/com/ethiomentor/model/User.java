package com.ethiomentor.model;

public class User {
    private int id;
    private String username;
    private String fullName;
    private String email;
    private String password;
    private String role;
    private String university;
    
    // NEW
    private String mentorStatus;

    // Getters and setters
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

    public String getMentorStatus() { return mentorStatus; }
    public void setMentorStatus(String mentorStatus) { this.mentorStatus = mentorStatus; }
}
