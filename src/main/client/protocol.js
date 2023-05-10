export const REQUEST_TYPE = {
    NICKNAME_SET: 1,
    PUBLIC_MESSAGE_SEND: 2,
    PRIVATE_MESSAGE_SEND: 3,
}

export const RESPONSE_TYPE = {
    NICKNAME_SET_SUCCESS: 1,
    NICKNAME_SET_ERROR: 2,
    APPEND_MESSAGE_HISTORY: 3,
    INVALID_MESSAGE: 4,
}

export function getUserNameMessage(name) {
    return JSON.stringify({
        type: REQUEST_TYPE.NICKNAME_SET,
        nickname: name,
    });
}

export function getSendPublicMessage(text) {
   return JSON.stringify({
        type: REQUEST_TYPE.PUBLIC_MESSAGE_SEND,
        text: text,
   });
}

export function getSendPrivateMessage(text, receiver) {
   return JSON.stringify({
        type: REQUEST_TYPE.PRIVATE_MESSAGE_SEND,
        text: text,
        receiver: receiver,
   });
}