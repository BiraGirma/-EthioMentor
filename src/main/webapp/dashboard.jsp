<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>
<html>
<<<<<<< HEAD
=======
<<<<<<< HEAD
  <head>
    <title>Ethiomentor | Dashboard</title>

    <link rel="stylesheet" href="css/global.css" />
    <link rel="stylesheet" href="css/sidebar.css" />
    <link rel="stylesheet" href="css/dashboard.css" />

    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
  </head>

  <body>
    <div class="app-layout">
      <!-- SIDEBAR -->
      <jsp:include page="WEB-INF/jsp/includes/sidebar.jsp" />

      <!-- MAIN -->
      <main class="main-content">
        <jsp:include page="WEB-INF/jsp/includes/header.jsp" />

        <section class="dashboard">
          <!-- HERO -->
          <header class="hero">
            <div class="hero-pattern"></div>

            <div class="hero-left">
              <h2>
                Selam, ${user.getFullName().split(' ')[0]}
                <span class="tibeb-badge">ጤና ይስጥልኝ</span>
              </h2>

              <p>
                Welcome to the national peer network. You are currently
                connected via the
                <strong>Ethiopian University Cluster</strong>.
              </p>
            </div>

            <div class="hero-right">
              <div class="hero-stat">
                <span>Semester Status</span>
                <strong>Weeks 12 of 16</strong>
              </div>

              <div class="hero-stat">
                <span>Exit Exam</span>
                <strong>102 Days Left</strong>
              </div>
            </div>
          </header>

          <!-- GRID -->
          <div class="dashboard-grid">
            <!-- LEFT (2 COLS) -->
            <div class="dashboard-main">
              <!-- MINI STATS -->
              <div class="mini-stats">
                <div class="mini-card">
                  <span>Network Health</span>
                  <strong>Excellent</strong>
                  <em>🇪🇹</em>
                </div>

                <div class="mini-card">
                  <span>Active Sessions</span>
                  <strong>1,240</strong>
                  <em>🔥</em>
                </div>

                <div class="mini-card">
                  <span>Files Shared</span>
                  <strong>8.4k</strong>
                  <em>📂</em>
                </div>
              </div>

              <!-- CHART -->
              <div class="chart-card">
                <h3>Community Analytics</h3>
                <canvas id="communityChart"></canvas>
              </div>
            </div>

            <!-- RIGHT SIDEBAR -->
            <div class="dashboard-side">
              <!-- CAMPUS VIBES -->
              <div class="vibes-card">
                <h3>
                  Campus Vibes
                  <span class="badge">Today</span>
                </h3>

                <div class="vibe">
                  <span>☕</span>
                  <div>
                    <strong>Buna Break</strong>
                    <p>
                      Traditional coffee is now ready at the Student Union
                      Lounge.
                    </p>
                  </div>
                </div>

                <div class="vibe">
                  <span>📚</span>
                  <div>
                    <strong>Library Shift</strong>
                    <p>24-hour study rooms open for Midterm preparation.</p>
                  </div>
                </div>

                <div class="vibe">
                  <span>📢</span>
                  <div>
                    <strong>Scholarship Alert</strong>
                    <p>New masters opportunities for STEM seniors in Europe.</p>
                  </div>
                </div>
              </div>

              <!-- MENTOR -->
              <div class="mentor-card">
                <h3>Mentor Spotlight</h3>

                <div class="mentor-info">
                  <img src="https://i.pravatar.cc/100?u=spotlight" />
                  <div>
                    <strong>Henok Getachew</strong>
                    <span>AAU • 5th Year Medicine</span>
                    <div class="stars">★★★★★</div>
                  </div>
                </div>

                <blockquote>
                  "I joined EthioMentor to help juniors navigate the medical
                  boards. Knowledge grows when shared."
                </blockquote>

                <button>Connect with Henok</button>
              </div>
            </div>
          </div>
        </section>

        <jsp:include page="WEB-INF/jsp/includes/footer.jsp" />
      </main>
    </div>

    <script>
      new Chart(document.getElementById('communityChart'), {
          type: 'bar',
          data: {
              labels: ['Study Groups', 'Mentors', 'Mentees', 'Discussions'],
              datasets: [{
                  data: [
                      ${studyGroupsCount},
                      ${mentorsCount},
                      ${menteesCount},
                      ${discussionsCount}
                  ],
                  backgroundColor: '#076921',
                  borderRadius: 8
              }]
          },
          options: {
              plugins: { legend: { display: false } },
              scales: { y: { beginAtZero: true } }
          }
      });

      document.addEventListener("DOMContentLoaded", () => {
          const sidebar = document.getElementById("sidebar");
          const collapseBtn = document.getElementById("collapseBtn");
          const mainContent = document.querySelector(".main-content"); // now it exists

          // Load saved collapsed state
          const isCollapsed = localStorage.getItem('sidebar-collapsed') === 'true';
          if (isCollapsed) {
              sidebar.classList.add('collapsed');
              if (mainContent) mainContent.classList.add('collapsed');
          }

          // Collapse toggle
          collapseBtn.addEventListener("click", () => {
              sidebar.classList.toggle('collapsed');
              if (mainContent) mainContent.classList.toggle('collapsed');

              // Save state
              localStorage.setItem('sidebar-collapsed', sidebar.classList.contains('collapsed'));
          });

          // Highlight active nav-item based on URL
          const navItems = document.querySelectorAll(".sidebar .nav-item");
          navItems.forEach(item => {
              const href = item.getAttribute("href");
              if (window.location.pathname.endsWith(href)) {
                  item.classList.add("active");
              } else {
                  item.classList.remove("active");
              }
          });
      });
    </script>
  </body>
