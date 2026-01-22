let socket;
const renderedMessageIds = new Set(); // track already rendered messages

function renderMessage(msg) {
    if (renderedMessageIds.has(msg.id)) return; // skip duplicates
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

function loadHistory() {
    fetch(CONTEXT_PATH + "/loadMessages?conversationId=" + CONVERSATION_ID)
        .then(res => res.json())
        .then(data => {
            data.forEach(msg => {
                renderMessage(msg); // will skip duplicates automatically
            });
        })
        .catch(err => console.error("Failed to load chat history", err));
}

function connect() {
    socket = new WebSocket("ws://" + window.location.host + CONTEXT_PATH + "/ws/chat");

    socket.onopen = () => console.log("✅ WebSocket connected");

    socket.onmessage = (event) => {
        const msg = JSON.parse(event.data);
        renderMessage(msg); // will not duplicate messages
    };

    socket.onclose = () => console.log("❌ WebSocket disconnected");
}

function sendMessage() {
    const input = document.getElementById("messageInput");
    const text = input.value.trim();
    if (!text) return;

    const tempId = "temp-" + Date.now();
    renderMessage({
        id: tempId,
        senderId: USER_ID,
        content: text,
        timestamp: new Date().toISOString()
    });

    const payload = {
        conversationId: CONVERSATION_ID,
        content: text,
        tempId: tempId // send tempId to server
    };

    socket.send(JSON.stringify(payload));

    input.value = "";
}


// helpers
function escapeHtml(text) {
    return text.replace(/[&<>"']/g, m => ({"&":"&amp;","<":"&lt;",">":"&gt;","\"":"&quot;","'":"&#039;"}[m]));
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
