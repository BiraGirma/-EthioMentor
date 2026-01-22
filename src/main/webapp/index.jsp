<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Ethiomentor | Learn. Connect. Grow.</title>

    <!-- Global styles (already existing in your project) -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/global.css">

    <!-- Landing-only styles -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/landing.css">
</head>
<body>


<!-- ================= NAVBAR ================= -->
<header class="lp-navbar">
    <div class="lp-logo">
        <span class="lp-logo-mark">◆</span>
        <span class="lp-logo-text">Ethiomentor</span>
    </div>

    <nav class="lp-actions">
        <a href="login.jsp" class="lp-btn lp-btn-outline">Login</a>
        <a href="register.jsp" class="lp-btn lp-btn-primary">Get Started</a>
    </nav>
</header>

<!-- ================= HERO ================= -->
<section class="lp-hero">
    <div class="lp-hero-content">
        <h1>
            Learn faster.<br>
            Grow smarter.<br>
            <span>With real mentors.</span>
        </h1>

        <p>
            Ethiomentor connects learners with experienced mentors,
            focused study paths, and a community that actually helps you grow.
        </p>

        <div class="lp-hero-cta">
            <a href="register.jsp" class="lp-btn lp-btn-primary lp-btn-lg">Join Free</a>
            <a href="login.jsp" class="lp-btn lp-btn-outline lp-btn-lg">Sign In</a>
        </div>
    </div>

    <div class="lp-hero-art">
        <div class="lp-orb lp-orb-gold"></div>
        <div class="lp-orb lp-orb-dark"></div>
    </div>
</section>

<!-- ================= VALUE ================= -->
<section class="lp-values">
    <div class="lp-card">
        <h3>🎓 Expert Mentors</h3>
        <p>Learn from professionals who’ve already walked the path.</p>
    </div>

    <div class="lp-card">
        <h3>🧭 Clear Roadmaps</h3>
        <p>No confusion. Step-by-step guidance built for real progress.</p>
    </div>

    <div class="lp-card">
        <h3>🤝 Strong Community</h3>
        <p>Grow with peers, feedback, and accountability.</p>
    </div>
</section>

<!-- ================= CTA ================= -->
<section class="lp-final-cta">
    <h2>Ready to level up your future?</h2>
    <p>Start learning with purpose today.</p>
    <a href="register.jsp" class="lp-btn lp-btn-primary lp-btn-xl">Create Account</a>
</section>

<!-- ================= FOOTER ================= -->
<footer class="lp-footer">
    © 2025 Ethiomentor. Built for growth.
</footer>

</body>
</html>