=======
>>>>>>> 2bdd21ccaf16834af801aaad4a078e24691fa627
<head>
    <title>Ethiomentor | Dashboard</title>

    <link rel="stylesheet" href="css/global.css">
<link rel="stylesheet" href="css/sidebar.css">
<link rel="stylesheet" href="css/dashboard.css">

    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
</head>

<body>

<div class="app-layout">

    <!-- SIDEBAR -->
    <jsp:include page="WEB-INF/jsp/includes/sidebar.jsp"/>

    <!-- MAIN -->
    <main class="main-content">

        <jsp:include page="WEB-INF/jsp/includes/header.jsp"/>

        <section class="dashboard">

            <!-- HERO -->
            <header class="hero">
                <div class="hero-pattern"></div>

                <div class="hero-left">
                    <h2>
                        Selam, ${user.getFullName().split(' ')[0]}
                        <span class="tibeb-badge">ጤና ይስጥልኝ</span>
                    </h2>

                    <p>
                        Welcome to the national peer network.
                        You are currently connected via the
                        <strong>Ethiopian University Cluster</strong>.
                    </p>
                </div>

                <div class="hero-right">
                    <div class="hero-stat">
                        <span>Semester Status</span>
                        <strong>Weeks 12 of 16</strong>
                    </div>

                    <div class="hero-stat">
                        <span>Exit Exam</span>
                        <strong>102 Days Left</strong>
                    </div>
                </div>
            </header>

            <!-- GRID -->
            <div class="dashboard-grid">

                <!-- LEFT (2 COLS) -->
                <div class="dashboard-main">

                    <!-- MINI STATS -->
                    <div class="mini-stats">
                        <div class="mini-card">
                            <span>Network Health</span>
                            <strong>Excellent</strong>
                            <em>🇪🇹</em>
                        </div>

                        <div class="mini-card">
                            <span>Active Sessions</span>
                            <strong>0</strong>
                            <em>🔥</em>
                        </div>

                        <div class="mini-card">
                            <span>Files Shared</span>
                            <strong>0</strong>
                            <em>📂</em>
                        </div>
                    </div>

                    <!-- CHART -->
                    <div class="chart-card">
                        <h3>Community Analytics</h3>
                        <canvas id="communityChart"></canvas>
                    </div>
                </div>

                <!-- RIGHT SIDEBAR -->
                <div class="dashboard-side">

                    <!-- CAMPUS VIBES -->
                    <div class="vibes-card">
                        <h3>
                            Campus Vibes
                            <span class="badge">Today</span>
                        </h3>

                        <div class="vibe">
                            <span>☕</span>
                            <div>
                                <strong>Buna Break</strong>
                                <p>Traditional coffee is now ready at the Student Union Lounge.</p>
                            </div>
                        </div>

                        <div class="vibe">
                            <span>📚</span>
                            <div>
                                <strong>Library Shift</strong>
                                <p>24-hour study rooms open for Midterm preparation.</p>
                            </div>
                        </div>

                        <div class="vibe">
                            <span>📢</span>
                            <div>
                                <strong>Scholarship Alert</strong>
                                <p>New masters opportunities for STEM seniors in Europe.</p>
                            </div>
                        </div>
                    </div>

                    <!-- MENTOR -->
                    <div class="mentor-card">
                        <h3>Mentor Spotlight</h3>

                        <div class="mentor-info">
                            <img src="https://i.pravatar.cc/100?u=spotlight">
                            <div>
                                <strong>Henok Getachew</strong>
                                <span>AAU • 5th Year Medicine</span>
                                <div class="stars">★★★★★</div>
                            </div>
                        </div>

                        <blockquote>
                            "I joined EthioMentor to help juniors navigate the medical boards.
                            Knowledge grows when shared."
                        </blockquote>

                        <button>Connect with Henok</button>
                    </div>

                </div>
            </div>
        </section>

        <jsp:include page="WEB-INF/jsp/includes/footer.jsp"/>

    </main>
</div>

<script>
new Chart(document.getElementById('communityChart'), {
    type: 'bar',
    data: {
        labels: ['Study Groups', 'Mentors', 'Mentees', 'Discussions'],
        datasets: [{
            data: [
                ${studyGroupsCount},
                ${mentorsCount},
                ${menteesCount},
                ${discussionsCount}
            ],
            backgroundColor: '#076921',
            borderRadius: 8
        }]
    },
    options: {
        plugins: { legend: { display: false } },
        scales: { y: { beginAtZero: true } }
    }
});

document.addEventListener("DOMContentLoaded", () => {
    const sidebar = document.getElementById("sidebar");
    const collapseBtn = document.getElementById("collapseBtn");
    const mainContent = document.querySelector(".main-content"); // now it exists

    // Load saved collapsed state
    const isCollapsed = localStorage.getItem('sidebar-collapsed') === 'true';
    if (isCollapsed) {
        sidebar.classList.add('collapsed');
        if (mainContent) mainContent.classList.add('collapsed');
    }

    // Collapse toggle
    collapseBtn.addEventListener("click", () => {
        sidebar.classList.toggle('collapsed');
        if (mainContent) mainContent.classList.toggle('collapsed');

        // Save state
        localStorage.setItem('sidebar-collapsed', sidebar.classList.contains('collapsed'));
    });

    // Highlight active nav-item based on URL
    const navItems = document.querySelectorAll(".sidebar .nav-item");
    navItems.forEach(item => {
        const href = item.getAttribute("href");
        if (window.location.pathname.endsWith(href)) {
            item.classList.add("active");
        } else {
            item.classList.remove("active");
        }
    });
});
</script>


</body>
<<<<<<< HEAD
=======
>>>>>>> 8fb5b46 (finilized)
>>>>>>> 2bdd21ccaf16834af801aaad4a078e24691fa627
</html>
