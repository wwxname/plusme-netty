package com.plusme.rope.privateprotocol;

import com.plusme.rope.model.PlusmeProtocol;
import com.plusme.rope.server.NettyServer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

/**
 * @author plusme
 * @create 2019-12-01 22:55
 */
public class NettyMarshallingServer extends NettyServer {
    @Override
    public ChannelInitializer<SocketChannel> init() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(MarshallingCodecFactory.buildMarshallingDecoder());
                ch.pipeline().addLast(MarshallingCodecFactory.buildMarshallingEncoder());
                ch.pipeline().addLast(processor.getClass().newInstance());
            }
        };
    }
}