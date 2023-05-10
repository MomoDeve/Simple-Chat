export function onNicknameSet() {
    document.getElementById('username-page').style.visibility = 'hidden';
    document.getElementById('chat-page').style.visibility = 'visible';
}

export function onNicknameInvalid(nickname) {
    const inputBox = document.getElementById('username-input');
    inputBox.value = '';
    inputBox.placeholder = 'try another nickname!';
}

export function onHistoryAppend(messages) {
    const chatBox = document.getElementById('chat');
    const current = chatBox.value;
    if (current) {
        chatBox.value = current + '\n' + messages.join('\n');
    } else {
        chatBox.value = messages.join('\n');
    }
}

export function onMessageSend() {
    const inputBox = document.getElementById('message-input');
    inputBox.value = '';
}

export function onMessageInvalid() {
    const chatBox = document.getElementById('chat');
    chatBox.value += "\n[]: Cannot send message to the receiver";
}