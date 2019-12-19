package com.plusme.rope.privateprotocol;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * @author plusme
 * @create 2019-12-01 23:07
 */
public class NettyMarshallingHandler extends SimpleChannelInboundHandler<NettyMessage> {



    @Override
    protected void messageReceived(ChannelHandlerContext ctx, NettyMessage msg) throws Exception {
        System.err.println(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

}
