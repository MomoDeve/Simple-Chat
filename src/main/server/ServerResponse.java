package main.server;

public class ServerResponse {
    public final String command;

    public final String receiver;

    ServerResponse(String command) {
        this.command = command;
        this.receiver = null;
    }

    ServerResponse(String command, String receiver) {
        this.command = command;
        this.receiver = receiver;
    }

    boolean broadcast() {
        return this.receiver == null;
    }
}
