function setRole(role, btn) {
    // Set the hidden input value to the selected role
    document.getElementById('role').value = role;

    // Remove 'active' class from all role buttons
    document.querySelectorAll('.role-btn').forEach(b => b.classList.remove('active'));

    // Add 'active' class to the clicked button
    btn.classList.add('active');
}
