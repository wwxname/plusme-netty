package com.plusme.rope.notbootstrap;

import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * @author plusme
 * @create 2019-12-02 16:35
 */
public class NoBootstrapServer {
    static class NettyStrHandler extends ChannelHandlerAdapter {


        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            ctx.flush();
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.err.println(msg);
        }
    }

    static EventLoopGroup group;
    static EventLoopGroup work;
    static ChannelHandler childHandler;
    static NioServerSocketChannel channel;


    public static void main(String[] args) throws InterruptedException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (; ; ) {
                        Thread.sleep(2000);
                        System.err.println("----------------------------------------------");
                        ThreadMXBean mxBean = ManagementFactory.getThreadMXBean();
                        ThreadInfo[] allThreads = mxBean.dumpAllThreads(false, false);
                        for (ThreadInfo threadInfo : allThreads) {
                            if (threadInfo.getThreadId() > 10) {
                                System.err.println(threadInfo.getThreadId() + "===" + threadInfo.getThreadName());

                            }

                        }
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        group = new NioEventLoopGroup(1);
        work = new NioEventLoopGroup(1);
        channel = new NioServerSocketChannel(group.next(), work);
        Map<ChannelOption<?>, Object> option = new HashMap();
        option.put(ChannelOption.SO_BACKLOG, 1024);
        channel.config().setOptions(option);
        ChannelPromise regFuture = channel.newPromise();
        channel.unsafe().register(regFuture);


        childHandler = new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
                ch.pipeline().addLast(new StringDecoder());
                ch.pipeline().addLast(new NettyStrHandler());
            }
        };

        channel.pipeline().addLast(new NoServerBootstrapAcceptor(childHandler, null, null));


        ChannelFuture future = channel.bind(new InetSocketAddress(80)).sync();
        future.channel().closeFuture().sync();
    }
}
