<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Admin Dashboard</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/global.css">
    <link rel="stylesheet" href="/css/app-shell.css">
    <script defer src="/js/shell.js"></script>
</head>
<body>

<div class="app-layout">

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
                        <th>Name</th><th>Username</th><th>Role</th><th>University</th><th>Status</th><th>Action</th>
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
                                        <c:choose>
                                            <c:when test="${u.active}"><span class="badge bg-success">Active</span></c:when>
                                            <c:otherwise><span class="badge bg-secondary">Deactivated</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <form method="post" style="display:inline">
                                            <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
                                            <input type="hidden" name="action" value="${u.active ? 'deactivateUser' : 'activateUser'}">
                                            <input type="hidden" name="id" value="${u.id}">
                                            <button class="btn btn-outline-secondary btn-sm">${u.active ? 'Deactivate' : 'Activate'}</button>
                                        </form>
                                        <form method="post" style="display:inline" onsubmit="return confirm('Delete this user and all their messages? This cannot be undone.');">
                                            <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
                                            <input type="hidden" name="action" value="deleteUser">
                                            <input type="hidden" name="id" value="${u.id}">
                                            <button class="btn btn-danger btn-sm">Delete</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr><td colspan="6">No users found.</td></tr>
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
                                            <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
                                            <input type="hidden" name="action" value="approveMentor">
                                            <input type="hidden" name="id" value="${m.id}">
                                            <button class="btn btn-success btn-sm">Approve</button>
                                        </form>
                                        <form method="post" style="display:inline">
                                            <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
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
                                        <form method="post" onsubmit="return confirm('Delete this study group?');">
                                            <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
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

        <!-- MESSAGES -->
        <div class="card mb-4 p-3">
            <h3>Messages</h3>
            <p class="mb-2">Chat with any user from the Messages page.</p>
            <div><a class="btn btn-primary" href="${pageContext.request.contextPath}/chat.jsp">Open Messages</a></div>
        </div>

    </div> <!-- END MAIN CONTENT -->
</div> <!-- END APP LAYOUT -->


</body>
</html>
