package com.plusme.rope.model;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author plusme
 * @create 2019-11-30 23:10
 */
@Slf4j
public class PlusmeProtoHandler extends ChannelHandlerAdapter {

    PlusmeService service = new PlusmeService();

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        log.error(cause.getMessage());
        ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        System.err.println("Thread Id :"+Thread.currentThread().getId());
        PlusmeProtocol.Request request = (PlusmeProtocol.Request) msg;
        PlusmeProtocol.Response response = service.execute(request);
        ctx.writeAndFlush(response);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
}
