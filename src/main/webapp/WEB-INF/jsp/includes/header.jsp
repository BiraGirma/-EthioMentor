<%@ page contentType="text/html;charset=UTF-8" %>

<header class="top-header">
    <div class="top-left">
        
        <h2 class="page-title">Dashboard</h2>
    </div>

    <div class="top-right">
        <span class="user-name">
            👤 ${user.getFullName()}
        </span>
    </div>
</header>

<script>
function toggleSidebar() {
    document.querySelector('.sidebar').classList.toggle('collapsed');
}
</script>
