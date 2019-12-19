package com.plusme.rope;

import com.plusme.rope.http.NettyHttpServer;
import com.plusme.rope.privateprotocol.NettyMarshallingHandler;
import com.plusme.rope.privateprotocol.NettyMarshallingServer;
import com.plusme.rope.str.NettyStrHandler;
import com.plusme.rope.str.NettyStrServer;
import io.netty.util.concurrent.SingleThreadEventExecutor;
import lombok.extern.slf4j.Slf4j;
import sun.misc.ThreadGroupUtils;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadFactory;

/**
 * @author plusme
 * @create 2019-11-29 16:53
 */
@Slf4j
public class App {
    private static final CountDownLatch latch = new CountDownLatch(1);
    SingleThreadEventExecutor executor;
    public static Thread findThread(long threadId) {
        ThreadGroup group = Thread.currentThread().getThreadGroup();
        while(group != null) {
            Thread[] threads = new Thread[(int)(group.activeCount() * 1.2)];
            int count = group.enumerate(threads, true);
            for(int i = 0; i < count; i++) {
                if(threadId == threads[i].getId()) {
                    return threads[i];
                }
            }
            group = group.getParent();
        }
        return null;
    }

    public static void main(String[] args) throws InterruptedException {
        Thread.currentThread().setName("app-main");
        //new NettyProtoServer().bind(80).setProcessor(new PlusmeProtoHandler()).run();
        //new NettyHttpServer("/").run();
        //new NettyMarshallingServer().setProcessor(new NettyMarshallingHandler()).run();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for(;;){
                        Thread.sleep(2000);
                        System.err.println("----------------------------------------------");
                        ThreadMXBean mxBean = ManagementFactory.getThreadMXBean();
                        ThreadInfo[] allThreads = mxBean.dumpAllThreads(false, false);
                        for (ThreadInfo threadInfo : allThreads) {
                            if (threadInfo.getThreadId()>10){
                                System.err.println(threadInfo.getThreadId() + "===" + threadInfo.getThreadName());

                            }

                        }
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        new NettyStrServer().setProcessor(new NettyStrHandler()).run();

        latch.await();
    }
}
