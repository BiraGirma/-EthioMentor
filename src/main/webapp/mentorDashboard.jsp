<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>EthioMentor | Mentor Dashboard</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dashboard.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

    
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
                <h2>Selam, ${user.fullName.split(' ')[0]} 👋</h2>
                <p>Welcome back to your mentor dashboard! Here you can view your mentees, manage study groups, and track your community impact.</p>
            </div>
            <div class="hero-right">
                <div class="hero-stat">
                    <span>Mentees Assigned</span>
                    <strong>${menteesCount}</strong>
                </div>
                <div class="hero-stat">
                    <span>Study Groups Managed</span>
                    <strong>${studyGroupsCount}</strong>
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
                        <strong>${mentorsCount}</strong>
                        <em>⭐</em>
                    </div>
                    <div class="mini-card">
                        <span>Messages</span>
                        <strong>${messagesCount}</strong>
                        <em>✉️</em>
                    </div>
                    <div class="mini-card">
                        <span>Active Groups</span>
                        <strong>${activeGroupsCount}</strong>
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
                    ${studyGroupsCount},
                    ${menteesCount},
                    ${mentorsCount},
                    ${messagesCount}
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
