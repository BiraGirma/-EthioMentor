<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
    <title>EthioMentor Registration</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/register.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/global.css">
</head>
<body>

<div class="register-page">

    <!-- Background Pattern -->
    <div class="bg-pattern"></div>

    <div class="register-card-wrapper">
        <!-- Hero / Brand Side -->
        <div class="hero-side">
            <div class="hero-overlay"></div>
            <div class="hero-content">
                <div class="hero-logo">EM</div>
                <h1>EthioMentor</h1>
                <p class="tagline">TIMHERT LE-HULUM</p>
                <p class="description">
                    Connect with mentors across the nation's premier universities. Your academic success is our collective goal.
                </p>
                <div class="milestone-card">
                    <p class="milestone-title">Platform Milestone</p>
                    <div class="milestone-content">
                        <span class="milestone-number">42+</span>
                        <span class="milestone-label">Active Campuses</span>
                    </div>
                </div>
            </div>
        </div>

        <!-- Form Side -->
        <div class="form-side">
            <div class="form-header">
                <h2>Student Gateway</h2>
                <p>Join your fellow scholars today.</p>
            </div>

            <c:if test="${not empty error}">
                <div class="error-msg">${error}</div>
            </c:if>

            <form action="${pageContext.request.contextPath}/register" method="post" class="registration-form">

                <!-- Role Selection -->
                <p class="role-label">Account Role</p>
                <div class="role-buttons">
                    <button type="button" onclick="setRole('MENTEE', this)" id="role-student" class="role-btn selected">Student</button>
                    <button type="button" onclick="setRole('MENTOR', this)" id="role-mentor" class="role-btn">Mentor</button>
                    <input type="hidden" name="role" id="role-input" value="MENTEE"/>

                </div>

                <!-- Username -->
                <div class="form-group">
                    <label>Username</label>
                    <input type="text" name="username" placeholder="Your username" required/>
                </div>

                <!-- Name and Email -->
                <div class="grid-2">
                    <div class="form-group">
                        <label>Full Name</label>
                        <input type="text" name="fullName" placeholder="Abebe Bikila" required/>
                    </div>
                    <div class="form-group">
                        <label>Email</label>
                        <input type="email" name="email" placeholder="user@university.edu.et" required/>
                    </div>
                </div>

                <!-- University Dropdown -->
                <div class="form-group">
                    <label>Institution</label>
                    <select name="uni" required>
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

                <!-- Password -->
                <div class="form-group">
                    <label>Password</label>
                    <input type="password" name="password" placeholder="Your password" required/>
                </div>

                <button type="submit" class="submit-btn">
                    Access Platform
                    <span class="btn-icon">&#10132;</span>
                </button>
            </form>

            <div class="version-label">v3.1.0-ABYSSINIA</div>
        </div>
    </div>
</div>

<script>
    function setRole(role, btn){
        // set hidden input value (this is what backend receives)
        document.getElementById('role-input').value = role;

        // remove selection from both buttons
        document.getElementById('role-student').classList.remove('selected');
        document.getElementById('role-mentor').classList.remove('selected');

        // add selection to clicked button
        btn.classList.add('selected');
    }
</script>


</body>
</html>
