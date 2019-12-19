package com.plusme.rope.jdk;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author plusme
 * @create 2019-12-03 1:01
 */
public class JdkTask implements Runnable {
    public volatile AtomicInteger readI = new AtomicInteger(0);
    volatile SelectionKey key;

    JdkTask(SelectionKey key) throws IOException {
        this.key = key;
    }

    @Override
    public void run() {

        SocketChannel sc = (SocketChannel) key.channel();
        ByteBuffer readBuffer = ByteBuffer.allocate(1024);
        try {
            synchronized (key) {
                if (!key.isValid()) return;
            }
            int readBytes = sc.read(readBuffer);
            if (readBytes > 0) {
                readBuffer.flip();
                byte[] bytes = new byte[readBuffer.remaining()];
                readBuffer.get(bytes);
                String body = new String(bytes);

                System.err.println("Thread id:" + Thread.currentThread().getId() + ";" + body);

            }
            if (readBytes == 1024) {

            }
            if (readBytes == -1) {
                synchronized (key) {
                    key.cancel();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            key.cancel();
            try {
                sc.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

    }
}
