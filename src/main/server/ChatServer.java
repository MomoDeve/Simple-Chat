package main.server;

import java.util.*;

public class ChatServer {

    Set<String> userNames = new HashSet<>();
    List<ChatMessage> messages = new ArrayList<>();

    public boolean addNewUser(String name) {
        if (name.isEmpty()) return false;
        if (name.contains(" ")) return false;

        return userNames.add(name);
    }

    public void removeUser(String name) {
        userNames.remove(name);
    }

    public boolean addPublicMessage(String author, String text) {
        if (text.isEmpty()) return false;

        messages.add(new ChatMessage(author, text));
        return true;
    }

    public boolean addPrivateMessage(String author, String text, String receiver) {
        if (!userNames.contains(author) || !userNames.contains(receiver)) return false;
        if (text.isEmpty()) return false;

        messages.add(new ChatMessage(author, text, receiver));
        return true;
    }

    public List<String> getMessageHistory(String forUser, int maxMessageCount) {
        ArrayList<String> userMessages = new ArrayList<>();
        for (int i = this.messages.size() - 1; i >= 0 && userMessages.size() < maxMessageCount; i--) {
            ChatMessage message = this.messages.get(i);
            if (!message.hasReceiver() || message.receiver.equals(forUser)) {
                userMessages.add(message.toString());
            }
        }
        Collections.reverse(userMessages);
        return userMessages;
    }
}
