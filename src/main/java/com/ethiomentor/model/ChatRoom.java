package com.ethiomentor.model;

import java.util.List;

public class ChatRoom {
    private Conversation conversation;
    private List<ChatMessage> messages;

    public ChatRoom() {}

    public ChatRoom(Conversation conversation, List<ChatMessage> messages) {
        this.conversation = conversation;
        this.messages = messages;
    }

    public Conversation getConversation() { return conversation; }
    public void setConversation(Conversation conversation) { this.conversation = conversation; }
    public List<ChatMessage> getMessages() { return messages; }
    public void setMessages(List<ChatMessage> messages) { this.messages = messages; }
}
