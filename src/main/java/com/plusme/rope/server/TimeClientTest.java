package com.plusme.rope.server;

import com.plusme.rope.model.PlusmeProtocol;
import com.plusme.rope.privateprotocol.NettyMarshallingClient;
import com.plusme.rope.privateprotocol.NettyMessage;
import com.plusme.rope.proto.NettyProtoClient;
import com.plusme.rope.str.NettyStrClient;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author plusme
 * @create 2019-11-30 0:13
 */
@Slf4j
public class TimeClientTest {
   static SimpleChannelInboundHandler handler;
    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 1; i++) {
            NettyStrClient client = new NettyStrClient();
            client.send("wwx\r\n");
        }

    }
}
