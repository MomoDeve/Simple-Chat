package main.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;

// source: https://medium.com/@irunika/how-to-write-a-http-websocket-server-using-netty-f3c136adcba9

public class HttpServerHandler extends ChannelInboundHandlerAdapter {
    WebSocketServerHandshaker handshaker;
    protected ChatServer chatServer = new ChatServer();
    protected UserSocketContext socketContext = new UserSocketContext();

    HttpServerHandler(ChatServer chatServer, UserSocketContext socketContext) {
        this.chatServer = chatServer;
        this.socketContext = socketContext;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof HttpRequest) {
            HttpRequest httpRequest = (HttpRequest) msg;
            HttpHeaders headers = httpRequest.headers();

            if (headers.get(HttpHeaderNames.CONNECTION).toUpperCase().contains("UPGRADE") &&
                    headers.get(HttpHeaderNames.UPGRADE).toUpperCase().contains("WEBSOCKET")) {
                ctx.pipeline().replace(this, "websocketHandler", new WebSocketHandler(chatServer, socketContext));
                handleHandshake(ctx, httpRequest);
            }
        }
    }

    protected void handleHandshake(ChannelHandlerContext ctx, HttpRequest req) {
        WebSocketServerHandshakerFactory wsFactory =
                new WebSocketServerHandshakerFactory(getWebSocketURL(req), null, true);
        handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), req);
        }
    }

    protected String getWebSocketURL(HttpRequest req) {
        return "ws://" + req.headers().get("Host") + req.uri();
    }
}
