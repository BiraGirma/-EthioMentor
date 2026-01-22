package com.ethiomentor.model;

public class MentorProfile {
    private int id;                 // mentor ID
    private int userId;             // reference to users.id
    private String name;            // full_name from users table
    private String username;        // username from users table
    private String email;           // email from users table
    private String university;      // university from users table
    private String[] expertise;     // comma-separated expertise split into array

    // Optional: You can include rating, year, avatar URL if needed
    private double rating;          // default 0.0
    private int year;               // optional (if you track year)
    private String avatar;          // optional avatar URL

    public MentorProfile() {
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String[] getExpertise() {
        return expertise;
    }

    public void setExpertise(String[] expertise) {
        this.expertise = expertise;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
