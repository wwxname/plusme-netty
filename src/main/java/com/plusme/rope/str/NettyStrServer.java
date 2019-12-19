package com.plusme.rope.str;

import com.alibaba.fastjson.JSONObject;
import com.plusme.rope.server.NettyServer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * @author plusme
 * @create 2019-12-02 13:04
 */
public class NettyStrServer extends NettyServer {
    @Override
    public ChannelInitializer<SocketChannel> init() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
                ch.pipeline().addLast(new StringDecoder());
                ch.pipeline().addLast(processor.getClass().newInstance());
                ch.pipeline().names();
                //System.err.println(JSONObject.toJSONString( ch.pipeline().get("NettyStrServer$1#0").getClass().getMethods()));
                String s1  =  JSONObject.toJSONString(ch.pipeline().names());
                System.err.println(s1);
            }
        };
    }
}

