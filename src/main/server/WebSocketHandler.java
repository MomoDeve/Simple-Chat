package main.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.util.List;

public class WebSocketHandler extends ChannelInboundHandlerAdapter {
    protected CommandExecutor commandExecutor = new CommandExecutor();
    protected ChatServer chatServer = new ChatServer();
    protected UserSocketContext socketContext = new UserSocketContext();

    WebSocketHandler(ChatServer chatServer, UserSocketContext socketContext) {
        this.chatServer = chatServer;
        this.socketContext = socketContext;
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        String username = socketContext.unbind(ctx.channel());
        chatServer.removeUser(username);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        if (msg instanceof WebSocketFrame) {
            Channel currentChannel = ctx.channel();
            System.out.println("Received webSocketFrame from channel: " + currentChannel);
            if (msg instanceof TextWebSocketFrame) {
                System.out.println("Processing text websocket: " + ((TextWebSocketFrame) msg).text());
                List<ServerResponse> commands = this.commandExecutor.execute(currentChannel, socketContext, (TextWebSocketFrame) msg, chatServer);
                for (ServerResponse response : commands) {
                    if (response.broadcast()) {
                        for (Channel channel : socketContext.allChannels()) {
                            channel.writeAndFlush(new TextWebSocketFrame(response.command));
                        }
                    } else {
                        Channel channel = socketContext.getChannel(response.receiver);
                        if (channel == null && socketContext.getUserName(currentChannel) == null) {
                            channel = ctx.channel(); // this means current channel was rejected by executor. We still want to send commands to send the error message
                        }
                        if (channel != null) {
                            channel.writeAndFlush(new TextWebSocketFrame(response.command));
                        }
                    }
                }
            } else {
                System.out.println("Received unsupported WebSocketFrame: " + msg.toString());
            }
        }
    }
}