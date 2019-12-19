package com.plusme.rope.tomcat;

import com.plusme.rope.tomcat.net.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.nio.ByteBuffer;
import java.util.Set;

/**
 * @author plusme
 * @create 2019-12-05 23:46
 */
public class WWXBootstrap {
    public static void main(String[] args) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (; ; ) {
                        Thread.sleep(2000);
                        //System.err.println("----------------------------------------------");
                        ThreadMXBean mxBean = ManagementFactory.getThreadMXBean();
                        ThreadInfo[] allThreads = mxBean.dumpAllThreads(false, false);
                        for (ThreadInfo threadInfo : allThreads) {
                            if (threadInfo.getThreadId() > 10) {
                                // System.err.println(threadInfo.getThreadId() + "===" + threadInfo.getThreadName());

                            }

                        }
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        NioEndpoint endpoint = new NioEndpoint();
        endpoint.setPort(8080);
        endpoint.setName("http-nio-8080");
        endpoint.setHandler(new ConnectionHandler<>());
        endpoint.setPollerThreadCount(1);
        endpoint.init();
        endpoint.start();
        Thread.sleep(300000);
    }
}

@Slf4j
class ConnectionHandler<S> implements AbstractEndpoint.Handler<S> {


    @Override
    public Object getGlobal() {
        return null;
    }

    @Override
    public Set<S> getOpenSockets() {
        return null;
    }

    @Override
    public void release(SocketWrapperBase<S> socketWrapper) throws IOException {
        socketWrapper.close();
    }

    @Override
    public void pause() {

    }

    @Override
    public void recycle() {

    }


    @Override
    public SocketState process(SocketWrapperBase<S> wrapper, SocketEvent status) {
        if (log.isDebugEnabled()) {
            log.info("abstractConnectionHandler.process" +
                    wrapper.getSocket() + status);
        }
        if (wrapper == null) {
            // Nothing to do. Socket has been closed.
            return SocketState.CLOSED;
        }
        System.err.println("实例执行的任务是 " + wrapper);
        System.err.println("实例执行的channel " + wrapper.getSocket());
        NioChannel socket = (NioChannel) wrapper.getSocket();


        ByteBuffer readBuffer = ByteBuffer.allocate(1024);


        int readBytes = 0;
        try {
            readBytes = socket.read(readBuffer);
        } catch (IOException e) {
            e.printStackTrace();
            return SocketState.CLOSED;
        }
        if (readBytes > 0) {
            readBuffer.flip();
            byte[] bytes = new byte[readBuffer.remaining()];
            readBuffer.get(bytes);
            String body = new String(bytes);
            System.err.println("Thread id:" + Thread.currentThread().getId() + ";" + body);
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            buffer.put(body.getBytes());
            buffer.flip();

            try {
                socket.write(buffer);
            } catch (IOException e) {
                e.printStackTrace();
                return SocketState.CLOSED;
            }

        } else if (readBytes == -1) {
            //客户端主动断开连接
            return SocketState.CLOSED;
        }
        if (readBytes == 1024) {
            wrapper.registerReadInterest();
        }


        if (status == SocketEvent.DISCONNECT || status == SocketEvent.ERROR) {
            // Nothing to do. Endpoint requested a close and there is no
            // longer a processor associated with this socket.
            return SocketState.CLOSED;
        }

        return SocketState.OPEN;
    }
}