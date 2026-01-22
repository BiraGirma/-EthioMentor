<<<<<<< HEAD
function setRole(role, btn) {
  document.getElementById("role").value = role;

  document.querySelectorAll(".role-btn")
    .forEach(b => b.classList.remove("active"));

  btn.classList.add("active");
}
=======
function setRole(role, btn) {
    document.getElementById('role').value = role;
    document.querySelectorAll('.role-btn').forEach(b => b.classList.remove('active'));
    btn.classList.add('active');
}
>>>>>>> 8fb5b46 (finilized)
