<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
  <title>EthioMentor | Login</title>

<<<<<<< HEAD
  <!-- FIX: contextPath added -->
=======
>>>>>>> 8fb5b46 (finilized)
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
<<<<<<< HEAD
          Connect with mentors across the nation's premier universities.
          Your academic success is our collective goal.
=======
          Connect with mentors across Ethiopia’s premier universities.
          Learn. Connect. Grow.
>>>>>>> 8fb5b46 (finilized)
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
<<<<<<< HEAD
        <h2>Student Gateway</h2>
        <p>Join your fellow scholars today.</p>
      </div>

      <!-- OPTIONAL ERROR MESSAGE -->
=======
        <h2>Secure Login</h2>
        <p>Access your EthioMentor account</p>
      </div>

      <!-- ERROR MESSAGE -->
>>>>>>> 8fb5b46 (finilized)
      <c:if test="${not empty error}">
        <div class="error">${error}</div>
      </c:if>

      <!-- AUTH FORM -->
      <form action="${pageContext.request.contextPath}/login" method="post">

<<<<<<< HEAD
        <!-- ROLE -->
        <input type="hidden" name="role" id="role" value="MENTEE">

        <div class="section">
          <p class="section-label">Account Role</p>

          <div class="role-grid">
            <button type="button" class="role-btn active"
                    onclick="setRole('MENTEE', this)">
              Student
            </button>
            <button type="button" class="role-btn"
                    onclick="setRole('MENTOR', this)">
              Mentor
            </button>
          </div>
        </div>

        <!-- EMAIL -->
        <div>
          <label>EDU EMAIL</label>
          <input type="email"
                 name="email"
                 required
                 placeholder="user@university.edu.et">
        </div>

        <!-- PASSWORD (FIXED) -->
=======
        <!-- ROLE (UI ONLY – BACKEND IGNORES THIS SAFELY) -->
        <input type="hidden" name="role" id="role" value="MENTEE">

        <div class="section">
          <p class="section-label">Account Type</p>

          <div class="role-grid">
            <button type="button" class="role-btn active"
                    onclick="setRole('MENTEE', this)">Student</button>

            <button type="button" class="role-btn"
                    onclick="setRole('MENTOR', this)">Mentor</button>

            <button type="button" class="role-btn"
                    onclick="setRole('ADMIN', this)">Admin</button>
          </div>
        </div>

        <!-- IDENTIFIER (EMAIL OR USERNAME) -->
        <div>
          <label>EMAIL OR USERNAME</label>
          <input type="text"
                 name="email"
                 required
                 placeholder="admin or user@university.edu.et">
        </div>

        <!-- PASSWORD -->
>>>>>>> 8fb5b46 (finilized)
        <div>
          <label>PASSWORD</label>
          <input type="password"
                 name="password"
                 required
                 placeholder="••••••••">
        </div>

<<<<<<< HEAD
        <!-- OPTIONAL: UNIVERSITY (not required for login) -->
        <div>
          <label>SELECT YOUR INSTITUTION</label>
          <select name="university">
            
                        <option value="Addis Ababa University">Addis Ababa University</option>
                        <option value="Jimma University">Jimma University</option>
                        <option value="Mekelle University">Mekelle University</option>
                        <option value="Bahir Dar University">Bahir Dar University</option>
                        <option value="Hawassa University">Hawassa University</option>
                        <option value="Gondar University">Gondar University</option>
                        <option value="Arba Minch University">Arba Minch University</option>
                        <option value="Adama Science and Technology University">Adama Science and Technology University</option>
                        <option value="Debre Markos University">Debre Markos University</option>
                        <option value="Wollo University">Wollo University</option>
                        <option value="Jimma Institute of Technology">Jimma Institute of Technology</option>
                        <option value="Wollega University">Wollega University</option>
                        <option value="Dilla University">Dilla University</option>
                        <option value="Debre Berhan University">Debre Berhan University</option>
                        <option value="Bule Hora University">Bule Hora University</option>
                        <option value="other">Other</option>
                    
          </select>
        </div>

        <button type="submit" class="submit">
          Access Platform
=======
        <button type="submit" class="submit">
          Login
>>>>>>> 8fb5b46 (finilized)
          <span class="arrow">→</span>
        </button>
      </form>

      <span class="version">v3.1.0-ABYSSINIA</span>
    </div>
  </div>
</div>

</body>
</html>
