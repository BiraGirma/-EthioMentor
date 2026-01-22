<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<<<<<<< HEAD
<c:set var="userRole" value="${sessionScope.role}" />

=======
<<<<<<< HEAD
=======
<c:set var="userRole" value="${sessionScope.role}" />

>>>>>>> 8fb5b46 (finilized)
>>>>>>> 2bdd21ccaf16834af801aaad4a078e24691fa627
<aside class="sidebar" id="sidebar">

    <!-- LOGO + COLLAPSE -->
    <div class="sidebar-logo">
        <span class="logo-mark">🇪🇹</span>
        <span class="logo-text">EthioMentor</span>

<<<<<<< HEAD
        <button class="collapse-btn" id="collapseBtn" title="Toggle sidebar">☰</button>
=======
<<<<<<< HEAD
        <!-- Collapse button -->
        <button class="collapse-btn" id="collapseBtn" title="Toggle sidebar">
            ☰
        </button>
=======
        <button class="collapse-btn" id="collapseBtn" title="Toggle sidebar">☰</button>
>>>>>>> 8fb5b46 (finilized)
>>>>>>> 2bdd21ccaf16834af801aaad4a078e24691fa627
    </div>

    <!-- NAVIGATION -->
    <nav class="sidebar-nav">
<<<<<<< HEAD
=======
<<<<<<< HEAD

        <a href="dashboard" class="nav-item active">
            <span class="icon">📊</span>
            <span class="nav-text">Dashboard</span>
            <span class="bubble"></span>
        </a>

        <a href="MentorMatching.jsp" class="nav-item">
            <span class="icon">🤝</span>
            <span class="nav-text">Mentor Matching</span>
            <span class="bubble"></span>
        </a>

        <a href="studygroups.jsp" class="nav-item">
            <span class="icon">👥</span>
            <span class="nav-text">Study Groups</span>
            <span class="bubble"></span>
        </a>

        <a href="forum" class="nav-item">
            <span class="icon">💬</span>
            <span class="nav-text">Forum</span>
            <span class="bubble"></span>
        </a>

        <a href="messaging" class="nav-item">
            <span class="icon">✉️</span>
            <span class="nav-text">Messages</span>
            <span class="bubble"></span>
        </a>

        <!-- ADMIN ONLY -->
        <c:if test="${userRole == 'admin'}">
            <a href="admindashboard" class="nav-item">
                <span class="icon">🛡️</span>
                <span class="nav-text">Admin</span>
                <span class="bubble"></span>
            </a>
        </c:if>

=======
>>>>>>> 2bdd21ccaf16834af801aaad4a078e24691fa627
        <c:choose>
            <c:when test="${userRole eq 'mentor'}">
                <a href="mentor-dashboard" class="nav-item">📊 Dashboard</a>
                <a href="studygroups.jsp" class="nav-item">👥 Study Groups</a>
                <a href="chat.jsp" class="nav-item">✉️ Messages</a>
            </c:when>

            <c:when test="${userRole eq 'admin'}">
                <a href="admin" class="nav-item">🛡️ Admin</a>
                <a href="studygroups.jsp" class="nav-item">👥 Study Groups</a>
                <a href="chat.jsp" class="nav-item">✉️ Messages</a>
            </c:when>

            <c:otherwise>
                <a href="dashboard" class="nav-item">📊 Dashboard</a>
                <a href="studygroups.jsp" class="nav-item">👥 Study Groups</a>
                <a href="MentorMatching.jsp" class="nav-item">🤝 Mentor Matching</a>
                <a href="chat.jsp" class="nav-item">✉️ Messages</a>
            </c:otherwise>
        </c:choose>
<<<<<<< HEAD
=======
>>>>>>> 8fb5b46 (finilized)
>>>>>>> 2bdd21ccaf16834af801aaad4a078e24691fa627
    </nav>

    <!-- FOOTER -->
    <div class="sidebar-footer">
<<<<<<< HEAD
        <a href="${pageContext.request.contextPath}/logout" class="logout-btn">🚪 Logout</a>
=======
<<<<<<< HEAD
        <a href="${pageContext.request.contextPath}/logout" class="logout-btn">
            <span class="icon">🚪</span>
            <span>Logout</span>
        </a>
=======
        <a href="${pageContext.request.contextPath}/logout" class="logout-btn">🚪 Logout</a>
>>>>>>> 8fb5b46 (finilized)
>>>>>>> 2bdd21ccaf16834af801aaad4a078e24691fa627
    </div>

</aside>

<<<<<<< HEAD
<script>
document.addEventListener("DOMContentLoaded", () => {
=======
<<<<<<< HEAD
<!-- COLLAPSE SCRIPT -->
<script>
=======
<script>
document.addEventListener("DOMContentLoaded", () => {
>>>>>>> 8fb5b46 (finilized)
>>>>>>> 2bdd21ccaf16834af801aaad4a078e24691fa627
    const sidebar = document.getElementById("sidebar");
    const collapseBtn = document.getElementById("collapseBtn");
    const mainContent = document.querySelector(".main-content");

<<<<<<< HEAD
=======
<<<<<<< HEAD
    collapseBtn.addEventListener("click", () => {
        sidebar.classList.toggle("collapsed");
        mainContent.classList.toggle("collapsed");
    });
=======
>>>>>>> 2bdd21ccaf16834af801aaad4a078e24691fa627
    const isCollapsed = localStorage.getItem('sidebar-collapsed') === 'true';
    if (isCollapsed) {
        sidebar.classList.add('collapsed');
        if (mainContent) mainContent.classList.add('collapsed');
    }

    collapseBtn.addEventListener("click", () => {
        sidebar.classList.toggle('collapsed');
        if (mainContent) mainContent.classList.toggle('collapsed');
        localStorage.setItem('sidebar-collapsed', sidebar.classList.contains('collapsed'));
    });
});
<<<<<<< HEAD
=======
>>>>>>> 8fb5b46 (finilized)
>>>>>>> 2bdd21ccaf16834af801aaad4a078e24691fa627
</script>
