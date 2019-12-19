package com.plusme.rope.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * @author plusme
 * @create 2019-12-01 2:17
 */
public interface Client {
    ChannelInitializer<SocketChannel> init();
}
