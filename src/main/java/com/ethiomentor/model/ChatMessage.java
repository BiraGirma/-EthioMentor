package com.ethiomentor.model;

import java.time.LocalDateTime;

public class ChatMessage {
    private int id;
    private int conversationId;
    private int senderId;
    private Integer recipientId; // optional, null for group messages
    private String content;
    private String type; // TEXT, IMAGE, etc.
    private boolean edited;
    private LocalDateTime timestamp;

    // Constructors
    public ChatMessage() {}

    public ChatMessage(int id, int conversationId, int senderId, Integer recipientId, String content, String type, boolean edited, LocalDateTime timestamp) {
        this.id = id;
        this.conversationId = conversationId;
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.content = content;
        this.type = type;
        this.edited = edited;
        this.timestamp = timestamp;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getConversationId() { return conversationId; }
    public void setConversationId(int conversationId) { this.conversationId = conversationId; }
    public int getSenderId() { return senderId; }
    public void setSenderId(int senderId) { this.senderId = senderId; }
    public Integer getRecipientId() { return recipientId; }
    public void setRecipientId(Integer recipientId) { this.recipientId = recipientId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public boolean isEdited() { return edited; }
    public void setEdited(boolean edited) { this.edited = edited; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
