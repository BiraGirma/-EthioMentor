<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>EthioMentor | Mentor Dashboard</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dashboard.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.1/dist/chart.umd.min.js"></script>

    
    <link rel="stylesheet" href="/css/app-shell.css">
    <script defer src="/js/shell.js"></script>
</head>
<body>

<div class="app-layout">

    <!-- SIDEBAR INCLUDE -->
    <jsp:include page="WEB-INF/jsp/includes/sidebar.jsp"/>

    <!-- MAIN CONTENT -->
    <main class="main-content">

        <!-- HERO / WELCOME -->
        <header class="hero">
            <div class="hero-pattern"></div>
            <div class="hero-left">
                <h2>Selam, <c:out value="${user.fullName.split(' ')[0]}"/> 👋</h2>
                <p>Welcome back to your mentor dashboard! Here you can view your mentees, manage study groups, and track your community impact.</p>
            </div>
            <div class="hero-right">
                <div class="hero-stat">
                    <span>Mentees</span>
                    <strong><c:out value="${menteesCount}"/></strong>
                </div>
                <div class="hero-stat">
                    <span>Study Groups Managed</span>
                    <strong><c:out value="${studyGroupsCount}"/></strong>
                </div>
            </div>
        </header>

        <!-- DASHBOARD GRID -->
        <div class="dashboard-grid">

            <!-- LEFT SECTION -->
            <div class="dashboard-main">

                <!-- MINI STATS -->
                <div class="mini-stats">
                    <div class="mini-card">
                        <span>Total Mentors</span>
                        <strong><c:out value="${mentorsCount}"/></strong>
                        <em>⭐</em>
                    </div>
                    <div class="mini-card">
                        <span>Messages</span>
                        <strong><c:out value="${messagesCount}"/></strong>
                        <em>✉️</em>
                    </div>
                    <div class="mini-card">
                        <span>Active Groups</span>
                        <strong><c:out value="${activeGroupsCount}"/></strong>
                        <em>👥</em>
                    </div>
                </div>

                <!-- COMMUNITY CHART -->
                <div class="chart-card">
                    <h3>Community Analytics</h3>
                    <canvas id="communityChart"></canvas>
                </div>

            </div>

            <!-- RIGHT SECTION -->
            <div class="dashboard-side">

                <!-- MENTOR SPOTLIGHT -->
                <div class="mentor-card">
                    <h3>Top Mentee Spotlight</h3>
                    <div class="mentor-info">
                        <img src="https://i.pravatar.cc/100?u=mentee1">
                        <div>
                            <strong>Liyaa Mekonnen</strong>
                            <span>AAU • Computer Science</span>
                        </div>
                    </div>
                    <blockquote>
                        "Thanks to my mentor's guidance, I was able to complete my project ahead of schedule. Truly grateful!"
                    </blockquote>
                    <button>Connect</button>
                </div>

            </div>

        </div>

    </main>
</div>

<script>
    // COMMUNITY CHART
    new Chart(document.getElementById('communityChart'), {
        type: 'bar',
        data: {
            labels: ['Study Groups', 'Mentees', 'Mentors', 'Messages'],
            datasets: [{
                data: [
                    <c:out value="${studyGroupsCount + 0}"/>,
                    <c:out value="${menteesCount + 0}"/>,
                    <c:out value="${mentorsCount + 0}"/>,
                    <c:out value="${messagesCount + 0}"/>
                ],
                backgroundColor: '#D4AF37',
                borderRadius: 8
            }]
        },
        options: {
            plugins: { legend: { display: false } },
            scales: { y: { beginAtZero: true } }
        }
    });
</script>

</body>
</html>
