package com.ethiomentor.controller;

import com.ethiomentor.model.ChatMessage;
import com.ethiomentor.service.ChatService;
import com.ethiomentor.util.InputUtil;
import com.ethiomentor.util.JsonUtil;
import com.ethiomentor.util.SessionUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/loadMessages")
public class LoadMessagesServlet extends HttpServlet {

    private final ChatService chatService = new ChatService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        var user = SessionUtil.currentUser(request);
        if (user == null) {
            JsonUtil.error(response, HttpServletResponse.SC_UNAUTHORIZED, "Not logged in");
            return;
        }

        int conversationId = InputUtil.parseId(request.getParameter("conversationId"));
        if (conversationId < 0) {
            JsonUtil.error(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid conversationId");
            return;
        }

        try {
            // Only members of a conversation may read it.
            if (!chatService.isParticipant(conversationId, user.getId())) {
                JsonUtil.error(response, HttpServletResponse.SC_FORBIDDEN, "Not a participant");
                return;
            }

            List<ChatMessage> messages = chatService.loadMessages(conversationId, 1000, 0);

            JSONArray arr = new JSONArray();
            for (ChatMessage m : messages) {
                arr.put(new JSONObject()
                        .put("id", m.getId())
                        .put("conversationId", m.getConversationId())
                        .put("senderId", m.getSenderId())
                        .put("content", m.getContent())
                        .put("timestamp", m.getTimestamp().toString()));
            }
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(arr.toString());

        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.error(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not load messages");
        }
    }
}
