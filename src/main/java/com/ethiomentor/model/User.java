package com.ethiomentor.model;

public class User {
    private int id;
<<<<<<< HEAD
    private String email;
    private String passwordHash;
    private String role;
    private boolean active;

    public User() {}

    public User(int id, String email, String role) {
        this.id = id;
        this.email = email;
        this.role = role;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
=======
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
>>>>>>> 8fb5b46 (finilized)

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

<<<<<<< HEAD
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
=======
    public String getUniversity() { return university; }
    public void setUniversity(String university) { this.university = university; }

    public String getMentorStatus() { return mentorStatus; }
    public void setMentorStatus(String mentorStatus) { this.mentorStatus = mentorStatus; }
>>>>>>> 8fb5b46 (finilized)
}
