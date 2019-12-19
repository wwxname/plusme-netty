package com.plusme.rope.jdk;

/**
 * @author plusme
 * @create 2019-12-03 0:40
 */
public class JdkNioServerBoot {
    public static void main(String[] args) {
        JdkNioServer server = new JdkNioServer();
        new Thread(server, "jdki-server-2-0").start();
    }
}
