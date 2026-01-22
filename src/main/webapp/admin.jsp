<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Admin Dashboard</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/sidebar.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
</head>
<body>

<div class="app-layout d-flex">

    <!-- SIDEBAR -->
    <jsp:include page="WEB-INF/jsp/includes/sidebar.jsp"/>

    <!-- MAIN CONTENT -->
    <div class="container-fluid p-4 main-content">

        <h1 class="mb-4">Admin Dashboard</h1>

        <!-- ANALYTICS CARDS -->
        <div class="row mb-4">
            <div class="col-md-4">
                <div class="card p-3 text-center">
                    <h5>Total Users</h5>
                    <h2><c:out value="${totalUsers}" /></h2>
                </div>
            </div>
            <div class="col-md-4">
                <div class="card p-3 text-center">
                    <h5>Total Study Groups</h5>
                    <h2><c:out value="${totalGroups}" /></h2>
                </div>
            </div>
            <div class="col-md-4">
                <div class="card p-3 text-center">
                    <h5>Total Messages</h5>
                    <h2><c:out value="${totalMessages}" /></h2>
                </div>
            </div>
        </div>

        <!-- USERS TABLE -->
        <div class="card mb-4 p-3">
            <h3>All Users</h3>
            <table class="table table-striped">
                <thead>
                    <tr>
                        <th>Name</th><th>Username</th><th>Role</th><th>University</th><th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${not empty users}">
                            <c:forEach var="u" items="${users}">
                                <tr>
                                    <td><c:out value="${u.fullName}" /></td>
                                    <td><c:out value="${u.username}" /></td>
                                    <td><c:out value="${u.role}" /></td>
                                    <td><c:out value="${u.university}" /></td>
                                    <td>
                                        <form method="post">
                                            <input type="hidden" name="action" value="deleteUser">
                                            <input type="hidden" name="id" value="${u.id}">
                                            <button class="btn btn-danger btn-sm">Delete</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr><td colspan="5">No users found.</td></tr>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>

        <!-- PENDING MENTORS -->
        <div class="card mb-4 p-3">
            <h3>Pending Mentor Requests</h3>
            <table class="table table-striped">
                <thead>
                    <tr><th>Name</th><th>Email</th><th>University</th><th>Action</th></tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${not empty mentorRequests}">
                            <c:forEach var="m" items="${mentorRequests}">
                                <tr>
                                    <td><c:out value="${m.fullName}" /></td>
                                    <td><c:out value="${m.email}" /></td>
                                    <td><c:out value="${m.university}" /></td>
                                    <td>
                                        <form method="post" style="display:inline">
                                            <input type="hidden" name="action" value="approveMentor">
                                            <input type="hidden" name="id" value="${m.id}">
                                            <button class="btn btn-success btn-sm">Approve</button>
                                        </form>
                                        <form method="post" style="display:inline">
                                            <input type="hidden" name="action" value="rejectMentor">
                                            <input type="hidden" name="id" value="${m.id}">
                                            <button class="btn btn-warning btn-sm">Reject</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr><td colspan="4">No mentor requests.</td></tr>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>

        <!-- STUDY GROUPS -->
        <div class="card mb-4 p-3">
            <h3>Study Groups</h3>
            <table class="table table-striped">
                <thead>
                    <tr><th>Name</th><th>Description</th><th>Members</th><th>Action</th></tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${not empty groups}">
                            <c:forEach var="g" items="${groups}">
                                <tr>
                                    <td><c:out value="${g.name}" /></td>
                                    <td><c:out value="${g.description}" /></td>
                                    <td><c:out value="${g.membersCount}" /></td>
                                    <td>
                                        <form method="post">
                                            <input type="hidden" name="action" value="deleteGroup">
                                            <input type="hidden" name="id" value="${g.id}">
                                            <button class="btn btn-danger btn-sm">Delete</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr><td colspan="4">No study groups found.</td></tr>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>

        <!-- ADMIN CHAT -->
        <div class="card mb-4 p-3">
            <h3>Admin Chat</h3>
            <div id="chatWindow" class="border p-2 mb-2" style="height:300px; overflow-y:scroll; background:#f8f9fa;">
                <!-- Messages will appear here -->
            </div>
            <form id="adminChatForm">
                <div class="input-group">
                    <input type="text" id="chatMessage" class="form-control" placeholder="Type a message...">
                    <button class="btn btn-primary" type="submit">Send</button>
                </div>
            </form>
        </div>

    </div> <!-- END MAIN CONTENT -->
</div> <!-- END APP LAYOUT -->

<script>
const ws = new WebSocket("ws://<%=request.getServerName()%>:<%=request.getServerPort()%><%=request.getContextPath()%>/ws/chat");
const chatWindow = document.getElementById("chatWindow");
const chatForm = document.getElementById("adminChatForm");
const chatInput = document.getElementById("chatMessage");

ws.onopen = () => console.log("Admin chat connected");
ws.onmessage = (event) => {
    const msg = JSON.parse(event.data);
    const p = document.createElement("p");
    p.innerHTML = `<strong>${msg.sender}:</strong> ${msg.content}`;
    chatWindow.appendChild(p);
    chatWindow.scrollTop = chatWindow.scrollHeight;
};

chatForm.addEventListener("submit", function(e) {
    e.preventDefault();
    const message = chatInput.value.trim();
    if(message) {
        ws.send(JSON.stringify({
            sender: "${sessionScope.user.username}",
            content: message
        }));
        chatInput.value = "";
    }
});
</script>

</body>
</html>
