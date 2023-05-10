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

export function onConnectionFailed(reconnectionDelay) {
    const sendMessageButton = document.getElementById('message-input-button');
    sendMessageButton.disabled = true;

    const sendNameButton = document.getElementById('username-input-button');
    sendNameButton.disabled = true;

    const errorBox = document.getElementById('error');
    const delayInSeconds = reconnectionDelay / 1000;
    for (let i = 0; i <= delayInSeconds; i++) {
        setTimeout(() => {
            errorBox.innerText = "connection failed! Trying to reconnect in " + (delayInSeconds - i) + " seconds...";
        }, i * 1000);
    }
}

export function onConnectionSuccess() {
    const errorBox = document.getElementById('error');
    errorBox.innerText = '';

    const sendMessageButton = document.getElementById('message-input-button');
    sendMessageButton.disabled = false;

    const sendNameButton = document.getElementById('username-input-button');
    sendNameButton.disabled = false;
}