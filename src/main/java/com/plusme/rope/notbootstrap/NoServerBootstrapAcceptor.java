package com.plusme.rope.notbootstrap;

import io.netty.channel.*;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author plusme
 * @create 2019-12-02 19:09
 */
@Slf4j
public class NoServerBootstrapAcceptor  extends ChannelHandlerAdapter{

    private final ChannelHandler childHandler;
    private final Map.Entry<ChannelOption<?>, Object>[] childOptions;
    private final Map.Entry<AttributeKey<?>, Object>[] childAttrs;

    NoServerBootstrapAcceptor(ChannelHandler childHandler, Map.Entry<ChannelOption<?>, Object>[] childOptions,
                            Map.Entry<AttributeKey<?>, Object>[] childAttrs) {
        this.childHandler = childHandler;
        this.childOptions = childOptions;
        this.childAttrs = childAttrs;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Channel child = (Channel) msg;

        child.pipeline().addLast(childHandler);

        child.unsafe().register(child.newPromise());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        final ChannelConfig config = ctx.channel().config();
        if (config.isAutoRead()) {
            // stop accept new connections for 1 second to allow the channel to recover
            // See https://github.com/netty/netty/issues/1328
            config.setAutoRead(false);
            ctx.channel().eventLoop().schedule(new Runnable() {
                @Override
                public void run() {
                    config.setAutoRead(true);
                }
            }, 1, TimeUnit.SECONDS);
        }
        // still let the exceptionCaught event flow through the pipeline to give the user
        // a chance to do something with it
        ctx.fireExceptionCaught(cause);
    }
}
