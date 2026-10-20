<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page session="true" %>
<%-- JS template literals below use ${...}; keep the JSP container from evaluating them as EL. --%>
<%@ page isELIgnored="true" %>

<!DOCTYPE html>
<html>
<head>
    <title>Ethiomentor | Mentor Matching</title>

    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/global.css">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/sidebar.css">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/mentormatching.css">
</head>

<body>

<div class="app-layout">

    <!-- SIDEBAR -->
    <jsp:include page="WEB-INF/jsp/includes/sidebar.jsp"/>

    <!-- MAIN -->
    <main class="main-content">

        <div class="mentor-container">
            <header class="mentor-header">
                <div>
                    <h2>AI Mentor Matching</h2>
                    <p>Let our AI find the best academic guides based on your profile and needs.</p>
                </div>
                <button id="findMentorsBtn">Find My Mentors</button>
            </header>

            <div id="errorMsg" class="error-msg" style="display:none;"></div>
            <div id="loading" class="loading" style="display:none;">Loading...</div>
            <div id="mentorList" class="mentor-list"></div>
        </div>

        <jsp:include page="WEB-INF/jsp/includes/footer.jsp"/>

    </main>
</div>

<script>
const findBtn = document.getElementById("findMentorsBtn");
const loading = document.getElementById("loading");
const error = document.getElementById("errorMsg");
const list = document.getElementById("mentorList");
const CTX = "<%=request.getContextPath()%>";

// Mentor data comes from the database: always escape before putting it into HTML.
function esc(s) {
    return String(s == null ? "" : s).replace(/[&<>"']/g, c =>
        ({"&":"&amp;","<":"&lt;",">":"&gt;","\"":"&quot;","'":"&#39;"}[c]));
}

findBtn.addEventListener("click", function() {
    loading.style.display = "block";
    error.style.display = "none";
    list.innerHTML = "";

    fetch(CTX + '/mentormatching')
        .then(res => res.json())
        .then(data => {
            loading.style.display = "none";

            if (data.error) {
                error.style.display = "block";
                error.innerText = data.error;
                return;
            }

            if (data.length === 0) {
                list.innerHTML = `
                    <div class="no-mentors">
                        <p>No approved mentors are available yet.</p>
                    </div>`;
                return;
            }

            data.forEach(mentor => {
                const div = document.createElement("div");
                div.className = "mentor-card";

                let expertiseHtml = "";
                mentor.expertise.forEach(skill => {
                    expertiseHtml += `<span>${esc(skill)}</span>`;
                });

                div.innerHTML = `
                    <div class="mentor-top-bar"></div>
                    <div class="mentor-content">
                        <div class="mentor-info">
                            <img src="https://picsum.photos/seed/${encodeURIComponent(mentor.id)}/100/100" alt="">
                            <div>
                                <h4>${esc(mentor.name)}</h4>
                                <p>${esc(mentor.university)}</p>
                            </div>
                        </div>
                        <div class="mentor-expertise">${expertiseHtml}</div>
                    </div>
                    <div class="mentor-actions">
                        <a href="${CTX}/chat.jsp?userId=${encodeURIComponent(mentor.id)}" class="btn-contact">Contact</a>
                    </div>
                `;
                list.appendChild(div);
            });
        })
        .catch(() => {
            loading.style.display = "none";
            error.style.display = "block";
            error.innerText = "Failed to load mentors. Please try again.";
        });
});
</script>

</body>
</html>
