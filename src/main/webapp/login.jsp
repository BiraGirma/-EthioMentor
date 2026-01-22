<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
  <title>EthioMentor | Login</title>

  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/login.css">
  <script defer src="${pageContext.request.contextPath}/js/login.js"></script>
</head>

<body>

<div class="page">
  <div class="pattern"></div>

  <div class="card">

    <!-- BRAND SIDE -->
    <div class="brand">
      <div class="gold-corner"></div>

      <div>
        <div class="logo">EM</div>
        <h1>EthioMentor</h1>
        <p class="tagline">TIMHERT LE-HULUM</p>
        <p class="desc">
          Connect with mentors across Ethiopia’s premier universities.
          Learn. Connect. Grow.
        </p>
      </div>

      <div class="milestone">
        <p class="label">Platform Milestone</p>
        <div class="stats">
          <span class="number">42+</span>
          <span class="text">Active Campuses</span>
        </div>
      </div>
    </div>

    <!-- FORM SIDE -->
    <div class="form-side">

      <div class="header">
        <h2>Student Gateway</h2>
        <p>Join your fellow scholars today.</p>
      </div>

      <!-- ERROR MESSAGE -->
      <c:if test="${not empty error}">
        <div class="error">${error}</div>
      </c:if>

      <!-- AUTH FORM -->
      <form action="${pageContext.request.contextPath}/login" method="post">

        <!-- ROLE (UI only, backend ignores safely) -->
        <input type="hidden" name="role" id="role" value="MENTEE">

        <div class="section">
          <p class="section-label">Account Type</p>

          <div class="role-grid">
            <button type="button" class="role-btn active" onclick="setRole('MENTEE', this)">Student</button>
            <button type="button" class="role-btn" onclick="setRole('MENTOR', this)">Mentor</button>
            <button type="button" class="role-btn" onclick="setRole('ADMIN', this)">Admin</button>
          </div>
        </div>

        <!-- IDENTIFIER -->
        <div>
          <label>EMAIL OR USERNAME</label>
          <input type="text"
                 name="email"
                 required
                 placeholder="admin or user@university.edu.et">
        </div>

        <!-- PASSWORD -->
        <div>
          <label>PASSWORD</label>
          <input type="password"
                 name="password"
                 required
                 placeholder="••••••••">
        </div>

        <button type="submit" class="submit">
          Login <span class="arrow">→</span>
        </button>
      </form>

      <span class="version">v3.1.0-ABYSSINIA</span>
    </div>
  </div>
</div>

</body>
</html>
