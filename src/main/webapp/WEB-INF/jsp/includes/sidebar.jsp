<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="userRole" value="${sessionScope.role}" />
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="uri" value="${pageContext.request.requestURI}" />

<!-- Route flags: which page is the current one (drives the active item and aria-current) -->
<c:set var="onDashboard"   value="${fn:endsWith(uri, '/dashboard')}" />
<c:set var="onMentorDash"  value="${fn:endsWith(uri, '/mentor-dashboard')}" />
<c:set var="onStudyGroups" value="${fn:endsWith(uri, '/studygroups') || fn:endsWith(uri, '/studygroups.jsp')}" />
<c:set var="onMatching"    value="${fn:endsWith(uri, '/MentorMatching.jsp')}" />
<c:set var="onChat"        value="${fn:endsWith(uri, '/chat.jsp')}" />
<c:set var="onAdmin"       value="${fn:endsWith(uri, '/admin') || fn:endsWith(uri, '/admin.jsp')}" />

<!-- Mobile menu button: hidden on desktop, shown in the top-left corner on small screens -->
<button type="button" class="nav-toggle" id="navToggle" aria-controls="sidebar" aria-expanded="false">
    <span aria-hidden="true">☰</span>
    <span class="visually-hidden">Navigation menu</span>
</button>

<aside class="sidebar" id="sidebar" aria-label="Main navigation">

    <div class="sidebar-top">
        <div class="sidebar-logo">
            <span class="logo-mark" aria-hidden="true">🇪🇹</span>
            <span class="logo-text">EthioMentor</span>
        </div>
        <!-- Desktop-only collapse control -->
        <button type="button" class="collapse-btn" id="collapseBtn" aria-controls="sidebar" aria-expanded="true" aria-label="Collapse navigation">
            <span aria-hidden="true">‹</span>
        </button>
    </div>

    <nav class="sidebar-nav" aria-label="Application">
        <c:choose>
            <c:when test="${userRole eq 'mentor'}">
                <a href="${ctx}/mentor-dashboard" class="nav-item ${onMentorDash ? 'active' : ''}" aria-current="${onMentorDash ? 'page' : 'false'}">
                    <span class="nav-icon" aria-hidden="true">📊</span><span class="nav-text">Dashboard</span>
                </a>
                <a href="${ctx}/studygroups" class="nav-item ${onStudyGroups ? 'active' : ''}" aria-current="${onStudyGroups ? 'page' : 'false'}">
                    <span class="nav-icon" aria-hidden="true">👥</span><span class="nav-text">Study Groups</span>
                </a>
                <a href="${ctx}/chat.jsp" class="nav-item ${onChat ? 'active' : ''}" aria-current="${onChat ? 'page' : 'false'}">
                    <span class="nav-icon" aria-hidden="true">✉️</span><span class="nav-text">Messages</span>
                </a>
            </c:when>

            <c:when test="${userRole eq 'admin'}">
                <a href="${ctx}/admin" class="nav-item ${onAdmin ? 'active' : ''}" aria-current="${onAdmin ? 'page' : 'false'}">
                    <span class="nav-icon" aria-hidden="true">🛡️</span><span class="nav-text">Admin</span>
                </a>
                <a href="${ctx}/studygroups" class="nav-item ${onStudyGroups ? 'active' : ''}" aria-current="${onStudyGroups ? 'page' : 'false'}">
                    <span class="nav-icon" aria-hidden="true">👥</span><span class="nav-text">Study Groups</span>
                </a>
                <a href="${ctx}/chat.jsp" class="nav-item ${onChat ? 'active' : ''}" aria-current="${onChat ? 'page' : 'false'}">
                    <span class="nav-icon" aria-hidden="true">✉️</span><span class="nav-text">Messages</span>
                </a>
            </c:when>

            <c:otherwise>
                <a href="${ctx}/dashboard" class="nav-item ${onDashboard ? 'active' : ''}" aria-current="${onDashboard ? 'page' : 'false'}">
                    <span class="nav-icon" aria-hidden="true">📊</span><span class="nav-text">Dashboard</span>
                </a>
                <a href="${ctx}/studygroups" class="nav-item ${onStudyGroups ? 'active' : ''}" aria-current="${onStudyGroups ? 'page' : 'false'}">
                    <span class="nav-icon" aria-hidden="true">👥</span><span class="nav-text">Study Groups</span>
                </a>
                <a href="${ctx}/MentorMatching.jsp" class="nav-item ${onMatching ? 'active' : ''}" aria-current="${onMatching ? 'page' : 'false'}">
                    <span class="nav-icon" aria-hidden="true">🤝</span><span class="nav-text">Mentor Matching</span>
                </a>
                <a href="${ctx}/chat.jsp" class="nav-item ${onChat ? 'active' : ''}" aria-current="${onChat ? 'page' : 'false'}">
                    <span class="nav-icon" aria-hidden="true">✉️</span><span class="nav-text">Messages</span>
                </a>
            </c:otherwise>
        </c:choose>
    </nav>

    <div class="sidebar-footer">
        <a href="${ctx}/logout" class="logout-btn">
            <span class="nav-icon" aria-hidden="true">🚪</span><span class="nav-text">Log out</span>
        </a>
    </div>

</aside>
