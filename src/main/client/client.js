import { getUserNameMessage, getSendPublicMessage, getSendPrivateMessage, RESPONSE_TYPE } from './protocol.js';
import { onNicknameSet, onNicknameInvalid, onHistoryAppend, onMessageSend, onMessageInvalid, onConnectionFailed, onConnectionSuccess } from './callbacks.js';

const SERVER_ADDRESS = 'ws://127.0.0.1:8090';
const RECONNECT_DELAY = 10000;

export class Client {
    constructor() {
        this.webSocket = undefined;
    }

    reconnectToServer(username) {
        setTimeout(() => this.connectToServer(username), RECONNECT_DELAY);
        onConnectionFailed(RECONNECT_DELAY);
    }

    connectToServer(username) {
        this.webSocket = new WebSocket(SERVER_ADDRESS);
        this.webSocket.onclose = () => this.reconnectToServer(username);
        this.webSocket.onopen = () => this.onConnectToServer(username);
        this.webSocket.onmessage = (e) => this.messageHandler(e.data);
    }

    onConnectToServer(username) {
        this.webSocket.send(getUserNameMessage(username));
        onConnectionSuccess();
    }

    sendMessage(text) {
        if (text.startsWith('@')) {
            const receiver = text.substring(1, text.indexOf(' '));
            const message = text.substring(text.indexOf(' ') + 1);
            this.webSocket.send(getSendPrivateMessage(message, receiver));
        } else {
            this.webSocket.send(getSendPublicMessage(text));
        }
        onMessageSend();
    }

    messageHandler(response) {
        let message = {};
        try {
            message = JSON.parse(response);
        } catch (e) {
            console.log('invalid response: ' + response);
            return;
        }
        console.log('received response: ' + response);
        switch (message.type) {
            case RESPONSE_TYPE.NICKNAME_SET_SUCCESS:
                onNicknameSet();
                break;
            case RESPONSE_TYPE.NICKNAME_SET_ERROR:
                onNicknameInvalid();
                break;
            case RESPONSE_TYPE.APPEND_MESSAGE_HISTORY:
                onHistoryAppend(message.history);
                break;
            case RESPONSE_TYPE.INVALID_MESSAGE:
                onMessageInvalid();
                break;
        }
    }
}