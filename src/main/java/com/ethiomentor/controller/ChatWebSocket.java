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

    @OnMessage
    public void onMessage(Session session, String jsonMessage) throws Exception {

        try {
            // Parse incoming JSON
            JSONObject obj = new JSONObject(jsonMessage);

            ChatMessage msg = new ChatMessage();
            msg.setConversationId(obj.getInt("conversationId"));
            msg.setSenderId(userId);
            msg.setContent(obj.getString("content"));
            msg.setType("TEXT");

            // Save message
            ChatMessage saved = chatService.sendMessage(msg);

            // Broadcast to all participants
            List<Integer> participants =
                    chatService.getParticipants(saved.getConversationId());

            JSONObject response = new JSONObject();
            response.put("id", saved.getId());
            response.put("conversationId", saved.getConversationId());
            response.put("senderId", saved.getSenderId());
            response.put("content", saved.getContent());
            response.put("timestamp", saved.getTimestamp().toString());

            for (int uid : participants) {
                Session s = activeUsers.get(uid);
                if (s != null && s.isOpen()) {
                    s.getBasicRemote().sendText(response.toString());
                }
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            try {
                session.getBasicRemote()
                        .sendText("{\"error\":\"Message failed\"}");
            } catch (IOException ignored) {}
        }
    }

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
