<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<aside class="sidebar" id="sidebar">

    <!-- LOGO + COLLAPSE -->
    <div class="sidebar-logo">
        <span class="logo-mark">🇪🇹</span>
        <span class="logo-text">EthioMentor</span>

        <!-- Collapse button -->
        <button class="collapse-btn" id="collapseBtn" title="Toggle sidebar">
            ☰
        </button>
    </div>

    <!-- NAVIGATION -->
    <nav class="sidebar-nav">

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

    </nav>

    <!-- FOOTER -->
    <div class="sidebar-footer">
        <a href="${pageContext.request.contextPath}/logout" class="logout-btn">
            <span class="icon">🚪</span>
            <span>Logout</span>
        </a>
    </div>

</aside>

<!-- COLLAPSE SCRIPT -->
<script>
    const sidebar = document.getElementById("sidebar");
    const collapseBtn = document.getElementById("collapseBtn");
    const mainContent = document.querySelector(".main-content");

    collapseBtn.addEventListener("click", () => {
        sidebar.classList.toggle("collapsed");
        mainContent.classList.toggle("collapsed");
    });
</script>
