package main.server;

import java.text.MessageFormat;

public class ChatMessage {
    public final String author;
    public final String text;
    public final String receiver;

    ChatMessage(String author, String text) {
        this.author = author;
        this.text = text;
        this.receiver = null;
    }

    ChatMessage(String author, String text, String receiver) {
        this.author = author;
        this.text = text;
        this.receiver = receiver;
    }

    public boolean hasReceiver() {
        return this.receiver != null;
    }

    @Override
    public String toString() {
        if (!this.hasReceiver()) {
            return MessageFormat.format("[{0}]: {1}", this.author, this.text);
        } else {
            return MessageFormat.format("[{0}] @{1} {2}", this.author, this.receiver, this.text);
        }
    }
}
