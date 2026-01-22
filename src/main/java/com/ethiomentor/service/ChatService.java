package com.ethiomentor.service;

import com.ethiomentor.dao.ChatDAO;
import com.ethiomentor.dao.ConversationDAO;
import com.ethiomentor.dao.MessageStatusDAO;
import com.ethiomentor.model.ChatMessage;
import com.ethiomentor.model.Conversation;
import com.ethiomentor.dao.UserDAO;
import com.ethiomentor.model.User;
import java.util.List;
import java.sql.SQLException;
import java.util.List;

public class ChatService {

    private final ChatDAO chatDAO;
    private final ConversationDAO conversationDAO;
    private final MessageStatusDAO messageStatusDAO;

    public ChatService() {
        this.chatDAO = new ChatDAO();
        this.conversationDAO = new ConversationDAO();
        this.messageStatusDAO = new MessageStatusDAO();
    }

    /**
     * Create a new conversation (PRIVATE or GROUP) and register participants
     */
    public Conversation createConversation(String type, List<Integer> participantIds)
            throws Exception {

        Conversation conversation = conversationDAO.createConversation(type);

        for (int userId : participantIds) {
            conversationDAO.addParticipant(conversation.getId(), userId);
        }

        conversation.setParticipantIds(participantIds);
        return conversation;
    }

    /**
     * Send a chat message and initialize message status
     */
    public ChatMessage sendMessage(ChatMessage message) throws Exception {

        // Persist message
        ChatMessage savedMessage = chatDAO.saveMessage(message);

        if (savedMessage == null) {
            throw new SQLException("Message could not be saved");
        }

        // Initialize status for all participants
        List<Integer> participants =
                conversationDAO.getParticipants(savedMessage.getConversationId());

        for (int userId : participants) {
            String status = (userId == savedMessage.getSenderId())
                    ? "READ"
                    : "SENT";

            messageStatusDAO.updateStatus(savedMessage.getId(), userId, status);
        }

        return savedMessage;
    }

    /**
     * Get participants of a conversation
     */
    public List<Integer> getParticipants(int conversationId)
            throws Exception {
        return conversationDAO.getParticipants(conversationId);
    }

    /**
     * Load chat history with pagination
     */
    public List<ChatMessage> loadMessages(int conversationId, int limit, int offset)
            throws Exception {
        return chatDAO.getMessagesByConversation(conversationId, limit, offset);
    }

    /**
     * Update message status (DELIVERED or READ)
     */
    public void updateMessageStatus(int messageId, int userId, String status)
            throws Exception {
        messageStatusDAO.updateStatus(messageId, userId, status);
    }

    /**
     * Fetch all conversations for a given user
     * Used by chat.jsp to populate the sidebar
     */
    public List<Conversation> getUserConversations(int userId) throws Exception {
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid userId");
        }
        return conversationDAO.getUserConversations(userId);
    }
    
    public List<User> getPotentialChatUsers(int currentUserId) throws Exception {
        UserDAO userDAO = new UserDAO();
        return userDAO.getAllOtherUsers(currentUserId); // implement in DAO
    }
    
    public int getOrCreatePrivateConversation(int user1Id, int user2Id) throws Exception {
<<<<<<< HEAD
=======
<<<<<<< HEAD
        // check if a private conversation exists
        Integer existingId = conversationDAO.getPrivateConversationId(user1Id, user2Id);
        if (existingId != null) return existingId;

        // create new conversation
        Conversation conv = conversationDAO.createConversation("PRIVATE");
        conversationDAO.addParticipant(conv.getId(), user1Id);
        conversationDAO.addParticipant(conv.getId(), user2Id);
        return conv.getId();
    }

=======
>>>>>>> 2bdd21ccaf16834af801aaad4a078e24691fa627

        if (user1Id <= 0 || user2Id <= 0) {
            throw new IllegalArgumentException(
                "Invalid user IDs: user1Id=" + user1Id + ", user2Id=" + user2Id
            );
        }

        if (user1Id == user2Id) {
            throw new IllegalArgumentException("Cannot create conversation with self");
        }

        Integer existingId =
            conversationDAO.getPrivateConversationId(user1Id, user2Id);

        if (existingId != null) return existingId;

        Conversation conv = conversationDAO.createConversation("PRIVATE");

        conversationDAO.addParticipant(conv.getId(), user1Id);
        conversationDAO.addParticipant(conv.getId(), user2Id);

        return conv.getId();
    }


<<<<<<< HEAD
=======
>>>>>>> 8fb5b46 (finilized)
>>>>>>> 2bdd21ccaf16834af801aaad4a078e24691fa627
}
