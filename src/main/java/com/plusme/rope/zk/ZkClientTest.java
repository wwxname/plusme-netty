package com.plusme.rope.zk;

import org.I0Itec.zkclient.DataUpdater;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import sun.nio.cs.StandardCharsets;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.UUID;

/**
 * @author plusme
 * @create 2019-12-06 5:40
 */
public class ZkClientTest {
    // 此demo使用的集群，所以有多个ip和端口
    private static String CONNECT_SERVER = "127.0.0.1:2181";
    private static int SESSION_TIMEOUT = 3000000;
    private static int CONNECTION_TIMEOUT = 300000;
    private static ZkClient zkClient;
    private static ZooKeeper zooKeeper;



    public static void main(String[] args) throws InterruptedException, IOException, KeeperException {
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
        }).start();


        ZooKeeper zooKeeper = new ZooKeeper(CONNECT_SERVER, 40000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.err.println(event.getWrapper().toString());
            }
        });
        System.err.println("------");
        int version = zooKeeper.exists("/config",true).getVersion();
        System.err.println("------");
        Stat s =zooKeeper.setData("/config",UUID.randomUUID().toString().getBytes(),version);

        System.err.println("-------");
        System.out.println("-------");
        Thread.sleep(3000000);


//        zkClient = new ZkClient(CONNECT_SERVER, SESSION_TIMEOUT, CONNECTION_TIMEOUT, new ZkSerializer(){
//            @Override
//            public byte[] serialize(Object data) throws ZkMarshallingError {
//                return data.toString().getBytes();
//            }
//
//            @Override
//            public Object deserialize(byte[] bytes) throws ZkMarshallingError {
//                return new String(bytes);
//            }
//        });
//        zkClient.subscribeDataChanges("/config", new IZkDataListener() {
//            @Override
//            public void handleDataDeleted(String arg0) throws Exception {
//                System.out.println("触发了删除事件：" + arg0);
//            }
//
//            @Override
//            public void handleDataChange(String arg0, Object arg1) throws Exception {
//                System.out.println("触发了改变事件：" + arg0 + "-->" + arg1);
//            }
//        });
//
////        zkClient.delete("/config");
////        zkClient.createPersistent("/config");
////        if (!zkClient.exists("/config")) {
//            zkClient.updateDataSerialized("/config", new DataUpdater<String>(){
//                @Override
//                public String update(String currentData) {
//                    return "python"+ UUID.randomUUID();
//                }
//            });
////        }
//        Thread.sleep(3000000);

    }


}
