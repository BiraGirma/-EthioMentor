// MODAL CONTROLS
function openModal() {
    document.getElementById("groupModal").classList.add("show");
}

function closeModal() {
    document.getElementById("groupModal").classList.remove("show");
}

// CREATE GROUP AJAX
document.getElementById("createGroupForm").addEventListener("submit", function(e){
    e.preventDefault();

    fetch("studygroups/create", {
        method: "POST",
        body: new FormData(this)
    })
    .then(res => res.json())
    .then(data => {
        closeModal();
        location.reload(); // refresh to show new group
    })
    .catch(() => alert("Failed to create group"));
});

// JOIN GROUP AJAX
function joinGroup(groupId, btn){
    const data = new URLSearchParams();
    data.append("groupId", groupId);

    fetch("studygroups/join", {
        method: "POST",
        body: data
    })
    .then(res => res.json())
    .then(res => {
        btn.textContent = "Already Member";
        btn.disabled = true;
        btn.classList.add("disabled");

        const counter = document.querySelector(`#group-${groupId} .members-count`);
        if(counter) counter.textContent = res.membersCount + " members";
    })
    .catch(() => alert("Could not join group"));
}

// CLOSE MODAL BY CLICKING OUTSIDE
document.getElementById("groupModal").addEventListener("click", e => {
    if(e.target.id === "groupModal") closeModal();
});
