<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<header class="top-header">
    <div class="top-left">
        <h2 class="page-title">Dashboard</h2>
    </div>

    <div class="top-right">
        <span class="user-name">
            👤 <c:out value="${user.fullName}"/>
        </span>
    </div>
</header>
