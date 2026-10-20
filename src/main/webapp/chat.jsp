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

    String initials = "";
    for (String part : selected.getFullName().trim().split("\\s+")) {
        if (!part.isEmpty() && initials.length() < 2) initials += part.substring(0, 1).toUpperCase();
    }
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Messages | EthioMentor</title>
    <link rel="stylesheet" href="<%=CONTEXT_PATH%>/css/global.css">
    <link rel="stylesheet" href="<%=CONTEXT_PATH%>/css/app-shell.css">
    <link rel="stylesheet" href="<%=CONTEXT_PATH%>/css/chat.css">
    <script defer src="<%=CONTEXT_PATH%>/js/shell.js"></script>
</head>
<body>

<div class="app-layout">

    <jsp:include page="WEB-INF/jsp/includes/sidebar.jsp"/>

    <main class="main-content chat-page">
        <div class="chat-layout">

            <!-- Conversation list -->
            <aside class="chat-contacts" aria-labelledby="contactsHeading">
                <h1 id="contactsHeading" class="chat-contacts-title">Messages</h1>
                <nav aria-label="Conversations">
                    <ul class="contact-list">
                        <% for (User u : potentialUsers) {
                               boolean current = u.getId() == selectedUserId; %>
                        <li>
                            <a class="contact<%= current ? " active" : "" %>"
                               href="<%=CONTEXT_PATH%>/chat.jsp?userId=<%=u.getId()%>"
                               <%= current ? "aria-current=\"page\"" : "" %>>
                                <span class="contact-name"><%=InputUtil.escapeHtml(u.getFullName())%></span>
                                <span class="contact-role"><%=InputUtil.escapeHtml(u.getRole())%></span>
                            </a>
                        </li>
                        <% } %>
                    </ul>
                </nav>
            </aside>

            <!-- Conversation -->
            <section class="chat-container" aria-labelledby="chatWithHeading">
                <header class="chat-header">
                    <span class="chat-avatar" aria-hidden="true"><%=InputUtil.escapeHtml(initials)%></span>
                    <div>
                        <h2 id="chatWithHeading" class="chat-with"><%=InputUtil.escapeHtml(selected.getFullName())%></h2>
                        <span class="chat-role"><%=InputUtil.escapeHtml(selected.getRole())%></span>
                    </div>
                </header>

                <div id="chatMessages" class="chat-messages" role="log" aria-live="polite" aria-label="Messages"></div>

                <form id="chatForm" class="chat-input" autocomplete="off">
                    <label class="visually-hidden" for="messageInput">Message</label>
                    <input type="text" id="messageInput" placeholder="Type your message…" maxlength="2000">
                    <button type="submit" class="btn-send">Send</button>
                </form>
            </section>

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
