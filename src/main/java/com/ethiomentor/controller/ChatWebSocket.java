package com.ethiomentor.controller;

import com.ethiomentor.model.ChatMessage;
import com.ethiomentor.service.ChatService;
import org.json.JSONObject;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.*;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.ServerEndpointConfig;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint(
        value = "/ws/chat",
        configurator = ChatWebSocket.HttpSessionConfigurator.class
)
public class ChatWebSocket {

    private static final ChatService chatService = new ChatService();

    // userId -> websocket session (thread-safe)
    private static final ConcurrentHashMap<Integer, Session> activeUsers =
            new ConcurrentHashMap<>();

    private int userId;

    /* =======================
       HttpSession bridge
       ======================= */
    public static class HttpSessionConfigurator
            extends ServerEndpointConfig.Configurator {

        @Override
        public void modifyHandshake(ServerEndpointConfig sec,
                                    HandshakeRequest request,
                                    HandshakeResponse response) {

            HttpSession httpSession =
                    (HttpSession) request.getHttpSession();

            if (httpSession != null) {
                sec.getUserProperties()
                        .put(HttpSession.class.getName(), httpSession);
            }
        }
    }

    /* =======================
       WebSocket lifecycle
       ======================= */

    @OnOpen
    public void onOpen(Session session, EndpointConfig config)
            throws IOException {

        HttpSession httpSession =
                (HttpSession) config.getUserProperties()
                        .get(HttpSession.class.getName());

        if (httpSession == null || httpSession.getAttribute("userId") == null) {
            session.close(new CloseReason(
                    CloseReason.CloseCodes.VIOLATED_POLICY,
                    "Unauthorized"
            ));
            return;
        }

        this.userId = (int) httpSession.getAttribute("userId");
        activeUsers.put(userId, session);

        System.out.println("WebSocket connected: userId=" + userId);
    }

<<<<<<< HEAD
  
=======
<<<<<<< HEAD
=======
  
>>>>>>> 8fb5b46 (finilized)
>>>>>>> 2bdd21ccaf16834af801aaad4a078e24691fa627
    @OnMessage
    public void onMessage(Session session, String jsonMessage) throws Exception {

        try {
<<<<<<< HEAD
=======
<<<<<<< HEAD
            // Parse incoming JSON
            JSONObject obj = new JSONObject(jsonMessage);

            ChatMessage msg = new ChatMessage();
            msg.setConversationId(obj.getInt("conversationId"));
            msg.setSenderId(userId);
            msg.setContent(obj.getString("content"));
            msg.setType("TEXT");
=======
>>>>>>> 2bdd21ccaf16834af801aaad4a078e24691fa627
            JSONObject obj = new JSONObject(jsonMessage);

            int conversationId = obj.getInt("conversationId");
            String content = obj.getString("content");
            Integer recipientId = obj.has("recipientId") ? obj.getInt("recipientId") : null;

            ChatMessage msg = new ChatMessage();
            msg.setConversationId(conversationId);
            msg.setSenderId(userId);
            msg.setContent(content);
            msg.setType("TEXT");
            msg.setRecipientId(recipientId); // now correctly set
<<<<<<< HEAD
=======
>>>>>>> 8fb5b46 (finilized)
>>>>>>> 2bdd21ccaf16834af801aaad4a078e24691fa627

            // Save message
            ChatMessage saved = chatService.sendMessage(msg);

            // Broadcast to all participants
<<<<<<< HEAD
            List<Integer> participants = chatService.getParticipants(saved.getConversationId());
=======
<<<<<<< HEAD
            List<Integer> participants =
                    chatService.getParticipants(saved.getConversationId());
=======
            List<Integer> participants = chatService.getParticipants(saved.getConversationId());
>>>>>>> 8fb5b46 (finilized)
>>>>>>> 2bdd21ccaf16834af801aaad4a078e24691fa627

            JSONObject response = new JSONObject();
            response.put("id", saved.getId());
            response.put("conversationId", saved.getConversationId());
            response.put("senderId", saved.getSenderId());
<<<<<<< HEAD
            response.put("recipientId", saved.getRecipientId());
=======
<<<<<<< HEAD
=======
            response.put("recipientId", saved.getRecipientId());
>>>>>>> 8fb5b46 (finilized)
>>>>>>> 2bdd21ccaf16834af801aaad4a078e24691fa627
            response.put("content", saved.getContent());
            response.put("timestamp", saved.getTimestamp().toString());

            for (int uid : participants) {
                Session s = activeUsers.get(uid);
                if (s != null && s.isOpen()) {
                    s.getBasicRemote().sendText(response.toString());
                }
            }

<<<<<<< HEAD
=======
<<<<<<< HEAD
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            try {
                session.getBasicRemote()
                        .sendText("{\"error\":\"Message failed\"}");
=======
>>>>>>> 2bdd21ccaf16834af801aaad4a078e24691fa627
        } catch (Exception e) {
            e.printStackTrace();
            try {
                session.getBasicRemote().sendText("{\"error\":\"Message failed\"}");
<<<<<<< HEAD
=======
>>>>>>> 8fb5b46 (finilized)
>>>>>>> 2bdd21ccaf16834af801aaad4a078e24691fa627
            } catch (IOException ignored) {}
        }
    }

<<<<<<< HEAD

=======
<<<<<<< HEAD
=======

>>>>>>> 8fb5b46 (finilized)
>>>>>>> 2bdd21ccaf16834af801aaad4a078e24691fa627
    @OnClose
    public void onClose(Session session, CloseReason reason) {
        activeUsers.remove(userId);
        System.out.println("WebSocket closed: userId=" + userId);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        throwable.printStackTrace();
    }
}
