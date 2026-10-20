package com.ethiomentor.controller;

import com.ethiomentor.model.ChatMessage;
import com.ethiomentor.service.ChatService;
import com.ethiomentor.util.SessionUtil;
import org.json.JSONObject;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.*;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Real-time chat. A connection is accepted only for a logged-in user whose
 * handshake Origin matches the Host. Messages may only be posted to
 * conversations the sender belongs to, and are delivered to all participants'
 * open connections (several tabs per user are supported).
 */
@ServerEndpoint(
        value = "/ws/chat",
        configurator = ChatWebSocket.HttpSessionConfigurator.class
)
public class ChatWebSocket {

    static final int MAX_MESSAGE_CHARS = 2000;

    private static final ChatService chatService = new ChatService();

    // userId -> open connections of that user
    private static final ConcurrentHashMap<Integer, Set<Session>> activeUsers = new ConcurrentHashMap<>();

    private int userId;

    /* ---------- handshake: carry the HttpSession, Origin and Host into the endpoint ---------- */
    public static class HttpSessionConfigurator extends ServerEndpointConfig.Configurator {

        @Override
        public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
            HttpSession httpSession = (HttpSession) request.getHttpSession();
            if (httpSession != null) {
                sec.getUserProperties().put(HttpSession.class.getName(), httpSession);
            }
            sec.getUserProperties().put("origin", first(request, "Origin"));
            sec.getUserProperties().put("host", first(request, "Host"));
        }

        private static String first(HandshakeRequest request, String header) {
            List<String> v = request.getHeaders().get(header);
            if (v == null) {
                for (var e : request.getHeaders().entrySet()) {
                    if (e.getKey().equalsIgnoreCase(header)) { v = e.getValue(); break; }
                }
            }
            return (v == null || v.isEmpty()) ? null : v.get(0);
        }
    }

    /** True if there is no Origin header (non-browser client) or its host:port equals the Host header. */
    public static boolean originMatchesHost(String origin, String host) {
        if (origin == null) return true;
        if (host == null) return false;
        try {
            URI u = URI.create(origin);
            String authority = u.getPort() == -1 ? u.getHost() : u.getHost() + ":" + u.getPort();
            return authority != null && authority.equalsIgnoreCase(host);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) throws IOException {
        HttpSession httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        String origin = (String) config.getUserProperties().get("origin");
        String host = (String) config.getUserProperties().get("host");

        Object id = null;
        try {
            id = httpSession == null ? null : httpSession.getAttribute(SessionUtil.USER_ID);
        } catch (IllegalStateException invalidated) {
            // session already invalidated
        }

        if (!(id instanceof Integer) || !originMatchesHost(origin, host)) {
            session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Unauthorized"));
            return;
        }

        this.userId = (Integer) id;
        activeUsers.computeIfAbsent(userId, k -> new CopyOnWriteArraySet<>()).add(session);
    }

    @OnMessage
    public void onMessage(Session session, String jsonMessage) {
        if (userId <= 0) return;
        try {
            JSONObject obj = new JSONObject(jsonMessage);

            int conversationId = obj.getInt("conversationId");
            String content = obj.getString("content").trim();
            if (content.isEmpty() || content.length() > MAX_MESSAGE_CHARS) {
                sendTo(session, "{\"error\":\"Message must be 1-" + MAX_MESSAGE_CHARS + " characters\"}");
                return;
            }

            List<Integer> participants = chatService.getParticipants(conversationId);
            if (!participants.contains(userId)) {
                sendTo(session, "{\"error\":\"Not a participant of this conversation\"}");
                return;
            }

            ChatMessage msg = new ChatMessage();
            msg.setConversationId(conversationId);
            msg.setSenderId(userId);
            msg.setContent(content);
            msg.setType("TEXT");
            // For a 1:1 chat the recipient is the other participant; never trust the client for this.
            if (participants.size() == 2) {
                msg.setRecipientId(participants.get(0) == userId ? participants.get(1) : participants.get(0));
            }

            ChatMessage saved = chatService.sendMessage(msg);

            JSONObject response = new JSONObject()
                    .put("id", saved.getId())
                    .put("conversationId", saved.getConversationId())
                    .put("senderId", saved.getSenderId())
                    .put("content", saved.getContent())
                    .put("timestamp", saved.getTimestamp().toString());
            if (saved.getRecipientId() != null) response.put("recipientId", saved.getRecipientId());
            String out = response.toString();

            for (int uid : participants) {
                Set<Session> sessions = activeUsers.get(uid);
                if (sessions == null) continue;
                for (Session s : sessions) sendTo(s, out);
            }

        } catch (Exception e) {
            e.printStackTrace();
            sendTo(session, "{\"error\":\"Message failed\"}");
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        if (userId <= 0) return;
        Set<Session> sessions = activeUsers.get(userId);
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) activeUsers.remove(userId, sessions);
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        throwable.printStackTrace();
    }

    /** Sends text; writes to one session are serialised because the container forbids concurrent sends. */
    private static void sendTo(Session s, String text) {
        if (s == null || !s.isOpen()) return;
        synchronized (s) {
            try {
                s.getBasicRemote().sendText(text);
            } catch (IOException | IllegalStateException ignored) {
                // connection went away; onClose will clean up
            }
        }
    }
}
