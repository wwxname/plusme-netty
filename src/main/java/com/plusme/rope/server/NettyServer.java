package com.plusme.rope.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author plusme
 * @create 2019-11-29 17:08
 */
@Slf4j
public abstract class NettyServer implements Runnable,Server {
    private int port = 80;
    protected ChannelHandlerAdapter processor = null;
    protected ChannelInitializer<SocketChannel> internal = null;

    @Override
    public void run() {
        internal = init();
        EventLoopGroup group = null;
        EventLoopGroup work = null;
        ServerBootstrap boot = null;
        try {
            group = new NioEventLoopGroup(1);
            work = new NioEventLoopGroup(2);
            boot = new ServerBootstrap();
            boot.group(group, work)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(internal);

            ChannelFuture future = boot.bind(port).sync();
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            log.info(e.getMessage());
        } finally {
            if (group != null) {
                group.shutdownGracefully();
            }
            if (work != null) {
                work.shutdownGracefully();
            }
        }
    }

    public NettyServer bind(int port) {
        this.port = port;
        return this;
    }

    public NettyServer setProcessor(ChannelHandlerAdapter processor) {
        this.processor = processor;
        return this;
    }


}
