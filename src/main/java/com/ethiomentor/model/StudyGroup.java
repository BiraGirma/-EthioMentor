package com.ethiomentor.model;

public class StudyGroup {
    private int id;
    private String name;
    private String description;
    private int createdBy;
    private int membersCount;
    private boolean member; // true if current user joined

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }

    public int getMembersCount() { return membersCount; }
    public void setMembersCount(int membersCount) { this.membersCount = membersCount; }

    public boolean isMember() { return member; }
    public void setMember(boolean member) { this.member = member; }
}
