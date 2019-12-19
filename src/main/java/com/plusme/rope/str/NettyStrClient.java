package com.plusme.rope.str;

import com.google.protobuf.Message;
import com.googlecode.protobuf.format.JsonFormat;
import com.plusme.rope.model.PlusmeProtocol;
import com.plusme.rope.proto.NettyProtoClient;
import com.plusme.rope.server.Client;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import jdk.management.resource.internal.inst.UnixAsynchronousServerSocketChannelImplRMHooks;
import lombok.extern.slf4j.Slf4j;

import java.nio.Buffer;

/**
 * @author plusme
 * @create 2019-12-02 13:11
 */
@Slf4j
public class NettyStrClient implements Client {
    public InternalReadHandler internalReadHandler = new InternalReadHandler();

    @Override
    public ChannelInitializer<SocketChannel> init() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new StringEncoder());
                ch.pipeline().addLast(internalReadHandler);
            }
        };
    }


    class InternalReadHandler extends ChannelHandlerAdapter {

        volatile ChannelHandlerContext ctx;

        public void send(String request) {
//            ByteBuf buf= Unpooled.buffer(1024);
//            buf.writeBytes(request.getBytes());
//            ctx.writeAndFlush(buf);
            ctx.writeAndFlush(request);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.err.println("Thread Id :" + Thread.currentThread().getId());
            System.err.println(JsonFormat.printToString((Message) msg));
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            log.error(cause.getMessage());
            ctx.close();
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {

            this.ctx = ctx;

        }
    }

    private int port = 80;

    private String Ip4 = "127.0.0.1";

    private EventLoopGroup group = null;


    public NettyStrClient() throws InterruptedException {
        super();
        EventLoopGroup group = new NioEventLoopGroup(20);
        Bootstrap b = new Bootstrap();
        b.group(group).channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(init());
        b.connect(Ip4, port).sync();
        this.group = group;
    }


    public void send(String request) {
        internalReadHandler.send(request);
    }

    public void close() throws InterruptedException {
        this.group.shutdownGracefully();
    }
}
