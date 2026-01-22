<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="userRole" value="${sessionScope.role}" />

<aside class="sidebar" id="sidebar">

    <!-- LOGO + COLLAPSE -->
    <div class="sidebar-logo">
        <span class="logo-mark">🇪🇹</span>
        <span class="logo-text">EthioMentor</span>

        <button class="collapse-btn" id="collapseBtn" title="Toggle sidebar">☰</button>
    </div>

    <!-- NAVIGATION -->
    <nav class="sidebar-nav">
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
    </nav>

    <!-- FOOTER -->
    <div class="sidebar-footer">
        <a href="${pageContext.request.contextPath}/logout" class="logout-btn">🚪 Logout</a>
    </div>

</aside>

<script>
document.addEventListener("DOMContentLoaded", () => {
    const sidebar = document.getElementById("sidebar");
    const collapseBtn = document.getElementById("collapseBtn");
    const mainContent = document.querySelector(".main-content");

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
</script>
