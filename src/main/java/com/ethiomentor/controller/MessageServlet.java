package com.ethiomentor.controller;

import com.ethiomentor.dao.MessageDAO;
import com.ethiomentor.util.JsonUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/api/message")
public class MessageServlet extends HttpServlet {

    private final MessageDAO dao = new MessageDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            int sender = Integer.parseInt(req.getParameter("senderId"));
            int receiver = Integer.parseInt(req.getParameter("receiverId"));
            String content = req.getParameter("content");

            dao.sendMessage(sender, receiver, content);
            JsonUtil.send(resp, 201, "{\"message\":\"Sent\"}");

        } catch (Exception e) {
            try {
                JsonUtil.send(resp, 400, "{\"error\":\"" + e.getMessage() + "\"}");
            } catch (Exception ignored) {}
        }
    }
}
