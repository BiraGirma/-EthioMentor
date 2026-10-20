const CTX = (document.querySelector('meta[name="ctx"]') || {}).content || "";
const CSRF = (document.querySelector('meta[name="csrf-token"]') || {}).content || "";

// MODAL CONTROLS
function openModal() {
    document.getElementById("groupModal").classList.add("show");
}

function closeModal() {
    document.getElementById("groupModal").classList.remove("show");
}

// POST helper: urlencoded body (multipart is not parsed by the servlets) plus CSRF header
function post(path, params) {
    return fetch(CTX + path, {
        method: "POST",
        headers: { "X-CSRF-Token": CSRF },
        body: params
    }).then(res => res.json().then(body => {
        if (!res.ok) throw new Error(body.error || ("HTTP " + res.status));
        return body;
    }));
}

// CREATE GROUP AJAX
document.getElementById("createGroupForm").addEventListener("submit", function (e) {
    e.preventDefault();

    post("/studygroups/create", new URLSearchParams(new FormData(this)))
        .then(() => {
            closeModal();
            location.reload(); // refresh to show new group
        })
        .catch(err => alert("Failed to create group: " + err.message));
});

// JOIN GROUP AJAX
function joinGroup(groupId, btn) {
    post("/studygroups/join", new URLSearchParams({ groupId: groupId }))
        .then(res => {
            btn.textContent = "Already Member";
            btn.disabled = true;
            btn.classList.add("disabled");

            const counter = document.querySelector(`#group-${groupId} .members-count`);
            if (counter) counter.textContent = res.membersCount + " members";
        })
        .catch(err => alert("Could not join group: " + err.message));
}

// CLOSE MODAL BY CLICKING OUTSIDE
document.getElementById("groupModal").addEventListener("click", e => {
    if (e.target.id === "groupModal") closeModal();
});
