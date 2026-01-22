package com.ethiomentor.model;

public class MenteeProfile {
    private int userId;
    private String department;
    private int yearOfStudy;
    private String interests;

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public int getYearOfStudy() { return yearOfStudy; }
    public void setYearOfStudy(int yearOfStudy) { this.yearOfStudy = yearOfStudy; }

    public String getInterests() { return interests; }
    public void setInterests(String interests) { this.interests = interests; }
}
