package main.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;
import java.util.List;


public class CommandExecutor {

    ObjectMapper mapper = new ObjectMapper();

    private String sendTypedMessage(int type) {
        ObjectNode root = mapper.createObjectNode();
        root.put("type", type);
        return root.toString();
    }

    private String sendHistoryMessage(List<String> messages) {
        ObjectNode root = mapper.createObjectNode();
        root.put("type", MessageResponse.APPEND_MESSAGE_HISTORY);

        ArrayNode historyNode = root.putArray("history");
        for (String message : messages.stream().map(StringEscapeUtils::escapeJava).toList()) {
            historyNode.add(message);
        }

        return root.toString();
    }

    private List<ServerResponse> executeNicknameSet(Channel socketChannel, UserSocketContext socketContext, ChatServer chatServer, String nickname) {
        List<ServerResponse> response = new ArrayList<>();
        boolean userAdded = chatServer.addNewUser(nickname);
        if (userAdded) {
            socketContext.bind(socketChannel, nickname);
            response.add(new ServerResponse(sendTypedMessage(MessageResponse.NICKNAME_SET_SUCCESS), nickname));
            response.add(new ServerResponse(sendHistoryMessage(chatServer.getMessageHistory(nickname, 50)), nickname));
            chatServer.addPublicMessage("", "@" + nickname + " joined the chat");
            response.add(new ServerResponse(sendHistoryMessage(chatServer.getMessageHistory(nickname, 1))));
        } else {
            response.add(new ServerResponse(sendTypedMessage(MessageResponse.NICKNAME_SET_ERROR), nickname));
        }
        return response;
    }

    private List<ServerResponse> executePublicMessageSend(Channel socketChannel, UserSocketContext socketContext, ChatServer chatServer, String author, String text) {
        List<ServerResponse> response = new ArrayList<>();
        boolean messageReceived = chatServer.addPublicMessage(author, text);
        if (messageReceived) {
            List<String> message = new ArrayList<>(1);
            message.add(new ChatMessage(author, text).toString());
            response.add(new ServerResponse(sendHistoryMessage(message)));
        } else {
            response.add(new ServerResponse(sendTypedMessage(MessageResponse.INVALID_MESSAGE), author));
        }
        return response;
    }

    private List<ServerResponse> executePrivateMessageSend(Channel socketChannel, UserSocketContext socketContext, ChatServer chatServer, String author, String text, String receiver) {
        List<ServerResponse> response = new ArrayList<>();
        boolean messageReceived = chatServer.addPrivateMessage(author, text, receiver);
        if (messageReceived) {
            List<String> messages = new ArrayList<>(1);
            messages.add(new ChatMessage(author, text, receiver).toString());
            String command = sendHistoryMessage(messages);
            response.add(new ServerResponse(command, author));
            if (!receiver.equals(author)) {
                response.add(new ServerResponse(command, receiver));
            }
        } else {
            response.add(new ServerResponse(sendTypedMessage(MessageResponse.INVALID_MESSAGE), author));
        }
        return response;
    }

    public List<ServerResponse> execute(Channel socketChannel, UserSocketContext socketContext, TextWebSocketFrame webSocket, ChatServer chatServer) {
        try {
            JsonNode root = mapper.readTree(webSocket.text());
            switch (root.get("type").asInt(MessageRequest.UNKNOWN)) {
                case MessageRequest.NICKNAME_SET: {
                    String nickname = root.get("nickname").asText("").trim();
                    return executeNicknameSet(socketChannel, socketContext, chatServer, nickname);
                }
                case MessageRequest.PUBLIC_MESSAGE_SEND: {
                    String author = socketContext.getUserName(socketChannel);
                    String text = root.get("text").asText("");
                    return executePublicMessageSend(socketChannel, socketContext, chatServer, author, text);
                }
                case MessageRequest.PRIVATE_MESSAGE_SEND: {
                    String author = socketContext.getUserName(socketChannel);
                    String text = root.get("text").asText("");
                    String receiver = root.get("receiver").asText("");
                    return executePrivateMessageSend(socketChannel, socketContext, chatServer, author, text, receiver);
                }
            }
        } catch (Exception e) {
            System.out.println("cannot parse json from socket: " + webSocket.text() + " (" + e.toString() + ")");
        }
        return new ArrayList<>();
    }
}
