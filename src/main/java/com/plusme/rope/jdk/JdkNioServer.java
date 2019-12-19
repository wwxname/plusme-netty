package com.plusme.rope.jdk;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.*;

/**
 * @author plusme
 * @create 2019-12-02 22:43
 */
public class JdkNioServer implements Runnable {

    private static ThreadFactory threadFactory = new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("jdk-task-" + thread.getId());
            return thread;
        }
    };

    private static ExecutorService executor = new ThreadPoolExecutor(5, 10, 1, TimeUnit.MINUTES,
            new LinkedBlockingDeque<Runnable>(1024), threadFactory, new ThreadPoolExecutor.AbortPolicy());
    private int port = 80;
    private Selector selector;
    private HashMap<SelectionKey, JdkTask> keyTask = new HashMap<>();
    private ServerSocketChannel serverChannel;

    private void init() throws Exception {
        selector = Selector.open();
        serverChannel = ServerSocketChannel.open();
        System.err.println("serverChannel is :" + serverChannel);
        serverChannel.configureBlocking(false);
        serverChannel.socket().bind(new InetSocketAddress(port), 1024);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.err.println("serverChannel is :" + serverChannel);
    }

    JdkNioServer() {
        this(80);
    }

    JdkNioServer(int port) {
        this.port = port;
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        int i = 0;
        while (true) {
            try {
                selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> it = selectedKeys.iterator();
                SelectionKey key = null;
                while (it.hasNext()) {
                    key = it.next();
                    synchronized (key) {
                        if (key.isValid()) {
                            if (key.isAcceptable()) {
                                it.remove();
                                SocketChannel sc = serverChannel.accept();
                                sc.configureBlocking(false);
                                sc.register(selector, SelectionKey.OP_READ);
                            } else {
                                it.remove();
                                if (keyTask.get(key) == null) {
                                    keyTask.put(key, new JdkTask(key));
                                }
                                keyTask.get(key).readI.incrementAndGet();
                                keyTask.get(key).run();

                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
    }
}
