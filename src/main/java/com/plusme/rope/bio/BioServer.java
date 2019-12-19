package com.plusme.rope.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author plusme
 * @create 2019-12-06 14:10
 */
public class BioServer {
    public static void main(String[] args) throws IOException {
        ServerSocket server = null;
        server = new ServerSocket(8080);

        Socket socket = null;
        while (true) {
            socket = server.accept();
            System.err.println("#######accept");
            new Thread(new BioHandler(socket)).start();
        }

    }

    static class BioHandler implements Runnable {

        private Socket socket;

        BioHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            BufferedReader in;
            PrintWriter out;
            try {
                in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
                out = new PrintWriter(this.socket.getOutputStream(), true);
                String str1 = in.readLine();
                System.err.println(str1);
                out.write(str1);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
