<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.ethiomentor.model.User" %>
<%@ page import="com.ethiomentor.model.ChatMessage" %>
<%@ page import="com.ethiomentor.service.ChatService" %>

<%
    Integer userId = (Integer) session.getAttribute("userId");
    if (userId == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    ChatService chatService = new ChatService();

    List<User> potentialUsers = chatService.getPotentialChatUsers(userId);

    String selectedUserIdParam = request.getParameter("userId");
    int selectedUserId;

    if (selectedUserIdParam != null && !selectedUserIdParam.isEmpty()) {
        selectedUserId = Integer.parseInt(selectedUserIdParam);
    } else if (!potentialUsers.isEmpty()) {
        selectedUserId = potentialUsers.get(0).getId();
    } else {
        out.println("<h2>No users available to chat.</h2>");
        return;
    }

    if (selectedUserId <= 0 || selectedUserId == userId) {
        out.println("<h2>Invalid user selected.</h2>");
        return;
    }

    // Get or create private conversation
    int conversationId = chatService.getOrCreatePrivateConversation(userId, selectedUserId);

    // Load previous messages
    List<ChatMessage> messages = chatService.loadMessages(conversationId, 1000, 0);

    String CONTEXT_PATH = request.getContextPath();
%>

<!DOCTYPE html>
<html>
<head>
    <title>Chat | Ethiomentor</title>
    <link rel="stylesheet" href="<%=CONTEXT_PATH%>/css/global.css">
    <link rel="stylesheet" href="<%=CONTEXT_PATH%>/css/chat.css">
</head>
<body>

<div class="app-layout">

    <!-- Sidebar -->
    <aside class="sidebar">
        <div class="sidebar-logo">
            <span class="logo-mark">◆</span>
            <span class="logo-text">Ethiomentor</span>
        </div>
        <nav class="sidebar-nav">
            <% for (User u : potentialUsers) { %>
                <a class="nav-item <%= (u.getId() == selectedUserId) ? "active" : "" %>"
                   href="<%=CONTEXT_PATH%>/chat.jsp?userId=<%=u.getId()%>">
                    <%=u.getFullName()%> (<%=u.getRole()%>)
                </a>
            <% } %>
        </nav>
    </aside>

    <!-- Main Chat Area -->
    <main class="main-content">
        <div class="chat-container">

            <!-- Chat Header -->
            <div class="chat-header">
                <div class="chat-user">
                    <img src="https://www.w3schools.com/howto/img_avatar.png" alt="avatar" width="50" height="50">

                    <div>
                        <strong>
                            <%= potentialUsers.stream()
                                .filter(u -> u.getId() == selectedUserId)
                                .findFirst()
                                .map(User::getFullName)
                                .orElse("User") %>
                        </strong>
                        <span class="status">● Online</span>
                    </div>
                </div>
            </div>

            <!-- Messages -->
            <div id="chatMessages" class="chat-messages"></div>

            <!-- Input -->
            <div class="chat-input">
                <input type="text" id="messageInput" placeholder="Type your message…">
                <button onclick="sendMessage()">Send</button>
            </div>

        </div>
    </main>

</div>

<script>
const USER_ID = <%= userId %>;
const SELECTED_USER_ID = <%= selectedUserId %>;
const CONVERSATION_ID = <%= conversationId %>;
const CONTEXT_PATH = "<%=CONTEXT_PATH%>";

const CHAT_HISTORY = [
<% for (ChatMessage m : messages) {
       String contentEscaped = m.getContent()
                               .replace("\\","\\\\")
                               .replace("\"","\\\"")
                               .replace("\n","\\n")
                               .replace("\r",""); %>
{
    id: <%= m.getId() %>,
    senderId: <%= m.getSenderId() %>,
    recipientId: <%= (m.getRecipientId() != null) ? m.getRecipientId() : "null" %>,
    content: "<%= contentEscaped %>",
    timestamp: "<%= m.getTimestamp() %>"
},
<% } %>
];
</script>

<script src="<%=CONTEXT_PATH%>/js/chat.js"></script>
</body>
</html>
