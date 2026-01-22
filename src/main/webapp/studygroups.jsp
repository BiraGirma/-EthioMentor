<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <title>EthioMentor | Study Groups</title>
    <link rel="stylesheet" href="css/global.css">
    <link rel="stylesheet" href="css/studygroups.css">
</head>
<body>

<div class="app-layout">
    <jsp:include page="WEB-INF/jsp/includes/sidebar.jsp"/>

    <main class="main-content">

        <div class="groups-header">
            <div>
                <h2>Study Groups</h2>
                <p>Collaborate with peers, join groups, or create your own learning circle.</p>
            </div>
            <button class="create-btn" onclick="openModal()">+ New Group</button>
        </div>

        <!-- Suggested Groups -->
        <div class="section">
            <h3 class="section-title">Suggested Groups</h3>
            <div class="groups-grid">
                <c:choose>
                    <c:when test="${not empty randomGroups}">
                        <c:forEach var="g" items="${randomGroups}">
                            <div class="group-card" id="group-${g.id}">
                                <div class="group-meta">
                                    <span class="subject">STUDY</span>
                                    <span class="members-count">${g.membersCount} members</span>
                                </div>
                                <h3>${g.name}</h3>
                                <p>${g.description}</p>
                                <c:choose>
                                    <c:when test="${g.member}">
                                        <button class="btn disabled" disabled>Already Member</button>
                                    </c:when>
                                    <c:otherwise>
                                        <button class="btn join-btn" onclick="joinGroup(${g.id}, this)">Join Group</button>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <p>No suggestions available.</p>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <!-- My Joined Groups -->
        <div class="section">
            <h3 class="section-title">My Groups</h3>
            <div class="groups-grid">
                <c:choose>
                    <c:when test="${not empty joinedGroups}">
                        <c:forEach var="g" items="${joinedGroups}">
                            <div class="group-card" id="group-${g.id}">
                                <div class="group-meta">
                                    <span class="subject">STUDY</span>
                                    <span class="members-count">${g.membersCount} members</span>
                                </div>
                                <h3>${g.name}</h3>
                                <p>${g.description}</p>
                                <button class="btn disabled" disabled>Already Member</button>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <p>You have not joined any groups yet.</p>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <!-- My Created Groups -->
        <div class="section">
            <h3 class="section-title">Groups I Created</h3>
            <div class="groups-grid">
                <c:choose>
                    <c:when test="${not empty createdGroups}">
                        <c:forEach var="g" items="${createdGroups}">
                            <div class="group-card" id="group-${g.id}">
                                <div class="group-meta">
                                    <span class="subject">STUDY</span>
                                    <span class="members-count">${g.membersCount} members</span>
                                </div>
                                <h3>${g.name}</h3>
                                <p>${g.description}</p>
                                <c:choose>
                                    <c:when test="${g.member}">
                                        <button class="btn disabled" disabled>Already Member</button>
                                    </c:when>
                                    <c:otherwise>
                                        <button class="btn join-btn" onclick="joinGroup(${g.id}, this)">Join Group</button>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <p>You have not created any groups yet.</p>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

    </main>
</div>

<!-- MODAL -->
<div id="groupModal" class="modal-overlay">
    <div class="modal">
        <div class="modal-header">
            <span class="modal-icon">👥</span>
            <div>
                <h3>Create a Study Group</h3>
                <p>Build a focused learning circle with peers.</p>
            </div>
            <button class="modal-close" onclick="closeModal()">✕</button>
        </div>

        <form id="createGroupForm" class="modal-form">
            <div class="form-group">
                <label>Group Name</label>
                <input type="text" name="name" placeholder="e.g. Java Backend Masters" required>
            </div>
            <div class="form-group">
                <label>Description</label>
                <textarea name="description" rows="4" placeholder="What will this group focus on?" required></textarea>
            </div>
            <div class="modal-actions">
                <button type="button" class="btn ghost" onclick="closeModal()">Cancel</button>
                <button type="submit" class="btn primary">Create Group 🚀</button>
            </div>
        </form>
    </div>
</div>

<script src="js/studygroups.js"></script>
</body>
</html>
