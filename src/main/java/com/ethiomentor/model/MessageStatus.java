package com.ethiomentor.model;

import java.time.LocalDateTime;

public class MessageStatus {
    private int messageId;
    private int userId;
    private String status; // SENT, DELIVERED, READ
    private LocalDateTime updatedAt;

    // Constructors
    public MessageStatus() {}

    public MessageStatus(int messageId, int userId, String status, LocalDateTime updatedAt) {
        this.messageId = messageId;
        this.userId = userId;
        this.status = status;
        this.updatedAt = updatedAt;
    }

    // Getters & Setters
    public int getMessageId() { return messageId; }
    public void setMessageId(int messageId) { this.messageId = messageId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
