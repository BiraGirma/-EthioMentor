package com.ethiomentor.model;

import java.time.LocalDateTime;
import java.util.List;

public class Conversation {
    private int id;
    private String type; // PRIVATE or GROUP
    private LocalDateTime createdAt;
    private List<Integer> participantIds; // user IDs

    // Constructors
    public Conversation() {}

    public Conversation(int id, String type, LocalDateTime createdAt) {
        this.id = id;
        this.type = type;
        this.createdAt = createdAt;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public List<Integer> getParticipantIds() { return participantIds; }
    public void setParticipantIds(List<Integer> participantIds) { this.participantIds = participantIds; }
}
