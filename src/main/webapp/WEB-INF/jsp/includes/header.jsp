<%@ page contentType="text/html;charset=UTF-8" %>

<header class="top-header">
    <div class="top-left">
        <button class="menu-toggle" onclick="toggleSidebar()">☰</button>
        <h2 class="page-title">Dashboard</h2>
    </div>

    <div class="top-right">
        <span class="user-name">
            👤 ${sessionScope.fullName}
        </span>
    </div>
</header>

<script>
function toggleSidebar() {
    document.querySelector('.sidebar').classList.toggle('collapsed');
}
</script>
