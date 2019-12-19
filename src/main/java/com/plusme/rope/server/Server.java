package com.plusme.rope.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * @author plusme
 * @create 2019-12-01 2:07
 */
public interface Server {
    ChannelInitializer<SocketChannel> init();
}
