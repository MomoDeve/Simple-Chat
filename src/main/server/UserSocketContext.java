package main.server;

import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class UserSocketContext {
    private final Map<Channel, String> mappings = new HashMap<>();
    private final Map<String, Channel> invMappings = new HashMap<>();

    public void bind(Channel channel, String username) {
        mappings.put(channel, username);
        invMappings.put(username, channel);
    }

    public Set<Channel> allChannels() {
        return mappings.keySet();
    }

    public String unbind(Channel channel) {
        String username = mappings.get(channel);
        mappings.remove(channel);
        invMappings.remove(username);
        return username;
    }

    public String getUserName(Channel channel) {
        return mappings.get(channel);
    }

    public Channel getChannel(String username) {
        return invMappings.get(username);
    }
}
