let socket;
const renderedMessageIds = new Set(); // track already rendered messages

function renderMessage(msg) {
    if (renderedMessageIds.has(msg.id)) return; // skip duplicates (history vs. live)
    renderedMessageIds.add(msg.id);

    const container = document.getElementById("chatMessages");
    const bubble = document.createElement("div");
    bubble.className = "chat-bubble " + (msg.senderId === USER_ID ? "sent" : "received");

    bubble.innerHTML = `
        <div class="bubble-text">${escapeHtml(msg.content)}</div>
        <div class="bubble-time">${formatTime(msg.timestamp)}</div>
    `;

    container.appendChild(bubble);
    container.scrollTop = container.scrollHeight;
}

function showNotice(text) {
    const container = document.getElementById("chatMessages");
    const note = document.createElement("div");
    note.className = "chat-notice";
    note.textContent = text;
    container.appendChild(note);
    container.scrollTop = container.scrollHeight;
}

function loadHistory() {
    fetch(CONTEXT_PATH + "/loadMessages?conversationId=" + CONVERSATION_ID)
        .then(res => {
            if (!res.ok) throw new Error("HTTP " + res.status);
            return res.json();
        })
        .then(data => data.forEach(renderMessage))
        .catch(err => {
            console.error("Failed to load chat history", err);
            showNotice("Could not load earlier messages.");
        });
}

function connect() {
    const scheme = window.location.protocol === "https:" ? "wss://" : "ws://";
    socket = new WebSocket(scheme + window.location.host + CONTEXT_PATH + "/ws/chat");

    socket.onmessage = (event) => {
        const msg = JSON.parse(event.data);
        if (msg.error) { showNotice(msg.error); return; }
        // The socket delivers messages for all of the user's conversations; show only this one.
        if (msg.conversationId === CONVERSATION_ID) renderMessage(msg);
    };

    socket.onclose = () => showNotice("Disconnected. Reload the page to reconnect.");
}

function sendMessage() {
    const input = document.getElementById("messageInput");
    const text = input.value.trim();
    if (!text) return;

    if (!socket || socket.readyState !== WebSocket.OPEN) {
        showNotice("Not connected. Reload the page and try again.");
        return;
    }

    // The server echoes the saved message back to the sender, so it is rendered once, on receipt.
    socket.send(JSON.stringify({ conversationId: CONVERSATION_ID, content: text }));
    input.value = "";
}

document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("messageInput").addEventListener("keydown", (e) => {
        if (e.key === "Enter") { e.preventDefault(); sendMessage(); }
    });
});

// helpers
function escapeHtml(text) {
    return String(text).replace(/[&<>"']/g, m => ({"&":"&amp;","<":"&lt;",">":"&gt;","\"":"&quot;","'":"&#039;"}[m]));
}

function formatTime(ts) {
    if (!ts) return "";
    const d = new Date(ts);
    const h = d.getHours().toString().padStart(2,"0");
    const m = d.getMinutes().toString().padStart(2,"0");
    return `${h}:${m}`;
}

// Init
loadHistory();
connect();
