package com.plusme.rope.http;

import com.plusme.rope.server.NettyServer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author plusme
 * @create 2019-12-01 2:24
 */
@Slf4j
public class NettyHttpServer extends NettyServer {
    class HttpFileServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
        private final String url;
        private final HttpServerService service;

        HttpFileServerHandler(String url) {
            this.url = url;
            service = new HttpServerService(url);
        }

        @Override
        protected void messageReceived(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
            log.info(msg.toString());
            service.execute(ctx, msg);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
        }
    }

    String url = "";

    public NettyHttpServer(String url) {
        this.url = url;
    }

    @Override
    public ChannelInitializer<SocketChannel> init() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast("http-decoder", new HttpRequestDecoder());
                ch.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65536));
                ch.pipeline().addLast("http-encoder", new HttpResponseEncoder());
                ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
                ch.pipeline().addLast("fileServer", new HttpFileServerHandler(url));
            }
        };
    }
}
