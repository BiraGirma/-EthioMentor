package com.ethiomentor.controller;

import com.ethiomentor.model.ChatMessage;
import com.ethiomentor.service.ChatService;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/loadMessages")
public class LoadMessagesServlet extends HttpServlet {

    private final ChatService chatService = new ChatService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int userId = (Integer) session.getAttribute("userId");
        String convParam = request.getParameter("conversationId");

        if (convParam == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        int conversationId = Integer.parseInt(convParam);

        try {
            List<ChatMessage> messages = chatService.loadMessages(conversationId, 1000, 0);

            JSONArray arr = new JSONArray();
            for (ChatMessage m : messages) {
                JSONObject obj = new JSONObject();
                obj.put("id", m.getId());
                obj.put("senderId", m.getSenderId());
                obj.put("content", m.getContent());
                obj.put("timestamp", m.getTimestamp().toString());
                arr.put(obj);
            }

            response.setContentType("application/json");
            response.getWriter().write(arr.toString());

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
