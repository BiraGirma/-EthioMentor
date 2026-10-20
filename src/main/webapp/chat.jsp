<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.ethiomentor.model.User" %>
<%@ page import="com.ethiomentor.service.ChatService" %>
<%@ page import="com.ethiomentor.util.InputUtil" %>
<%@ page import="com.ethiomentor.util.SessionUtil" %>

<%
    User me = SessionUtil.currentUser(request);
    if (me == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    int userId = me.getId();
    String CONTEXT_PATH = request.getContextPath();

    ChatService chatService = new ChatService();
    List<User> potentialUsers = chatService.getPotentialChatUsers(userId);

    if (potentialUsers.isEmpty()) {
        out.println("<h2>No users available to chat.</h2>");
        return;
    }

    // Choose the chat partner: ?userId=... if it names an available user, otherwise the first one.
    int requestedId = InputUtil.parseId(request.getParameter("userId"));
    User selected = null;
    for (User u : potentialUsers) {
        if (u.getId() == requestedId) { selected = u; break; }
    }
    if (selected == null) {
        if (request.getParameter("userId") != null && !request.getParameter("userId").isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user selected.");
            return;
        }
        selected = potentialUsers.get(0);
    }
    int selectedUserId = selected.getId();

    // Get or create the private conversation (history is fetched by chat.js via /loadMessages)
    int conversationId = chatService.getOrCreatePrivateConversation(userId, selectedUserId);
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
            <a class="nav-item" href="<%=CONTEXT_PATH + SessionUtil.homeFor(me)%>">← Dashboard</a>
            <% for (User u : potentialUsers) { %>
                <a class="nav-item <%= (u.getId() == selectedUserId) ? "active" : "" %>"
                   href="<%=CONTEXT_PATH%>/chat.jsp?userId=<%=u.getId()%>">
                    <%=InputUtil.escapeHtml(u.getFullName())%> (<%=InputUtil.escapeHtml(u.getRole())%>)
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
                        <strong><%= InputUtil.escapeHtml(selected.getFullName()) %></strong>
                    </div>
                </div>
            </div>

            <!-- Messages -->
            <div id="chatMessages" class="chat-messages"></div>

            <!-- Input -->
            <div class="chat-input">
                <input type="text" id="messageInput" placeholder="Type your message…" maxlength="2000">
                <button onclick="sendMessage()">Send</button>
            </div>

        </div>
    </main>

</div>

<script>
const USER_ID = <%= userId %>;
const SELECTED_USER_ID = <%= selectedUserId %>;
const CONVERSATION_ID = <%= conversationId %>;
const CONTEXT_PATH = "<%= CONTEXT_PATH %>";
</script>

<script src="<%=CONTEXT_PATH%>/js/chat.js"></script>
</body>
</html>
